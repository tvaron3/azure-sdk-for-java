// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.cosmos.implementation;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.AccessTokenCache;
import com.azure.core.credential.ProofOfPossessionOptions;
import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;
import com.azure.cosmos.CosmosException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class is used internally and act as a helper in authorization of
 * AAD tokens and its supporting method.
 *
 */
public class AadTokenAuthorizationHelper {
    private static final String AAD_APP_NOT_FOUND = "AADSTS500011";
    private static final String BEARER_SCHEME = "Bearer";
    private static final String INSUFFICIENT_CLAIMS = "insufficient_claims";
    private static final ObjectMapper OBJECT_MAPPER = Utils.getSimpleObjectMapper();
    public static final String AAD_AUTH_SCHEMA_TYPE_SEGMENT = "type";
    public static final String AAD_AUTH_VERSION_SEGMENT = "ver";
    public static final String AAD_AUTH_SIGNATURE_SEGMENT = "sig";
    public static final String AAD_AUTH_SCHEMA_TYPE_VALUE = "aad";
    public static final String AAD_AUTH_VERSION_VALUE = "1.0";
    public static final String AAD_AUTH_TOKEN_COSMOS_SCOPE = "https://cosmos.azure.com/.default";
    private static final String AUTH_PREFIX =
            AAD_AUTH_SCHEMA_TYPE_SEGMENT + "=" + AAD_AUTH_SCHEMA_TYPE_VALUE
            + "&"
            + AAD_AUTH_VERSION_SEGMENT + "=" + AAD_AUTH_VERSION_VALUE
            + "&"
            + AAD_AUTH_SIGNATURE_SEGMENT + "=";
    private static final Logger logger = LoggerFactory.getLogger(AadTokenAuthorizationHelper.class);
    private final TokenCredential cacheCredential;
    private final AtomicReference<AccessTokenCache> tokenCache;
    private final AtomicReference<String> cachedClaims = new AtomicReference<>();
    private final AtomicReference<ChallengeRefresh> activeChallengeRefresh = new AtomicReference<>();
    private final String scope;

    /**
     * Creates an AAD token authorization helper.
     *
     * @param tokenCredential the token credential.
     * @param scope the primary scope.
     * @param fallbackScope the fallback scope, or {@code null} when fallback is disabled.
     */
    public AadTokenAuthorizationHelper(TokenCredential tokenCredential, String scope, String fallbackScope) {
        this.scope = scope;
        TokenCredential cacheCredential = fallbackScope == null
            ? tokenCredential
            : new ScopeFallbackTokenCredential(tokenCredential, scope, fallbackScope);
        this.cacheCredential = cacheCredential;
        this.tokenCache = new AtomicReference<>(new AccessTokenCache(cacheCredential));
    }

    /**
     * Gets the cached AAD authorization token.
     *
     * @return the encoded Cosmos authorization token.
     */
    public Mono<String> getAuthorizationToken() {
        TokenRequestContext tokenRequestContext = createTokenRequestContext(null);
        return tokenCache.get().getToken(tokenRequestContext, false)
            .map(AadTokenAuthorizationHelper::encodeAuthorizationToken);
    }

    /**
     * Attempts to handle a CAE claims challenge.
     *
     * @param cosmosException the unauthorized response.
     * @return the authorization value acquired for the challenge, or an empty Mono when the challenge is not handled.
     */
    public Mono<String> getCaeAuthorizationToken(CosmosException cosmosException) {
        if (cosmosException == null
            || cosmosException.getStatusCode() != HttpConstants.StatusCodes.UNAUTHORIZED) {
            return Mono.empty();
        }

        String challenge = getHeaderValue(cosmosException.getResponseHeaders(),
            HttpConstants.HttpHeaders.WWW_AUTHENTICATE);
        Map<String, String> challengeParameters = getBearerChallengeParameters(challenge);
        String error = challengeParameters.get("error");
        String encodedClaims = challengeParameters.get("claims");
        if (!INSUFFICIENT_CLAIMS.equalsIgnoreCase(error) || encodedClaims == null || encodedClaims.isEmpty()) {
            return Mono.empty();
        }

        final String claims;
        try {
            claims = decodeClaims(encodedClaims);
        } catch (IllegalArgumentException exception) {
            logger.warn("Failed to decode the CAE claims challenge.");
            return Mono.empty();
        }

        return this.startChallengeRefresh(claims);
    }

