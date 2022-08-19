package com.github.arhor.simple.expense.tracker.data.repository;

import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.github.arhor.simple.expense.tracker.data.model.Notification;

public interface NotificationRepository extends ReactiveCrudRepository<Notification, UUID> {

    Flux<Notification.Projection> findAllByTargetUserIdIn(Collection<Long> targetUserIds);
}
