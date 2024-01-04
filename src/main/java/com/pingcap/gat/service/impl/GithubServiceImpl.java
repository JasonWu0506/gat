package com.pingcap.gat.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pingcap.gat.constant.ConfigConstant;
import com.pingcap.gat.mapper.GhDailyCountMapper;
import com.pingcap.gat.mapper.GithubIssueMapper;
import com.pingcap.gat.mapper.SprintPlanObjMapper;
import com.pingcap.gat.model.GhDailyCount;
import com.pingcap.gat.model.GithubIssue;
import com.pingcap.gat.model.SprintPlanObj;
import com.pingcap.gat.service.GithubService;
import com.pingcap.gat.util.DateUtil;
import com.pingcap.gat.util.GithubUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
public class GithubServiceImpl implements GithubService {

    private static final Logger log = LoggerFactory.getLogger(GithubServiceImpl.class);

    @Value("${com.pingcap.gat.repos}")
    private String repos;
    @Autowired
    private GithubIssueMapper githugIssueMapper;
    @Autowired
    private GhDailyCountMapper ghDailyCountMapper;

    @Autowired
    private SprintPlanObjMapper sprintPlanObjMapper;
    @Override
    public int insertIssue(GithubIssue githubIssue) {
        githugIssueMapper.insertIssues(githubIssue);
        return 0;
    }

    @Override
    public int updateIssue(GithubIssue githubIssue) {
        int error = githugIssueMapper.updateIssue(githubIssue);
        return error;
    }


    @Override
    public List<GithubIssue> searchForNewIssue(Date date) {
        List<GithubIssue> githubIssueList = new ArrayList<GithubIssue>();
        JsonArray jsonArray = (JsonArray) new JsonParser().parse(repos);
        Iterator<JsonElement> jsonElementIterator  = jsonArray.iterator();
        while(jsonElementIterator.hasNext()){
            JsonObject jsonObject = jsonElementIterator.next().getAsJsonObject();
            String org = jsonObject.get("org").getAsString();
            String repo = jsonObject.get("repo").getAsString();
            githubIssueList.addAll(GithubUtil.searchForNew(org, repo,date));
        }
        //
        return githubIssueList;
    }
    @Override
    public int searchForCloseCount(String org, String repo, String type, Date date) {

        int closePerRepo = GithubUtil.searchCloseCount(org,repo,type, date);

        return closePerRepo;
    }

    @Override
    public int searchForNewCount(String org, String repo, String type, Date date) {

        int createPerRepo = GithubUtil.searchNewCount(org,repo,type, date);
        return createPerRepo;
    }
    @Override
    public int searchForOpenCount(String org, String repo, String type, Date date) {
        int openPerRepo = GithubUtil.searchOpenCount(org,repo,type,date);
        return openPerRepo;
    }

    @Override
    public List<GithubIssue> searchForLegacyUpdate(Date date) {
        List<GithubIssue> githubIssueList = new ArrayList<GithubIssue>();
        JsonArray jsonArray = (JsonArray) new JsonParser().parse(repos);
        Iterator<JsonElement> jsonElementIterator  = jsonArray.iterator();
        while(jsonElementIterator.hasNext()){
            JsonObject jsonObject = jsonElementIterator.next().getAsJsonObject();
            String org = jsonObject.get("org").getAsString();
            String repo = jsonObject.get("repo").getAsString();
            githubIssueList.addAll(GithubUtil.searchForUpdate(org, repo,date));
        }
        return githubIssueList;
    }
    @Override
    public List<GithubIssue> searchForClose(Date date) {
        List<GithubIssue> githubIssueList = new ArrayList<GithubIssue>();
        JsonArray jsonArray = (JsonArray) new JsonParser().parse(repos);
        Iterator<JsonElement> jsonElementIterator  = jsonArray.iterator();
        while(jsonElementIterator.hasNext()){
            JsonObject jsonObject = jsonElementIterator.next().getAsJsonObject();
            String org = jsonObject.get("org").getAsString();
            String repo = jsonObject.get("repo").getAsString();
            githubIssueList.addAll(GithubUtil.searchForClose(org, repo,date));
        }
        return githubIssueList;
    }

    @Override
    public int updateLastModified() {
        int error = 0;
        try{
           error =  githugIssueMapper.udpateLastModifiedAll();
           List<Date> dateList = githugIssueMapper.getLastModifiedAt();
           log.info("updateLastModified succeedly to {}", DateUtil.formatDateString(dateList.get(0), ConfigConstant.DATE_FORMAT));

        }catch(Exception ec) {
            log.info("updateLastModified failed");
        }
        return error;
    }
    @Override
    public List<Date> getLastModifiedAt(){
        return githugIssueMapper.getLastModifiedAt();
    }

    @Override
    public List<GhDailyCount> getAllDaily() {
        List<GhDailyCount> ghDailyCountList = ghDailyCountMapper.getAllDaily();
        return ghDailyCountList;
    }


    @Override
    public int insertSprintPlanObj(SprintPlanObj sprintPlanObj){
        try {
            sprintPlanObjMapper.insertSprintPlanObj(sprintPlanObj);
        }catch(Exception ec){
            ec.printStackTrace();
        }
        return 0;
    }
    @Override
    public int updateSprintPlanObj(SprintPlanObj sprintPlanObj){
        try {
            sprintPlanObjMapper.updateBySprint(sprintPlanObj);
        }catch(Exception ec){
            ec.printStackTrace();
        }
        return 0;
    }
    @Override
    public List<Date> getLastModifiedAtFromDailyCount(String org, String repo, char type){
        try {
            List<Date> date = ghDailyCountMapper.select(org, repo, type);
            return date;
        }catch(Exception ec){
            ec.printStackTrace();
        }
        return null;
    }

    @Override
    public GithubIssue getGhIssueByLink(String link) {
        return githugIssueMapper.getGhIssueByLink(link);

    }
    public GithubIssue getGhIssueById(String repo, String org, int id) {
        return githugIssueMapper.getGhIssueById(repo,org, id);

    }
    public List<GithubIssue> searchClosedPRByTime(Date startDate, Date endDate) throws UnsupportedEncodingException {
        if(endDate == null){
            endDate = new Date();
        }
        List<GithubIssue> githubIssueList = GithubUtil.searchClosedPRByTime("pingcap","tidb", startDate,endDate);
        return  githubIssueList;
    }

    @Override
    public int updatePRTested(GithubIssue githubIssue) {
        int error = githugIssueMapper.updateIssue(githubIssue);
        log.info("updatePRTested {} succeeded!",githubIssue.toString());
        return error;
    }
    @Override
    public int updatePRBugfix(GithubIssue githubIssue) {
        int error = githugIssueMapper.updateIssue(githubIssue);
        log.info("updatePRBugfix {} succeeded!",githubIssue.toString());
        return error;
    }

    @Override
    public boolean isPRBugfix(String org, String repo, String bugId) {
        if(bugId == null || bugId.length() == 0 || bugId.indexOf("xxx")!=-1){
            return false;
        }
        String[] bugIds = bugId.split(",");
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
    @Override
    public List<GithubIssue> getGhIssueListByRepo(String repo) {
        List<GithubIssue> githubIssueList = githugIssueMapper.getGhIssueListByRepo(repo);
        return githubIssueList;

    }

}
