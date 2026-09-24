package com.arist.eform.mis_equipment_approval.model;

import java.time.LocalDateTime;

public class Ticket {

    private Integer id;
    private Integer equipmentId;
    private Integer applicantId;
    private Integer currentTechnicianId;
    private String description;
    private Severity severity;
    private TicketStatus status;
    private String repairResult;
    private Integer rejectionCount;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;

    public Ticket() {
    }

    // Getter 和 Setter
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getCurrentTechnicianId() {
        return currentTechnicianId;
    }

    public void setCurrentTechnicianId(Integer currentTechnicianId) {
        this.currentTechnicianId = currentTechnicianId;
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

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public String getRepairResult() {
        return repairResult;
    }

    public void setRepairResult(String repairResult) {
        this.repairResult = repairResult;
    }

    public Integer getRejectionCount() {
        return rejectionCount;
    }

    public void setRejectionCount(Integer rejectionCount) {
        this.rejectionCount = rejectionCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }
}