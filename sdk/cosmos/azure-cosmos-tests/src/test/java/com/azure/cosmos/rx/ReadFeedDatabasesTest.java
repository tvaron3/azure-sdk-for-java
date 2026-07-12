// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.cosmos.rx;

import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.util.CosmosPagedFlux;
import com.azure.cosmos.models.CosmosDatabaseProperties;
import com.azure.cosmos.models.CosmosDatabaseRequestOptions;
import com.azure.cosmos.implementation.FeedResponseListValidator;
import com.azure.cosmos.implementation.FeedResponseValidator;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class ReadFeedDatabasesTest extends TestSuiteBase {

    private List<CosmosDatabaseProperties> createdDatabases = new ArrayList<>();

    private CosmosAsyncClient client;

    @Factory(dataProvider = "clientBuilders")
    public ReadFeedDatabasesTest(CosmosClientBuilder clientBuilder) {
        super(clientBuilder);
    }

    @Test(groups = { "query" }, timeOut = FEED_TIMEOUT)
    public void readDatabases() throws Exception {
        int maxItemCount = 2;

        CosmosPagedFlux<CosmosDatabaseProperties> feedObservable = client.readAllDatabases();

        // readAllDatabases is an account-global read. Live tests share a fixed account, so
        // other test runs may create/delete databases concurrently. Assert only that the
        // databases created by this test are present (containment) rather than asserting the
        // exact account-wide set/count/page-count, which would be non-deterministic.
        FeedResponseListValidator<CosmosDatabaseProperties> validator = new FeedResponseListValidator.Builder<CosmosDatabaseProperties>()
                .containsResourceIds(createdDatabases.stream().map(d -> d.getResourceId()).collect(Collectors.toList()))
                .numberOfPagesIsGreaterThanOrEqualTo(1)
                .pageSatisfy(0, new FeedResponseValidator.Builder<CosmosDatabaseProperties>()
                        .requestChargeGreaterThanOrEqualTo(1.0).build())
                .build();

        validateQuerySuccess(feedObservable.byPage(maxItemCount), validator, FEED_TIMEOUT);
    }

    @BeforeClass(groups = { "query" }, timeOut = SETUP_TIMEOUT)
    public void before_ReadFeedDatabasesTest() throws URISyntaxException {
        client = getClientBuilder().buildAsyncClient();
        for(int i = 0; i < 5; i++) {
            createdDatabases.add(createDatabase(client));
        }

        // Guard against the containment assertion silently degrading to a no-op: if no databases
        // were created the readDatabases() test would assert nothing meaningful on a shared account.
        assertThat(createdDatabases)
            .describedAs("databases created by this test")
            .isNotEmpty();
    }

    public CosmosDatabaseProperties createDatabase(CosmosAsyncClient client) {
        CosmosDatabaseProperties db = new CosmosDatabaseProperties(UUID.randomUUID().toString());
        return client.createDatabase(db, new CosmosDatabaseRequestOptions()).block().getProperties();
    }

    @AfterClass(groups = { "query" }, timeOut = 5 * SHUTDOWN_TIMEOUT, alwaysRun = true)
    public void afterClass() {
        for (int i = 0; i < createdDatabases.size(); i ++) {
            safeDeleteDatabase(client.getDatabase(createdDatabases.get(i).getId()));
        }
        safeClose(client);
    }
}
