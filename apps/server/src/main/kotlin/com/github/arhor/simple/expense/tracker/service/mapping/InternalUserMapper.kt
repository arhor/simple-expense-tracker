package com.github.arhor.simple.expense.tracker.service.mapping

import com.github.arhor.simple.expense.tracker.data.model.InternalUser
import com.github.arhor.simple.expense.tracker.data.model.projection.CompactInternalUserProjection
import com.github.arhor.simple.expense.tracker.model.UserRequestDTO
import com.github.arhor.simple.expense.tracker.model.UserResponseDTO
import com.github.arhor.simple.expense.tracker.service.CustomUserDetails
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.Arrays

@Mapper(config = MapstructCommonConfig::class, imports = [Arrays::class, SimpleGrantedAuthority::class])
interface InternalUserMapper {

    @IgnoreAuditProps
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "externalProvider", ignore = true)
    @Mapping(target = "currency", defaultValue = "USD")
    fun mapToInternalUser(request: UserRequestDTO): InternalUser

    fun mapToUserResponse(entity: InternalUser): UserResponseDTO

    fun mapToUserResponse(projection: CompactInternalUserProjection): UserResponseDTO

    @Mapping(target = "authorities", expression = "java(Arrays.asList(new SimpleGrantedAuthority(\"ROLE_USER\")))")
    fun mapToUserDetails(user: InternalUser): CustomUserDetails
}
