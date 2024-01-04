package com.pingcap.gat.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.pingcap.gat.constant.ConfigConstant;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtil {
    private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);
    public static void main(String[] args){

        JsonArray jsonArray = getAllGhIssues("tikv","pd");
        //System.out.println(jsonObject);
    }
    public static JsonElement doRestRequest(String requestURI) {

        //Object object = new Object();
        //requestURI = requestURI+"&page="+page+"&per_page=100";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestURI))
                .header("accept", "application/json")
                .header("authorization", "bearer " + ConfigConstant.GHTOKEN)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return new JsonParser().parse(response.body());
               // log.info("doRestRequest successfully {}",jsonObject.get("total_count").getAsInt());
            } else {
                log.info("doRestRequest failed : {}/{}",response.statusCode(),response.body());
            }
        } catch (Exception ec) {
            ec.printStackTrace();
        }
        return null;
    }
    public static JsonElement doRestRequest(String requestURI, String token) {

        //Object object = new Object();
        //requestURI = requestURI+"&page="+page+"&per_page=100";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestURI))
                .header("accept", "application/json")
                .header("authorization", "Bearer " + token)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return new JsonParser().parse(response.body());
                // log.info("doRestRequest successfully {}",jsonObject.get("total_count").getAsInt());
            } else {
                log.info("doRestRequest failed : {}/{}",response.statusCode(),response.body());
            }
        } catch (Exception ec) {
            ec.printStackTrace();
        }
        return null;
    }
    public static JsonObject getGhIssueById( String org, String repo, int id){
        String requestURI = String.format(ConfigConstant.ISSUE_REQUEST_URI, org, repo, id);
        //requestURI = requestURI+"&"+String.valueOf(id);
        JsonElement jsonElementIteration = doRestRequest(requestURI);
        return jsonElementIteration.getAsJsonObject();
    }
    public static JsonArray getAllGhIssues( String org, String repo){

        String firstRequestURI = String.format(ConfigConstant.GENERAL_SEARCH_URI,org, repo);
        //String searchRequestURI = String.format(ConfigConstant.GENERALSEARCHURI,org, repo);
        firstRequestURI = firstRequestURI +"&"+ ConfigConstant.PAGING_POSTFIX +  String.valueOf("1");
        JsonElement jsonElement = doRestRequest(firstRequestURI);
        JsonArray jsonArray = new JsonArray();
        if(jsonElement.isJsonObject()) {
            jsonArray.addAll(jsonElement.getAsJsonObject().get("items").getAsJsonArray());
        }
        int total_count = jsonElement.getAsJsonObject().get("total_count").getAsInt();
        int iteration = total_count/100;
        String requestURI = String.format(ConfigConstant.REST_REQUEST_URI, org,repo);
        for(int i=2; i<= iteration; i++) {
            String laterRequestURI = requestURI+"&"+ConfigConstant.PAGING_POSTFIX+String.valueOf(i);
           // log.info("request URI {}",laterRequestURI);
            JsonElement jsonElementIteration = doRestRequest(laterRequestURI);
          //  log.info("jsonElementIteration size {}, and first ID is {}, last ID is {}",jsonElementIteration.getAsJsonArray().size(), jsonElementIteration.getAsJsonArray().get(0).getAsJsonObject().get("number"),jsonElementIteration.getAsJsonArray().get(jsonElementIteration.getAsJsonArray().size()-1).getAsJsonObject().get("number"));
            jsonArray.addAll(jsonElementIteration.getAsJsonArray());
          //  log.info("getJsonArray size {}",jsonArray.size());

        }
        String lastRequestURI = requestURI+"&"+ConfigConstant.PAGING_POSTFIX + String.valueOf(iteration+1);
        JsonElement jsonLastPage = doRestRequest(lastRequestURI);
        jsonArray.addAll(jsonLastPage.getAsJsonArray());
        log.info(" {}/{} total objects: {} ", org, repo, jsonArray.size());
        return jsonArray;
    }
    public static JsonObject getGhIssuesCreatedAfter( String org, String repo,String date, boolean after){
        //TODO: fix logic
        JsonObject jsonObject = new JsonObject();
        String requestURI = String.format("https://api.github.com/search/issues?q=repo:%s/%s/+type:issue", org, repo);
        if(after) {
            requestURI = requestURI+"+created:%3e%3d"+date;
        }else {
            requestURI = requestURI+"+created:%3c%3d"+date;
        }
        jsonObject = doRestRequest(requestURI).getAsJsonObject();
        return jsonObject;
    }
    /*
     * @return jsonObject
     * @param after
     */
    public static JsonObject getGhIssuesUpdatedAfter( String org, String repo,String date, boolean after){
        //after is false, means before the date;
        //after is true, means after the date
        //todo fix logic
        JsonObject jsonObject = new JsonObject();
        String requestURI = String.format("https://api.github.com/search/issues?q=repo:%s/%s/+type:issue", org, repo);
        if(after) {
            requestURI = requestURI+"+updated:%3e%3d"+date;
        }else {
            requestURI = requestURI+"+updated:%3c%3d"+date;
        }
        jsonObject = doRestRequest(requestURI).getAsJsonObject();
        return jsonObject;
    }

}
