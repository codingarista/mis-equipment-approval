package com.arist.eform.mis_equipment_approval.dto;

public class QcReviewRequest {

    private Integer actorId;
    private String repairResult;  // "REPAIRED" 或 "SCRAP_RECOMMENDED"
    private String comment;

    public QcReviewRequest() {
    }

    public Integer getActorId() {
        return actorId;
    }

    public void setActorId(Integer actorId) {
        this.actorId = actorId;
    }

    public String getRepairResult() {
        return repairResult;
    }

    public void setRepairResult(String repairResult) {
        this.repairResult = repairResult;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}