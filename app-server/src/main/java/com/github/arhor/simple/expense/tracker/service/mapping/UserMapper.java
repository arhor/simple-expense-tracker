package com.github.arhor.simple.expense.tracker.service.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.github.arhor.simple.expense.tracker.config.mapping.IgnoreAuditProps;
import com.github.arhor.simple.expense.tracker.config.mapping.MapStructConfig;
import com.github.arhor.simple.expense.tracker.data.model.InternalUser;
import com.github.arhor.simple.expense.tracker.model.UserRequestDTO;
import com.github.arhor.simple.expense.tracker.model.UserResponseDTO;

@Mapper(config = MapStructConfig.class, uses = PasswordEncodingMapper.class)
public interface UserMapper {

    @IgnoreAuditProps
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "externalProvider", ignore = true)
    @Mapping(target = "password", qualifiedByName = "encodePassword")
    InternalUser mapToUser(UserRequestDTO request);

    UserResponseDTO mapToResponse(InternalUser entity);

    UserResponseDTO mapToResponse(InternalUser.CompactProjection projection);
}
