package com.github.arhor.simple.expense.tracker.service.mapping;

import com.github.arhor.simple.expense.tracker.data.model.InternalUser;
import com.github.arhor.simple.expense.tracker.data.model.projection.CompactInternalUserProjection;
import com.github.arhor.simple.expense.tracker.model.UserRequestDTO;
import com.github.arhor.simple.expense.tracker.model.UserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(config = SharedMappingConfig.class)
public abstract class InternalUserMapper {

    @Autowired
    protected PasswordEncoder encoder;

    @IgnoreAuditProps
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "externalProvider", ignore = true)
    @Mapping(target = "password", expression = "java(encoder.encode(request.getPassword()))")
    @Mapping(target = "currency", defaultValue = "USD")
    public abstract InternalUser mapToUser(UserRequestDTO request);

    public abstract UserResponseDTO mapToResponse(InternalUser entity);

    public abstract UserResponseDTO mapToResponse(CompactInternalUserProjection projection);
}
