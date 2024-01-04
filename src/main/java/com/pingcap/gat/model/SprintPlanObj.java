package com.pingcap.gat.model;

import com.pingcap.gat.constant.ConfigConstant;
import com.pingcap.gat.util.DateUtil;

import java.util.Date;

public class SprintPlanObj {
    String sprint;
    String org;
    String repo;
    int id;
    String link;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    String label;

    public char getType() {
        return issue_type;
    }

    public void setType(char type) {
        this.issue_type = type;
    }

    char issue_type;


    public String getSprint() {
        return sprint;
    }

    public void setSprint(String sprint) {
        this.sprint = sprint;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

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

    public String getState() {
        return issue_state;
    }

    public void setState(String state) {
        this.issue_state = state;
    }

    public Date getClosed_at() {
        return closed_at;
    }

    public void setClosed_at(Date closed_at) {
        this.closed_at = closed_at;
    }

    String component;

    public String getSig() {
        return sig;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }

    String sig;

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getOncallid() {
        return oncallid;
    }

    public void setOncallid(String oncallid) {
        this.oncallid = oncallid;
    }

    String oncallid;


    String issue_state;
    Date closed_at;
    public String toString() {

        StringBuffer sb = new StringBuffer();
        sb.append(sprint);
        sb.append("\t");
        sb.append(link);
        sb.append("\t");
        sb.append(id);
        sb.append("\t");
        sb.append(DateUtil.formatDateString(closed_at,ConfigConstant.DATE_FORMAT));
        sb.append("\t");
        sb.append(issue_state);
        sb.append("\t");
        sb.append(org);
        sb.append("\t");
        sb.append(repo);
        sb.append("\t");
        sb.append(issue_type);
        sb.append("\t");
        sb.append(label);
        sb.append("\t");
        return sb.toString();
    }
}
