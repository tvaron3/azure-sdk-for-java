// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.appcontainers.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.appcontainers.models.JavaComponentServiceBind;
import org.junit.jupiter.api.Assertions;

public final class JavaComponentServiceBindTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        JavaComponentServiceBind model = BinaryData.fromString("{\"name\":\"rqofulopmjnlexwh\",\"serviceId\":\"jpib\"}")
            .toObject(JavaComponentServiceBind.class);
        Assertions.assertEquals("rqofulopmjnlexwh", model.name());
        Assertions.assertEquals("jpib", model.serviceId());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        JavaComponentServiceBind model
            = new JavaComponentServiceBind().withName("rqofulopmjnlexwh").withServiceId("jpib");
        model = BinaryData.fromObject(model).toObject(JavaComponentServiceBind.class);
        Assertions.assertEquals("rqofulopmjnlexwh", model.name());
        Assertions.assertEquals("jpib", model.serviceId());
    }
}
