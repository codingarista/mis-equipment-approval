package com.arist.eform.mis_equipment_approval.repository;

import com.arist.eform.mis_equipment_approval.model.Notification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationRepository {

    private final JdbcTemplate jdbcTemplate;

    public NotificationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 新增一筆通知(預設未讀)
    public void insert(Notification notification) {
        String sql = "INSERT INTO notifications (ticket_id, recipient_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, notification.getTicketId(), notification.getRecipientId());
    }
}