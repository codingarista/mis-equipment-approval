package com.arist.eform.mis_equipment_approval.controller;

import com.arist.eform.mis_equipment_approval.model.Notification;
import com.arist.eform.mis_equipment_approval.model.User;
import com.arist.eform.mis_equipment_approval.repository.NotificationRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    private User currentUser(HttpServletRequest request) {
        return (User) request.getAttribute("currentUser");
    }

    // 查詢「目前登入的人」的所有通知
    @GetMapping
    public ResponseEntity<?> getMyNotifications(HttpServletRequest httpRequest) {
        User user = currentUser(httpRequest);
        List<Notification> notifications = notificationRepository.findByRecipientId(user.getId());
        return ResponseEntity.ok(notifications);
    }

    // 查詢「目前登入的人」有幾筆未讀通知
    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount(HttpServletRequest httpRequest) {
        User user = currentUser(httpRequest);
        int count = notificationRepository.countUnreadByRecipientId(user.getId());

        Map<String, Integer> result = new HashMap<>();
        result.put("unreadCount", count);
        return ResponseEntity.ok(result);
    }

    // 標記某一筆通知為已讀
    @PatchMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Integer id) {
        notificationRepository.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}