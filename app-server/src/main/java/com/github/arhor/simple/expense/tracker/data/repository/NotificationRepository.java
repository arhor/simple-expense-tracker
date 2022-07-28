package com.github.arhor.simple.expense.tracker.data.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.github.arhor.simple.expense.tracker.data.model.Notification;

public interface NotificationRepository extends CrudRepository<Notification, UUID> {

    List<Notification.CompactProjection> findAllByTargetUserIdIn(Collection<Long> targetUserIds);
}
