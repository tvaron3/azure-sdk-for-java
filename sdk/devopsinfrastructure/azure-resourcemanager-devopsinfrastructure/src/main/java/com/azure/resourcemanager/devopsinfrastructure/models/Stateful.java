// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package com.azure.resourcemanager.devopsinfrastructure.models;

import com.azure.core.annotation.Fluent;
import com.azure.json.JsonReader;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;
import java.io.IOException;

/**
 * Stateful profile meaning that the machines will be returned to the pool after running a job.
 */
@Fluent
public final class Stateful extends AgentProfile {
    /*
     * Discriminator property for AgentProfile.
     */
    private String kind = "Stateful";

    /*
     * How long should stateful machines be kept around. The maximum is one week.
     */
    private String maxAgentLifetime;

    /*
     * How long should the machine be kept around after it ran a workload when there are no stand-by agents. The maximum
     * is one week.
     */
    private String gracePeriodTimeSpan;

    /**
     * Creates an instance of Stateful class.
     */
    public Stateful() {
    }

    /**
     * Get the kind property: Discriminator property for AgentProfile.
     * 
     * @return the kind value.
     */
    @Override
    public String kind() {
        return this.kind;
    }

    /**
     * Get the maxAgentLifetime property: How long should stateful machines be kept around. The maximum is one week.
     * 
     * @return the maxAgentLifetime value.
     */
    public String maxAgentLifetime() {
        return this.maxAgentLifetime;
    }

    /**
     * Set the maxAgentLifetime property: How long should stateful machines be kept around. The maximum is one week.
     * 
     * @param maxAgentLifetime the maxAgentLifetime value to set.
     * @return the Stateful object itself.
     */
    public Stateful withMaxAgentLifetime(String maxAgentLifetime) {
        this.maxAgentLifetime = maxAgentLifetime;
        return this;
    }

    /**
     * Get the gracePeriodTimeSpan property: How long should the machine be kept around after it ran a workload when
     * there are no stand-by agents. The maximum is one week.
     * 
     * @return the gracePeriodTimeSpan value.
     */
    public String gracePeriodTimeSpan() {
        return this.gracePeriodTimeSpan;
    }

    /**
     * Set the gracePeriodTimeSpan property: How long should the machine be kept around after it ran a workload when
     * there are no stand-by agents. The maximum is one week.
     * 
     * @param gracePeriodTimeSpan the gracePeriodTimeSpan value to set.
     * @return the Stateful object itself.
     */
    public Stateful withGracePeriodTimeSpan(String gracePeriodTimeSpan) {
        this.gracePeriodTimeSpan = gracePeriodTimeSpan;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stateful withResourcePredictions(ResourcePredictions resourcePredictions) {
        super.withResourcePredictions(resourcePredictions);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stateful withResourcePredictionsProfile(ResourcePredictionsProfile resourcePredictionsProfile) {
        super.withResourcePredictionsProfile(resourcePredictionsProfile);
        return this;
    }

    /**
     * Validates the instance.
     * 
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    @Override
    public void validate() {
        super.validate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeJsonField("resourcePredictions", resourcePredictions());
        jsonWriter.writeJsonField("resourcePredictionsProfile", resourcePredictionsProfile());
        jsonWriter.writeStringField("kind", this.kind);
        jsonWriter.writeStringField("maxAgentLifetime", this.maxAgentLifetime);
        jsonWriter.writeStringField("gracePeriodTimeSpan", this.gracePeriodTimeSpan);
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of Stateful from the JsonReader.
     * 
     * @param jsonReader The JsonReader being read.
     * @return An instance of Stateful if the JsonReader was pointing to an instance of it, or null if it was pointing
     * to JSON null.
     * @throws IOException If an error occurs while reading the Stateful.
     */
    public static Stateful fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            Stateful deserializedStateful = new Stateful();
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();

                if ("resourcePredictions".equals(fieldName)) {
                    deserializedStateful.withResourcePredictions(ResourcePredictions.fromJson(reader));
                } else if ("resourcePredictionsProfile".equals(fieldName)) {
                    deserializedStateful.withResourcePredictionsProfile(ResourcePredictionsProfile.fromJson(reader));
                } else if ("kind".equals(fieldName)) {
                    deserializedStateful.kind = reader.getString();
                } else if ("maxAgentLifetime".equals(fieldName)) {
                    deserializedStateful.maxAgentLifetime = reader.getString();
                } else if ("gracePeriodTimeSpan".equals(fieldName)) {
                    deserializedStateful.gracePeriodTimeSpan = reader.getString();
                } else {
                    reader.skipChildren();
                }
            }

            return deserializedStateful;
        });
    }
}
