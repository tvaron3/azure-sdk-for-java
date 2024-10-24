// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.appcontainers.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.appcontainers.models.ScaleConfiguration;
import org.junit.jupiter.api.Assertions;

public final class ScaleConfigurationTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        ScaleConfiguration model
            = BinaryData.fromString("{\"maxConcurrentSessions\":1728300209,\"readySessionInstances\":1151499002}")
                .toObject(ScaleConfiguration.class);
        Assertions.assertEquals(1728300209, model.maxConcurrentSessions());
        Assertions.assertEquals(1151499002, model.readySessionInstances());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        ScaleConfiguration model
            = new ScaleConfiguration().withMaxConcurrentSessions(1728300209).withReadySessionInstances(1151499002);
        model = BinaryData.fromObject(model).toObject(ScaleConfiguration.class);
        Assertions.assertEquals(1728300209, model.maxConcurrentSessions());
        Assertions.assertEquals(1151499002, model.readySessionInstances());
    }
}
