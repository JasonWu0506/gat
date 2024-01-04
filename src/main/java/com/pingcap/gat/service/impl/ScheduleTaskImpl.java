package com.pingcap.gat.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pingcap.gat.constant.ConfigConstant;
import com.pingcap.gat.mapper.GithubIssueMapper;
import com.pingcap.gat.model.GhDailyCount;
import com.pingcap.gat.model.GithubIssue;
import com.pingcap.gat.service.DailyCountService;
import com.pingcap.gat.service.GithubService;
import com.pingcap.gat.service.ScheduleTask;
import com.pingcap.gat.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
public class ScheduleTaskImpl implements ScheduleTask {

    @Autowired
    GithubIssueMapper githubIssueMapper;
    @Autowired
    GithubService githubService;

    @Autowired
    DailyCountService dailyCountService;
    private static final Logger log = LoggerFactory.getLogger(ScheduleTaskImpl.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");


    @Override
    @Scheduled(cron = "0 45 12 * * ?")//22：35:00 am everyday
    public int syncIssuesStatus() {
        List<Date> lastModified = githubService.getLastModifiedAt();
        List<GithubIssue> githubIssueList = githubService.searchForNewIssue(lastModified.get(0));
        log.info("grab {} newly created issues/pr since last sync",githubIssueList.size());
        boolean isNew = false;
        for(GithubIssue githubIssue:githubIssueList){
            try {
                int error = githubService.insertIssue(githubIssue);
                isNew = true;
                log.info("insert githubissue succeedly: {}", githubIssue.toString());
            }catch(Exception ec){
                log.info("insert githubissue failed: {}", githubIssue.toString());
            }finally {
                continue;
            }
        }
        boolean isUpdated= false;
        githubIssueList = githubService.searchForLegacyUpdate((lastModified.get(0)));
        log.info("grab {} updated issues/pr since last sync",githubIssueList.size());
        for(GithubIssue githubIssue:githubIssueList){
            try {
                int error = githubService.updateIssue(githubIssue);
                log.info("update githubissue succeedly: {}", githubIssue.toString());
                isUpdated = true;
            }catch(Exception ec){
                ec.printStackTrace();
                log.info("update githubissue failed: {}", githubIssue.toString());
            }finally {
                continue;
            }
        }
        if(isUpdated || isNew) {
            githubService.updateLastModified();
        }

        //todo to fix return value logic
        return 0;
    }
    @Value("${com.pingcap.gat.repos}")
    private String repos;
    @Override
    @Scheduled(cron = "0 50 12 * * ?")//22：35:00 am everyday
    public int syncDailyOpenClose() {

        JsonArray jsonArray = (JsonArray) new JsonParser().parse(repos);
        Iterator<JsonElement> jsonElementIterator  = jsonArray.iterator();
        while(jsonElementIterator.hasNext()) {
            JsonObject jsonObject = jsonElementIterator.next().getAsJsonObject();
            String org = jsonObject.get("org").getAsString();
            String repo = jsonObject.get("repo").getAsString();
            List<Date> lastmodified = githubService.getLastModifiedAtFromDailyCount(org,repo,'y');//issues
            GhDailyCount ghDailyCountIssue = new GhDailyCount();
            ghDailyCountIssue.setOrg(org);
            ghDailyCountIssue.setRepo(repo);
            if(lastmodified != null) {
                ghDailyCountIssue.setCreated(githubService.searchForNewCount(org, repo, "issue", lastmodified.get(lastmodified.size()-1)));
                ghDailyCountIssue.setClosed(githubService.searchForCloseCount(org, repo, "issue", lastmodified.get(lastmodified.size()-1)));
                ghDailyCountIssue.setOpen(githubService.searchForOpenCount(org, repo, "issue", lastmodified.get(lastmodified.size()-1)));
            }else{
                ghDailyCountIssue.setCreated(githubService.searchForNewCount(org, repo, "issue", DateUtil.getBeginOfCurrentUTCTime("null")));
                ghDailyCountIssue.setClosed(githubService.searchForCloseCount(org, repo, "issue", DateUtil.getBeginOfCurrentUTCTime("null")));
                ghDailyCountIssue.setOpen(githubService.searchForOpenCount(org, repo, "issue", DateUtil.getBeginOfCurrentUTCTime("null")));
                //ghDailyCountIssue.setTime(DateUtil.getBeginOfCurrentUTCTime("null"));
            }
            ghDailyCountIssue.setTime(DateUtil.getBeginOfCurrentUTCTime(DateUtil.formatDateString(new Date(),ConfigConstant.DATE_FORMAT)));
            ghDailyCountIssue.setType('y');//y for issue
            GhDailyCount ghDailyCountPR = new GhDailyCount();
            ghDailyCountPR.setOrg(org);
            ghDailyCountPR.setRepo(repo);
            lastmodified = githubService.getLastModifiedAtFromDailyCount(org,repo,'n');//PR
            if(lastmodified != null) {
                ghDailyCountPR.setCreated(githubService.searchForNewCount(org, repo, "pr", lastmodified.get(lastmodified.size()-1)));
                ghDailyCountPR.setClosed(githubService.searchForCloseCount(org, repo, "pr", lastmodified.get(lastmodified.size()-1)));
                ghDailyCountPR.setOpen(githubService.searchForOpenCount(org, repo, "pr", lastmodified.get(lastmodified.size()-1)));
               // ghDailyCountPR.setTime(DateUtil.getBeginOfCurrentUTCTime(DateUtil.formatDateString(new Date(),ConfigConstant.DATE_FORMAT)));
            }else{
                ghDailyCountPR.setCreated(githubService.searchForNewCount(org, repo, "pr", DateUtil.getBeginOfCurrentUTCTime("null")));
                ghDailyCountPR.setClosed(githubService.searchForCloseCount(org, repo, "pr", DateUtil.getBeginOfCurrentUTCTime("null")));
                ghDailyCountPR.setOpen(githubService.searchForOpenCount(org, repo, "pr", DateUtil.getBeginOfCurrentUTCTime("null")));
                //ghDailyCountPR.setTime(DateUtil.getBeginOfCurrentUTCTime("null"));


            }
            ghDailyCountPR.setTime(DateUtil.getBeginOfCurrentUTCTime(DateUtil.formatDateString(new Date(),ConfigConstant.DATE_FORMAT)));
            ghDailyCountPR.setType('n');//y for issue
            dailyCountService.insertDailyCount(ghDailyCountPR);
            dailyCountService.insertDailyCount(ghDailyCountIssue);
        }
        log.info("syncDailyOpenClose done successfully");
        return 0;
    }
}
