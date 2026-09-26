package com.arist.eform.mis_equipment_approval.repository;

import com.arist.eform.mis_equipment_approval.model.Notification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NotificationRepository {

    private final JdbcTemplate jdbcTemplate;

    public NotificationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Notification> rowMapper = (rs, rowNum) -> {
        Notification n = new Notification();
        n.setId(rs.getInt("id"));
        n.setTicketId(rs.getInt("ticket_id"));
        n.setRecipientId(rs.getInt("recipient_id"));
        n.setIsRead(rs.getBoolean("is_read"));
        if (rs.getTimestamp("read_at") != null) {
            n.setReadAt(rs.getTimestamp("read_at").toLocalDateTime());
        }
        n.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return n;
    };

    // 新增一筆通知(預設未讀)
    public void insert(Notification notification) {
        String sql = "INSERT INTO notifications (ticket_id, recipient_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, notification.getTicketId(), notification.getRecipientId());
    }

    // 查詢某人的所有通知,最新的排前面
    public List<Notification> findByRecipientId(Integer recipientId) {
        String sql = "SELECT * FROM notifications WHERE recipient_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, rowMapper, recipientId);
    }

    // 計算某人有幾筆未讀通知
    public int countUnreadByRecipientId(Integer recipientId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE recipient_id = ? AND is_read = FALSE";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, recipientId);
        return count != null ? count : 0;
    }

    // 標記單一通知為已讀
    public void markAsRead(Integer notificationId) {
        String sql = "UPDATE notifications SET is_read = TRUE, read_at = NOW() WHERE id = ?";
        jdbcTemplate.update(sql, notificationId);
    }
}