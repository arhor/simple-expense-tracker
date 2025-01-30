package com.github.arhor.simple.expense.tracker.service.mapping.impl

import com.github.arhor.simple.expense.tracker.data.model.InternalUser
import com.github.arhor.simple.expense.tracker.data.model.projection.CompactInternalUserProjection
import com.github.arhor.simple.expense.tracker.model.UserRequestDTO
import com.github.arhor.simple.expense.tracker.model.UserResponseDTO
import com.github.arhor.simple.expense.tracker.service.CustomUserDetails
import com.github.arhor.simple.expense.tracker.service.mapping.InternalUserMapper
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component

@Component
class InternalUserMapperImpl : InternalUserMapper {

    override fun mapToInternalUser(request: UserRequestDTO) = InternalUser(
        username = request.username,
        password = request.password,
        currency = request.currency ?: "USD",
    )

    override fun mapToUserResponse(entity: InternalUser) = UserResponseDTO(
        entity.id,
        entity.username,
        entity.currency,
    )

    override fun mapToUserResponse(projection: CompactInternalUserProjection) = UserResponseDTO(
        projection.id,
        projection.username,
        projection.currency,
    )

    override fun mapToUserDetails(user: InternalUser) = CustomUserDetails(
        user.id,
        user.currency,
        user.username!!,
        user.password!!,
        listOf(SimpleGrantedAuthority("ROLE_USER"))
    )
}
