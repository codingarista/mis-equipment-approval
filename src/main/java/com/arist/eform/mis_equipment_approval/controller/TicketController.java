package com.arist.eform.mis_equipment_approval.controller;

import com.arist.eform.mis_equipment_approval.dto.CreateTicketRequest;
import com.arist.eform.mis_equipment_approval.dto.DisputeTicketRequest;
import com.arist.eform.mis_equipment_approval.dto.ManagerCloseRequest;
import com.arist.eform.mis_equipment_approval.dto.QcReviewRequest;
import com.arist.eform.mis_equipment_approval.dto.ResolveTicketRequest;
import com.arist.eform.mis_equipment_approval.dto.ResubmitTicketRequest;
import com.arist.eform.mis_equipment_approval.dto.TicketResponse;
import com.arist.eform.mis_equipment_approval.model.ApprovalLog;
import com.arist.eform.mis_equipment_approval.model.Ticket;
import com.arist.eform.mis_equipment_approval.model.TicketStatus;
import com.arist.eform.mis_equipment_approval.model.User;
import com.arist.eform.mis_equipment_approval.repository.ApprovalLogRepository;
import com.arist.eform.mis_equipment_approval.service.TicketService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final ApprovalLogRepository approvalLogRepository;

    public TicketController(TicketService ticketService, ApprovalLogRepository approvalLogRepository) {
        this.ticketService = ticketService;
        this.approvalLogRepository = approvalLogRepository;
    }

    private User currentUser(HttpServletRequest request) {
        return (User) request.getAttribute("currentUser");
    }

    @PostMapping
    public ResponseEntity<?> createTicket(@RequestBody CreateTicketRequest request, HttpServletRequest httpRequest) {
        try {
            User user = currentUser(httpRequest);
            Ticket ticket = ticketService.createTicket(
                request.getEquipmentId(),
                user.getId(),
                request.getDescription(),
                request.getSeverity()
            );
            return ResponseEntity.ok(TicketResponse.fromTicket(ticket));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/claim")
    public ResponseEntity<?> claimTicket(@PathVariable Integer id, HttpServletRequest httpRequest) {
        try {
            User user = currentUser(httpRequest);
            Ticket ticket = ticketService.claimTicket(id, user.getId());
            return ResponseEntity.ok(TicketResponse.fromTicket(ticket));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/resolve")
    public ResponseEntity<?> resolveTicket(@PathVariable Integer id,
                                            @RequestBody ResolveTicketRequest request,
                                            HttpServletRequest httpRequest) {
        try {
            User user = currentUser(httpRequest);
            Ticket ticket = ticketService.resolveTicket(id, user.getId(), request.getAction(), request.getComment());
            return ResponseEntity.ok(TicketResponse.fromTicket(ticket));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/withdraw")
    public ResponseEntity<?> withdrawTicket(@PathVariable Integer id, HttpServletRequest httpRequest) {
        try {
            User user = currentUser(httpRequest);
            Ticket ticket = ticketService.withdrawTicket(id, user.getId());
            return ResponseEntity.ok(TicketResponse.fromTicket(ticket));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/resubmit")
    public ResponseEntity<?> resubmitTicket(@PathVariable Integer id,
                                             @RequestBody ResubmitTicketRequest request,
                                             HttpServletRequest httpRequest) {
        try {
            User user = currentUser(httpRequest);
            Ticket ticket = ticketService.resubmitTicket(
                id, user.getId(), request.getDescription(), request.getSeverity()
            );
            return ResponseEntity.ok(TicketResponse.fromTicket(ticket));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/qc-review")
    public ResponseEntity<?> qcReview(@PathVariable Integer id,
                                       @RequestBody QcReviewRequest request,
                                       HttpServletRequest httpRequest) {
        try {
            User user = currentUser(httpRequest);
            Ticket ticket = ticketService.qcReview(
                id, user.getId(), request.getRepairResult(), request.getComment()
            );
            return ResponseEntity.ok(TicketResponse.fromTicket(ticket));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<?> confirmTicket(@PathVariable Integer id, HttpServletRequest httpRequest) {
        try {
            User user = currentUser(httpRequest);
            Ticket ticket = ticketService.confirmTicket(id, user.getId());
            return ResponseEntity.ok(TicketResponse.fromTicket(ticket));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/dispute")
    public ResponseEntity<?> disputeTicket(@PathVariable Integer id,
                                            @RequestBody DisputeTicketRequest request,
                                            HttpServletRequest httpRequest) {
        try {
            User user = currentUser(httpRequest);
            Ticket ticket = ticketService.disputeTicket(id, user.getId(), request.getComment());
            return ResponseEntity.ok(TicketResponse.fromTicket(ticket));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/manager-close")
    public ResponseEntity<?> managerClose(@PathVariable Integer id,
                                           @RequestBody ManagerCloseRequest request,
                                           HttpServletRequest httpRequest) {
        try {
            User user = currentUser(httpRequest);
            Ticket ticket = ticketService.managerClose(id, user.getId(), request.getComment());
            return ResponseEntity.ok(TicketResponse.fromTicket(ticket));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getTickets(
            @RequestParam(required = false) Integer applicantId,
            @RequestParam(required = false) Integer technicianId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer departmentId) {

        TicketStatus statusEnum = null;
        if (status != null) {
            try {
                statusEnum = TicketStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("無效的狀態值: " + status);
            }
        }

        List<Ticket> tickets = ticketService.getTickets(applicantId, technicianId, statusEnum, departmentId);
        List<TicketResponse> responses = tickets.stream()
                .map(TicketResponse::fromTicket)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTicketById(@PathVariable Integer id) {
        Ticket ticket = ticketService.getTicketById(id);
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(TicketResponse.fromTicket(ticket));
    }

    @GetMapping("/{id}/logs")
    public ResponseEntity<?> getTicketLogs(@PathVariable Integer id) {
        List<ApprovalLog> logs = approvalLogRepository.findByTicketId(id);
        return ResponseEntity.ok(logs);
    }
}