package com.pingcap.gat.model;

import com.pingcap.gat.constant.ConfigConstant;
import com.pingcap.gat.util.DateUtil;

import java.util.Date;

public class CodeCoverage {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public float getCoverage() {
        return coverage;
    }

    public void setCoverage(float coverage) {
        this.coverage = coverage;
    }

    public String toString(){
        return name+"\t"+org+"\t"+repo+"\t"+String.valueOf(coverage)+"\t"+ DateUtil.formatDateString(insertAt, ConfigConstant.DATE_FORMAT)+"\t"+String.valueOf(loc) +"\t"+String.valueOf(hits);//;+"\t"+name+"\t"+
    }
    String org;
    String repo;
    String name;
    Date createAt;
    float coverage;

    public int getLoc() {
        return loc;
    }

    public void setLoc(int loc) {
        this.loc = loc;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    int loc;
    int hits;
    public Date getInsertAt() {
        return insertAt;
    }

    public void setInsertAt(Date insertAt) {
        this.insertAt = insertAt;
    }

    Date insertAt;

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    String branch;


}
