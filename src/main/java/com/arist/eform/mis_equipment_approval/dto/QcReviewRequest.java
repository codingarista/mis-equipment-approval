package com.arist.eform.mis_equipment_approval.dto;

public class QcReviewRequest {

    private String repairResult;
    private String comment;

    public QcReviewRequest() {
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