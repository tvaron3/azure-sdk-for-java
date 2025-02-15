// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.securityinsights.implementation;

import com.azure.resourcemanager.securityinsights.fluent.models.SentinelOnboardingStateInner;
import com.azure.resourcemanager.securityinsights.fluent.models.SentinelOnboardingStatesListInner;
import com.azure.resourcemanager.securityinsights.models.SentinelOnboardingState;
import com.azure.resourcemanager.securityinsights.models.SentinelOnboardingStatesList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class SentinelOnboardingStatesListImpl implements SentinelOnboardingStatesList {
    private SentinelOnboardingStatesListInner innerObject;

    private final com.azure.resourcemanager.securityinsights.SecurityInsightsManager serviceManager;

    SentinelOnboardingStatesListImpl(SentinelOnboardingStatesListInner innerObject,
        com.azure.resourcemanager.securityinsights.SecurityInsightsManager serviceManager) {
        this.innerObject = innerObject;
        this.serviceManager = serviceManager;
    }

    public List<SentinelOnboardingState> value() {
        List<SentinelOnboardingStateInner> inner = this.innerModel().value();
        if (inner != null) {
            return Collections.unmodifiableList(inner.stream()
                .map(inner1 -> new SentinelOnboardingStateImpl(inner1, this.manager()))
                .collect(Collectors.toList()));
        } else {
            return Collections.emptyList();
        }
    }

    public SentinelOnboardingStatesListInner innerModel() {
        return this.innerObject;
    }

    private com.azure.resourcemanager.securityinsights.SecurityInsightsManager manager() {
        return this.serviceManager;
    }
}
