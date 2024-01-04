package com.pingcap.gat.util;
import com.google.gson.JsonElement;
import com.pingcap.gat.constant.ConfigConstant;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CodecovApiExample {

    public static void main(String[] args) {
        // Replace with your Codecov API token and repository details
        String codecovToken = ConfigConstant.CCTOKEN;
        String repositoryOwner = "pingcap";
        String repositoryName = "tidb";
        String branch = "master";
        String directory = "pkg/ddl";

        // Construct the API URL
        String apiUrl = String.format("https://codecov.io/api/gh/%s/%s/branch/%s?directory=%s",
                repositoryOwner, repositoryName, branch, directory);
        JsonElement jsonElement = HttpUtil.doRestRequest(apiUrl,codecovToken);

        // Make the API request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Authorization", "Bearer " + codecovToken)
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Check if the request was successful (status code 200)
            if (response.statusCode() == 200) {
                // Parse the JSON response
                String responseBody = response.body();

                // Process the coverage information as needed
                // For example, print the coverage percentage for the specified directory
                // Note: You may want to use a JSON library for more robust parsing
                System.out.println("Response: " + responseBody);

                // Extract and print the coverage percentage for the specified directory
                int coverage = extractCoverage(responseBody, directory);
                System.out.println("Coverage for " + directory + ": " + coverage + "%");
            } else {
                // Print an error message if the request was not successful
                System.out.println("Error: " + response.statusCode() + ", " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int extractCoverage(String responseBody, String directory) {
        // Note: This is a simplified example, and you may want to use a JSON library for robust parsing
        // This assumes the response contains a JSON structure with "directories" and "coverage" properties
        int coverage = 0;

        // Extract coverage information for the specified directory
        if (responseBody.contains("\"directories\"")) {
            int startIndex = responseBody.indexOf("\"" + directory + "\"");
            int endIndex = responseBody.indexOf("}", startIndex);

            if (startIndex != -1 && endIndex != -1) {
                String directoryInfo = responseBody.substring(startIndex, endIndex);
                String coverageString = directoryInfo.split("\"coverage\":")[1].split(",")[0].trim();
                coverage = Integer.parseInt(coverageString);
            }
        }

        return coverage;
    }
}


