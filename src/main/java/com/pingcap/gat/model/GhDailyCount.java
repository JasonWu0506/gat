package com.pingcap.gat.model;

import com.pingcap.gat.constant.ConfigConstant;
import com.pingcap.gat.util.DateUtil;

import java.util.Date;

public class GhDailyCount {
    String org;
    String repo;
    Date time;
    int created;
    public String toString() {

        StringBuffer sb = new StringBuffer();
        sb.append(org);
        sb.append("\t");
        sb.append(repo);
        sb.append("\t");
        sb.append(time);
        sb.append("\t");
        sb.append(String.valueOf(created));
        sb.append("\t");
        sb.append(String.valueOf(closed));
        sb.append("\t");
        sb.append(String.valueOf(open));
        sb.append("\t");
        sb.append(type);
        sb.append("\t");
        return sb.toString();
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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public int getClosed() {
        return closed;
    }

    public void setClosed(int closed) {
        this.closed = closed;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    int closed;
    int open;
    char type;

}
