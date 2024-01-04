package com.pingcap.gat.mapper;

import com.pingcap.gat.model.BugKiller;
import com.pingcap.gat.model.GithubIssue;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface GithubIssueMapper {
     int insertIssues(GithubIssue githubIssue);
     int updateIssue(GithubIssue githubIssue);
     List<Date> getLastModifiedAt();
     int udpateLastModifiedAll();
     List<BugKiller> getTopBugKiller(Date since);
     GithubIssue getGhIssueByLink(String link);
     List<BugKiller> getBugKillerList(Date since);

     int updatePRBugfix();
     List<GithubIssue> getAllClosedPRFromDB();
     List<GithubIssue> getPRFromDB(int id);
     GithubIssue getGhIssueById(String repo, String org, int id);
     List<GithubIssue> getGhIssueListByRepo(String repo);
}
