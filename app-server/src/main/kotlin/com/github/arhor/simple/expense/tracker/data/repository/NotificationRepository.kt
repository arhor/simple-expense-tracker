package com.github.arhor.simple.expense.tracker.data.repository

import com.github.arhor.simple.expense.tracker.data.model.Notification
import com.github.arhor.simple.expense.tracker.data.model.projection.CompactNotificationProjection
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.util.UUID
import java.util.stream.Stream

interface NotificationRepository : CrudRepository<Notification, UUID> {

    @Query(name = "Notification.findAllByTargetingIdIn")
    fun findAllByTargetUserIdIn(targetUserIds: Collection<Long>): Stream<CompactNotificationProjection>
}
