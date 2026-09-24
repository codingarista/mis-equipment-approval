package com.arist.eform.mis_equipment_approval.dto;

import com.arist.eform.mis_equipment_approval.model.Severity;

public class CreateTicketRequest {

    private Integer equipmentId;
    private Integer applicantId;
    private String description;
    private Severity severity;

    public CreateTicketRequest() {
    }

    public Integer getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Integer equipmentId) {
        this.equipmentId = equipmentId;
    }

    public Integer getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(Integer applicantId) {
        this.applicantId = applicantId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }
}