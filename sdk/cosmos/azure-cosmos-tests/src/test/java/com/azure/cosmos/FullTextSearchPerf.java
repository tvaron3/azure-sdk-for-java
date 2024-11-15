package com.azure.cosmos;

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

    private static boolean diagnostics = false;
    private static int queryIndex = 1; // 0 for full text search, 1 for full text rank, and 2 for hybrid search
    private static int warmUpTime = 60000; // 120000
    private static int testTime = 60000; // 600000
    private static int top = 1000;

    public static void main(String[] args) throws InterruptedException {

        CosmosAsyncClient cosmosAsyncClient = new CosmosClientBuilder()
                .endpoint("")
                .key("")
                .gatewayMode()
                .buildAsyncClient();
        CosmosAsyncDatabase cosmosAsyncDatabase = cosmosAsyncClient.getDatabase("QueryHybridRankTesting");
        CosmosAsyncContainer cosmosAsyncContainer = cosmosAsyncDatabase
                .getContainer("arxiv-250kdocuments-index");
        String embedding  = createEmbedding().toString();
        String ftsQuery = "SELECT c.text AS Text FROM c WHERE FullTextContains(c.text, 'shoulder')";
        String ftsRankQuery = "SELECT TOP 1000 c.text AS Text FROM c Order By Rank FullTextScore(c.text, ['may', 'music'])";
        String hybridQuery = "SELECT TOP 1000 c.text AS Text FROM c Order By Rank RRF(FullTextScore(c.text, ['may', 'music']), VectorDistance(c.vector," + embedding + "))";
        String vectorSearchQuery = "SELECT TOP 1000 c.text AS Text FROM c Order By VectorDistance(c.vector," + embedding + ")";
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
        while(new Date().getTime() - start2 < testTime) {
            long start = new Date().getTime();
            cosmosAsyncContainer.queryItems(query , Doc.class).byPage()
                    .flatMap(passengerFeedResponse -> {
//                        for (Passenger passenger : passengerFeedResponse.getResults()) {
//                           System.out.println(passenger);
//                        }
                        if (diagnostics) {
                            diagnosticsStr.set(diagnosticsStr.get() + passengerFeedResponse.getCosmosDiagnostics().toString() + "\n ---------- \n");
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
        if (diagnostics) {
            writeDiagnostics(diagnosticsStr.get());
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

