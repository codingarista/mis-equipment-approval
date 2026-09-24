package com.arist.eform.mis_equipment_approval.model;

public enum TicketStatus {
    PENDING_SUPERVISOR,      // 待主管簽核
    REJECTED_BY_SUPERVISOR,  // 主管駁回(結束)
    PENDING_REPAIR,          // 待維修
    IN_PROGRESS,             // 維修人員處理中
    RETURNED_TO_APPLICANT,   // 維修人員退回給申請人
    WITHDRAWN,                // 申請人撤回(結束)
    PENDING_QC,               // 待品管審核
    NOTIFIED,                 // 已通知申請人維修結果
    COMPLETED,                 // 申請人確認完成(結束)
    ESCALATED,                 // 已升級待經理處理
    CLOSED_BY_MANAGER          // 經理結案(結束)
}