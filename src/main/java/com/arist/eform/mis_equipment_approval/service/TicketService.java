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
}