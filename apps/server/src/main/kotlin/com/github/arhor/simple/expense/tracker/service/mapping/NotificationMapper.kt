package com.github.arhor.simple.expense.tracker.service.mapping

import com.github.arhor.simple.expense.tracker.data.model.Notification
import com.github.arhor.simple.expense.tracker.data.model.projection.CompactNotificationProjection
import com.github.arhor.simple.expense.tracker.model.NotificationDTO
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import java.time.LocalDateTime

@Mapper(config = MapstructCommonConfig::class)
interface NotificationMapper {

    @Mapping(target = "id", ignore = true)
    fun mapDtoToEntity(
        dto: NotificationDTO,
        targetUserId: Long,
        sourceUserId: Long,
        timestamp: LocalDateTime
    ): Notification

    fun mapProjectionToDto(projection: CompactNotificationProjection): NotificationDTO
}
