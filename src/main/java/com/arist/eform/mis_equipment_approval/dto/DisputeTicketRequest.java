package com.arist.eform.mis_equipment_approval.dto;

public class DisputeTicketRequest {

    private Integer actorId;
    private String comment;

    public DisputeTicketRequest() {
    }

    public Integer getActorId() {
        return actorId;
    }

    public void setActorId(Integer actorId) {
        this.actorId = actorId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}