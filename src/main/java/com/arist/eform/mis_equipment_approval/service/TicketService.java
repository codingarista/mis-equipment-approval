package com.arist.eform.mis_equipment_approval.service;

import com.arist.eform.mis_equipment_approval.model.ApprovalLog;
import com.arist.eform.mis_equipment_approval.model.Equipment;
import com.arist.eform.mis_equipment_approval.model.Notification;
import com.arist.eform.mis_equipment_approval.model.Severity;
import com.arist.eform.mis_equipment_approval.model.Ticket;
import com.arist.eform.mis_equipment_approval.model.TicketStatus;
import com.arist.eform.mis_equipment_approval.repository.ApprovalLogRepository;
import com.arist.eform.mis_equipment_approval.repository.EquipmentRepository;
import com.arist.eform.mis_equipment_approval.repository.NotificationRepository;
import com.arist.eform.mis_equipment_approval.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final EquipmentRepository equipmentRepository;
    private final ApprovalLogRepository approvalLogRepository;
    private final NotificationRepository notificationRepository;

    public TicketService(TicketRepository ticketRepository,
                          EquipmentRepository equipmentRepository,
                          ApprovalLogRepository approvalLogRepository,
                          NotificationRepository notificationRepository) {
        this.ticketRepository = ticketRepository;
        this.equipmentRepository = equipmentRepository;
        this.approvalLogRepository = approvalLogRepository;
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public Ticket createTicket(Integer equipmentId, Integer applicantId, String description, Severity severity) {

        // 步驟 1:確認設備存在
        Equipment equipment = equipmentRepository.findById(equipmentId);
        if (equipment == null) {
            throw new IllegalArgumentException("找不到指定的設備");
        }

        // 步驟 2:新增維修單
        Ticket ticket = new Ticket();
        ticket.setEquipmentId(equipmentId);
        ticket.setApplicantId(applicantId);
        ticket.setDescription(description);
        ticket.setSeverity(severity);
        ticket.setStatus(TicketStatus.PENDING_SUPERVISOR);

        Integer ticketId = ticketRepository.insert(ticket);
        ticket.setId(ticketId);

        // 步驟 3:新增簽核紀錄(SUBMIT)
        ApprovalLog log = new ApprovalLog();
        log.setTicketId(ticketId);
        log.setActorId(applicantId);
        log.setAction("SUBMIT");
        approvalLogRepository.insert(log);

        // 步驟 4:通知設備保管者(如果申請人不是保管者本人)
        if (!equipment.getCustodianId().equals(applicantId)) {
            Notification notification = new Notification();
            notification.setTicketId(ticketId);
            notification.setRecipientId(equipment.getCustodianId());
            notificationRepository.insert(notification);
        }

        return ticketRepository.findById(ticketId);
    }
}