    private Mono<String> startChallengeRefresh(String claims) {
        return Mono.defer(() -> {
            while (true) {
                ChallengeRefresh activeRefresh = this.activeChallengeRefresh.get();
                if (activeRefresh != null) {
                    if (activeRefresh.claims.equals(claims)) {
                        return activeRefresh.result;
                    }
                    return activeRefresh.result
                        .onErrorResume(ignored -> Mono.empty())
                        .then(Mono.defer(() -> this.startChallengeRefresh(claims)));
                }

                ChallengeRefresh refresh = new ChallengeRefresh(claims);
                refresh.result = Mono.defer(() -> this.refreshTokenForChallenge(claims))
                    .doOnSuccess(ignored -> this.activeChallengeRefresh.compareAndSet(refresh, null))
                    .doOnError(ignored -> this.activeChallengeRefresh.compareAndSet(refresh, null))
                    .cache();
                if (this.activeChallengeRefresh.compareAndSet(null, refresh)) {
                    return refresh.result;
                }
            }
        });
    }

    private Mono<String> refreshTokenForChallenge(String claims) {
        AccessTokenCache cache;
        if (Objects.equals(this.cachedClaims.getAndSet(claims), claims)) {
            cache = new AccessTokenCache(this.cacheCredential);
            this.tokenCache.set(cache);
        } else {
            cache = this.tokenCache.get();
        }
        return cache.getToken(createTokenRequestContext(claims), true)
            .map(AadTokenAuthorizationHelper::encodeAuthorizationToken);
    }

    private static String encodeAuthorizationToken(AccessToken accessToken) {
        String authorizationPayload = AUTH_PREFIX + accessToken.getToken();
        try {
            return URLEncoder.encode(authorizationPayload, "UTF-8");
        } catch (UnsupportedEncodingException exception) {
            throw new IllegalStateException("Failed to encode authorization token.", exception);
        }
    }

    private TokenRequestContext createTokenRequestContext(String claims) {
        TokenRequestContext tokenRequestContext = new TokenRequestContext()
            .addScopes(this.scope)
            .setCaeEnabled(true);
        if (claims != null) {
            tokenRequestContext.setClaims(claims);
        }
        return tokenRequestContext;
    }

    private static String getHeaderValue(Map<String, String> headers, String headerName) {
        if (headers == null) {
            return null;
        }

        for (Map.Entry<String, String> header : headers.entrySet()) {
            if (headerName.equalsIgnoreCase(header.getKey())) {
                return header.getValue();
            }
        }
        return null;
    }

    private static Map<String, String> getBearerChallengeParameters(String challenge) {
        Map<String, String> parameters = new HashMap<>();
        if (challenge == null) {
            return parameters;
        }

        boolean inBearerChallenge = false;
        boolean foundBearerChallenge = false;
        for (String segment : splitChallengeSegments(challenge)) {
            String value = segment.trim();
            if (value.isEmpty()) {
                continue;
            }

            int tokenEnd = 0;
            while (tokenEnd < value.length()
                && !Character.isWhitespace(value.charAt(tokenEnd))
                && value.charAt(tokenEnd) != '=') {
                tokenEnd++;
            }
            int nextCharacter = tokenEnd;
            while (nextCharacter < value.length() && Character.isWhitespace(value.charAt(nextCharacter))) {
                nextCharacter++;
            }

            boolean parameter = nextCharacter < value.length() && value.charAt(nextCharacter) == '=';
            if (!parameter) {
                if (foundBearerChallenge) {
                    break;
                }

                String scheme = value.substring(0, tokenEnd);
                inBearerChallenge = BEARER_SCHEME.equalsIgnoreCase(scheme);
                if (!inBearerChallenge) {
                    continue;
                }

                foundBearerChallenge = true;
                value = value.substring(nextCharacter).trim();
                if (value.isEmpty()) {
                    continue;
                }
            } else if (!inBearerChallenge) {
                continue;
            }

            addChallengeParameter(parameters, value);
        }
        return parameters;
    }

