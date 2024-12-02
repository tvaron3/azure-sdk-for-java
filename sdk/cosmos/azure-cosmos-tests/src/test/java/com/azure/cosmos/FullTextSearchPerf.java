package com.azure.cosmos;

import com.azure.cosmos.models.CosmosQueryRequestOptions;
import reactor.core.publisher.Flux;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class FullTextSearchPerf{

    private static final boolean diagnostics = false;
    private static final int queryIndex = 2; // 0 for full text search, 1 for full text rank, and 2 for hybrid search
    private static final int warmUpTime = 0; // 120000
    private static final int testTime = 100; // 600000
    private static final int top = 1000;
    private static final boolean useTop = true;
    private static final boolean ruCharge = false;
    private static final boolean memory = false;

    public static void main(String[] args) throws InterruptedException {

        CosmosAsyncClient cosmosAsyncClient = new CosmosClientBuilder()
                .endpoint("")
                .key("")
                .gatewayMode()
                //.userAgentSuffix("tomas26")
                .buildAsyncClient();
        CosmosAsyncDatabase cosmosAsyncDatabase = cosmosAsyncClient.getDatabase("perf-tests-sdks");
        CosmosAsyncContainer cosmosAsyncContainer = cosmosAsyncDatabase
                .getContainer("fts");
        String topStr = useTop ? "TOP " + top : "";
        String embedding  = createEmbedding().toString();
        String ftsQuery = "SELECT " + topStr + " c.id AS Text FROM c WHERE FullTextContains(c.text, 'shoulder')";
        String ftsRankQuery = "SELECT " + topStr + " c.id AS Text FROM c Order By Rank FullTextScore(c.text, ['may', 'music'])";
        String hybridQuery = "SELECT " + topStr + " c.id AS Text FROM c Order By Rank RRF(FullTextScore(c.text, ['may', 'music']), VectorDistance(c.vector," + embedding + "))";
        String vectorSearchQuery = "SELECT TOP 10000 c.id AS Text FROM c Order By VectorDistance(c.vector," + embedding + ")";
        String query;
        switch (queryIndex) {
            case 0:
                query = ftsQuery;
                break;
            case 1:
                query = ftsRankQuery;
                break;
            case 2:
                query = hybridQuery;
                break;
            default:
                query = null;
                System.out.println("Invalid query index");
        }
        long start1 = new Date().getTime();
        System.out.println("Diagnostics is " + diagnostics);
        while(new Date().getTime()  - start1 < warmUpTime) {
            cosmosAsyncContainer.queryItems(query  , Doc.class).byPage()
                    .flatMap(passengerFeedResponse -> {
//                        for (Passenger passenger : passengerFeedResponse.getResults()) {
//                           System.out.println(passenger);
//                        }
                        //System.out.println(passengerFeedResponse.getCosmosDiagnostics());
                        //System.out.println(passengerFeedResponse.getActivityId());
                        //System.out.println(passengerFeedResponse.getCorrelationActivityId());
                        return Flux.empty();
                    })
                    .blockLast();
        }
        System.out.println("Warm Up is over");
        int i = 0;
        LinkedList<Long> times = new LinkedList<>();
        long start2 = new Date().getTime();
        AtomicReference<String> diagnosticsStr = new AtomicReference<>("");
        AtomicReference<Double> totalRUCharge = new AtomicReference<>(0.0);
        while(new Date().getTime() - start2 < testTime) {
            long start = new Date().getTime();
            if (memory) {
                Runtime runtime = Runtime.getRuntime();
                long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
                for (int z = 0; z < 50; z++) {

                    cosmosAsyncContainer.queryItems(query, Doc.class).byPage()
                            .flatMap(feedResponse -> Flux.empty()).blockLast();

                }
                long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
                System.out.println("Used memory in bytes before queries: " + memoryBefore);
                System.out.println("Used memory in bytes after queries: " + memoryAfter);
                runtime.gc();
                long memoryAfterGC = runtime.totalMemory() - runtime.freeMemory();
                System.out.println("Used memory in bytes after GC: " + memoryAfterGC);
            } else {
                CosmosQueryRequestOptions options = new CosmosQueryRequestOptions().setMaxDegreeOfParallelism(10);
                cosmosAsyncContainer.queryItems(query, Doc.class).byPage()
                        .flatMap(feedResponse -> {
//                        for (Passenger passenger : passengerFeedResponse.getResults()) {
//                           System.out.println(passenger);
//                        }
                            if (diagnostics) {
                                diagnosticsStr.set(diagnosticsStr.get() + feedResponse.getCosmosDiagnostics().toString() + "\n ---------- \n");
                            }
                            if (ruCharge) {
                                //System.out.println(feedResponse.getActivityId());
                                totalRUCharge.set(feedResponse.getRequestCharge() + totalRUCharge.get());
                            }
                            //System.out.println(passengerFeedResponse.getActivityId());
                            //System.out.println(passengerFeedResponse.getCorrelationActivityId());
                            return Flux.empty();
                        })
                        .blockLast();
                long diff = new Date().getTime() - start;
                times.add(diff);
                i++;
            }

        }
        if (diagnostics) {
            writeDiagnostics(diagnosticsStr.get());
        }
        if (ruCharge) {
            System.out.println("Total RU charge: " + totalRUCharge.get());
        }


        long sum = 0;
        for (Long value : times) {
            sum += value;
        }
        // System.out.println(sum);

        // Calculate the average
        double average = (double) sum / times.size();

        System.out.println("Sum: " + sum);
        System.out.println("Average: " + average);
        System.out.println("Total Operations: " + times.size());


    }

    private static void writeDiagnostics(String diagnosticsString) {
        try {
            // Create FileWriter object
            FileWriter writer = new FileWriter("diagnostics.txt");

            // Write some text
            writer.write(diagnosticsString);


            // Close the writer after writing is done
            writer.close();

            System.out.println("Successfully wrote to the diagnostics.");
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
    static final class Doc {
        private final String id;
        private ArrayList<Double> embedding;
        private String pk;
        private String text;
        Doc() {
            this.id = UUID.randomUUID().toString();
        }
        Doc(String pk, ArrayList<Double> embedding, String text) {
            this.id = UUID.randomUUID().toString();
            this.embedding = embedding;
            this.pk = pk;
            this.text = text;
        }

        public String getId() {
            return id;
        }
        public String getPk() { return this.pk; }
        public String getText() { return this.text; }



        public ArrayList<Double> getEmbedding() {
            return embedding;
        }
    }

    public static List<Double> createEmbedding() {
        Random random = new Random();
        List<Double> randomNumbers = new ArrayList<>();

        for (int i = 0; i < 128; i++) {
            double randomNumber = -1 + (1 - (-1)) * random.nextDouble();
            randomNumbers.add(randomNumber);
        }
        return randomNumbers;
    }
}

