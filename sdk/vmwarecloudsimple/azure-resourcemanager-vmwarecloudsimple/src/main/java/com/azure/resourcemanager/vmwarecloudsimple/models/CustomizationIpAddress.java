// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.vmwarecloudsimple.models;

import com.azure.core.annotation.Fluent;
import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;
import java.io.IOException;

/**
 * The CustomizationIpAddress model.
 */
@Fluent
public final class CustomizationIpAddress implements JsonSerializable<CustomizationIpAddress> {
    /*
     * Argument when Custom ip type is selected
     */
    private String argument;

    /*
     * Defined Ip Address when Fixed ip type is selected
     */
    private String ipAddress;

    /*
     * Customization Specification ip type
     */
    private CustomizationIpAddressType type;

    /**
     * Creates an instance of CustomizationIpAddress class.
     */
    public CustomizationIpAddress() {
    }

    /**
     * Get the argument property: Argument when Custom ip type is selected.
     * 
     * @return the argument value.
     */
    public String argument() {
        return this.argument;
    }

    /**
     * Set the argument property: Argument when Custom ip type is selected.
     * 
     * @param argument the argument value to set.
     * @return the CustomizationIpAddress object itself.
     */
    public CustomizationIpAddress withArgument(String argument) {
        this.argument = argument;
        return this;
    }

    /**
     * Get the ipAddress property: Defined Ip Address when Fixed ip type is selected.
     * 
     * @return the ipAddress value.
     */
    public String ipAddress() {
        return this.ipAddress;
    }

    /**
     * Set the ipAddress property: Defined Ip Address when Fixed ip type is selected.
     * 
     * @param ipAddress the ipAddress value to set.
     * @return the CustomizationIpAddress object itself.
     */
    public CustomizationIpAddress withIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    /**
     * Get the type property: Customization Specification ip type.
     * 
     * @return the type value.
     */
    public CustomizationIpAddressType type() {
        return this.type;
    }

    /**
     * Set the type property: Customization Specification ip type.
     * 
     * @param type the type value to set.
     * @return the CustomizationIpAddress object itself.
     */
    public CustomizationIpAddress withType(CustomizationIpAddressType type) {
        this.type = type;
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
        jsonWriter.writeStringField("argument", this.argument);
        jsonWriter.writeStringField("ipAddress", this.ipAddress);
        jsonWriter.writeStringField("type", this.type == null ? null : this.type.toString());
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of CustomizationIpAddress from the JsonReader.
     * 
     * @param jsonReader The JsonReader being read.
     * @return An instance of CustomizationIpAddress if the JsonReader was pointing to an instance of it, or null if it
     * was pointing to JSON null.
     * @throws IOException If an error occurs while reading the CustomizationIpAddress.
     */
    public static CustomizationIpAddress fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            CustomizationIpAddress deserializedCustomizationIpAddress = new CustomizationIpAddress();
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();

                if ("argument".equals(fieldName)) {
                    deserializedCustomizationIpAddress.argument = reader.getString();
                } else if ("ipAddress".equals(fieldName)) {
                    deserializedCustomizationIpAddress.ipAddress = reader.getString();
                } else if ("type".equals(fieldName)) {
                    deserializedCustomizationIpAddress.type = CustomizationIpAddressType.fromString(reader.getString());
                } else {
                    reader.skipChildren();
                }
            }

            return deserializedCustomizationIpAddress;
        });
    }
}
