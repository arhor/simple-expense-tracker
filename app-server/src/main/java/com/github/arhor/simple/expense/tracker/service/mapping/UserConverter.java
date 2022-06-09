package com.github.arhor.simple.expense.tracker.service.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.github.arhor.simple.expense.tracker.config.mapping.IgnoreAuditProps;
import com.github.arhor.simple.expense.tracker.config.mapping.MapStructConfig;
import com.github.arhor.simple.expense.tracker.data.model.InternalUser;
import com.github.arhor.simple.expense.tracker.model.UserRequest;
import com.github.arhor.simple.expense.tracker.model.UserResponse;

@Mapper(config = MapStructConfig.class, uses = PasswordEncoderConverter.class)
public interface UserConverter {

    @IgnoreAuditProps
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "externalProvider", ignore = true)
    @Mapping(target = "password", qualifiedByName = "encode")
    InternalUser mapToUser(UserRequest request);

    UserResponse mapToResponse(InternalUser user);
}
