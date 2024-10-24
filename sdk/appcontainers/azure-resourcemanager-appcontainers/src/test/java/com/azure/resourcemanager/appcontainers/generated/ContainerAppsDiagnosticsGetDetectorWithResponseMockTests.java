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
import com.azure.resourcemanager.appcontainers.models.Diagnostics;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public final class ContainerAppsDiagnosticsGetDetectorWithResponseMockTests {
    @Test
    public void testGetDetectorWithResponse() throws Exception {
        String responseStr
            = "{\"properties\":{\"metadata\":{\"id\":\"jgwecywnfyszzacz\",\"name\":\"nqbdnddbboz\",\"description\":\"vrmkjmyitrchwu\",\"author\":\"xeeihtpmno\",\"category\":\"hqlfmsib\",\"supportTopicList\":[{\"id\":\"fgxkydpmypgf\",\"pesId\":\"mtywhla\"},{\"id\":\"p\",\"pesId\":\"pewpyj\"}],\"analysisTypes\":[\"ampqcrzgeuq\",\"b\"],\"type\":\"atwfauj\",\"score\":63.017303},\"dataset\":[{\"table\":{\"tableName\":\"r\",\"columns\":[{},{},{},{}],\"rows\":[\"datahjkrukizyhgs\",\"datatnqsktx\",\"datafpjbqggwe\"]},\"renderingProperties\":{\"type\":670221649,\"title\":\"dmncgbf\",\"description\":\"scstunmlhxd\",\"isVisible\":true}},{\"table\":{\"tableName\":\"iichgjsysmvxodgw\",\"columns\":[{},{},{}],\"rows\":[\"dataifc\",\"datavbdujgcwxvecbb\",\"datajtrdxr\"]},\"renderingProperties\":{\"type\":975430900,\"title\":\"bgiark\",\"description\":\"kpgdqxwabzrwiq\",\"isVisible\":false}},{\"table\":{\"tableName\":\"lcdosqkpt\",\"columns\":[{},{},{}],\"rows\":[\"datafmmainwhedxkpbq\",\"dataunt\",\"databuizazzelwg\"]},\"renderingProperties\":{\"type\":1673275402,\"title\":\"f\",\"description\":\"klblaxp\",\"isVisible\":true}}],\"status\":{\"message\":\"abalfdxaglz\",\"statusId\":1995991969},\"dataProviderMetadata\":{\"providerName\":\"tlqh\",\"propertyBag\":[{\"name\":\"uvmrsiflikyyp\",\"value\":\"gxfx\"},{\"name\":\"yrqsdbpokszanm\",\"value\":\"gpterdiu\"}]}},\"id\":\"i\",\"name\":\"kskw\",\"type\":\"tsdetjygowifcq\"}";

        HttpClient httpClient
            = response -> Mono.just(new MockHttpResponse(response, 200, responseStr.getBytes(StandardCharsets.UTF_8)));
        ContainerAppsApiManager manager = ContainerAppsApiManager.configure()
            .withHttpClient(httpClient)
            .authenticate(tokenRequestContext -> Mono.just(new AccessToken("this_is_a_token", OffsetDateTime.MAX)),
                new AzureProfile("", "", AzureEnvironment.AZURE));

        Diagnostics response = manager.containerAppsDiagnostics()
            .getDetectorWithResponse("qwm", "q", "moxsa", com.azure.core.util.Context.NONE)
            .getValue();

        Assertions.assertEquals("ampqcrzgeuq", response.properties().metadata().analysisTypes().get(0));
        Assertions.assertEquals("r", response.properties().dataset().get(0).table().tableName());
        Assertions.assertEquals(670221649, response.properties().dataset().get(0).renderingProperties().type());
        Assertions.assertEquals("dmncgbf", response.properties().dataset().get(0).renderingProperties().title());
        Assertions.assertEquals("scstunmlhxd",
            response.properties().dataset().get(0).renderingProperties().description());
        Assertions.assertEquals(true, response.properties().dataset().get(0).renderingProperties().isVisible());
        Assertions.assertEquals("abalfdxaglz", response.properties().status().message());
        Assertions.assertEquals(1995991969, response.properties().status().statusId());
        Assertions.assertEquals("tlqh", response.properties().dataProviderMetadata().providerName());
        Assertions.assertEquals("uvmrsiflikyyp",
            response.properties().dataProviderMetadata().propertyBag().get(0).name());
        Assertions.assertEquals("gxfx", response.properties().dataProviderMetadata().propertyBag().get(0).value());
    }
}
