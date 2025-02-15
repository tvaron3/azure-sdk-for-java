// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.synapse.generated;

/**
 * Samples for KustoPoolDataConnections Get.
 */
public final class KustoPoolDataConnectionsGetSamples {
    /*
     * x-ms-original-file: specification/synapse/resource-manager/Microsoft.Synapse/preview/2021-06-01-preview/examples/
     * KustoPoolDataConnectionsGet.json
     */
    /**
     * Sample code: KustoPoolDataConnectionsGet.
     * 
     * @param manager Entry point to SynapseManager.
     */
    public static void kustoPoolDataConnectionsGet(com.azure.resourcemanager.synapse.SynapseManager manager) {
        manager.kustoPoolDataConnections()
            .getWithResponse("kustorptest", "synapseWorkspaceName", "kustoclusterrptest4", "KustoDatabase8",
                "DataConnections8", com.azure.core.util.Context.NONE);
    }
}
