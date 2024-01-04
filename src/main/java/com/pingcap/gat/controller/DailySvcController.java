package com.pingcap.gat.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pingcap.gat.constant.ConfigConstant;
import com.pingcap.gat.model.GhDailyCount;
import com.pingcap.gat.model.GithubIssue;
import com.pingcap.gat.service.DailyCountService;
import com.pingcap.gat.service.GithubService;
import com.pingcap.gat.util.DateUtil;
import com.pingcap.gat.util.GithubUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@RestController
@RequestMapping("/daily")
public class DailySvcController {
    @Value("${com.pingcap.gat.repos}")
    private String repos;
    @Autowired
    DailyCountService dailyCountService;

    @Autowired
    GithubService githubService;
    private static final Logger log = LoggerFactory.getLogger(DailySvcController.class);

    @RequestMapping("/getDailyCount")
    public Map<String, Object> getDailyCount(@RequestParam("orgBug") String org, @RequestParam("repoBug") String repo){
        Map<String, Map<String, List<Integer>>> allBugStatistics = new HashMap<>();
        Map<String, List<Integer>> repoBugStatistics = new HashMap<>(0);
        Map<String, Object> mapRet = new HashMap<>();
        if( (org.compareTo("all")==0) || (repo.compareTo("all")==0)){
            JsonArray jsonArray = (JsonArray) new JsonParser().parse(repos);
            Iterator<JsonElement> jsonElementIterator  = jsonArray.iterator();
            while(jsonElementIterator.hasNext()) {
                JsonObject jsonObject = jsonElementIterator.next().getAsJsonObject();
                String orgLocal = jsonObject.get("org").getAsString();
                String repolocal = jsonObject.get("repo").getAsString();
                repoBugStatistics = dailyCountService.getDailyCountByRepo(orgLocal, repolocal);
                allBugStatistics.put(repolocal, repoBugStatistics);
            }
        }else {
            repoBugStatistics = dailyCountService.getDailyCountByRepo(org, repo);
            allBugStatistics.put(repo, repoBugStatistics);
        }
        List<String> dates = dailyCountService.getDatesForDailyBugs();
        mapRet.put("allBugStatistics", allBugStatistics);
        mapRet.put("dates", dates);
        return mapRet; // Return the chart.html template
    }
    @RequestMapping("/syncDailyTrend")
    public ModelAndView syncDailyTrend(){
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
            ghDailyCountIssue.setTime(DateUtil.getBeginOfCurrentUTCTime(DateUtil.formatDateString(new Date(), ConfigConstant.DATE_FORMAT)));
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
            log.info("dialyCountService.isDailyCountExisted(PR) {}", ghDailyCountPR);
            log.info("dialyCountService.isDailyCountExisted(Issue) {}", ghDailyCountIssue);
            if(dailyCountService.isDailyCountExisted(ghDailyCountPR)!=0) {
                log.info("do update dialy count");
                dailyCountService.updateDailyCount(ghDailyCountPR);
                dailyCountService.updateDailyCount(ghDailyCountIssue);
                log.info("done with update dialy count");

            }else{
                log.info("do insert dialy count");
                dailyCountService.insertDailyCount(ghDailyCountPR);
                dailyCountService.insertDailyCount(ghDailyCountIssue);
                log.info("done insert dialy count");
            }
        }
        log.info("syncDailyOpenClose done successfully");
        return null;
    }


    @RequestMapping("/syncDailyUpdate")
    public ModelAndView syncDailyUpdate(@RequestParam("orgDailySync") String org,  @RequestParam("repoDailySync")String repo){

        StringBuffer strLog = new StringBuffer();
        List<Date> lastModified = githubService.getLastModifiedAt();
        List<GithubIssue> githubIssueList = githubService.searchForNewIssue(lastModified.get(0));
        strLog.append("Found "+ githubIssueList.size()+" newly created issues/pr since last sync <br>");
        log.info("grab {} newly created issues/pr since last sync",githubIssueList.size());
        boolean isNew = false;
        for(GithubIssue githubIssue:githubIssueList){
            try {
                int error = githubService.insertIssue(githubIssue);
                isNew = true;
                strLog.append("insert githubissue succeedly: "+ githubIssue.toString()+"<br>");
                log.info("insert githubissue succeedly: {}", githubIssue.toString());
            }catch(Exception ec){
                ec.printStackTrace();
                strLog.append("insert githubissue failed: "+ githubIssue.toString()+"<br>");
                log.info("insert githubissue failed: {}", githubIssue.toString());
            }finally {
                continue;
            }
        }
        ModelAndView modelAndView = new ModelAndView("syncDaily"); // Specify the view name
        modelAndView.addObject("createdDataList",githubIssueList);
        boolean isUpdated= false;
        githubIssueList = githubService.searchForLegacyUpdate((lastModified.get(0)));
        strLog.append("Found "+ githubIssueList.size()+" updated issues/pr since last sync <br>");
        log.info("grab {} updated issues/pr since last sync",githubIssueList.size());
        for(GithubIssue githubIssue:githubIssueList){
            try {
                int error = githubService.updateIssue(githubIssue);
                strLog.append("update githubissue succeedly: "+ githubIssue.toString()+"<br>");
                log.info("update githubissue succeedly: {}", githubIssue.toString());
                isUpdated = true;
            }catch(Exception ec){
                ec.printStackTrace();
                strLog.append("update githubissue failed: "+ githubIssue.toString()+"<br>");
                log.info("update githubissue failed: {}", githubIssue.toString());
            }finally {
                continue;
            }
        }
        if(isUpdated || isNew) {
            githubService.updateLastModified();
        }
        strLog.append("sync tasks is done<br>");

        modelAndView.addObject("updatedDataList",githubIssueList);

        return modelAndView;
    }
    @RequestMapping("/updateByRepo")
    public ModelAndView syncDailyUpdate( @RequestParam("repo")String repo){
        List<GithubIssue> githubIssueList = githubService.getGhIssueListByRepo(repo);
        for(GithubIssue githubIssue:githubIssueList){
            GithubIssue githubIssueFromCloud = GithubUtil.getGhIssueById("tikv", repo,githubIssue.getId());
            //if(githubIssueFromCloud.isDiff(githubIssue)) {
                githubService.updateIssue(githubIssueFromCloud);
                log.info("update isssueby Repo with diff:" +githubIssueFromCloud);
         //   }else{
             //   log.info("githubcloud: "+githubIssueFromCloud.toString());
             //   log.info("githublocal: "+githubIssue.toString());
          //  }

        }
        ModelAndView modelAndView = new ModelAndView("updateByRepo"); // Specify the view name

        modelAndView.addObject("updatedDataList",githubIssueList);
        return modelAndView;
    }
}
