package com.arist.eform.mis_equipment_approval.repository;

import com.arist.eform.mis_equipment_approval.model.ApprovalLog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ApprovalLogRepository {

    private final JdbcTemplate jdbcTemplate;

    public ApprovalLogRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 新增一筆簽核/處理紀錄
    public void insert(ApprovalLog log) {
        String sql = "INSERT INTO approval_logs (ticket_id, actor_id, action, comment) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, log.getTicketId(), log.getActorId(), log.getAction(), log.getComment());
    }
}