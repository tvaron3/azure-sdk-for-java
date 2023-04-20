// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.paloaltonetworks.ngfw.models;

import com.azure.core.annotation.Fluent;
import com.azure.core.util.logging.ClientLogger;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

/** Billing plan information. */
@Fluent
public final class PlanData {
    /*
     * different usage type like PAYG/COMMITTED
     */
    @JsonProperty(value = "usageType")
    private UsageType usageType;

    /*
     * different billing cycles like MONTHLY/WEEKLY
     */
    @JsonProperty(value = "billingCycle", required = true)
    private BillingCycle billingCycle;

    /*
     * plan id as published by Liftr.PAN
     */
    @JsonProperty(value = "planId", required = true)
    private String planId;

    /*
     * date when plan was applied
     */
    @JsonProperty(value = "effectiveDate", access = JsonProperty.Access.WRITE_ONLY)
    private OffsetDateTime effectiveDate;

    /** Creates an instance of PlanData class. */
    public PlanData() {
    }

    /**
     * Get the usageType property: different usage type like PAYG/COMMITTED.
     *
     * @return the usageType value.
     */
    public UsageType usageType() {
        return this.usageType;
    }

    /**
     * Set the usageType property: different usage type like PAYG/COMMITTED.
     *
     * @param usageType the usageType value to set.
     * @return the PlanData object itself.
     */
    public PlanData withUsageType(UsageType usageType) {
        this.usageType = usageType;
        return this;
    }

    /**
     * Get the billingCycle property: different billing cycles like MONTHLY/WEEKLY.
     *
     * @return the billingCycle value.
     */
    public BillingCycle billingCycle() {
        return this.billingCycle;
    }

    /**
     * Set the billingCycle property: different billing cycles like MONTHLY/WEEKLY.
     *
     * @param billingCycle the billingCycle value to set.
     * @return the PlanData object itself.
     */
    public PlanData withBillingCycle(BillingCycle billingCycle) {
        this.billingCycle = billingCycle;
        return this;
    }

    /**
     * Get the planId property: plan id as published by Liftr.PAN.
     *
     * @return the planId value.
     */
    public String planId() {
        return this.planId;
    }

    /**
     * Set the planId property: plan id as published by Liftr.PAN.
     *
     * @param planId the planId value to set.
     * @return the PlanData object itself.
     */
    public PlanData withPlanId(String planId) {
        this.planId = planId;
        return this;
    }

    /**
     * Get the effectiveDate property: date when plan was applied.
     *
     * @return the effectiveDate value.
     */
    public OffsetDateTime effectiveDate() {
        return this.effectiveDate;
    }

    /**
     * Validates the instance.
     *
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
        if (billingCycle() == null) {
            throw LOGGER
                .logExceptionAsError(
                    new IllegalArgumentException("Missing required property billingCycle in model PlanData"));
        }
        if (planId() == null) {
            throw LOGGER
                .logExceptionAsError(
                    new IllegalArgumentException("Missing required property planId in model PlanData"));
        }
    }

    private static final ClientLogger LOGGER = new ClientLogger(PlanData.class);
}