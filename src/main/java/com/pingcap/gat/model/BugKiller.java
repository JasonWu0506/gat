package com.pingcap.gat.model;

public class BugKiller {
    int count;

    public int getBugCount() {
        return count;
    }

    public void setBugCount(int bugCount) {
        this.count = bugCount;
    }

    public String getAuthor() {
        return assignee;
    }

    public void setAuthor(String author) {
        this.assignee = author;
    }


    String assignee;

    public int getDiScore() {
        return diScore;
    }

    public void setDiScore(int diScore) {
        this.diScore = diScore;
    }

    int diScore;

}
