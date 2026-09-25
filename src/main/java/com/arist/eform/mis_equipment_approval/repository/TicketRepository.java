package com.arist.eform.mis_equipment_approval.repository;

import com.arist.eform.mis_equipment_approval.model.Severity;
import com.arist.eform.mis_equipment_approval.model.Ticket;
import com.arist.eform.mis_equipment_approval.model.TicketStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class TicketRepository {

    private final JdbcTemplate jdbcTemplate;

    public TicketRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 新增一張維修單,回傳資料庫自動產生的 id
    public Integer insert(Ticket ticket) {
        String sql = "INSERT INTO tickets (equipment_id, applicant_id, description, severity, status) " +
                     "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, ticket.getEquipmentId());
            ps.setInt(2, ticket.getApplicantId());
            ps.setString(3, ticket.getDescription());
            ps.setString(4, ticket.getSeverity().name());
            ps.setString(5, ticket.getStatus().name());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    // 依照 id 查詢一張維修單,找不到就回傳 null
    public Ticket findById(Integer id) {
        String sql = "SELECT id, equipment_id, applicant_id, current_technician_id, description, " +
                     "severity, status, repair_result, rejection_count, created_at, closed_at " +
                     "FROM tickets WHERE id = ?";

        List<Ticket> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Ticket ticket = new Ticket();
            ticket.setId(rs.getInt("id"));
            ticket.setEquipmentId(rs.getInt("equipment_id"));
            ticket.setApplicantId(rs.getInt("applicant_id"));

            int technicianId = rs.getInt("current_technician_id");
            ticket.setCurrentTechnicianId(rs.wasNull() ? null : technicianId);

            ticket.setDescription(rs.getString("description"));
            ticket.setSeverity(Severity.valueOf(rs.getString("severity")));
            ticket.setStatus(TicketStatus.valueOf(rs.getString("status")));
            ticket.setRepairResult(rs.getString("repair_result"));
            ticket.setRejectionCount(rs.getInt("rejection_count"));

            if (rs.getTimestamp("created_at") != null) {
                ticket.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }
            if (rs.getTimestamp("closed_at") != null) {
                ticket.setClosedAt(rs.getTimestamp("closed_at").toLocalDateTime());
            }

            return ticket;
        }, id);

        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }
        // 認領單據:設定技術員 id,並更新狀態
    public void claimTicket(Integer ticketId, Integer technicianId, TicketStatus newStatus) {
        String sql = "UPDATE tickets SET current_technician_id = ?, status = ? WHERE id = ?";
        jdbcTemplate.update(sql, technicianId, newStatus.name(), ticketId);
    }
}