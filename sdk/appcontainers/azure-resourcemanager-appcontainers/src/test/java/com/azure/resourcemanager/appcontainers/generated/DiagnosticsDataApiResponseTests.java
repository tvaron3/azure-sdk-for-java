// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.appcontainers.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.appcontainers.models.DiagnosticDataTableResponseColumn;
import com.azure.resourcemanager.appcontainers.models.DiagnosticDataTableResponseObject;
import com.azure.resourcemanager.appcontainers.models.DiagnosticRendering;
import com.azure.resourcemanager.appcontainers.models.DiagnosticsDataApiResponse;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;

public final class DiagnosticsDataApiResponseTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        DiagnosticsDataApiResponse model = BinaryData.fromString(
            "{\"table\":{\"tableName\":\"cxlzhcoxovnekh\",\"columns\":[{\"columnName\":\"sfnrdtjxt\",\"dataType\":\"dcqtjvidttge\",\"columnType\":\"slvyjtcvuwkasi\"},{\"columnName\":\"esfuught\",\"dataType\":\"fecjxeygtuhx\",\"columnType\":\"cbuewmrswnjlxuz\"},{\"columnName\":\"wpusxjbaqehg\",\"dataType\":\"ohzjqatucoigeb\",\"columnType\":\"cnwfepbnwgfmxjg\"},{\"columnName\":\"bjb\",\"dataType\":\"lfgtdysnaquflqbc\",\"columnType\":\"hamzjrwdkqze\"}],\"rows\":[\"dataleziunjxdfzant\",\"datawcegyamlbn\",\"dataeqacjjvpilguooq\"]},\"renderingProperties\":{\"type\":255263020,\"title\":\"itgueiookjbs\",\"description\":\"rtdtpdelq\",\"isVisible\":false}}")
            .toObject(DiagnosticsDataApiResponse.class);
        Assertions.assertEquals("cxlzhcoxovnekh", model.table().tableName());
        Assertions.assertEquals("sfnrdtjxt", model.table().columns().get(0).columnName());
        Assertions.assertEquals("dcqtjvidttge", model.table().columns().get(0).dataType());
        Assertions.assertEquals("slvyjtcvuwkasi", model.table().columns().get(0).columnType());
        Assertions.assertEquals(255263020, model.renderingProperties().type());
        Assertions.assertEquals("itgueiookjbs", model.renderingProperties().title());
        Assertions.assertEquals("rtdtpdelq", model.renderingProperties().description());
        Assertions.assertEquals(false, model.renderingProperties().isVisible());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        DiagnosticsDataApiResponse model = new DiagnosticsDataApiResponse()
            .withTable(new DiagnosticDataTableResponseObject().withTableName("cxlzhcoxovnekh")
                .withColumns(Arrays.asList(
                    new DiagnosticDataTableResponseColumn().withColumnName("sfnrdtjxt")
                        .withDataType("dcqtjvidttge")
                        .withColumnType("slvyjtcvuwkasi"),
                    new DiagnosticDataTableResponseColumn().withColumnName("esfuught")
                        .withDataType("fecjxeygtuhx")
                        .withColumnType("cbuewmrswnjlxuz"),
                    new DiagnosticDataTableResponseColumn().withColumnName("wpusxjbaqehg")
                        .withDataType("ohzjqatucoigeb")
                        .withColumnType("cnwfepbnwgfmxjg"),
                    new DiagnosticDataTableResponseColumn().withColumnName("bjb")
                        .withDataType("lfgtdysnaquflqbc")
                        .withColumnType("hamzjrwdkqze")))
                .withRows(Arrays.asList("dataleziunjxdfzant", "datawcegyamlbn", "dataeqacjjvpilguooq")))
            .withRenderingProperties(new DiagnosticRendering().withType(255263020)
                .withTitle("itgueiookjbs")
                .withDescription("rtdtpdelq")
                .withIsVisible(false));
        model = BinaryData.fromObject(model).toObject(DiagnosticsDataApiResponse.class);
        Assertions.assertEquals("cxlzhcoxovnekh", model.table().tableName());
        Assertions.assertEquals("sfnrdtjxt", model.table().columns().get(0).columnName());
        Assertions.assertEquals("dcqtjvidttge", model.table().columns().get(0).dataType());
        Assertions.assertEquals("slvyjtcvuwkasi", model.table().columns().get(0).columnType());
        Assertions.assertEquals(255263020, model.renderingProperties().type());
        Assertions.assertEquals("itgueiookjbs", model.renderingProperties().title());
        Assertions.assertEquals("rtdtpdelq", model.renderingProperties().description());
        Assertions.assertEquals(false, model.renderingProperties().isVisible());
    }
}
