package com.arist.eform.mis_equipment_approval.controller;

import com.arist.eform.mis_equipment_approval.dto.CreateTicketRequest;
import com.arist.eform.mis_equipment_approval.dto.TicketResponse;
import com.arist.eform.mis_equipment_approval.model.Ticket;
import com.arist.eform.mis_equipment_approval.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<?> createTicket(@RequestBody CreateTicketRequest request) {
        try {
            Ticket ticket = ticketService.createTicket(
                request.getEquipmentId(),
                request.getApplicantId(),
                request.getDescription(),
                request.getSeverity()
            );

            return ResponseEntity.ok(TicketResponse.fromTicket(ticket));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}