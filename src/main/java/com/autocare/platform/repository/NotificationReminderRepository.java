package com.autocare.platform.repository;

import com.autocare.platform.model.NotificationReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationReminderRepository extends JpaRepository<NotificationReminder, Long> {
    List<NotificationReminder> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<NotificationReminder> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);
    long countByUserIdAndIsReadFalse(Long userId);
}
