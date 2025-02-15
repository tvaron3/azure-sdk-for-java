// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.logic.models;

import com.azure.core.annotation.Fluent;
import com.azure.core.util.logging.ClientLogger;
import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;
import java.io.IOException;

/**
 * The Edifact message filter for odata query.
 */
@Fluent
public final class EdifactMessageFilter implements JsonSerializable<EdifactMessageFilter> {
    /*
     * The message filter type.
     */
    private MessageFilterType messageFilterType;

    /**
     * Creates an instance of EdifactMessageFilter class.
     */
    public EdifactMessageFilter() {
    }

    /**
     * Get the messageFilterType property: The message filter type.
     * 
     * @return the messageFilterType value.
     */
    public MessageFilterType messageFilterType() {
        return this.messageFilterType;
    }

    /**
     * Set the messageFilterType property: The message filter type.
     * 
     * @param messageFilterType the messageFilterType value to set.
     * @return the EdifactMessageFilter object itself.
     */
    public EdifactMessageFilter withMessageFilterType(MessageFilterType messageFilterType) {
        this.messageFilterType = messageFilterType;
        return this;
    }

    /**
     * Validates the instance.
     * 
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
        if (messageFilterType() == null) {
            throw LOGGER.atError()
                .log(new IllegalArgumentException(
                    "Missing required property messageFilterType in model EdifactMessageFilter"));
        }
    }

    private static final ClientLogger LOGGER = new ClientLogger(EdifactMessageFilter.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeStringField("messageFilterType",
            this.messageFilterType == null ? null : this.messageFilterType.toString());
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of EdifactMessageFilter from the JsonReader.
     * 
     * @param jsonReader The JsonReader being read.
     * @return An instance of EdifactMessageFilter if the JsonReader was pointing to an instance of it, or null if it
     * was pointing to JSON null.
     * @throws IllegalStateException If the deserialized JSON object was missing any required properties.
     * @throws IOException If an error occurs while reading the EdifactMessageFilter.
     */
    public static EdifactMessageFilter fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            EdifactMessageFilter deserializedEdifactMessageFilter = new EdifactMessageFilter();
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();

                if ("messageFilterType".equals(fieldName)) {
                    deserializedEdifactMessageFilter.messageFilterType
                        = MessageFilterType.fromString(reader.getString());
                } else {
                    reader.skipChildren();
                }
            }

            return deserializedEdifactMessageFilter;
        });
    }
}
