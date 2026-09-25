package com.arist.eform.mis_equipment_approval.controller;

import com.arist.eform.mis_equipment_approval.dto.ClaimTicketRequest;
import com.arist.eform.mis_equipment_approval.dto.CreateTicketRequest;
import com.arist.eform.mis_equipment_approval.dto.TicketResponse;
import com.arist.eform.mis_equipment_approval.model.Ticket;
import com.arist.eform.mis_equipment_approval.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.arist.eform.mis_equipment_approval.dto.ResolveTicketRequest;


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
        @PatchMapping("/{id}/claim")
    public ResponseEntity<?> claimTicket(@PathVariable Integer id,
                                          @RequestBody ClaimTicketRequest request) {
        try {
            Ticket ticket = ticketService.claimTicket(id, request.getActorId());
            return ResponseEntity.ok(TicketResponse.fromTicket(ticket));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }
        @PatchMapping("/{id}/resolve")
    public ResponseEntity<?> resolveTicket(@PathVariable Integer id,
                                            @RequestBody ResolveTicketRequest request) {
        try {
            Ticket ticket = ticketService.resolveTicket(
                id,
                request.getActorId(),
                request.getAction(),
                request.getComment()
            );
            return ResponseEntity.ok(TicketResponse.fromTicket(ticket));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }
}