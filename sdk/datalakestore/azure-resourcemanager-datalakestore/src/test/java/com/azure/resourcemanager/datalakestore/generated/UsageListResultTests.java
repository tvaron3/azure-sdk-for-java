// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.datalakestore.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.datalakestore.fluent.models.UsageInner;
import com.azure.resourcemanager.datalakestore.models.UsageListResult;
import java.util.Arrays;

public final class UsageListResultTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        UsageListResult model =
            BinaryData
                .fromString(
                    "{\"value\":[{\"unit\":\"Seconds\",\"id\":\"hrdxwzywqsmbs\",\"currentValue\":854764023,\"limit\":1524544291,\"name\":{\"value\":\"ryocfsfksymdd\",\"localizedValue\":\"tki\"}},{\"unit\":\"Seconds\",\"id\":\"qyud\",\"currentValue\":2120884016,\"limit\":346344422,\"name\":{\"value\":\"poczvyifqrvkdvjs\",\"localizedValue\":\"rm\"}},{\"unit\":\"BytesPerSecond\",\"id\":\"watkpnpulexxb\",\"currentValue\":1890498246,\"limit\":1309614398,\"name\":{\"value\":\"iqzbq\",\"localizedValue\":\"sovmyokacspkwl\"}}]}")
                .toObject(UsageListResult.class);
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        UsageListResult model =
            new UsageListResult().withValue(Arrays.asList(new UsageInner(), new UsageInner(), new UsageInner()));
        model = BinaryData.fromObject(model).toObject(UsageListResult.class);
    }
}
