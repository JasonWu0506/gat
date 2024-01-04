package com.pingcap.gat.model;

public class Oncall {
    String id;
    String ticket;
    String customer;
    String ghissue;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getGhissue() {
        return ghissue;
    }

    public void setGhissue(String ghissue) {
        this.ghissue = ghissue;
    }

    public String getFoundstage() {
        return foundstage;
    }

    public void setFoundstage(String foundstage) {
        this.foundstage = foundstage;
    }

    public String getFoundmethod() {
        return foundmethod;
    }

    public void setFoundmethod(String foundmethod) {
        this.foundmethod = foundmethod;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    String foundstage;
    String foundmethod;
    String comments;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    String version;
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append(id);
        sb.append("\t");
        sb.append(ticket);
        sb.append("\t");
        sb.append(customer);
        sb.append("\t");
        sb.append(ghissue);
        sb.append("\t");
        sb.append(version);
        sb.append("\t");
        sb.append(foundstage);
        sb.append("\t");
        sb.append(foundmethod);
        sb.append("\t");
        sb.append(comments);
        sb.append("\t");
        return sb.toString();

    }

}
