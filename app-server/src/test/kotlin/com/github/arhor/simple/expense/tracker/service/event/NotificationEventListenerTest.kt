package com.github.arhor.simple.expense.tracker.service.event

import com.github.arhor.simple.expense.tracker.model.NotificationDTO
import com.github.arhor.simple.expense.tracker.service.NotificationService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class NotificationEventListenerTest {

    @MockK
    private lateinit var notificationService: NotificationService

    @InjectMockKs
    private lateinit var notificationEventListener: NotificationEventListener

    @Test
    fun `should call notification service with expected parameters`() {
        // given
        val expectedSourceUserId = -1L
        val expectedTargetUserId = -2L
        val expectedNotification = NotificationDTO()

        every { notificationService.sendNotification(senderId = any(), userId = any(), dto = any()) } just runs

        // when
        notificationEventListener.handleNotificationEvent(
            event = NotificationEvent(
                sourceUserId = expectedSourceUserId,
                targetUserId = expectedTargetUserId,
                notification = expectedNotification,
            )
        )

        // then
        verify(exactly = 1) {
            notificationService.sendNotification(
                senderId = expectedSourceUserId,
                userId = expectedTargetUserId,
                dto = expectedNotification,
            )
        }
    }
}
