package com.github.arhor.simple.expense.tracker.service.mapping.impl

import com.github.arhor.simple.expense.tracker.data.model.Notification
import com.github.arhor.simple.expense.tracker.data.model.projection.CompactNotificationProjection
import com.github.arhor.simple.expense.tracker.model.NotificationDTO
import com.github.arhor.simple.expense.tracker.service.mapping.NotificationMapper
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class NotificationMapperImpl : NotificationMapper {
    override fun mapDtoToEntity(
        dto: NotificationDTO,
        targetUserId: Long,
        sourceUserId: Long,
        timestamp: LocalDateTime
    ) = Notification(
        message = dto.message,
        severity = dto.severity.name,
        sourceUserId = sourceUserId,
        targetUserId = targetUserId,
        timestamp = timestamp,
    )

    override fun mapProjectionToDto(projection: CompactNotificationProjection) = NotificationDTO(
        projection.message,
        NotificationDTO.Severity.valueOf(projection.severity),
    )
}
