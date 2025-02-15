// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.mediaservices.fluent.models;

import com.azure.core.annotation.Fluent;
import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;
import com.azure.resourcemanager.mediaservices.models.ArmStreamingEndpointSkuInfo;
import java.io.IOException;
import java.util.List;

/**
 * The StreamingEndpointSkuInfoListResult model.
 */
@Fluent
public final class StreamingEndpointSkuInfoListResultInner
    implements JsonSerializable<StreamingEndpointSkuInfoListResultInner> {
    /*
     * The result of the List StreamingEndpoint skus.
     */
    private List<ArmStreamingEndpointSkuInfo> value;

    /**
     * Creates an instance of StreamingEndpointSkuInfoListResultInner class.
     */
    public StreamingEndpointSkuInfoListResultInner() {
    }

    /**
     * Get the value property: The result of the List StreamingEndpoint skus.
     * 
     * @return the value value.
     */
    public List<ArmStreamingEndpointSkuInfo> value() {
        return this.value;
    }

    /**
     * Set the value property: The result of the List StreamingEndpoint skus.
     * 
     * @param value the value value to set.
     * @return the StreamingEndpointSkuInfoListResultInner object itself.
     */
    public StreamingEndpointSkuInfoListResultInner withValue(List<ArmStreamingEndpointSkuInfo> value) {
        this.value = value;
        return this;
    }

    /**
     * Validates the instance.
     * 
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
        if (value() != null) {
            value().forEach(e -> e.validate());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeArrayField("value", this.value, (writer, element) -> writer.writeJson(element));
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of StreamingEndpointSkuInfoListResultInner from the JsonReader.
     * 
     * @param jsonReader The JsonReader being read.
     * @return An instance of StreamingEndpointSkuInfoListResultInner if the JsonReader was pointing to an instance of
     * it, or null if it was pointing to JSON null.
     * @throws IOException If an error occurs while reading the StreamingEndpointSkuInfoListResultInner.
     */
    public static StreamingEndpointSkuInfoListResultInner fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            StreamingEndpointSkuInfoListResultInner deserializedStreamingEndpointSkuInfoListResultInner
                = new StreamingEndpointSkuInfoListResultInner();
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();

                if ("value".equals(fieldName)) {
                    List<ArmStreamingEndpointSkuInfo> value
                        = reader.readArray(reader1 -> ArmStreamingEndpointSkuInfo.fromJson(reader1));
                    deserializedStreamingEndpointSkuInfoListResultInner.value = value;
                } else {
                    reader.skipChildren();
                }
            }

            return deserializedStreamingEndpointSkuInfoListResultInner;
        });
    }
}
