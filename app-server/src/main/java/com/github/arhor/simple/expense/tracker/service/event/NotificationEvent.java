package com.github.arhor.simple.expense.tracker.service.event;

import com.github.arhor.simple.expense.tracker.model.NotificationDTO;

public record NotificationEvent(Long senderId, Long userId, NotificationDTO notification) {
}
