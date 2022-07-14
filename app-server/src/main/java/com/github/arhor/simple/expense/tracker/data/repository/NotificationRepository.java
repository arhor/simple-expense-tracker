package com.github.arhor.simple.expense.tracker.data.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;

import com.github.arhor.simple.expense.tracker.data.model.Notification;

public interface NotificationRepository extends BaseRepository<Notification, UUID> {

    @Query("""
        SELECT n.*
        FROM notifications n
        WHERE n.user_id IN (:userIds)
        FOR UPDATE SKIP LOCKED
        LIMIT 50""")
    List<Notification> findAllByUserIdIn(Iterable<Long> userIds);
}
