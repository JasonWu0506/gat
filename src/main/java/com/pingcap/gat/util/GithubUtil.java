package com.pingcap.gat.util;

import com.google.gson.*;
import com.pingcap.gat.constant.ConfigConstant;
import com.pingcap.gat.mapper.GithubIssueMapper;
import com.pingcap.gat.model.GhDailyCount;
import com.pingcap.gat.model.GithubIssue;
import com.pingcap.gat.model.SprintPlanObj;
import com.pingcap.gat.service.GithubService;
import com.pingcap.gat.service.impl.ScheduleTaskImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GithubUtil {
    private static final Logger log = LoggerFactory.getLogger(GithubUtil.class);
    public static void main(String[] args) throws UnsupportedEncodingException {
       //List<GithubIssue> githubIssueList =
        GithubIssue githubIssue = getGhIssueById("tikv","pd", 7272);

    }

    public static GithubIssue getGhIssueById(String org, String repo, int id){

        JsonObject jsonObject = HttpUtil.getGhIssueById(org,repo,id);
        GithubIssue githubIssue = convertToGithubIssue(org, repo, jsonObject);
        return githubIssue;

    }
    private static GithubIssue convertToGithubIssue(String org, String repo, JsonObject jsonObject){

        GithubIssue githubIssue = new GithubIssue();
        githubIssue.setId(jsonObject.get("number").getAsInt());
        githubIssue.setAuthor(jsonObject.get("user").getAsJsonObject().get("login").getAsString());
        githubIssue.setCreatedAt(DateUtil.getDate(jsonObject.get("created_at").getAsString(),ConfigConstant.DATE_FORMAT));
        githubIssue.setLink(jsonObject.get("html_url").getAsString());
        githubIssue.setRepo(repo);
        githubIssue.setOrg(org);
        githubIssue.setSeverity(getIssueSeverity(jsonObject.get("labels").getAsJsonArray()));

        if(isIssue(jsonObject)) {
            githubIssue.setIsIssue('y');
        } else{
            githubIssue.setIsIssue('n');
            githubIssue.setSize(getPRSize(jsonObject.get("labels").getAsJsonArray()));
            boolean isJsonNull = jsonObject.get("pull_request").isJsonNull();
            if(isJsonNull) githubIssue.setMergedAt(DateUtil.getDate(ConfigConstant.NULL_DATE,ConfigConstant.DATE_FORMAT));
            else {
                if(jsonObject.get("pull_request").getAsJsonObject().get("merged_at") == null) {
                    githubIssue.setMergedAt(DateUtil.getDate(ConfigConstant.NULL_DATE,ConfigConstant.DATE_FORMAT));
                }else {
                    JsonElement jsonElement = jsonObject.get("pull_request").getAsJsonObject().get("merged_at");
                    if(jsonElement.isJsonNull()) githubIssue.setMergedAt(DateUtil.getDate(ConfigConstant.NULL_DATE,ConfigConstant.DATE_FORMAT));
                    else
                    githubIssue.setMergedAt(DateUtil.getDate(jsonObject.get("pull_request").getAsJsonObject().get("merged_at").getAsString(),ConfigConstant.DATE_FORMAT));
                }
            }
            if(!jsonObject.get("body").isJsonNull() ) {
                githubIssue.setAssociatedIssues(getIssueNumber(jsonObject.get("body").getAsString()));
                String body = jsonObject.get("body").getAsString();
                boolean isPRTested = isPRTested(body);
                githubIssue.setPRTested(isPRTested);

            }
            //parse whether a bugfix
            githubIssue.setPRBugfix(isPRBugfix(org, repo, githubIssue.getAssociatedIssues()));
            //end of parsing
        }
        githubIssue.setIssueState(jsonObject.get("state").getAsString());
        JsonElement jsonElement = jsonObject.get("closed_at");
        boolean isJsonNull = jsonElement.isJsonNull();
        if(isJsonNull) githubIssue.setClosedAt(DateUtil.getDate(ConfigConstant.NULL_DATE, ConfigConstant.DATE_FORMAT));
        else githubIssue.setClosedAt(DateUtil.getDate(jsonObject.get("closed_at").getAsString(),ConfigConstant.DATE_FORMAT));
        jsonElement = jsonObject.get("updated_at");
        isJsonNull = jsonElement.isJsonNull();
        if(isJsonNull) githubIssue.setUpdatedAt(DateUtil.getDate(ConfigConstant.NULL_DATE,ConfigConstant.DATE_FORMAT));
        else githubIssue.setUpdatedAt(DateUtil.getDate(jsonObject.get("updated_at").getAsString(),ConfigConstant.DATE_FORMAT));
        githubIssue.setIssueType(getIssueType(jsonObject.get("labels").getAsJsonArray()));

        githubIssue.setSig(getIssueSig(jsonObject.get("labels").getAsJsonArray()));
        githubIssue.setComponent(getIssueComponent(jsonObject.get("labels").getAsJsonArray()));
        jsonElement = jsonObject.get("assignee");
        isJsonNull = jsonElement.isJsonNull();
        if(isJsonNull) githubIssue.setAssignee("null");
        else githubIssue.setAssignee(jsonObject.get("assignee").getAsJsonObject().get("login").getAsString());
        return githubIssue;
    }
    public static boolean isPRBugfix(String org, String repo, String associatedBug) {
        if(associatedBug == null || associatedBug.length() == 0 || associatedBug.indexOf("xxx")!=-1){
            return false;
        }
        String[] bugIds = associatedBug.split(",");
        for(String bug: bugIds){
            try{
                bug = bug.trim();
                String link = String.format("https://github.com/%s/%s/issues/%d", org,repo, Integer.parseInt(bug));
                GithubIssue githubIssue = getGhIssueByLink(link);
                // if(githubIssue.isIsIssue() == 'y') continue;
                if(githubIssue.getIssueType() == null || githubIssue.getIssueType().length() == 0) continue;
                boolean bugfix = githubIssue.getIssueType().indexOf("type/bug")==-1? false:true;
                if(bugfix) return bugfix;
            }
            catch(Exception ec){
                ec.printStackTrace();
            }
        }
        return false;
    }
    public static List<GithubIssue> getAllGHIssues(String org, String repo){
        List<GithubIssue> githubIssueList = new ArrayList<GithubIssue>();
        JsonArray jsonArray = HttpUtil.getAllGhIssues(org,repo);
        //JsonArray jsonArray = jsonObject.get("items").getAsJsonArray();
        Iterator iterator = jsonArray.iterator();
        while(iterator.hasNext()) {
            JsonObject jsonIssue = (JsonObject) iterator.next();
            GithubIssue githubIssue = convertToGithubIssue(org, repo, jsonIssue);
            githubIssueList.add(githubIssue);

        }
        return githubIssueList;
    }

    public static boolean isIssue(JsonObject jsonObject){
        boolean isIssue = true;
        JsonElement jsonElement = jsonObject.get("pull_request");
        if(jsonElement != null) {
            isIssue = false;
        }
        return isIssue;
    }
    public static String getPRSize(JsonArray jsonArray){
        return getIssueLabels(jsonArray,"size/");
    }
    public static String getIssueComponent(JsonArray jsonArray){
      return getIssueLabels(jsonArray,"component/");
    }
    public static String getIssueSeverity(JsonArray jsonArray){
        return getIssueLabels(jsonArray,"severity/");
    }
    public static String getIssueLabels(JsonArray jsonArray, String pattern){
        String labels = "";
        Iterator<JsonElement> labelElementIterator = jsonArray.iterator();
        while(labelElementIterator.hasNext()){
            JsonObject jsonObject = (JsonObject) labelElementIterator.next();
            int index = jsonObject.get("name").getAsString().indexOf(pattern);
            if(index != -1) {
                labels = labels + jsonObject.get("name").getAsString()+", ";
            }
        }
        if(labels.endsWith(", ")) labels = labels.substring(0,labels.length()-2);
        return labels;
    }
    public static String getIssueSig(JsonArray jsonArray){
        return getIssueLabels(jsonArray,"sig/");
    }

    public static String getIssueType(JsonArray jsonArray){
        return getIssueLabels(jsonArray,"type/");
    }
    public static boolean isPRTested(String body){
       // System.out.println("body");
        boolean isUnitTest = false;
        boolean isIntTest = false;
        boolean isManualTest = false;
        String[] lines = body.split("\r\n\r\n");
        String UNIT_TEST_CHECK="-[x]unittest";
        String INT_TEST_CHECK="-[x]integrationtest";
        String MANUAL_TEST_CHECK="-[x]manualtest";
        //String NO_CODE_CHECK="-[x]nocode";
        for(String line:lines){
            line = line.trim().toLowerCase();
            line = line.replaceAll("\\s", "");
            if(line.indexOf(UNIT_TEST_CHECK)!=-1) isUnitTest=true;
            if(line.indexOf(INT_TEST_CHECK)!=-1) isIntTest = true;
            if(line.indexOf(MANUAL_TEST_CHECK)!=-1) isManualTest = true;
            if(isManualTest || isIntTest ||isUnitTest) break;
        }
        return isManualTest || isIntTest ||isUnitTest;
    }
    public static String getIssueNumber(String body) {
        String issueNumber = "";
        String[] lines = body.split("\r\n");
       // System.out.println(Arrays.asList(lines));
        for(String line:lines) {
            line = line.trim().toLowerCase();
            line = line.replaceAll("\r\n"," ");

            if(line.startsWith("issue number")){
                Pattern pattern = Pattern.compile("(#\\d+)");
                Matcher matcher = pattern.matcher(line);
                while (matcher.find())
                {
                  issueNumber = issueNumber + matcher.group(1).substring(1)+", ";
                }
                pattern = Pattern.compile("(#[x]+)");
                matcher = pattern.matcher(line);
                while (matcher.find())
                {
                    issueNumber = issueNumber + matcher.group(1).substring(1)+", ";
                }
                pattern = Pattern.compile("(https://.+?)/.([0-9]+)");
                matcher = pattern.matcher(line);
                while (matcher.find())
                {//grab group 0
                    String str = matcher.group(0);
                    str = str.substring(str.lastIndexOf("/")+1,str.length());
                    issueNumber = issueNumber + str+", ";
                }
            }
        }
        if(issueNumber.endsWith(", ")) issueNumber = issueNumber.substring(0,issueNumber.length()-2);
        return issueNumber;
    }
    public static List<GithubIssue> searchForNew(String org, String repo, Date date){
        List<GithubIssue> githubIssueList = new ArrayList<GithubIssue>();
       // String requestURI = String.format(ConfigConstant.SEARCHCREATEDURI"https://api.github.com/search/issues?q=repo:%s/%s/+created:",org, repo);
        String requestURI = String.format(ConfigConstant.SEARCH_CREATED_URI,org, repo);
        requestURI = requestURI+"%3e="+DateUtil.formatDateString(date, ConfigConstant.DATE_FORMAT)+"&"+ConfigConstant.PAGING_POSTFIX;
        String firstRequestURI = requestURI+"1";
        JsonElement jsonElement = HttpUtil.doRestRequest(firstRequestURI);
        JsonObject jsonObject = new JsonObject();
        if(jsonElement.isJsonObject()) jsonObject = jsonElement.getAsJsonObject();
        //JsonArray jsonArray = jsonObject.get("items").getAsJsonArray();
        int total_count = jsonObject.get("total_count").getAsInt();
        JsonArray jsonArray = jsonObject.get("items").getAsJsonArray();
        if(total_count<1000 & total_count >100){
            int iteration = total_count/100;
            for(int i=2; i<= iteration; i++) {
                String laterRequestURI = requestURI+String.valueOf(i);
                JsonElement jsonElementIteration = HttpUtil.doRestRequest(laterRequestURI);
                jsonArray.addAll(jsonElementIteration.getAsJsonObject().get("items").getAsJsonArray());
            }
            String lastRequestURI = requestURI + String.valueOf(iteration+1);
            JsonElement jsonLastPage = HttpUtil.doRestRequest(lastRequestURI);
            //log.info("new grab # in  iteration "+ (iteration+1) + "\t"+ jsonLastPage.getAsJsonArray().size());
            jsonArray.addAll(jsonLastPage.getAsJsonObject().get("items").getAsJsonArray());
            //jsonObject.get("items").getAsJsonArray().addAll(jsonLastPage.getAsJsonArray());
        }
        for(int i=0;i< jsonArray.size();i++){
            githubIssueList.add(convertToGithubIssue(org, repo,jsonArray.get(i).getAsJsonObject()));
        }
        return githubIssueList;
    }
    public static int searchNewCount(String org, String repo, String type, Date date){
        String requestURI = String.format(ConfigConstant.SEARCH_CREATED_BYTYPE_URI,org, repo, type);
        requestURI = requestURI+"%3e="+DateUtil.formatDateString(date, ConfigConstant.DATE_FORMAT)+"&"+ConfigConstant.PAGING_POSTFIX;
        String firstRequestURI = requestURI+"1";
        JsonElement jsonElement = HttpUtil.doRestRequest(firstRequestURI);
        log.info("org is {}, repo is {}, type is {} ", org,repo,type);
        if(jsonElement != null){
            int total_count = jsonElement.getAsJsonObject().get("total_count").getAsInt();
            return total_count;
        }else return 0;

    }
    public static int searchCloseCount(String org, String repo, String type, Date date){
        String requestURI = String.format(ConfigConstant.SEARCH_CLOSED_BYTYPE_URI,org, repo, type);
        requestURI = requestURI+"%3e="+DateUtil.formatDateString(date, ConfigConstant.DATE_FORMAT)+"&"+ConfigConstant.PAGING_POSTFIX;
        String firstRequestURI = requestURI+"1";
        JsonElement jsonElement = HttpUtil.doRestRequest(firstRequestURI);
        if(jsonElement != null){
            int total_count = jsonElement.getAsJsonObject().get("total_count").getAsInt();
            return total_count;
        }else return 0;
    }
    public static int searchOpenCount(String org, String repo, String type, Date date){
        String requestURI = String.format(ConfigConstant.SEARCH_OPEN_BYTYPE_URI,org, repo, type);
       // requestURI = requestURI+"%3e="+DateUtil.formatDateString(date, ConfigConstant.DATE_FORMAT)+"&"+ConfigConstant.PAGING_POSTFIX;
       // String firstRequestURI = requestURI+"1";
        JsonElement jsonElement = HttpUtil.doRestRequest(requestURI);
        //int total_count = jsonElement.getAsJsonObject().get("total_count").getAsInt();
        if(jsonElement != null){
            int total_count = jsonElement.getAsJsonObject().get("total_count").getAsInt();
            return total_count;
        }else return 0;
    }

    public static List<GithubIssue> searchForUpdate(String org, String repo, Date date){
        List<GithubIssue> githubIssueList = new ArrayList<GithubIssue>();
        // String requestURI = String.format(ConfigConstant.SEARCHCREATEDURI"https://api.github.com/search/issues?q=repo:%s/%s/+created:",org, repo);
        String requestURI = String.format(ConfigConstant.SEARCH_UPDATED_URI,org, repo);
        requestURI = requestURI+"%3e="+DateUtil.formatDateString(date, ConfigConstant.DATE_FORMAT)+"&"+ConfigConstant.PAGING_POSTFIX;
        String firstRequestURI = requestURI+"1";
        JsonElement jsonElement = HttpUtil.doRestRequest(firstRequestURI);
        JsonObject jsonObject = new JsonObject();
        if(jsonElement.isJsonObject()) jsonObject = jsonElement.getAsJsonObject();
        //JsonArray jsonArray = jsonObject.get("items").getAsJsonArray();
        int total_count = jsonObject.get("total_count").getAsInt();
        JsonArray jsonArray = jsonObject.get("items").getAsJsonArray();
        if(total_count<1000 & total_count >100){
            int iteration = total_count/100;
            for(int i=2; i<= iteration; i++) {
                String laterRequestURI = requestURI+String.valueOf(i);
                JsonElement jsonElementIteration = HttpUtil.doRestRequest(laterRequestURI);
                jsonArray.addAll(jsonElementIteration.getAsJsonObject().get("items").getAsJsonArray());
            }
            String lastRequestURI = requestURI + String.valueOf(iteration+1);
            JsonElement jsonLastPage = HttpUtil.doRestRequest(lastRequestURI);
            //log.info("new grab # in  iteration "+ (iteration+1) + "\t"+ jsonLastPage.getAsJsonArray().size());
            jsonArray.addAll(jsonLastPage.getAsJsonObject().get("items").getAsJsonArray());
            //jsonObject.get("items").getAsJsonArray().addAll(jsonLastPage.getAsJsonArray());
        }
        for(int i=0;i< jsonArray.size();i++){
            githubIssueList.add(convertToGithubIssue(org, repo,jsonArray.get(i).getAsJsonObject()));
        }
        return githubIssueList;
    }
    public static List<GithubIssue> searchForClose(String org, String repo, Date date){
        List<GithubIssue> githubIssueList = new ArrayList<GithubIssue>();
        // String requestURI = String.format(ConfigConstant.SEARCHCREATEDURI"https://api.github.com/search/issues?q=repo:%s/%s/+created:",org, repo);
        String requestURI = String.format(ConfigConstant.SEARCH_CLOSED_URI,org, repo);
        requestURI = requestURI+"%3e="+DateUtil.formatDateString(date, ConfigConstant.DATE_FORMAT)+"&"+ConfigConstant.PAGING_POSTFIX;
        String firstRequestURI = requestURI+"1";
        JsonElement jsonElement = HttpUtil.doRestRequest(firstRequestURI);
        JsonObject jsonObject = new JsonObject();
        if(jsonElement.isJsonObject()) jsonObject = jsonElement.getAsJsonObject();
        //JsonArray jsonArray = jsonObject.get("items").getAsJsonArray();
        int total_count = jsonObject.get("total_count").getAsInt();
        JsonArray jsonArray = jsonObject.get("items").getAsJsonArray();
        if(total_count<1000 & total_count >100){
            int iteration = total_count/100;
            for(int i=2; i<= iteration; i++) {
                String laterRequestURI = requestURI+String.valueOf(i);
                JsonElement jsonElementIteration = HttpUtil.doRestRequest(laterRequestURI);
                jsonArray.addAll(jsonElementIteration.getAsJsonObject().get("items").getAsJsonArray());
            }
            String lastRequestURI = requestURI + String.valueOf(iteration+1);
            JsonElement jsonLastPage = HttpUtil.doRestRequest(lastRequestURI);
            //log.info("new grab # in  iteration "+ (iteration+1) + "\t"+ jsonLastPage.getAsJsonArray().size());
            jsonArray.addAll(jsonLastPage.getAsJsonObject().get("items").getAsJsonArray());
            //jsonObject.get("items").getAsJsonArray().addAll(jsonLastPage.getAsJsonArray());
        }
        for(int i=0;i< jsonArray.size();i++){
            githubIssueList.add(convertToGithubIssue(org, repo,jsonArray.get(i).getAsJsonObject()));
        }
        return githubIssueList;
    }
    public static GithubIssue getGhIssueByLink(String link){
        String[] tokens = link.split("/");
        String org = tokens[3];
        String repo = tokens[4];
        int id = Integer.parseInt(tokens[6]);
        String requestURI = String.format(ConfigConstant.ISSUE_REQUEST_URI,org,repo,id);
        JsonElement jsonElement = HttpUtil.doRestRequest(requestURI);
        GithubIssue githubIssue = convertToGithubIssue(org, repo, jsonElement.getAsJsonObject());
        return githubIssue;
    }

    public static GithubIssue getGhIssueBySeverity(String org, String repo, String severity){
        GithubIssue githubIssue = new GithubIssue();
//        String requestURI = String.format(ConfigConstant.ISSUE_REQUEST_URI,org,repo,id);
//        JsonElement jsonElement = HttpUtil.doRestRequest(requestURI);
//        GithubIssue githubIssue = convertToGithubIssue(org, repo, jsonElement.getAsJsonObject());
       return githubIssue;
        }

    public static SprintPlanObj convertToSprintPlanObj(GithubIssue githubIssue, char type){
        SprintPlanObj sprintPlanObj = new SprintPlanObj();
        sprintPlanObj.setId(githubIssue.getId());
        sprintPlanObj.setLink(githubIssue.getLink());
        sprintPlanObj.setOrg(githubIssue.getOrg());
        sprintPlanObj.setRepo(githubIssue.getRepo());
        sprintPlanObj.setState(githubIssue.getIssueState());
        sprintPlanObj.setSprint("sprint19");
        sprintPlanObj.setType(type);//1 for github open issues, 0 for oncall issues
        sprintPlanObj.setId(githubIssue.getId());
        sprintPlanObj.setClosed_at(githubIssue.getClosedAt());
        sprintPlanObj.setLabel(githubIssue.getIssueType());
        sprintPlanObj.setLabel(githubIssue.getComponent());
        sprintPlanObj.setComponent(githubIssue.getComponent());
        sprintPlanObj.setOncallid("null");
        sprintPlanObj.setSig(githubIssue.getSig());
        return sprintPlanObj;
    }
    public static String constructClosedPRRequest(String org, String repo, Date startDate, Date endDate, int page) throws UnsupportedEncodingException {
        String uri = "";
        String requestURI = String.format(ConfigConstant.SEARCH_CLOSED_PR_SINCE_URI,100, page);
        String startDateStr = DateUtil.formatDateString(startDate,ConfigConstant.DATE_FORMAT);
        String endDateStr = DateUtil.formatDateString(endDate,ConfigConstant.DATE_FORMAT);
        String queryURI = String.format(ConfigConstant.SEARCH_CLOSED_PR_SINCE_QUERY, org,repo,startDateStr,endDateStr);
        queryURI = queryURI.replace(">","%3e");
        queryURI = queryURI.replace("<","%3c");
        return requestURI+queryURI;
    }
    public static List<GithubIssue> searchClosedPRByTime(String org, String repo, Date startDate, Date endDate)throws UnsupportedEncodingException{
        List<GithubIssue> githubIssueList = new ArrayList<>();
        String requestURI = constructClosedPRRequest(org, repo, startDate,endDate, 1);
        JsonElement jsonElement = HttpUtil.doRestRequest(requestURI);
        JsonObject jsonObject = new JsonObject();
        if(jsonElement.isJsonObject()) jsonObject = jsonElement.getAsJsonObject();
        //JsonArray jsonArray = jsonObject.get("items").getAsJsonArray();
        int total_count = jsonObject.get("total_count").getAsInt();
        JsonArray jsonArray = jsonObject.get("items").getAsJsonArray();
        String lastCreatedTime = jsonArray.get(jsonArray.size()-1).getAsJsonObject().get("created_at").getAsString();
        if(total_count >1000){
            int requestIteration = total_count/1000;
            int lastCount =  total_count-requestIteration*1000;
            for(int i=0;i<requestIteration;i++){
                if(i == 0) {
                    for(int j=1; j<= 9; j++) {
                        String laterRequestURI = constructClosedPRRequest(org,repo,startDate,endDate,j+1);

                        JsonElement jsonElementIteration = HttpUtil.doRestRequest(laterRequestURI);
                        String first = jsonElementIteration.getAsJsonObject().get("items").getAsJsonArray().get(0).getAsJsonObject().get("number").getAsString();
                        String last = jsonElementIteration.getAsJsonObject().get("items").getAsJsonArray().get(99).getAsJsonObject().get("number").getAsString();
                        System.out.println(String.format("Iteration: %d\t inner Iteration: %d\t Request: %s\t 1st: %s\t last: %s\t", i,j,laterRequestURI,first,last));
                        jsonArray.addAll(jsonElementIteration.getAsJsonObject().get("items").getAsJsonArray());
                    }
                    lastCreatedTime = jsonArray.get(jsonArray.size()-1).getAsJsonObject().get("created_at").getAsString();
                    System.out.println("lastCreatedTime is: "+lastCreatedTime);
                }else {
                    //change request URI
                    for(int j=0;j<=9;j++){
                        Date start = DateUtil.getDate(lastCreatedTime,ConfigConstant.DATE_FORMAT);
                        start.setTime(start.getTime()+1000);//move 1 sec'

                        String laterRequestURI = constructClosedPRRequest(org,repo,start,endDate,j+1);
                        JsonElement jsonElementIteration = HttpUtil.doRestRequest(laterRequestURI);
                        String first = jsonElementIteration.getAsJsonObject().get("items").getAsJsonArray().get(0).getAsJsonObject().get("number").getAsString();
                        String last = jsonElementIteration.getAsJsonObject().get("items").getAsJsonArray().get(99).getAsJsonObject().get("number").getAsString();
                        System.out.println(String.format("Iteration: %d\t inner Iteration: %d\t Request: %s\t 1st: %s\t last: %s\t", i,j,laterRequestURI,first,last));
                        jsonArray.addAll(jsonElementIteration.getAsJsonObject().get("items").getAsJsonArray());
                    }
                    lastCreatedTime = jsonArray.get(jsonArray.size()-1).getAsJsonObject().get("created_at").getAsString();
                    System.out.println("lastCreatedTime is: "+lastCreatedTime);
                }
            }
            if(lastCount>0){
                int iteration = lastCount/100;
                for(int i=0; i<= iteration; i++) {
                    Date start = DateUtil.getDate(lastCreatedTime,ConfigConstant.DATE_FORMAT);
                    start.setTime(start.getTime()+1000);//move 1 sec'
                    String laterRequestURI = constructClosedPRRequest(org,repo,start,endDate,i+1);

                    JsonElement jsonElementIteration = HttpUtil.doRestRequest(laterRequestURI);
                    String first = jsonElementIteration.getAsJsonObject().get("items").getAsJsonArray().get(0).getAsJsonObject().get("number").getAsString();
                    String last = jsonElementIteration.getAsJsonObject().get("items").getAsJsonArray().get(jsonElementIteration.getAsJsonObject().get("items").getAsJsonArray().size()-1).getAsJsonObject().get("number").getAsString();
                    System.out.println(String.format("Iteration: %d\t inner Iteration: %d\t Request: %s\t 1st: %s\t last: %s\t", -1,i,laterRequestURI,first,last));
                    jsonArray.addAll(jsonElementIteration.getAsJsonObject().get("items").getAsJsonArray());
                }
                String lastRequestURI = requestURI + String.valueOf(iteration+1);
                JsonElement jsonLastPage = HttpUtil.doRestRequest(lastRequestURI);
                jsonArray.addAll(jsonLastPage.getAsJsonObject().get("items").getAsJsonArray());
            }
        } else if(total_count <1000 & total_count> 100){
            int iteration = total_count/100;
            for(int i=2; i<= iteration; i++) {
                String laterRequestURI = requestURI+String.valueOf(i);
                JsonElement jsonElementIteration = HttpUtil.doRestRequest(laterRequestURI);
                jsonArray.addAll(jsonElementIteration.getAsJsonObject().get("items").getAsJsonArray());
            }
            String lastRequestURI = requestURI + String.valueOf(iteration+1);
            JsonElement jsonLastPage = HttpUtil.doRestRequest(lastRequestURI);
            jsonArray.addAll(jsonLastPage.getAsJsonObject().get("items").getAsJsonArray());
        }
        System.out.println(jsonArray.size());
        for(int i=0;i< jsonArray.size();i++){
            githubIssueList.add(convertToGithubIssue(org, repo,jsonArray.get(i).getAsJsonObject()));
        }
        return githubIssueList;
    }

}
