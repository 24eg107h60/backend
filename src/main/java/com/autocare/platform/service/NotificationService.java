package com.autocare.platform.service;

import com.autocare.platform.model.NotificationReminder;
import com.autocare.platform.model.User;
import com.autocare.platform.model.Vehicle;
import com.autocare.platform.repository.NotificationReminderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationReminderRepository notificationRepository;

    public NotificationService(NotificationReminderRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public NotificationReminder createNotification(User user, Vehicle vehicle, String title, String message, String type) {
        NotificationReminder reminder = new NotificationReminder(user, vehicle, title, message, type);
        return notificationRepository.save(reminder);
    }

    public List<NotificationReminder> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public NotificationReminder markAsRead(Long id, Long userId) {
        NotificationReminder reminder = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found with ID: " + id));

        if (!reminder.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized to modify this notification");
        }

        reminder.setRead(true);
        return notificationRepository.save(reminder);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<NotificationReminder> unread = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        for (NotificationReminder n : unread) {
            n.setRead(true);
        }
        notificationRepository.saveAll(unread);
    }
}
