// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.appcontainers.generated;

import com.azure.core.credential.AccessToken;
import com.azure.core.http.HttpClient;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.core.test.http.MockHttpResponse;
import com.azure.resourcemanager.appcontainers.ContainerAppsApiManager;
import com.azure.resourcemanager.appcontainers.models.DaprSubscription;
import com.azure.resourcemanager.appcontainers.models.DaprSubscriptionBulkSubscribeOptions;
import com.azure.resourcemanager.appcontainers.models.DaprSubscriptionRouteRule;
import com.azure.resourcemanager.appcontainers.models.DaprSubscriptionRoutes;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public final class DaprSubscriptionsCreateOrUpdateWithResponseMockTests {
    @Test
    public void testCreateOrUpdateWithResponse() throws Exception {
        String responseStr
            = "{\"properties\":{\"pubsubName\":\"u\",\"topic\":\"uyuafixlxicw\",\"deadLetterTopic\":\"th\",\"routes\":{\"rules\":[{\"match\":\"z\",\"path\":\"as\"},{\"match\":\"d\",\"path\":\"palvngtw\"},{\"match\":\"skwgqr\",\"path\":\"au\"},{\"match\":\"rcjlvkrkegtyczup\",\"path\":\"yxlz\"}],\"default\":\"yddeeqz\"},\"scopes\":[\"bm\"],\"metadata\":{\"trlq\":\"exduetbapfczew\"},\"bulkSubscribe\":{\"enabled\":false,\"maxMessagesCount\":868320545,\"maxAwaitDurationMs\":1023832857}},\"id\":\"ir\",\"name\":\"zxvbczwhyegbthms\",\"type\":\"i\"}";

        HttpClient httpClient
            = response -> Mono.just(new MockHttpResponse(response, 200, responseStr.getBytes(StandardCharsets.UTF_8)));
        ContainerAppsApiManager manager = ContainerAppsApiManager.configure()
            .withHttpClient(httpClient)
            .authenticate(tokenRequestContext -> Mono.just(new AccessToken("this_is_a_token", OffsetDateTime.MAX)),
                new AzureProfile("", "", AzureEnvironment.AZURE));

        DaprSubscription response
            = manager.daprSubscriptions()
                .define("rpahuuonjkkxuk")
                .withExistingManagedEnvironment("uugkw", "rq")
                .withPubsubName("hvvpx")
                .withTopic("egcjojlleuid")
                .withDeadLetterTopic("tonvhgnhtmeplhb")
                .withRoutes(
                    new DaprSubscriptionRoutes()
                        .withRules(Arrays.asList(new DaprSubscriptionRouteRule().withMatch("mumm").withPath("vavucg"),
                            new DaprSubscriptionRouteRule().withMatch("ua").withPath("pmn")))
                        .withDefaultProperty("zhrchx"))
                .withScopes(Arrays.asList("zdmh", "wlvi"))
                .withMetadata(mapOf("viscot", "xmlitqdsjip"))
                .withBulkSubscribe(new DaprSubscriptionBulkSubscribeOptions().withEnabled(false)
                    .withMaxMessagesCount(49608617)
                    .withMaxAwaitDurationMs(1621257859))
                .create();

        Assertions.assertEquals("u", response.pubsubName());
        Assertions.assertEquals("uyuafixlxicw", response.topic());
        Assertions.assertEquals("th", response.deadLetterTopic());
        Assertions.assertEquals("z", response.routes().rules().get(0).match());
        Assertions.assertEquals("as", response.routes().rules().get(0).path());
        Assertions.assertEquals("yddeeqz", response.routes().defaultProperty());
        Assertions.assertEquals("bm", response.scopes().get(0));
        Assertions.assertEquals("exduetbapfczew", response.metadata().get("trlq"));
        Assertions.assertEquals(false, response.bulkSubscribe().enabled());
        Assertions.assertEquals(868320545, response.bulkSubscribe().maxMessagesCount());
        Assertions.assertEquals(1023832857, response.bulkSubscribe().maxAwaitDurationMs());
    }

    // Use "Map.of" if available
    @SuppressWarnings("unchecked")
    private static <T> Map<String, T> mapOf(Object... inputs) {
        Map<String, T> map = new HashMap<>();
        for (int i = 0; i < inputs.length; i += 2) {
            String key = (String) inputs[i];
            T value = (T) inputs[i + 1];
            map.put(key, value);
        }
        return map;
    }
}
