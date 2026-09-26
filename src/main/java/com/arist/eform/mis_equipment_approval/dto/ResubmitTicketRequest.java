package com.arist.eform.mis_equipment_approval.dto;

import com.arist.eform.mis_equipment_approval.model.Severity;

public class ResubmitTicketRequest {

    private String description;
    private Severity severity;

    public ResubmitTicketRequest() {
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