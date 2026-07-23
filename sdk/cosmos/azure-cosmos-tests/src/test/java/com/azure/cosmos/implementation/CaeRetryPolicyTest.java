// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.implementation;

import com.azure.cosmos.CosmosException;
import com.azure.cosmos.implementation.http.HttpHeaders;
import org.mockito.Mockito;
import org.testng.annotations.Test;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class CaeRetryPolicyTest {
    @Test(groups = "unit")
    public void retriesHandledChallengeOnce() {
        DocumentClientRetryPolicy retryPolicy = Mockito.mock(DocumentClientRetryPolicy.class);
        IAuthorizationTokenProvider tokenProvider = Mockito.mock(IAuthorizationTokenProvider.class);
        UnauthorizedException unauthorizedException =
            new UnauthorizedException(null, 0, null, Collections.emptyMap());
        Mockito.when(tokenProvider.getCaeAuthorizationToken(unauthorizedException)).thenReturn(Mono.just("cae-auth"));
        Mockito.when(retryPolicy.shouldRetry(unauthorizedException))
            .thenReturn(Mono.just(ShouldRetryResult.noRetry()));

        CaeRetryPolicy caeRetryPolicy = new CaeRetryPolicy(retryPolicy, tokenProvider);
        RxDocumentServiceRequest request = Mockito.mock(RxDocumentServiceRequest.class);
        request.requestContext = new DocumentServiceRequestContext();
        caeRetryPolicy.onBeforeSendRequest(request);

        ShouldRetryResult firstResult = caeRetryPolicy.shouldRetry(unauthorizedException).block();
        ShouldRetryResult secondResult = caeRetryPolicy.shouldRetry(unauthorizedException).block();

        assertThat(firstResult.shouldRetry).isTrue();
        assertThat(firstResult.backOffTime).isZero();
        assertThat(secondResult.shouldRetry).isFalse();
        assertThat(request.requestContext.caeAuthorizationToken).isEqualTo("cae-auth");
        Mockito.verify(tokenProvider).getCaeAuthorizationToken(unauthorizedException);
        Mockito.verify(retryPolicy).shouldRetry(unauthorizedException);
    }

    @Test(groups = "unit")
    public void delegatesUnhandledChallenge() {
        DocumentClientRetryPolicy retryPolicy = Mockito.mock(DocumentClientRetryPolicy.class);
        IAuthorizationTokenProvider tokenProvider = Mockito.mock(IAuthorizationTokenProvider.class);
        UnauthorizedException unauthorizedException =
            new UnauthorizedException(null, 0, null, Collections.emptyMap());
        ShouldRetryResult delegatedResult = ShouldRetryResult.error(unauthorizedException);
        Mockito.when(tokenProvider.getCaeAuthorizationToken(unauthorizedException)).thenReturn(Mono.empty());
        Mockito.when(retryPolicy.shouldRetry(unauthorizedException)).thenReturn(Mono.just(delegatedResult));

        assertThat(new CaeRetryPolicy(retryPolicy, tokenProvider).shouldRetry(unauthorizedException).block())
            .isSameAs(delegatedResult);
    }

    @Test(groups = "unit")
    public void propagatesRefreshFailure() {
        DocumentClientRetryPolicy retryPolicy = Mockito.mock(DocumentClientRetryPolicy.class);
        IAuthorizationTokenProvider tokenProvider = Mockito.mock(IAuthorizationTokenProvider.class);
        UnauthorizedException unauthorizedException =
            new UnauthorizedException(null, 0, null, Collections.emptyMap());
        RuntimeException refreshFailure = new RuntimeException("refresh failed");
        Mockito.when(tokenProvider.getCaeAuthorizationToken(unauthorizedException))
            .thenReturn(Mono.error(refreshFailure));

        StepVerifier.create(new CaeRetryPolicy(retryPolicy, tokenProvider).shouldRetry(unauthorizedException))
            .expectErrorMatches(error -> error == refreshFailure)
            .verify();
        Mockito.verifyNoInteractions(retryPolicy);
    }

    @Test(groups = "unit")
    public void delegatesRequestAndRetryContext() {
        DocumentClientRetryPolicy retryPolicy = Mockito.mock(DocumentClientRetryPolicy.class);
        IAuthorizationTokenProvider tokenProvider = Mockito.mock(IAuthorizationTokenProvider.class);
        RxDocumentServiceRequest request = Mockito.mock(RxDocumentServiceRequest.class);
        RetryContext retryContext = Mockito.mock(RetryContext.class);
        CosmosException nonUnauthorizedException = Mockito.mock(CosmosException.class);
        Mockito.when(nonUnauthorizedException.getStatusCode()).thenReturn(HttpConstants.StatusCodes.BADREQUEST);
        Mockito.when(retryPolicy.getRetryContext()).thenReturn(retryContext);
        Mockito.when(retryPolicy.shouldRetry(nonUnauthorizedException))
            .thenReturn(Mono.just(ShouldRetryResult.noRetry()));

        CaeRetryPolicy caeRetryPolicy = new CaeRetryPolicy(retryPolicy, tokenProvider);
        caeRetryPolicy.onBeforeSendRequest(request);

        assertThat(caeRetryPolicy.getRetryContext()).isSameAs(retryContext);
        assertThat(caeRetryPolicy.shouldRetry(nonUnauthorizedException).block().shouldRetry).isFalse();
        Mockito.verify(retryPolicy).onBeforeSendRequest(request);
        Mockito.verifyNoInteractions(tokenProvider);
    }

    @Test(groups = "unit")
    public void databaseAccountReadRetriesHandledChallengeOnce() {
        RxDocumentClientImpl client = Mockito.mock(RxDocumentClientImpl.class, Mockito.CALLS_REAL_METHODS);
        URI endpoint = URI.create("https://account.documents.azure.com");
        UnauthorizedException unauthorizedException =
            new UnauthorizedException(null, 0, null, Collections.emptyMap());
        DatabaseAccount databaseAccount = new DatabaseAccount();
        Mockito.doReturn(Flux.error(unauthorizedException))
            .when(client)
            .getDatabaseAccountFromEndpointInternal(endpoint);
        Mockito.doReturn(Flux.just(databaseAccount))
            .when(client)
            .getDatabaseAccountFromEndpointInternal(endpoint, "cae-auth");
        Mockito.doReturn(Mono.just("cae-auth")).when(client).getCaeAuthorizationToken(unauthorizedException);

        StepVerifier.create(client.getDatabaseAccountFromEndpoint(endpoint))
            .expectNext(databaseAccount)
            .verifyComplete();

        Mockito.verify(client).getDatabaseAccountFromEndpointInternal(endpoint);
        Mockito.verify(client).getDatabaseAccountFromEndpointInternal(endpoint, "cae-auth");
        Mockito.verify(client).getCaeAuthorizationToken(unauthorizedException);
    }

    @Test(groups = "unit")
    public void databaseAccountReadDoesNotRetrySecondChallenge() {
        RxDocumentClientImpl client = Mockito.mock(RxDocumentClientImpl.class, Mockito.CALLS_REAL_METHODS);
        URI endpoint = URI.create("https://account.documents.azure.com");
        UnauthorizedException unauthorizedException =
            new UnauthorizedException(null, 0, null, Collections.emptyMap());
        Mockito.doReturn(Flux.error(unauthorizedException))
            .when(client)
            .getDatabaseAccountFromEndpointInternal(endpoint);
        Mockito.doReturn(Flux.error(unauthorizedException))
            .when(client)
            .getDatabaseAccountFromEndpointInternal(endpoint, "cae-auth");
        Mockito.doReturn(Mono.just("cae-auth")).when(client).getCaeAuthorizationToken(unauthorizedException);

        StepVerifier.create(client.getDatabaseAccountFromEndpoint(endpoint))
            .expectErrorMatches(error -> error == unauthorizedException)
            .verify();

        Mockito.verify(client).getDatabaseAccountFromEndpointInternal(endpoint);
        Mockito.verify(client).getDatabaseAccountFromEndpointInternal(endpoint, "cae-auth");
        Mockito.verify(client).getCaeAuthorizationToken(unauthorizedException);
    }

    @Test(groups = "unit")
    public void requestBoundAuthorizationIsReused() {
        RxDocumentClientImpl client = Mockito.mock(RxDocumentClientImpl.class, Mockito.CALLS_REAL_METHODS);
        RxDocumentServiceRequest request = RxDocumentServiceRequest.createFromName(
            TestUtils.mockDiagnosticsClientContext(),
            OperationType.Read,
            "/dbs/db",
            ResourceType.Database);
        request.requestContext.caeAuthorizationToken = "request-auth";

        RxDocumentServiceRequest authorizedRequest = client.populateAuthorizationHeader(request).block();

        assertThat(authorizedRequest.getHeaders().get(HttpConstants.HttpHeaders.AUTHORIZATION))
            .isEqualTo("request-auth");
        assertThat(request.requestContext.caeAuthorizationToken).isEqualTo("request-auth");
    }

    @Test(groups = "unit")
    public void addressAuthorizationIsReused() {
        RxDocumentClientImpl client = Mockito.mock(RxDocumentClientImpl.class, Mockito.CALLS_REAL_METHODS);
        RxDocumentServiceRequest request = RxDocumentServiceRequest.createFromName(
            TestUtils.mockDiagnosticsClientContext(),
            OperationType.Read,
            "/dbs/db/colls/coll",
            ResourceType.DocumentCollection);
        request.requestContext.caeAuthorizationToken = "address-auth";
        HttpHeaders headers = new HttpHeaders();

        HttpHeaders authorizedHeaders = client.populateAuthorizationHeader(headers, request).block();

        assertThat(authorizedHeaders.value(HttpConstants.HttpHeaders.AUTHORIZATION)).isEqualTo("address-auth");
        assertThat(request.requestContext.caeAuthorizationToken).isEqualTo("address-auth");
    }

    @Test(groups = "unit")
    public void clonedRequestContextRetainsCaeAuthorization() {
        DocumentServiceRequestContext requestContext = new DocumentServiceRequestContext();
        requestContext.caeAuthorizationToken = "cloned-auth";

        assertThat(requestContext.clone().caeAuthorizationToken).isEqualTo("cloned-auth");
    }

    @Test(groups = "unit")
    public void recreatedRequestReceivesPendingAuthorization() {
        DocumentClientRetryPolicy retryPolicy = Mockito.mock(DocumentClientRetryPolicy.class);
        IAuthorizationTokenProvider tokenProvider = Mockito.mock(IAuthorizationTokenProvider.class);
        UnauthorizedException unauthorizedException =
            new UnauthorizedException(null, 0, null, Collections.emptyMap());
        Mockito.when(tokenProvider.getCaeAuthorizationToken(unauthorizedException))
            .thenReturn(Mono.just("pending-auth"));
        CaeRetryPolicy caeRetryPolicy = new CaeRetryPolicy(retryPolicy, tokenProvider);
        RxDocumentServiceRequest failedRequest = Mockito.mock(RxDocumentServiceRequest.class);
        failedRequest.requestContext = new DocumentServiceRequestContext();
        caeRetryPolicy.onBeforeSendRequest(failedRequest);
        assertThat(caeRetryPolicy.shouldRetry(unauthorizedException).block().shouldRetry).isTrue();

        RxDocumentServiceRequest recreatedRequest = Mockito.mock(RxDocumentServiceRequest.class);
        recreatedRequest.requestContext = new DocumentServiceRequestContext();
        caeRetryPolicy.onBeforeSendRequest(recreatedRequest);

        assertThat(recreatedRequest.requestContext.caeAuthorizationToken).isEqualTo("pending-auth");
    }
}
