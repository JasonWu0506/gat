package com.pingcap.gat.model;

import com.pingcap.gat.constant.ConfigConstant;
import com.pingcap.gat.util.DateUtil;

import java.util.Date;

public class GithubIssue {
    int id;
    String link;
    String repo;
    String org;
    Date created_at;
    Date closed_at;

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    Date updated_at;

    public Date getLastModifiedAt() {
        return last_modified_at;
    }

    public void setLastModifiedAt(Date last_modified_at) {
        this.last_modified_at = last_modified_at;
    }

    Date last_modified_at;

    public boolean isPRBugfix() {
        return isPRBugfix;
    }

    public void setPRBugfix(boolean PRBugfix) {
        isPRBugfix = PRBugfix;
    }

    boolean isPRBugfix;

    public boolean isPRTested() {
        return isPRTested;
    }

    public void setPRTested(boolean PRTested) {
        isPRTested = PRTested;
    }

    boolean isPRTested;


    public Date getMergedAt() {
        return merged_at;
    }

    public void setMergedAt(Date merged_at) {
        this.merged_at = merged_at;
    }

    Date merged_at;

    String component;
    String sig;
    String severity;
    String issue_type;
    String author;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public Date getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(Date created_at) {
        this.created_at = created_at;
    }

    public Date getClosedAt() {
        return closed_at;
    }

    public void setClosedAt(Date closed_at) {
        this.closed_at = closed_at;
    }

    public Date getUpdatedAt() {
        return updated_at;
    }

    public void setUpdatedAt(Date updated_at) {
        this.updated_at = updated_at;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getSig() {
        return sig;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getIssueType() {
        return issue_type;
    }

    public void setIssueType(String type) {
        this.issue_type = type;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String asignee) {
        this.assignee = asignee;
    }

    public String getIssueState() {
        return issue_state;
    }

    public void setIssueState(String issue_state) {
        this.issue_state = issue_state;
    }

    String assignee;
    String issue_state;

    public char isIsIssue() {
        return is_issue;
    }

    public void setIsIssue(char is_issue) {
        this.is_issue = is_issue;
    }

    char is_issue;

    public String getSize() {
        return pr_size;
    }

    public void setSize(String size) {
        this.pr_size = size;
    }

    String pr_size;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    String body;

    public String getAssociatedIssues() {
        return associated_issues;
    }

    public void setAssociatedIssues(String associated_issues) {
        this.associated_issues = associated_issues;
    }

    String associated_issues;


    @Override
    public boolean equals(Object object){
        boolean compare = false;

        if(object instanceof GithubIssue) {

        }else {
            return compare;
        }
        return compare;
    }
    public String toString() {

        StringBuffer sb = new StringBuffer();
        sb.append(id);
        sb.append("\t");
        sb.append(link);
        sb.append("\t");
        sb.append(author);
        sb.append("\t");
        sb.append(DateUtil.formatDateString(created_at, ConfigConstant.DATE_FORMAT));
        sb.append("\t");
        sb.append(DateUtil.formatDateString(updated_at,ConfigConstant.DATE_FORMAT));
        sb.append("\t");
        sb.append(DateUtil.formatDateString(closed_at,ConfigConstant.DATE_FORMAT));
        sb.append("\t");
        sb.append(DateUtil.formatDateString(merged_at,ConfigConstant.DATE_FORMAT));
        sb.append("\t");
        sb.append(component);
        sb.append("\t");
        sb.append(sig);
        sb.append("\t");
        sb.append(severity);
        sb.append("\t");
        sb.append(assignee);
        sb.append("\t");
        sb.append(issue_state);
        sb.append("\t");
        sb.append(issue_type);
        sb.append("\t");
        sb.append(is_issue);
        sb.append("\t");
        sb.append(pr_size);
        sb.append("\t");
        sb.append(associated_issues);
        sb.append("\t");
        sb.append(last_modified_at);
        sb.append("\t");
        sb.append(isPRTested);
        sb.append("\t");
        sb.append(isPRBugfix);
        sb.append("\t");
        return sb.toString();
    }
    public boolean isDiff(GithubIssue githubIssue) {
        if(this.toString().compareTo(githubIssue.toString()) !=0) {
            return false;
        }else return true;
    }

}
