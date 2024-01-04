package com.pingcap.gat.controller;

import com.pingcap.gat.mapper.GithubIssueMapper;
import com.pingcap.gat.model.BugKiller;
import com.pingcap.gat.model.GithubIssue;
import com.pingcap.gat.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    @Autowired
    GithubIssueMapper githubIssueMapper;

    @RequestMapping("/getGHOpenIssues")
    public ModelAndView getGHOpenIssues(@RequestParam("orgBug") String org, @RequestParam("repoBug") String repo){
        return null;
    }

    @RequestMapping("/getTopBugKiller")
    public Map<String, Integer> getDailyCount(@RequestParam("since") String since){
        Date dateSince = DateUtil.getDate(since,"yyyy-MM-dd");
        List<BugKiller> bugKillerList = githubIssueMapper.getTopBugKiller(dateSince);
        Map<String, Integer> mapRet = new HashMap<>();
        int count = 0;
        for(BugKiller bugkiller:bugKillerList){
            if(bugkiller.getAuthor().compareTo("null") == 0) continue;
            if(count >20 ) break;
            mapRet.put(bugkiller.getAuthor(),bugkiller.getBugCount());
            count ++;
        }
        return mapRet;

    }

//    @RequestMapping("/getTopBugKiller")
//    public Map<String, Integer> getDailyCountByTime(@RequestParam("since") String since, @RequestParam("till") String till){
//        Date dateSince = DateUtil.getDate(since,"yyyy-MM-dd");
//        Date dateTill = DateUtil.getDate(since,"yyyy-MM-dd");
//
//
//        List<BugKiller> bugKillerList = githubIssueMapper.getTopBugKiller(dateSince);
//        Map<String, Integer> mapRet = new HashMap<>();
//        int count = 0;
//        for(BugKiller bugkiller:bugKillerList){
//            if(bugkiller.getAuthor().compareTo("null") == 0) continue;
//            if(count >20 ) break;
//            mapRet.put(bugkiller.getAuthor(),bugkiller.getBugCount());
//            count ++;
//        }
//        return mapRet;
//
//    }
    @RequestMapping("/getSprintGoal")
    public Map<String, Integer> getSprintGoal(@RequestParam("sprint") String sprint){

//        Date dateSince = DateUtil.getDate("","yyyy-MM-dd");
//        List<BugKiller> bugKillerList =  githubIssueMapper.getTopBugKiller(dateSince);
       Map<String, Integer> mapRet = new HashMap<>();
//        int count = 0;
//        for(BugKiller bugKiller:bugKillerList){
//            if(bugKiller.getAuthor().compareTo("null") == 0) continue;
//            if(count >15 ) break;
//            mapRet.put(bugKiller.getAuthor(),bugKiller.getBugCount());
//            count ++;
//        }
        return mapRet;

    }


}
