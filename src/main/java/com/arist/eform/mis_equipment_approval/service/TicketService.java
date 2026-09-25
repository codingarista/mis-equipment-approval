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
}