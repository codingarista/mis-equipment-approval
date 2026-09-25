package com.arist.eform.mis_equipment_approval.dto;

public class ResolveTicketRequest {

    private Integer actorId;
    private String action;  // "COMPLETE" 或 "RETURN"
    private String comment;

    public ResolveTicketRequest() {
    }

    public Integer getActorId() {
        return actorId;
    }

    public void setActorId(Integer actorId) {
        this.actorId = actorId;
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