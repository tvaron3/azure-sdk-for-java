// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.implementation;

import com.azure.cosmos.CosmosException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Handles one Continuous Access Evaluation claims challenge before delegating to the normal retry policy.
 */
public final class CaeRetryPolicy extends DocumentClientRetryPolicy {
    private final IRetryPolicy retryPolicy;
    private final IAuthorizationTokenProvider authorizationTokenProvider;
    private final AtomicBoolean hasRetried = new AtomicBoolean();
    private final AtomicReference<String> pendingAuthorization = new AtomicReference<>();
    private volatile RxDocumentServiceRequest request;

    /**
     * Creates a CAE retry policy.
     *
     * @param retryPolicy the normal retry policy.
     * @param authorizationTokenProvider the authorization token provider.
     */
    public CaeRetryPolicy(IRetryPolicy retryPolicy,
        IAuthorizationTokenProvider authorizationTokenProvider) {
        this.retryPolicy = retryPolicy;
        this.authorizationTokenProvider = authorizationTokenProvider;
    }

    @Override
    public void onBeforeSendRequest(RxDocumentServiceRequest request) {
        this.request = request;
        String authorization = this.pendingAuthorization.get();
        if (authorization != null && request.requestContext != null) {
            request.requestContext.caeAuthorizationToken = authorization;
        }
        if (this.retryPolicy instanceof DocumentClientRetryPolicy) {
            ((DocumentClientRetryPolicy) this.retryPolicy).onBeforeSendRequest(request);
        }
    }

    @Override
    public Mono<ShouldRetryResult> shouldRetry(Exception exception) {
        CosmosException cosmosException = Utils.as(exception, CosmosException.class);
        if (this.hasRetried.get()
            || cosmosException == null
            || cosmosException.getStatusCode() != HttpConstants.StatusCodes.UNAUTHORIZED) {
            return this.retryPolicy.shouldRetry(exception);
        }

        return this.authorizationTokenProvider.getCaeAuthorizationToken(cosmosException)
            .flatMap(authorization -> {
                if (this.request == null
                    || this.request.requestContext == null
                    || !this.hasRetried.compareAndSet(false, true)) {
                    return this.retryPolicy.shouldRetry(exception);
                }

                this.pendingAuthorization.set(authorization);
                this.request.requestContext.caeAuthorizationToken = authorization;
                return Mono.just(ShouldRetryResult.retryAfter(Duration.ZERO));
            })
            .switchIfEmpty(Mono.defer(() -> this.retryPolicy.shouldRetry(exception)));
    }

    @Override
    public RetryContext getRetryContext() {
        return this.retryPolicy.getRetryContext();
    }
}
