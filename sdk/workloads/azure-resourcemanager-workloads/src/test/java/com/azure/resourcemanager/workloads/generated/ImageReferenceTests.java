// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.workloads.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.workloads.models.ImageReference;
import org.junit.jupiter.api.Assertions;

public final class ImageReferenceTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        ImageReference model = BinaryData.fromString(
            "{\"publisher\":\"iysui\",\"offer\":\"ynkedyatrwyhqmib\",\"sku\":\"hwit\",\"version\":\"ypyynpcdpumnzg\"}")
            .toObject(ImageReference.class);
        Assertions.assertEquals("iysui", model.publisher());
        Assertions.assertEquals("ynkedyatrwyhqmib", model.offer());
        Assertions.assertEquals("hwit", model.sku());
        Assertions.assertEquals("ypyynpcdpumnzg", model.version());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        ImageReference model = new ImageReference().withPublisher("iysui")
            .withOffer("ynkedyatrwyhqmib")
            .withSku("hwit")
            .withVersion("ypyynpcdpumnzg");
        model = BinaryData.fromObject(model).toObject(ImageReference.class);
        Assertions.assertEquals("iysui", model.publisher());
        Assertions.assertEquals("ynkedyatrwyhqmib", model.offer());
        Assertions.assertEquals("hwit", model.sku());
        Assertions.assertEquals("ypyynpcdpumnzg", model.version());
    }
}
