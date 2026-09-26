package com.arist.eform.mis_equipment_approval.dto;

public class ResolveTicketRequest {

    private String action;
    private String comment;

    public ResolveTicketRequest() {
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}