// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.appcontainers.models;

import com.azure.core.annotation.Fluent;
import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;
import java.io.IOException;

/**
 * Scale configuration.
 */
@Fluent
public final class ScaleConfiguration implements JsonSerializable<ScaleConfiguration> {
    /*
     * The maximum count of sessions at the same time.
     */
    private Integer maxConcurrentSessions;

    /*
     * The minimum count of ready session instances.
     */
    private Integer readySessionInstances;

    /**
     * Creates an instance of ScaleConfiguration class.
     */
    public ScaleConfiguration() {
    }

    /**
     * Get the maxConcurrentSessions property: The maximum count of sessions at the same time.
     * 
     * @return the maxConcurrentSessions value.
     */
    public Integer maxConcurrentSessions() {
        return this.maxConcurrentSessions;
    }

    /**
     * Set the maxConcurrentSessions property: The maximum count of sessions at the same time.
     * 
     * @param maxConcurrentSessions the maxConcurrentSessions value to set.
     * @return the ScaleConfiguration object itself.
     */
    public ScaleConfiguration withMaxConcurrentSessions(Integer maxConcurrentSessions) {
        this.maxConcurrentSessions = maxConcurrentSessions;
        return this;
    }

    /**
     * Get the readySessionInstances property: The minimum count of ready session instances.
     * 
     * @return the readySessionInstances value.
     */
    public Integer readySessionInstances() {
        return this.readySessionInstances;
    }

    /**
     * Set the readySessionInstances property: The minimum count of ready session instances.
     * 
     * @param readySessionInstances the readySessionInstances value to set.
     * @return the ScaleConfiguration object itself.
     */
    public ScaleConfiguration withReadySessionInstances(Integer readySessionInstances) {
        this.readySessionInstances = readySessionInstances;
        return this;
    }

    /**
     * Validates the instance.
     * 
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeNumberField("maxConcurrentSessions", this.maxConcurrentSessions);
        jsonWriter.writeNumberField("readySessionInstances", this.readySessionInstances);
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of ScaleConfiguration from the JsonReader.
     * 
     * @param jsonReader The JsonReader being read.
     * @return An instance of ScaleConfiguration if the JsonReader was pointing to an instance of it, or null if it was
     * pointing to JSON null.
     * @throws IOException If an error occurs while reading the ScaleConfiguration.
     */
    public static ScaleConfiguration fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            ScaleConfiguration deserializedScaleConfiguration = new ScaleConfiguration();
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();

                if ("maxConcurrentSessions".equals(fieldName)) {
                    deserializedScaleConfiguration.maxConcurrentSessions = reader.getNullable(JsonReader::getInt);
                } else if ("readySessionInstances".equals(fieldName)) {
                    deserializedScaleConfiguration.readySessionInstances = reader.getNullable(JsonReader::getInt);
                } else {
                    reader.skipChildren();
                }
            }

            return deserializedScaleConfiguration;
        });
    }
}
