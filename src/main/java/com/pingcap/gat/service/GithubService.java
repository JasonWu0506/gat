package com.pingcap.gat.service;

import com.pingcap.gat.model.GhDailyCount;
import com.pingcap.gat.model.GithubIssue;
import com.pingcap.gat.model.SprintPlanObj;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface GithubService {
    int insertIssue(GithubIssue githubIssue);
    int updateIssue(GithubIssue githubIssue);
    List<GithubIssue> searchForNewIssue(Date date);
    List<GithubIssue> searchForLegacyUpdate(Date date);
    List<GithubIssue> searchForClose(Date date);
    int searchForCloseCount(String org, String repo,String type,  Date date);
    int searchForNewCount(String org, String repo,String type, Date date);
    int searchForOpenCount(String org, String repo,String type, Date date);
    int updateLastModified();
    List<Date> getLastModifiedAt();
    List<GhDailyCount> getAllDaily();
    int insertSprintPlanObj(SprintPlanObj sprintPlanObj);
    int updateSprintPlanObj(SprintPlanObj sprintPlanObj);

    List<Date> getLastModifiedAtFromDailyCount(String org, String repo, char type);
    GithubIssue getGhIssueByLink(String link);
    GithubIssue getGhIssueById(String org, String repo, int id);
    List<GithubIssue> searchClosedPRByTime(Date startDate, Date endDate) throws UnsupportedEncodingException;
    int updatePRTested(GithubIssue githubIssue);
    int updatePRBugfix(GithubIssue githubIssue);
    boolean isPRBugfix(String org, String repo, String bugId);
    List<GithubIssue> getGhIssueListByRepo(String repo);


}
