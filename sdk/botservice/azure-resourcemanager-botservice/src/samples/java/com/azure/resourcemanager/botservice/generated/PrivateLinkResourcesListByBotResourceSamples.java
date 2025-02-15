// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.botservice.generated;

/**
 * Samples for PrivateLinkResources ListByBotResource.
 */
public final class PrivateLinkResourcesListByBotResourceSamples {
    /*
     * x-ms-original-file:
     * specification/botservice/resource-manager/Microsoft.BotService/preview/2021-05-01-preview/examples/
     * ListPrivateLinkResources.json
     */
    /**
     * Sample code: List Private Link Resources.
     * 
     * @param manager Entry point to BotServiceManager.
     */
    public static void listPrivateLinkResources(com.azure.resourcemanager.botservice.BotServiceManager manager) {
        manager.privateLinkResources()
            .listByBotResourceWithResponse("res6977", "sto2527", com.azure.core.util.Context.NONE);
    }
}