    private static List<String> splitChallengeSegments(String challenge) {
        List<String> segments = new ArrayList<>();
        StringBuilder segment = new StringBuilder();
        boolean quoted = false;
        boolean escaped = false;
        for (int i = 0; i < challenge.length(); i++) {
            char character = challenge.charAt(i);
            if (escaped) {
                escaped = false;
            } else if (character == '\\' && quoted) {
                escaped = true;
            } else if (character == '"') {
                quoted = !quoted;
            } else if (character == ',' && !quoted) {
                segments.add(segment.toString());
                segment.setLength(0);
                continue;
            }
            segment.append(character);
        }
        segments.add(segment.toString());
        return segments;
    }

    private static void addChallengeParameter(Map<String, String> parameters, String parameter) {
        int separatorIndex = parameter.indexOf('=');
        if (separatorIndex < 0) {
            return;
        }

        String name = parameter.substring(0, separatorIndex).trim().toLowerCase(Locale.ROOT);
        String value = parameter.substring(separatorIndex + 1).trim();
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        parameters.put(name, value);
    }

    private static String decodeClaims(String encodedClaims) {
        String normalizedClaims = encodedClaims.replace('-', '+').replace('_', '/');
        int remainder = normalizedClaims.length() % 4;
        if (remainder == 1) {
            throw new IllegalArgumentException("Invalid base64 claims challenge.");
        }
        if (remainder > 1) {
            StringBuilder builder = new StringBuilder(normalizedClaims);
            for (int i = remainder; i < 4; i++) {
                builder.append('=');
            }
            normalizedClaims = builder.toString();
        }

        try {
            String claims = StandardCharsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT)
                .decode(ByteBuffer.wrap(Base64.getDecoder().decode(normalizedClaims)))
                .toString();
            JsonNode claimsJson = OBJECT_MAPPER.reader()
                .with(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
                .readTree(claims);
            if (claimsJson == null || !claimsJson.isObject()) {
                throw new IllegalArgumentException("The claims challenge must be a JSON object.");
            }
            return claims;
        } catch (Exception exception) {
            throw new IllegalArgumentException("Invalid claims challenge.", exception);
        }
    }

    private static final class ScopeFallbackTokenCredential implements TokenCredential {
        private final TokenCredential tokenCredential;
        private final String primaryScope;
        private final String fallbackScope;

        private ScopeFallbackTokenCredential(TokenCredential tokenCredential, String primaryScope,
            String fallbackScope) {
            this.tokenCredential = tokenCredential;
            this.primaryScope = primaryScope;
            this.fallbackScope = fallbackScope;
        }

        @Override
        public Mono<com.azure.core.credential.AccessToken> getToken(TokenRequestContext tokenRequestContext) {
            return this.tokenCredential.getToken(tokenRequestContext)
                .onErrorResume(error -> {
                    Throwable root = reactor.core.Exceptions.unwrap(error);
                    String message = root.getMessage() == null ? "" : root.getMessage();
                    if (!message.contains(AAD_APP_NOT_FOUND) || !usesPrimaryScope(tokenRequestContext)) {
                        return Mono.error(error);
                    }

                    logger.warn(
                        "AAD token: account scope failed with AADSTS500011; retrying with fallback scope: {}",
                        this.fallbackScope);
                    return this.tokenCredential.getToken(copyWithScope(tokenRequestContext, this.fallbackScope));
                });
        }

        private boolean usesPrimaryScope(TokenRequestContext tokenRequestContext) {
            return tokenRequestContext.getScopes().size() == 1
                && this.primaryScope.equals(tokenRequestContext.getScopes().get(0));
        }

        private static TokenRequestContext copyWithScope(TokenRequestContext source, String scope) {
            TokenRequestContext copy = new TokenRequestContext()
                .addScopes(scope)
                .setClaims(source.getClaims())
                .setTenantId(source.getTenantId())
                .setCaeEnabled(source.isCaeEnabled());
            ProofOfPossessionOptions proofOfPossessionOptions = source.getProofOfPossessionOptions();
            if (proofOfPossessionOptions != null) {
                copy.setProofOfPossessionOptions(proofOfPossessionOptions);
            }
            return copy;
        }
    }

    private static final class ChallengeRefresh {
        private final String claims;
        private Mono<String> result;

        private ChallengeRefresh(String claims) {
            this.claims = claims;
        }
    }
}
