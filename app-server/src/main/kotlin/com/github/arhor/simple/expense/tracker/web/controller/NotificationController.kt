package com.github.arhor.simple.expense.tracker.web.controller


import com.github.arhor.simple.expense.tracker.model.NotificationDTO
import com.github.arhor.simple.expense.tracker.service.NotificationService
import com.github.arhor.simple.expense.tracker.service.UserService
import org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/notifications")
@PreAuthorize("isAuthenticated()")
class NotificationController(
    private val notificationService: NotificationService,
    private val userService: UserService,
) {

    @PostMapping(path = ["/subscribe"], produces = [TEXT_EVENT_STREAM_VALUE])
    fun subscribe(auth: Authentication): SseEmitter {
        val currentUserId = userService.determineUserId(auth)
        return notificationService.subscribe(currentUserId)
    }

    @PostMapping(path = ["/unsubscribe"])
    fun unsubscribe(auth: Authentication) {
        val currentUserId = userService.determineUserId(auth)
        notificationService.unsubscribe(currentUserId)
    }

    @PostMapping
    fun postNotification(
        @RequestParam userId: Long,
        @RequestBody dto: NotificationDTO,
        auth: Authentication,
    ) {
        val currentUserId = userService.determineUserId(auth)
        notificationService.handleNotification(currentUserId, userId, dto)
    }
}
