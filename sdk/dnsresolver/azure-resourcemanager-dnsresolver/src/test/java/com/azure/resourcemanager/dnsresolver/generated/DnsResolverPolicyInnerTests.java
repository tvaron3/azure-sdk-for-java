// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.dnsresolver.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.dnsresolver.fluent.models.DnsResolverPolicyInner;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;

public final class DnsResolverPolicyInnerTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        DnsResolverPolicyInner model = BinaryData.fromString(
            "{\"etag\":\"cn\",\"properties\":{\"provisioningState\":\"Updating\",\"resourceGuid\":\"tkphywpnvjtoqn\"},\"location\":\"mclfplphoxuscr\",\"tags\":{\"zq\":\"gyepsbjt\",\"fjz\":\"gxywpmue\"},\"id\":\"fqkquj\",\"name\":\"dsuyonobgla\",\"type\":\"cq\"}")
            .toObject(DnsResolverPolicyInner.class);
        Assertions.assertEquals("mclfplphoxuscr", model.location());
        Assertions.assertEquals("gyepsbjt", model.tags().get("zq"));
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        DnsResolverPolicyInner model = new DnsResolverPolicyInner().withLocation("mclfplphoxuscr")
            .withTags(mapOf("zq", "gyepsbjt", "fjz", "gxywpmue"));
        model = BinaryData.fromObject(model).toObject(DnsResolverPolicyInner.class);
        Assertions.assertEquals("mclfplphoxuscr", model.location());
        Assertions.assertEquals("gyepsbjt", model.tags().get("zq"));
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
