package com.github.arhor.simple.expense.tracker.data.repository

import com.github.arhor.simple.expense.tracker.data.model.Notification
import com.github.arhor.simple.expense.tracker.data.model.projection.CompactNotificationProjection
import org.springframework.data.repository.CrudRepository
import java.util.*

interface NotificationRepository : CrudRepository<Notification, UUID> {

    fun findAllByTargetUserIdIn(targetUserIds: Collection<Long>): List<CompactNotificationProjection>
}
