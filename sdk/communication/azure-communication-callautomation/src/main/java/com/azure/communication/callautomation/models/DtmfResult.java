// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.communication.callautomation.models;

import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.stream.Collectors;

/** The DtmfResult model. */
@Fluent
public final class DtmfResult extends RecognizeResult {
    /*
     * The tones property.
     */
    @JsonProperty(value = "tones")
    private List<DtmfTone> dtmfTones;

    /**
     * Get the tones property: The tones property.
     *
     * @return the tones value.
     */
    public List<DtmfTone> getTones() {
        return this.dtmfTones;
    }

    /**
     * Set the tones property: The tones property.
     *
     * @param dtmfTones the tones value to set.
     * @return the DtmfResult object itself.
     */
    public DtmfResult setTones(List<DtmfTone> dtmfTones) {
        this.dtmfTones = dtmfTones;
        return this;
    }

    /**
     * Set the tones property: The tones property.
     *
     * @return the DtmfResult object itself.
     */
    public String convertToString() {
        if (this.dtmfTones == null) {
            return "";
        }

        return this.dtmfTones.stream()
                    .map(x -> x.convertToString())
                    .collect(Collectors.joining());
    }
}