// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.digitaltwins.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.digitaltwins.fluent.models.DigitalTwinsDescriptionInner;
import com.azure.resourcemanager.digitaltwins.models.DigitalTwinsDescriptionListResult;
import com.azure.resourcemanager.digitaltwins.models.DigitalTwinsIdentity;
import com.azure.resourcemanager.digitaltwins.models.DigitalTwinsIdentityType;
import com.azure.resourcemanager.digitaltwins.models.PublicNetworkAccess;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;

public final class DigitalTwinsDescriptionListResultTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        DigitalTwinsDescriptionListResult model = BinaryData.fromString(
            "{\"nextLink\":\"c\",\"value\":[{\"properties\":{\"createdTime\":\"2021-11-18T18:16Z\",\"lastUpdatedTime\":\"2021-09-22T20:17:09Z\",\"provisioningState\":\"Provisioning\",\"hostName\":\"iachbo\",\"privateEndpointConnections\":[],\"publicNetworkAccess\":\"Enabled\"},\"identity\":{\"type\":\"UserAssigned\",\"principalId\":\"fqpte\",\"tenantId\":\"zzvypyqrimzinp\",\"userAssignedIdentities\":{}},\"location\":\"jdkirsoodqx\",\"tags\":{\"t\":\"mnoh\",\"soifiyipjxsqw\":\"kwh\"},\"id\":\"gr\",\"name\":\"bznorcjxvsnby\",\"type\":\"qabnmoc\"},{\"properties\":{\"createdTime\":\"2021-02-04T07:45:14Z\",\"lastUpdatedTime\":\"2021-10-15T17:46:31Z\",\"provisioningState\":\"Updating\",\"hostName\":\"fblj\",\"privateEndpointConnections\":[],\"publicNetworkAccess\":\"Disabled\"},\"identity\":{\"type\":\"None\",\"principalId\":\"jmkljavbqidtqajz\",\"tenantId\":\"l\",\"userAssignedIdentities\":{}},\"location\":\"dj\",\"tags\":{\"e\":\"khbzhfepgzg\",\"scpai\":\"zloc\"},\"id\":\"rhhbcs\",\"name\":\"l\",\"type\":\"mmajtjaodx\"},{\"properties\":{\"createdTime\":\"2021-10-01T06:10:31Z\",\"lastUpdatedTime\":\"2021-09-14T02:49:01Z\",\"provisioningState\":\"Deleted\",\"hostName\":\"xo\",\"privateEndpointConnections\":[],\"publicNetworkAccess\":\"Disabled\"},\"identity\":{\"type\":\"SystemAssigned,UserAssigned\",\"principalId\":\"mexgstxgcp\",\"tenantId\":\"gmaajrm\",\"userAssignedIdentities\":{}},\"location\":\"wzrlovmclwhij\",\"tags\":{\"s\":\"jctbza\",\"ukdkexxppofmxa\":\"sycbkbfk\",\"jpgd\":\"c\",\"j\":\"toc\"},\"id\":\"hvpmoue\",\"name\":\"hd\",\"type\":\"xibqeojnx\"},{\"properties\":{\"createdTime\":\"2021-05-15T04:32:43Z\",\"lastUpdatedTime\":\"2021-02-11T07:14:49Z\",\"provisioningState\":\"Deleted\",\"hostName\":\"ndei\",\"privateEndpointConnections\":[],\"publicNetworkAccess\":\"Enabled\"},\"identity\":{\"type\":\"None\",\"principalId\":\"oqvuhr\",\"tenantId\":\"f\",\"userAssignedIdentities\":{}},\"location\":\"ddglm\",\"tags\":{\"qciwqvhkhixuigdt\":\"jqkwpyeicx\"},\"id\":\"pbobjo\",\"name\":\"hm\",\"type\":\"w\"}]}")
            .toObject(DigitalTwinsDescriptionListResult.class);
        Assertions.assertEquals("c", model.nextLink());
        Assertions.assertEquals("jdkirsoodqx", model.value().get(0).location());
        Assertions.assertEquals("mnoh", model.value().get(0).tags().get("t"));
        Assertions.assertEquals(DigitalTwinsIdentityType.USER_ASSIGNED, model.value().get(0).identity().type());
        Assertions.assertEquals(PublicNetworkAccess.ENABLED, model.value().get(0).publicNetworkAccess());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        DigitalTwinsDescriptionListResult model
            = new DigitalTwinsDescriptionListResult().withNextLink("c")
                .withValue(
                    Arrays.asList(
                        new DigitalTwinsDescriptionInner().withLocation("jdkirsoodqx")
                            .withTags(mapOf("t", "mnoh", "soifiyipjxsqw", "kwh"))
                            .withIdentity(new DigitalTwinsIdentity().withType(DigitalTwinsIdentityType.USER_ASSIGNED)
                                .withUserAssignedIdentities(mapOf()))
                            .withPrivateEndpointConnections(Arrays.asList())
                            .withPublicNetworkAccess(PublicNetworkAccess.ENABLED),
                        new DigitalTwinsDescriptionInner().withLocation("dj")
                            .withTags(mapOf("e", "khbzhfepgzg", "scpai", "zloc"))
                            .withIdentity(new DigitalTwinsIdentity().withType(DigitalTwinsIdentityType.NONE)
                                .withUserAssignedIdentities(mapOf()))
                            .withPrivateEndpointConnections(Arrays.asList())
                            .withPublicNetworkAccess(PublicNetworkAccess.DISABLED),
                        new DigitalTwinsDescriptionInner().withLocation("wzrlovmclwhij")
                            .withTags(mapOf("s", "jctbza", "ukdkexxppofmxa", "sycbkbfk", "jpgd", "c", "j", "toc"))
                            .withIdentity(new DigitalTwinsIdentity()
                                .withType(DigitalTwinsIdentityType.SYSTEM_ASSIGNED_USER_ASSIGNED)
                                .withUserAssignedIdentities(mapOf()))
                            .withPrivateEndpointConnections(Arrays.asList())
                            .withPublicNetworkAccess(PublicNetworkAccess.DISABLED),
                        new DigitalTwinsDescriptionInner().withLocation("ddglm")
                            .withTags(mapOf("qciwqvhkhixuigdt", "jqkwpyeicx"))
                            .withIdentity(new DigitalTwinsIdentity().withType(DigitalTwinsIdentityType.NONE)
                                .withUserAssignedIdentities(mapOf()))
                            .withPrivateEndpointConnections(Arrays.asList())
                            .withPublicNetworkAccess(PublicNetworkAccess.ENABLED)));
        model = BinaryData.fromObject(model).toObject(DigitalTwinsDescriptionListResult.class);
        Assertions.assertEquals("c", model.nextLink());
        Assertions.assertEquals("jdkirsoodqx", model.value().get(0).location());
        Assertions.assertEquals("mnoh", model.value().get(0).tags().get("t"));
        Assertions.assertEquals(DigitalTwinsIdentityType.USER_ASSIGNED, model.value().get(0).identity().type());
        Assertions.assertEquals(PublicNetworkAccess.ENABLED, model.value().get(0).publicNetworkAccess());
    }

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
