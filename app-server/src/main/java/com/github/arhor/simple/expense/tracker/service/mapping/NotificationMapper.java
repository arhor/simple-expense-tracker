package com.github.arhor.simple.expense.tracker.service.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.github.arhor.simple.expense.tracker.data.model.Notification;
import com.github.arhor.simple.expense.tracker.model.NotificationDTO;

@Mapper(config = SharedMappingConfig.class)
public interface NotificationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    Notification mapDtoToEntity(NotificationDTO dto, Long targetUserId, Long sourceUserId);

    NotificationDTO mapProjectionToDto(Notification.CompactProjection projection);
}
