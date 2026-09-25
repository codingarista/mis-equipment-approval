package com.arist.eform.mis_equipment_approval.service;

import com.arist.eform.mis_equipment_approval.model.ApprovalLog;
import com.arist.eform.mis_equipment_approval.model.Equipment;
import com.arist.eform.mis_equipment_approval.model.Notification;
import com.arist.eform.mis_equipment_approval.model.Role;
import com.arist.eform.mis_equipment_approval.model.Severity;
import com.arist.eform.mis_equipment_approval.model.Ticket;
import com.arist.eform.mis_equipment_approval.model.TicketStatus;
import com.arist.eform.mis_equipment_approval.model.User;
import com.arist.eform.mis_equipment_approval.repository.ApprovalLogRepository;
import com.arist.eform.mis_equipment_approval.repository.EquipmentRepository;
import com.arist.eform.mis_equipment_approval.repository.NotificationRepository;
import com.arist.eform.mis_equipment_approval.repository.TicketRepository;
import com.arist.eform.mis_equipment_approval.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final EquipmentRepository equipmentRepository;
    private final ApprovalLogRepository approvalLogRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public TicketService(TicketRepository ticketRepository,
                          EquipmentRepository equipmentRepository,
                          ApprovalLogRepository approvalLogRepository,
                          NotificationRepository notificationRepository,
                          UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.equipmentRepository = equipmentRepository;
        this.approvalLogRepository = approvalLogRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Ticket createTicket(Integer equipmentId, Integer applicantId, String description, Severity severity) {

        // 步驟 1:確認設備存在
        Equipment equipment = equipmentRepository.findById(equipmentId);
        if (equipment == null) {
            throw new IllegalArgumentException("找不到指定的設備");
        }

        // 步驟 2:確認申請人存在(這一步是新增的,因為要知道申請人的部門,才能找出對應的主管)
        User applicant = userRepository.findById(applicantId);
        if (applicant == null) {
            throw new IllegalArgumentException("找不到指定的申請人");
        }

        // 步驟 3:新增維修單,狀態直接設為「待維修」(主管這一關只是知會,不影響流程走向)
        Ticket ticket = new Ticket();
        ticket.setEquipmentId(equipmentId);
        ticket.setApplicantId(applicantId);
        ticket.setDescription(description);
        ticket.setSeverity(severity);
        ticket.setStatus(TicketStatus.PENDING_REPAIR);

        Integer ticketId = ticketRepository.insert(ticket);
        ticket.setId(ticketId);

        // 步驟 4:新增簽核紀錄(SUBMIT)
        ApprovalLog log = new ApprovalLog();
        log.setTicketId(ticketId);
        log.setActorId(applicantId);
        log.setAction("SUBMIT");
        approvalLogRepository.insert(log);

        // 步驟 5:通知設備保管者(如果申請人不是保管者本人)
        if (!equipment.getCustodianId().equals(applicantId)) {
            Notification custodianNotification = new Notification();
            custodianNotification.setTicketId(ticketId);
            custodianNotification.setRecipientId(equipment.getCustodianId());
            notificationRepository.insert(custodianNotification);
        }

        // 步驟 6:通知申請人同部門的所有主管(僅供知會,類似 mail 副本)
        List<User> supervisors = userRepository.findByDepartmentIdAndRole(applicant.getDepartmentId(), Role.SUPERVISOR);
        for (User supervisor : supervisors) {
            Notification supervisorNotification = new Notification();
            supervisorNotification.setTicketId(ticketId);
            supervisorNotification.setRecipientId(supervisor.getId());
            notificationRepository.insert(supervisorNotification);
        }

        return ticketRepository.findById(ticketId);
    }
        // 認領單據:維修人員自行認領一張「待維修」的單
    @Transactional
    public Ticket claimTicket(Integer ticketId, Integer technicianId) {

        // 步驟 1:確認單據存在
        Ticket ticket = ticketRepository.findById(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("找不到指定的維修單");
        }

        // 步驟 2:確認單據目前狀態允許被認領
        if (ticket.getStatus() != TicketStatus.PENDING_REPAIR) {
            throw new IllegalStateException("這張單目前的狀態是 " + ticket.getStatus() + ",無法被認領");
        }

        // 步驟 3:更新單據,設定技術員並轉換狀態
        ticketRepository.claimTicket(ticketId, technicianId, TicketStatus.IN_PROGRESS);

        // 步驟 4:新增簽核紀錄
        ApprovalLog log = new ApprovalLog();
        log.setTicketId(ticketId);
        log.setActorId(technicianId);
        log.setAction("TECH_CLAIM");
        approvalLogRepository.insert(log);

        return ticketRepository.findById(ticketId);
    }
        // 維修人員處理完畢或退回
    @Transactional
    public Ticket resolveTicket(Integer ticketId, Integer actorId, String action, String comment) {

        // 步驟 1:確認單據存在
        Ticket ticket = ticketRepository.findById(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("找不到指定的維修單");
        }

        // 步驟 2:確認單據狀態是「處理中」
        if (ticket.getStatus() != TicketStatus.IN_PROGRESS) {
            throw new IllegalStateException("這張單目前的狀態是 " + ticket.getStatus() + ",無法執行這個動作");
        }

        // 步驟 3:確認操作者就是被指派的技術員本人
        if (!ticket.getCurrentTechnicianId().equals(actorId)) {
            throw new IllegalStateException("只有負責處理這張單的技術員,才能執行這個動作");
        }

        // 步驟 4:依照 action 分流處理
        if ("COMPLETE".equals(action)) {

            ticketRepository.updateStatus(ticketId, TicketStatus.PENDING_QC);

            ApprovalLog log = new ApprovalLog();
            log.setTicketId(ticketId);
            log.setActorId(actorId);
            log.setAction("TECH_COMPLETE");
            log.setComment(comment);
            approvalLogRepository.insert(log);

        } else if ("RETURN".equals(action)) {

            if (comment == null || comment.isBlank()) {
                throw new IllegalArgumentException("退回時必須填寫原因");
            }

            ticketRepository.updateStatus(ticketId, TicketStatus.RETURNED_TO_APPLICANT);

            ApprovalLog log = new ApprovalLog();
            log.setTicketId(ticketId);
            log.setActorId(actorId);
            log.setAction("TECH_RETURN");
            log.setComment(comment);
            approvalLogRepository.insert(log);

        } else {
            throw new IllegalArgumentException("無效的動作類型,只能是 COMPLETE 或 RETURN");
        }

        return ticketRepository.findById(ticketId);
    }
        // 申請人撤回一張被退回的單
    @Transactional
    public Ticket withdrawTicket(Integer ticketId, Integer actorId) {

        Ticket ticket = ticketRepository.findById(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("找不到指定的維修單");
        }

        if (ticket.getStatus() != TicketStatus.RETURNED_TO_APPLICANT) {
            throw new IllegalStateException("這張單目前的狀態是 " + ticket.getStatus() + ",無法撤回");
        }

        if (!ticket.getApplicantId().equals(actorId)) {
            throw new IllegalStateException("只有原申請人本人,才能撤回這張單");
        }

        ticketRepository.updateStatus(ticketId, TicketStatus.WITHDRAWN);

        ApprovalLog log = new ApprovalLog();
        log.setTicketId(ticketId);
        log.setActorId(actorId);
        log.setAction("APPLICANT_WITHDRAW");
        approvalLogRepository.insert(log);

        return ticketRepository.findById(ticketId);
    }

    // 申請人修改內容後重新提交
    @Transactional
    public Ticket resubmitTicket(Integer ticketId, Integer actorId, String description, Severity severity) {

        Ticket ticket = ticketRepository.findById(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("找不到指定的維修單");
        }

        if (ticket.getStatus() != TicketStatus.RETURNED_TO_APPLICANT) {
            throw new IllegalStateException("這張單目前的狀態是 " + ticket.getStatus() + ",無法重新提交");
        }

        if (!ticket.getApplicantId().equals(actorId)) {
            throw new IllegalStateException("只有原申請人本人,才能重新提交這張單");
        }

        ticketRepository.resubmit(ticketId, description, severity, TicketStatus.PENDING_REPAIR);

        ApprovalLog log = new ApprovalLog();
        log.setTicketId(ticketId);
        log.setActorId(actorId);
        log.setAction("APPLICANT_RESUBMIT");
        approvalLogRepository.insert(log);

        return ticketRepository.findById(ticketId);
    }
        // 品管審核
    @Transactional
    public Ticket qcReview(Integer ticketId, Integer actorId, String repairResult, String comment) {

        // 步驟 1:確認單據存在
        Ticket ticket = ticketRepository.findById(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("找不到指定的維修單");
        }

        // 步驟 2:確認單據狀態是「待品管審核」
        if (ticket.getStatus() != TicketStatus.PENDING_QC) {
            throw new IllegalStateException("這張單目前的狀態是 " + ticket.getStatus() + ",無法進行品管審核");
        }

        // 步驟 3:確認操作者存在
        User actor = userRepository.findById(actorId);
        if (actor == null) {
            throw new IllegalArgumentException("找不到指定的品管人員");
        }

        // 步驟 4:確認設備存在,並比對部門
        Equipment equipment = equipmentRepository.findById(ticket.getEquipmentId());
        if (equipment == null) {
            throw new IllegalArgumentException("找不到這張單對應的設備");
        }

        if (!actor.getDepartmentId().equals(equipment.getDepartmentId())) {
            throw new IllegalStateException("只有設備所屬部門的品管人員,才能審核這張單");
        }

        // 步驟 5:確認 repairResult 是合法值
        if (!"REPAIRED".equals(repairResult) && !"SCRAP_RECOMMENDED".equals(repairResult)) {
            throw new IllegalArgumentException("維修結果只能是 REPAIRED 或 SCRAP_RECOMMENDED");
        }

        // 步驟 6:更新單據
        ticketRepository.qcReview(ticketId, repairResult, TicketStatus.NOTIFIED);

        // 步驟 7:新增簽核紀錄
        ApprovalLog log = new ApprovalLog();
        log.setTicketId(ticketId);
        log.setActorId(actorId);
        log.setAction("QC_APPROVE");
        log.setComment(comment);
        approvalLogRepository.insert(log);

        // 步驟 8:通知申請人
        Notification notification = new Notification();
        notification.setTicketId(ticketId);
        notification.setRecipientId(ticket.getApplicantId());
        notificationRepository.insert(notification);

        return ticketRepository.findById(ticketId);
    }
        // 申請人確認完成
    @Transactional
    public Ticket confirmTicket(Integer ticketId, Integer actorId) {

        Ticket ticket = ticketRepository.findById(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("找不到指定的維修單");
        }

        if (ticket.getStatus() != TicketStatus.NOTIFIED) {
            throw new IllegalStateException("這張單目前的狀態是 " + ticket.getStatus() + ",無法確認");
        }

        if (!ticket.getApplicantId().equals(actorId)) {
            throw new IllegalStateException("只有原申請人本人,才能確認這張單");
        }

        ticketRepository.confirmTicket(ticketId);

        ApprovalLog log = new ApprovalLog();
        log.setTicketId(ticketId);
        log.setActorId(actorId);
        log.setAction("APPLICANT_CONFIRM");
        approvalLogRepository.insert(log);

        return ticketRepository.findById(ticketId);
    }

    // 申請人駁回維修結果
    @Transactional
    public Ticket disputeTicket(Integer ticketId, Integer actorId, String comment) {

        Ticket ticket = ticketRepository.findById(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("找不到指定的維修單");
        }

        if (ticket.getStatus() != TicketStatus.NOTIFIED) {
            throw new IllegalStateException("這張單目前的狀態是 " + ticket.getStatus() + ",無法駁回");
        }

        if (!ticket.getApplicantId().equals(actorId)) {
            throw new IllegalStateException("只有原申請人本人,才能駁回這張單");
        }

        if (comment == null || comment.isBlank()) {
            throw new IllegalArgumentException("駁回時必須填寫原因");
        }

        // 判斷這次駁回後,次數是否達到升級標準
        int newRejectionCount = ticket.getRejectionCount() + 1;
        TicketStatus newStatus = (newRejectionCount >= 3) ? TicketStatus.ESCALATED : TicketStatus.PENDING_REPAIR;

        ticketRepository.disputeTicket(ticketId, newStatus);

        ApprovalLog log = new ApprovalLog();
        log.setTicketId(ticketId);
        log.setActorId(actorId);
        log.setAction("APPLICANT_DISPUTE");
        log.setComment(comment);
        approvalLogRepository.insert(log);

        return ticketRepository.findById(ticketId);
    }
        // 經理結案
    @Transactional
    public Ticket managerClose(Integer ticketId, Integer actorId, String comment) {

        // 步驟 1:確認單據存在
        Ticket ticket = ticketRepository.findById(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("找不到指定的維修單");
        }

        // 步驟 2:確認單據狀態是「已升級」
        if (ticket.getStatus() != TicketStatus.ESCALATED) {
            throw new IllegalStateException("這張單目前的狀態是 " + ticket.getStatus() + ",無法進行經理結案");
        }

        // 步驟 3:確認 comment 必填
        if (comment == null || comment.isBlank()) {
            throw new IllegalArgumentException("經理結案時必須填寫處理說明");
        }

        // 步驟 4:確認操作者存在
        User actor = userRepository.findById(actorId);
        if (actor == null) {
            throw new IllegalArgumentException("找不到指定的經理");
        }

        // 步驟 5:確認設備存在,並比對部門
        Equipment equipment = equipmentRepository.findById(ticket.getEquipmentId());
        if (equipment == null) {
            throw new IllegalArgumentException("找不到這張單對應的設備");
        }

        if (!actor.getDepartmentId().equals(equipment.getDepartmentId())) {
            throw new IllegalStateException("只有設備所屬部門的經理,才能處理這張升級單");
        }

        // 步驟 6:更新單據
        ticketRepository.managerClose(ticketId, TicketStatus.CLOSED_BY_MANAGER);

        // 步驟 7:新增簽核紀錄
        ApprovalLog log = new ApprovalLog();
        log.setTicketId(ticketId);
        log.setActorId(actorId);
        log.setAction("MANAGER_CLOSE");
        log.setComment(comment);
        approvalLogRepository.insert(log);

        return ticketRepository.findById(ticketId);
    }
}