package com.pingcap.gat.controller;

import com.pingcap.gat.mapper.GithubIssueMapper;
import com.pingcap.gat.model.GithubIssue;
import com.pingcap.gat.model.SprintPlanObj;
import com.pingcap.gat.service.GithubService;
import com.pingcap.gat.util.ExcelUtil;
import com.pingcap.gat.util.GithubUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/sprint")
public class SprintSvcController {

    @Autowired
    GithubService githubService;

    private static final Logger log = LoggerFactory.getLogger(SprintSvcController.class);

    @RequestMapping("/updateSprintGoal")
    public List<SprintPlanObj> updateSprintGoal(String sprint){
        String fileName = "all-major-issue.xlsx";
        List<SprintPlanObj> sprintPlanObjs = new ArrayList<>();
        Set<String> links = new ExcelUtil().readFile(fileName);
        int count = 0;
        for(String link: links){
            GithubIssue githubIssue = githubService.getGhIssueByLink(link);
            SprintPlanObj sprintPlan = GithubUtil.convertToSprintPlanObj(githubIssue,'1');
            int error = 0;
            try{
                error = githubService.updateSprintPlanObj(sprintPlan);
                log.info("updateSprintPlanObj: {}",sprintPlan.toString());
                count ++;
                sprintPlanObjs.add(sprintPlan);
            }catch(Exception ec){
                log.info("update {} as sprintgoal failed"+ sprintPlan.toString());
            }

        }
        log.info("update {} as sprintgoal succeeded!", count);

        return sprintPlanObjs;
    }
    @RequestMapping("/setSprintGoal")
    public List<SprintPlanObj> setSprintGoal(String sprint){
        String fileName = "/Users/jasonwu/Documents/all-major-issue.xlsx";
        List<SprintPlanObj> sprintPlanObjs = new ArrayList<>();
        Set<String> links = new ExcelUtil().readFile(fileName);
        log.info("sprint 19 total: {}",links.size());
        int count = 0;
        for(String link: links){
            GithubIssue githubIssue = githubService.getGhIssueByLink(link);
            if(githubIssue == null ){
                log.info("github issue is null, and link is {}", link);
                continue;
            }
            SprintPlanObj sprintPlan = GithubUtil.convertToSprintPlanObj(githubIssue,'1');
            int error = 0;
            try{
                error = githubService.insertSprintPlanObj(sprintPlan);
                log.info("insertSprintPlanObj: {}",sprintPlan.toString());
                count ++;
                sprintPlanObjs.add(sprintPlan);
            }catch(Exception ec){
                log.info("update {} as sprintgoal failed"+ sprintPlan.toString());
            }

        }
        log.info("update {} as sprintgoal succeeded!", count);

        return sprintPlanObjs;
    }
}
