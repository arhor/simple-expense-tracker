package com.github.arhor.simple.expense.tracker.service.impl

import com.github.arhor.simple.expense.tracker.data.repository.NotificationRepository
import com.github.arhor.simple.expense.tracker.model.NotificationDTO
import com.github.arhor.simple.expense.tracker.service.event.NotificationEvent
import com.github.arhor.simple.expense.tracker.service.mapping.NotificationMapper
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.slot
import io.mockk.spyk
import io.mockk.unmockkObject
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.ApplicationEventPublisher
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@ExtendWith(MockKExtension::class)
internal class NotificationServiceImplTest {

    @MockK
    private lateinit var applicationEventPublisher: ApplicationEventPublisher

    @MockK
    private lateinit var notificationRepository: NotificationRepository

    @MockK
    private lateinit var notificationMapper: NotificationMapper

    @InjectMockKs
    private lateinit var notificationService: NotificationServiceImpl

    @Test
    fun `should subscribe once and then return the same event source on each call`() {
        // given
        val subscriberId = 1L

        // when
        val result = runBlocking(Dispatchers.Default) {
            (1..10).map {
                async {
                    notificationService.subscribe(subscriberId)
                }
            }.awaitAll()
        }

        // then
        assertThat(result)
            .hasSize(10)
            .containsOnly(result.first())
    }

    @Test
    fun `should publish notification event with expected data`() {
        // given
        val expectedSourceUserId = 1L
        val expectedTargetUserId = 2L
        val expectedNotification = NotificationDTO("test-message", NotificationDTO.Severity.SUCCESS)

        val notificationEvent = slot<NotificationEvent>()

        every { applicationEventPublisher.publishEvent(any<NotificationEvent>()) } just runs

        // when
        notificationService.handleNotification(
            senderId = expectedSourceUserId,
            userId = expectedTargetUserId,
            dto = expectedNotification,
        )

        // then
        verify(exactly = 1) { applicationEventPublisher.publishEvent(capture(notificationEvent)) }

        assertThat(notificationEvent.captured)
            .returns(expectedSourceUserId, from { it.senderId })
            .returns(expectedTargetUserId, from { it.userId })
            .returns(expectedNotification, from { it.notification })
    }

    @Test
    fun `should successfully send notification via SSE emitter without persiting it`() {
        // given
        val expectedSourceUserId = 1L
        val expectedTargetUserId = 2L
        val expectedNotification = NotificationDTO("test-message", NotificationDTO.Severity.SUCCESS)
        val sseEventBuilder = spyk(SseEmitter.event())
        val eventSource = notificationService.subscribe(expectedTargetUserId)

        mockkObject(eventSource)
        mockkStatic(SseEmitter::class)

        every { eventSource.send(any()) } just runs
        every { SseEmitter.event() } returns sseEventBuilder

        // when
        notificationService.sendNotification(
            senderId = expectedSourceUserId,
            userId = expectedTargetUserId,
            dto = expectedNotification,
        )

        // then
        verify(exactly = 1) { eventSource.send(sseEventBuilder) }
        verify(exactly = 1) { sseEventBuilder.id(any()) }
        verify(exactly = 1) { sseEventBuilder.name(any()) }
        verify(exactly = 1) { sseEventBuilder.data(expectedNotification) }

        verify { applicationEventPublisher wasNot called }
        verify { notificationRepository wasNot called }
        verify { notificationMapper wasNot called }

        // finally
        unmockkObject(eventSource)
        unmockkStatic(SseEmitter::class)
    }
}
