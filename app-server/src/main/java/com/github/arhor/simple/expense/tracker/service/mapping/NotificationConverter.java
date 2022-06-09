package com.github.arhor.simple.expense.tracker.service.mapping;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.github.arhor.simple.expense.tracker.config.mapping.MapStructConfig;
import com.github.arhor.simple.expense.tracker.data.model.Notification;
import com.github.arhor.simple.expense.tracker.model.NotificationDTO;

@Mapper(config = MapStructConfig.class)
public interface NotificationConverter {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    Notification mapDtoToEntity(NotificationDTO dto);

    @InheritInverseConfiguration
    NotificationDTO mapEntityToDto(Notification entity);
}
