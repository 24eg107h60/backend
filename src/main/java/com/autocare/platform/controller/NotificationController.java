package com.autocare.platform.controller;

import com.autocare.platform.model.NotificationReminder;
import com.autocare.platform.model.User;
import com.autocare.platform.service.NotificationService;
import com.autocare.platform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Reminders & Notifications", description = "Maintenance Reminders and System Alerts APIs")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get all reminder notifications for current user")
    public ResponseEntity<List<NotificationReminder>> getNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getByEmail(userDetails.getUsername());
        return ResponseEntity.ok(notificationService.getUserNotifications(user.getId()));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get count of unread notifications")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getByEmail(userDetails.getUsername());
        long count = notificationService.getUnreadCount(user.getId());
        return ResponseEntity.ok(Collections.singletonMap("unreadCount", count));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark a notification as read")
    public ResponseEntity<NotificationReminder> markAsRead(@AuthenticationPrincipal UserDetails userDetails,
                                                          @PathVariable Long id) {
        User user = userService.getByEmail(userDetails.getUsername());
        return ResponseEntity.ok(notificationService.markAsRead(id, user.getId()));
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark all notifications as read")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getByEmail(userDetails.getUsername());
        notificationService.markAllAsRead(user.getId());
        return ResponseEntity.noContent().build();
    }
}
