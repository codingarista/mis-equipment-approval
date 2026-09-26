package com.arist.eform.mis_equipment_approval.repository;

import com.arist.eform.mis_equipment_approval.model.ApprovalLog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ApprovalLogRepository {

    private final JdbcTemplate jdbcTemplate;

    public ApprovalLogRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<ApprovalLog> rowMapper = (rs, rowNum) -> {
        ApprovalLog log = new ApprovalLog();
        log.setId(rs.getInt("id"));
        log.setTicketId(rs.getInt("ticket_id"));
        log.setActorId(rs.getInt("actor_id"));
        log.setAction(rs.getString("action"));
        log.setComment(rs.getString("comment"));
        log.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return log;
    };

    // 新增一筆簽核/處理紀錄
    public void insert(ApprovalLog log) {
        String sql = "INSERT INTO approval_logs (ticket_id, actor_id, action, comment) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, log.getTicketId(), log.getActorId(), log.getAction(), log.getComment());
    }

    // 查詢某張單據的完整簽核歷程,依照時間先後排序
    public List<ApprovalLog> findByTicketId(Integer ticketId) {
        String sql = "SELECT * FROM approval_logs WHERE ticket_id = ? ORDER BY created_at ASC";
        return jdbcTemplate.query(sql, rowMapper, ticketId);
    }
}