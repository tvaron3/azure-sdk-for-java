// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package com.azure.resourcemanager.iotoperations.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.iotoperations.models.OperationalMode;
import com.azure.resourcemanager.iotoperations.models.SelfTracing;
import org.junit.jupiter.api.Assertions;

public final class SelfTracingTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        SelfTracing model = BinaryData.fromString("{\"mode\":\"Disabled\",\"intervalSeconds\":621270176}")
            .toObject(SelfTracing.class);
        Assertions.assertEquals(OperationalMode.DISABLED, model.mode());
        Assertions.assertEquals(621270176, model.intervalSeconds());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        SelfTracing model = new SelfTracing().withMode(OperationalMode.DISABLED).withIntervalSeconds(621270176);
        model = BinaryData.fromObject(model).toObject(SelfTracing.class);
        Assertions.assertEquals(OperationalMode.DISABLED, model.mode());
        Assertions.assertEquals(621270176, model.intervalSeconds());
    }
}
