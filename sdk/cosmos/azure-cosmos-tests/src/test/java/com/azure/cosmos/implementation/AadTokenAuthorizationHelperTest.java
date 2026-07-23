// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.implementation;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;
import org.testng.annotations.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class AadTokenAuthorizationHelperTest {
    private static final String ACCOUNT_SCOPE = "https://account.documents.azure.com/.default";
    private static final String CLAIMS = "{\"access_token\":{\"nbf\":{\"essential\":false,\"value\":\"1\"}}}";
    private static final String OTHER_CLAIMS =
        "{\"access_token\":{\"nbf\":{\"essential\":false,\"value\":\"2\"}}}";

    @Test(groups = "unit")
    public void normalAuthorizationUsesCaeEnabledCache() throws Exception {
        RecordingTokenCredential credential = new RecordingTokenCredential();
        AadTokenAuthorizationHelper helper =
            new AadTokenAuthorizationHelper(credential, ACCOUNT_SCOPE, Constants.AAD_DEFAULT_SCOPE);

        String firstAuthorization = helper.getAuthorizationToken().block();
        String secondAuthorization = helper.getAuthorizationToken().block();

        assertThat(firstAuthorization).isEqualTo(secondAuthorization);
        assertThat(firstAuthorization).isEqualTo(URLEncoder.encode(
            "type=aad&ver=1.0&sig=token-1",
            StandardCharsets.UTF_8.name()));
        assertThat(credential.contexts).hasSize(1);
        assertTokenContext(credential.contexts.get(0), ACCOUNT_SCOPE, null);
    }

    @Test(groups = "unit")
    public void fallbackPreservesCaeClaimsContext() {
        RecordingTokenCredential credential = new RecordingTokenCredential(ACCOUNT_SCOPE);
        AadTokenAuthorizationHelper helper =
            new AadTokenAuthorizationHelper(credential, ACCOUNT_SCOPE, Constants.AAD_DEFAULT_SCOPE);

        helper.getAuthorizationToken().block();
        assertThat(credential.contexts).hasSize(2);
        assertTokenContext(credential.contexts.get(0), ACCOUNT_SCOPE, null);
        assertTokenContext(credential.contexts.get(1), Constants.AAD_DEFAULT_SCOPE, null);

        assertThat(helper.getCaeAuthorizationToken(
            createCaeException("WwW-AuThEnTiCaTe", encodeClaims(false))).block()).isNotNull();
        assertThat(credential.contexts).hasSize(4);
        assertTokenContext(credential.contexts.get(2), ACCOUNT_SCOPE, CLAIMS);
        assertTokenContext(credential.contexts.get(3), Constants.AAD_DEFAULT_SCOPE, CLAIMS);
    }

    @Test(groups = "unit")
    public void overrideScopeDoesNotFallback() {
        RecordingTokenCredential credential = new RecordingTokenCredential(ACCOUNT_SCOPE);
        AadTokenAuthorizationHelper helper =
            new AadTokenAuthorizationHelper(credential, ACCOUNT_SCOPE, null);

        StepVerifier.create(helper.getAuthorizationToken())
            .expectErrorMatches(error -> error.getMessage().contains("AADSTS500011"))
            .verify();

        assertThat(credential.contexts).hasSize(1);
        assertTokenContext(credential.contexts.get(0), ACCOUNT_SCOPE, null);
    }

    @Test(groups = "unit")
    public void handlesUrlSafeClaimsChallenge() {
        RecordingTokenCredential credential = new RecordingTokenCredential();
        AadTokenAuthorizationHelper helper = new AadTokenAuthorizationHelper(credential, ACCOUNT_SCOPE, null);
        helper.getAuthorizationToken().block();

        assertThat(helper.getCaeAuthorizationToken(createCaeException(
            HttpConstants.HttpHeaders.WWW_AUTHENTICATE,
            encodeClaims(true))).block()).isNotNull();

        assertThat(credential.contexts).hasSize(2);
        assertTokenContext(credential.contexts.get(1), ACCOUNT_SCOPE, CLAIMS);
        assertThat(helper.getAuthorizationToken().block()).contains("token-2");
        assertThat(credential.contexts).hasSize(2);
    }

    @Test(groups = "unit")
    public void repeatedClaimsChallengeForcesRefresh() {
        RecordingTokenCredential credential = new RecordingTokenCredential();
        AadTokenAuthorizationHelper helper = new AadTokenAuthorizationHelper(credential, ACCOUNT_SCOPE, null);
        helper.getAuthorizationToken().block();
        UnauthorizedException challenge =
            createCaeException(HttpConstants.HttpHeaders.WWW_AUTHENTICATE, encodeClaims(false));

        assertThat(helper.getCaeAuthorizationToken(challenge).block()).isNotNull();
        assertThat(helper.getCaeAuthorizationToken(challenge).block()).isNotNull();

        assertThat(credential.contexts).hasSize(3);
        assertTokenContext(credential.contexts.get(1), ACCOUNT_SCOPE, CLAIMS);
        assertTokenContext(credential.contexts.get(2), ACCOUNT_SCOPE, CLAIMS);
    }

    @Test(groups = "unit")
    public void concurrentDistinctClaimsAreRefreshedSeparately() {
        List<TokenRequestContext> contexts = new CopyOnWriteArrayList<>();
        AtomicInteger calls = new AtomicInteger();
        TokenCredential credential = tokenRequestContext -> {
            contexts.add(tokenRequestContext);
            Mono<AccessToken> token = Mono.fromSupplier(() -> new AccessToken(
                "token-" + calls.incrementAndGet(),
                OffsetDateTime.now().plusHours(1)));
            return tokenRequestContext.getClaims() == null
                ? token
                : Mono.delay(Duration.ofMillis(25)).then(token);
        };
        AadTokenAuthorizationHelper helper = new AadTokenAuthorizationHelper(credential, ACCOUNT_SCOPE, null);
        helper.getAuthorizationToken().block();

        reactor.util.function.Tuple2<String, String> authorizations = Mono.zip(
            helper.getCaeAuthorizationToken(createCaeException(
                HttpConstants.HttpHeaders.WWW_AUTHENTICATE,
                encodeClaims(CLAIMS, false))),
            helper.getCaeAuthorizationToken(createCaeException(
                HttpConstants.HttpHeaders.WWW_AUTHENTICATE,
                encodeClaims(OTHER_CLAIMS, false))))
            .block();

        assertThat(authorizations.getT1()).contains("token-2");
        assertThat(authorizations.getT2()).contains("token-3");
        assertThat(contexts).hasSize(3);
        assertThat(contexts).extracting(TokenRequestContext::getClaims)
            .containsExactly(null, CLAIMS, OTHER_CLAIMS);
    }

    @Test(groups = "unit")
    public void distinctClaimsRefreshRunsAfterActiveRefreshFailure() {
        List<TokenRequestContext> contexts = new CopyOnWriteArrayList<>();
        TokenCredential credential = tokenRequestContext -> {
            contexts.add(tokenRequestContext);
            if (CLAIMS.equals(tokenRequestContext.getClaims())) {
                return Mono.delay(Duration.ofMillis(25))
                    .then(Mono.<AccessToken>error(new RuntimeException("first refresh failed")));
            }
            return Mono.just(new AccessToken("token", OffsetDateTime.now().plusHours(1)));
        };
        AadTokenAuthorizationHelper helper = new AadTokenAuthorizationHelper(credential, ACCOUNT_SCOPE, null);
        helper.getAuthorizationToken().block();

        Mono<String> firstRefresh = helper.getCaeAuthorizationToken(createCaeException(
            HttpConstants.HttpHeaders.WWW_AUTHENTICATE,
            encodeClaims(CLAIMS, false)));
        Mono<String> secondRefresh = helper.getCaeAuthorizationToken(createCaeException(
            HttpConstants.HttpHeaders.WWW_AUTHENTICATE,
            encodeClaims(OTHER_CLAIMS, false)));

        StepVerifier.create(Mono.zip(
            firstRefresh.onErrorReturn("failed"),
            secondRefresh))
            .assertNext(result -> {
                assertThat(result.getT1()).isEqualTo("failed");
                assertThat(result.getT2()).isNotEmpty();
            })
            .verifyComplete();
        assertThat(contexts).extracting(TokenRequestContext::getClaims)
            .containsExactly(null, CLAIMS, OTHER_CLAIMS);
    }

    @Test(groups = "unit")
    public void handlesBearerAmongMultipleChallenges() {
        RecordingTokenCredential credential = new RecordingTokenCredential();
        AadTokenAuthorizationHelper helper = new AadTokenAuthorizationHelper(credential, ACCOUNT_SCOPE, null);
        helper.getAuthorizationToken().block();

        UnauthorizedException challenge = createUnauthorizedException(
            "Basic realm=\"example\", Bearer error=\"insufficient_claims\", claims=\""
                + encodeClaims(false) + "\"");

        assertThat(helper.getCaeAuthorizationToken(challenge).block()).isNotNull();
        assertTokenContext(credential.contexts.get(1), ACCOUNT_SCOPE, CLAIMS);
    }

    @Test(groups = "unit")
    public void ignoresInvalidChallenges() {
        RecordingTokenCredential credential = new RecordingTokenCredential();
        AadTokenAuthorizationHelper helper = new AadTokenAuthorizationHelper(credential, ACCOUNT_SCOPE, null);
        helper.getAuthorizationToken().block();

        assertThat(helper.getCaeAuthorizationToken(createUnauthorizedException(
            "Bearer error=\"invalid_token\", claims=\"" + encodeClaims(false) + "\"")).block()).isNull();
        assertThat(helper.getCaeAuthorizationToken(createUnauthorizedException(
            "Bearer error=\"insufficient_claims\"")).block()).isNull();
        assertThat(helper.getCaeAuthorizationToken(createUnauthorizedException(
            "Bearer error=\"insufficient_claims\", claims=\"not-base64!\"")).block()).isNull();
        assertThat(helper.getCaeAuthorizationToken(createUnauthorizedException(
            "Bearer error=\"insufficient_claims\", claims=\""
                + Base64.getEncoder().encodeToString("not-json".getBytes(StandardCharsets.UTF_8))
                + "\"")).block()).isNull();
        assertThat(helper.getCaeAuthorizationToken(createUnauthorizedException(
            "NotBearer error=\"insufficient_claims\", claims=\"" + encodeClaims(false) + "\"")).block()).isNull();

        assertThat(credential.contexts).hasSize(1);
    }

    private static UnauthorizedException createCaeException(String headerName, String encodedClaims) {
        Map<String, String> headers = new HashMap<>();
        headers.put(headerName,
            "Bearer realm=\"\", authorization_uri=\"\", error=\"insufficient_claims\", claims=\""
                + encodedClaims + "\"");
        return new UnauthorizedException(null, 0, null, headers);
    }

    private static UnauthorizedException createUnauthorizedException(String challenge) {
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpConstants.HttpHeaders.WWW_AUTHENTICATE, challenge);
        return new UnauthorizedException(null, 0, null, headers);
    }

    private static String encodeClaims(boolean urlSafe) {
        return encodeClaims(CLAIMS, urlSafe);
    }

    private static String encodeClaims(String claims, boolean urlSafe) {
        String encoded = Base64.getEncoder().encodeToString(claims.getBytes(StandardCharsets.UTF_8));
        return urlSafe
            ? encoded.replace('+', '-').replace('/', '_').replace("=", "")
            : encoded;
    }

    private static void assertTokenContext(TokenRequestContext context, String scope, String claims) {
        assertThat(context.getScopes()).containsExactly(scope);
        assertThat(context.isCaeEnabled()).isTrue();
        assertThat(context.getClaims()).isEqualTo(claims);
    }

    private static final class RecordingTokenCredential implements TokenCredential {
        private final List<TokenRequestContext> contexts = new ArrayList<>();
        private final AtomicInteger calls = new AtomicInteger();
        private final String failingScope;

        private RecordingTokenCredential() {
            this(null);
        }

        private RecordingTokenCredential(String failingScope) {
            this.failingScope = failingScope;
        }

        @Override
        public Mono<AccessToken> getToken(TokenRequestContext tokenRequestContext) {
            this.contexts.add(tokenRequestContext);
            if (tokenRequestContext.getScopes().contains(this.failingScope)) {
                return Mono.error(new RuntimeException("AADSTS500011: Application was not found."));
            }
            return Mono.just(new AccessToken(
                "token-" + this.calls.incrementAndGet(),
                OffsetDateTime.now().plusHours(1)));
        }
    }
}
