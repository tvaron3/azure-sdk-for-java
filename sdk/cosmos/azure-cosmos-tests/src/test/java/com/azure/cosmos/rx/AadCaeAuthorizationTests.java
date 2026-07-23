// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.rx;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;
import com.azure.cosmos.ConnectionMode;
import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.TestObject;
import com.azure.cosmos.implementation.HttpConstants;
import com.azure.cosmos.implementation.TestConfigurations;
import com.azure.cosmos.test.faultinjection.CosmosFaultInjectionHelper;
import com.azure.cosmos.test.faultinjection.FaultInjectionConditionBuilder;
import com.azure.cosmos.test.faultinjection.FaultInjectionConnectionType;
import com.azure.cosmos.test.faultinjection.FaultInjectionOperationType;
import com.azure.cosmos.test.faultinjection.FaultInjectionResultBuilders;
import com.azure.cosmos.test.faultinjection.FaultInjectionRule;
import com.azure.cosmos.test.faultinjection.FaultInjectionRuleBuilder;
import com.azure.cosmos.test.faultinjection.FaultInjectionServerErrorType;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class AadCaeAuthorizationTests extends TestSuiteBase {
    private static final String CLAIMS =
        "{\"access_token\":{\"nbf\":{\"essential\":false,\"value\":\"1\"}}}";
    private final String databaseId = "cae-db-" + UUID.randomUUID();
    private final String containerId = "cae-container-" + UUID.randomUUID();
    private CosmosAsyncClient setupClient;

    @BeforeClass(groups = "long-emulator", timeOut = SETUP_TIMEOUT)
    public void beforeClass() {
        this.setupClient = new CosmosClientBuilder()
            .endpoint(TestConfigurations.HOST)
            .key(TestConfigurations.MASTER_KEY)
            .gatewayMode()
            .buildAsyncClient();
        this.setupClient.createDatabase(this.databaseId).block();
        this.setupClient.getDatabase(this.databaseId).createContainer(this.containerId, "/id").block();
    }

    @AfterClass(groups = "long-emulator", timeOut = SHUTDOWN_TIMEOUT, alwaysRun = true)
    public void afterClass() {
        if (this.setupClient != null) {
            this.setupClient.getDatabase(this.databaseId).delete().onErrorResume(error -> Mono.empty()).block();
            safeClose(this.setupClient);
        }
    }

    @Test(groups = "long-emulator", timeOut = 5 * TIMEOUT)
    public void gatewayDataPlaneRefreshesTokenAndRetries() {
        RecordingEmulatorCredential credential = new RecordingEmulatorCredential(true);
        try (CosmosAsyncClient client = createClient(ConnectionMode.GATEWAY, credential)) {
            CosmosAsyncContainer container = getContainer(client);
            configureRule(container, FaultInjectionOperationType.CREATE_ITEM, Integer.MAX_VALUE);

            TestObject item = TestObject.create();
            assertThat(container.createItem(item).block().getStatusCode())
                .isEqualTo(HttpConstants.StatusCodes.CREATED);
            assertCaeContexts(credential.contexts);
        }
    }

    @Test(groups = "long-emulator", timeOut = 5 * TIMEOUT)
    public void directMetadataRefreshesTokenAndRetries() {
        RecordingEmulatorCredential credential = new RecordingEmulatorCredential(true);
        try (CosmosAsyncClient client = createClient(ConnectionMode.DIRECT, credential)) {
            CosmosAsyncContainer container = getContainer(client);
            configureRule(container, FaultInjectionOperationType.METADATA_REQUEST_CONTAINER, Integer.MAX_VALUE);

            assertThat(container.read().block().getStatusCode()).isEqualTo(HttpConstants.StatusCodes.OK);
            assertCaeContexts(credential.contexts);
        }
    }

    @Test(groups = "long-emulator", timeOut = 5 * TIMEOUT)
    public void challengeRetryIsLimitedToOne() {
        RecordingEmulatorCredential credential = new RecordingEmulatorCredential(false);
        try (CosmosAsyncClient client = createClient(ConnectionMode.GATEWAY, credential)) {
            CosmosAsyncContainer container = getContainer(client);
            configureRule(container, FaultInjectionOperationType.CREATE_ITEM, 2);

            TestObject item = TestObject.create();
            Throwable failure = catchThrowable(
                () -> container.createItem(item).block());
            Throwable unwrappedFailure = Exceptions.unwrap(failure);

            assertThat(unwrappedFailure).isInstanceOf(CosmosException.class);
            assertThat(((CosmosException) unwrappedFailure).getStatusCode())
                .isEqualTo(HttpConstants.StatusCodes.UNAUTHORIZED);
            assertThat(credential.contexts.stream().filter(context -> CLAIMS.equals(context.claims)).count())
                .isEqualTo(1);
        }
    }

    private CosmosAsyncClient createClient(ConnectionMode connectionMode, TokenCredential credential) {
        CosmosClientBuilder builder = new CosmosClientBuilder()
            .endpoint(TestConfigurations.HOST)
            .credential(credential);
        return connectionMode == ConnectionMode.GATEWAY
            ? builder.gatewayMode().buildAsyncClient()
            : builder.directMode().buildAsyncClient();
    }

    private CosmosAsyncContainer getContainer(CosmosAsyncClient client) {
        return client.getDatabase(this.databaseId).getContainer(this.containerId);
    }

    private static void configureRule(CosmosAsyncContainer container, FaultInjectionOperationType operationType,
        int times) {
        FaultInjectionConditionBuilder conditionBuilder =
            new FaultInjectionConditionBuilder()
                .operationType(operationType)
                .connectionType(FaultInjectionConnectionType.GATEWAY);

        FaultInjectionRule rule = new FaultInjectionRuleBuilder("cae-" + UUID.randomUUID())
            .condition(conditionBuilder.build())
            .result(FaultInjectionResultBuilders
                .getResultBuilder(FaultInjectionServerErrorType.AAD_TOKEN_REVOKED)
                .times(times)
                .build())
            .build();
        CosmosFaultInjectionHelper.configureFaultInjectionRules(container, Collections.singletonList(rule)).block();
    }

    private static void assertCaeContexts(List<ContextSnapshot> contexts) {
        assertThat(contexts).isNotEmpty();
        assertThat(contexts).allMatch(context -> context.caeEnabled);
        assertThat(contexts).allMatch(context -> context.scopes.size() == 1);
        assertThat(contexts).anyMatch(context -> context.claims == null);
        assertThat(contexts).anyMatch(context -> CLAIMS.equals(context.claims));
    }

    private static final class RecordingEmulatorCredential implements TokenCredential {
        private final TokenCredential emulatorCredential =
            new AadAuthorizationTests.AadSimpleEmulatorTokenCredential(TestConfigurations.MASTER_KEY);
        private final List<ContextSnapshot> contexts = new CopyOnWriteArrayList<>();
        private final boolean issueDistinctClaimsToken;
        private final AtomicReference<AccessToken> fixedToken = new AtomicReference<>();

        private RecordingEmulatorCredential(boolean issueDistinctClaimsToken) {
            this.issueDistinctClaimsToken = issueDistinctClaimsToken;
        }

        @Override
        public Mono<AccessToken> getToken(TokenRequestContext tokenRequestContext) {
            this.contexts.add(new ContextSnapshot(tokenRequestContext));
            Mono<AccessToken> token = Mono.defer(() -> this.emulatorCredential.getToken(tokenRequestContext));
            if (!this.issueDistinctClaimsToken) {
                AccessToken cachedToken = this.fixedToken.get();
                return cachedToken == null
                    ? token.doOnNext(newToken -> this.fixedToken.compareAndSet(null, newToken))
                        .map(ignored -> this.fixedToken.get())
                    : Mono.just(cachedToken);
            }
            return tokenRequestContext.getClaims() == null
                ? token
                : Mono.delay(Duration.ofMillis(1100)).then(token);
        }
    }

    private static final class ContextSnapshot {
        private final List<String> scopes;
        private final String claims;
        private final boolean caeEnabled;

        private ContextSnapshot(TokenRequestContext tokenRequestContext) {
            this.scopes = new ArrayList<>(tokenRequestContext.getScopes());
            this.claims = tokenRequestContext.getClaims();
            this.caeEnabled = tokenRequestContext.isCaeEnabled();
        }
    }
}
