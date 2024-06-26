// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.frontdoor.generated;

import com.azure.core.management.SubResource;
import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.frontdoor.models.Backend;
import com.azure.resourcemanager.frontdoor.models.BackendEnabledState;
import com.azure.resourcemanager.frontdoor.models.BackendPoolUpdateParameters;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;

public final class BackendPoolUpdateParametersTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        BackendPoolUpdateParameters model = BinaryData.fromString(
            "{\"backends\":[{\"address\":\"yfln\",\"privateLinkAlias\":\"wmd\",\"privateLinkResourceId\":\"wpklvxw\",\"privateLinkLocation\":\"gdxpg\",\"privateEndpointStatus\":\"Approved\",\"privateLinkApprovalMessage\":\"isze\",\"httpPort\":511466566,\"httpsPort\":1448184483,\"enabledState\":\"Enabled\",\"priority\":1933702310,\"weight\":1689782344,\"backendHostHeader\":\"daxconfozauorsuk\"},{\"address\":\"wbqpl\",\"privateLinkAlias\":\"vnuuepzl\",\"privateLinkResourceId\":\"hw\",\"privateLinkLocation\":\"oldweyuqdu\",\"privateEndpointStatus\":\"Pending\",\"privateLinkApprovalMessage\":\"nrwrbiork\",\"httpPort\":1451955961,\"httpsPort\":1371763092,\"enabledState\":\"Enabled\",\"priority\":38060246,\"weight\":1472152819,\"backendHostHeader\":\"xmsivfomiloxggdu\"},{\"address\":\"q\",\"privateLinkAlias\":\"ieuzaofjchvcyyy\",\"privateLinkResourceId\":\"gdotcubiipuipwo\",\"privateLinkLocation\":\"nmacj\",\"privateEndpointStatus\":\"Timeout\",\"privateLinkApprovalMessage\":\"zshq\",\"httpPort\":1591499316,\"httpsPort\":932128156,\"enabledState\":\"Disabled\",\"priority\":1733722494,\"weight\":64895608,\"backendHostHeader\":\"rrilbywdxsmic\"},{\"address\":\"rwfscjfnynszquj\",\"privateLinkAlias\":\"dvoqyt\",\"privateLinkResourceId\":\"yo\",\"privateLinkLocation\":\"blgyavutpthj\",\"privateEndpointStatus\":\"Pending\",\"privateLinkApprovalMessage\":\"smsks\",\"httpPort\":791689891,\"httpsPort\":1015646968,\"enabledState\":\"Enabled\",\"priority\":1062811723,\"weight\":1488909990,\"backendHostHeader\":\"gxxlxsffgcvizq\"}],\"loadBalancingSettings\":{\"id\":\"l\"},\"healthProbeSettings\":{\"id\":\"youpfgfbkj\"}}")
            .toObject(BackendPoolUpdateParameters.class);
        Assertions.assertEquals("yfln", model.backends().get(0).address());
        Assertions.assertEquals("wmd", model.backends().get(0).privateLinkAlias());
        Assertions.assertEquals("wpklvxw", model.backends().get(0).privateLinkResourceId());
        Assertions.assertEquals("gdxpg", model.backends().get(0).privateLinkLocation());
        Assertions.assertEquals("isze", model.backends().get(0).privateLinkApprovalMessage());
        Assertions.assertEquals(511466566, model.backends().get(0).httpPort());
        Assertions.assertEquals(1448184483, model.backends().get(0).httpsPort());
        Assertions.assertEquals(BackendEnabledState.ENABLED, model.backends().get(0).enabledState());
        Assertions.assertEquals(1933702310, model.backends().get(0).priority());
        Assertions.assertEquals(1689782344, model.backends().get(0).weight());
        Assertions.assertEquals("daxconfozauorsuk", model.backends().get(0).backendHostHeader());
        Assertions.assertEquals("l", model.loadBalancingSettings().id());
        Assertions.assertEquals("youpfgfbkj", model.healthProbeSettings().id());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        BackendPoolUpdateParameters model = new BackendPoolUpdateParameters()
            .withBackends(Arrays.asList(
                new Backend().withAddress("yfln")
                    .withPrivateLinkAlias("wmd")
                    .withPrivateLinkResourceId("wpklvxw")
                    .withPrivateLinkLocation("gdxpg")
                    .withPrivateLinkApprovalMessage("isze")
                    .withHttpPort(511466566)
                    .withHttpsPort(1448184483)
                    .withEnabledState(BackendEnabledState.ENABLED)
                    .withPriority(1933702310)
                    .withWeight(1689782344)
                    .withBackendHostHeader("daxconfozauorsuk"),
                new Backend().withAddress("wbqpl")
                    .withPrivateLinkAlias("vnuuepzl")
                    .withPrivateLinkResourceId("hw")
                    .withPrivateLinkLocation("oldweyuqdu")
                    .withPrivateLinkApprovalMessage("nrwrbiork")
                    .withHttpPort(1451955961)
                    .withHttpsPort(1371763092)
                    .withEnabledState(BackendEnabledState.ENABLED)
                    .withPriority(38060246)
                    .withWeight(1472152819)
                    .withBackendHostHeader("xmsivfomiloxggdu"),
                new Backend().withAddress("q")
                    .withPrivateLinkAlias("ieuzaofjchvcyyy")
                    .withPrivateLinkResourceId("gdotcubiipuipwo")
                    .withPrivateLinkLocation("nmacj")
                    .withPrivateLinkApprovalMessage("zshq")
                    .withHttpPort(1591499316)
                    .withHttpsPort(932128156)
                    .withEnabledState(BackendEnabledState.DISABLED)
                    .withPriority(1733722494)
                    .withWeight(64895608)
                    .withBackendHostHeader("rrilbywdxsmic"),
                new Backend().withAddress("rwfscjfnynszquj")
                    .withPrivateLinkAlias("dvoqyt")
                    .withPrivateLinkResourceId("yo")
                    .withPrivateLinkLocation("blgyavutpthj")
                    .withPrivateLinkApprovalMessage("smsks")
                    .withHttpPort(791689891)
                    .withHttpsPort(1015646968)
                    .withEnabledState(BackendEnabledState.ENABLED)
                    .withPriority(1062811723)
                    .withWeight(1488909990)
                    .withBackendHostHeader("gxxlxsffgcvizq")))
            .withLoadBalancingSettings(new SubResource().withId("l"))
            .withHealthProbeSettings(new SubResource().withId("youpfgfbkj"));
        model = BinaryData.fromObject(model).toObject(BackendPoolUpdateParameters.class);
        Assertions.assertEquals("yfln", model.backends().get(0).address());
        Assertions.assertEquals("wmd", model.backends().get(0).privateLinkAlias());
        Assertions.assertEquals("wpklvxw", model.backends().get(0).privateLinkResourceId());
        Assertions.assertEquals("gdxpg", model.backends().get(0).privateLinkLocation());
        Assertions.assertEquals("isze", model.backends().get(0).privateLinkApprovalMessage());
        Assertions.assertEquals(511466566, model.backends().get(0).httpPort());
        Assertions.assertEquals(1448184483, model.backends().get(0).httpsPort());
        Assertions.assertEquals(BackendEnabledState.ENABLED, model.backends().get(0).enabledState());
        Assertions.assertEquals(1933702310, model.backends().get(0).priority());
        Assertions.assertEquals(1689782344, model.backends().get(0).weight());
        Assertions.assertEquals("daxconfozauorsuk", model.backends().get(0).backendHostHeader());
        Assertions.assertEquals("l", model.loadBalancingSettings().id());
        Assertions.assertEquals("youpfgfbkj", model.healthProbeSettings().id());
    }
}
