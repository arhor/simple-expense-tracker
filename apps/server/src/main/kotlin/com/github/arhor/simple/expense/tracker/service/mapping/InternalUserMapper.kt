package com.github.arhor.simple.expense.tracker.service.mapping

import com.github.arhor.simple.expense.tracker.data.model.InternalUser
import com.github.arhor.simple.expense.tracker.data.model.projection.CompactInternalUserProjection
import com.github.arhor.simple.expense.tracker.model.UserRequestDTO
import com.github.arhor.simple.expense.tracker.model.UserResponseDTO
import com.github.arhor.simple.expense.tracker.service.CustomUserDetails

interface InternalUserMapper {
    fun mapToInternalUser(request: UserRequestDTO): InternalUser
    fun mapToUserResponse(entity: InternalUser): UserResponseDTO
    fun mapToUserResponse(projection: CompactInternalUserProjection): UserResponseDTO
    fun mapToUserDetails(user: InternalUser): CustomUserDetails
}
