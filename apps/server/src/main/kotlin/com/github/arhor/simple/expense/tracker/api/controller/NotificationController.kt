package com.github.arhor.simple.expense.tracker.api.controller


import com.github.arhor.simple.expense.tracker.model.NotificationDTO
import com.github.arhor.simple.expense.tracker.service.CustomUserDetails
import com.github.arhor.simple.expense.tracker.service.NotificationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/notifications")
@PreAuthorize("isAuthenticated()")
@SecurityRequirements(
    value = [
        SecurityRequirement(name = "authenticated", scopes = ["USER"])
    ]
)
class NotificationController(
    private val notificationService: NotificationService,
) {

    @Operation(
        summary = "",
        description = "",
    )
    @PostMapping(path = ["/subscribe"], produces = [TEXT_EVENT_STREAM_VALUE])
    fun subscribe(@AuthenticationPrincipal currentUser: CustomUserDetails): SseEmitter {
        return notificationService.subscribe(currentUser.id!!)
    }

    @Operation(
        summary = "",
        description = "",
    )
    @PostMapping
    fun postNotification(
        @RequestParam userId: Long,
        @RequestBody requestBody: NotificationDTO,
        @AuthenticationPrincipal currentUser: CustomUserDetails,
    ) {
        notificationService.handleNotification(currentUser.id!!, userId, requestBody)
    }
}
