package com.github.arhor.simple.expense.tracker.service.impl

import com.github.arhor.simple.expense.tracker.data.model.Notification
import com.github.arhor.simple.expense.tracker.data.model.projection.CompactNotificationProjection
import com.github.arhor.simple.expense.tracker.data.repository.NotificationRepository
import com.github.arhor.simple.expense.tracker.model.NotificationDTO
import com.github.arhor.simple.expense.tracker.service.event.NotificationEvent
import com.github.arhor.simple.expense.tracker.service.mapping.NotificationMapper
import com.github.arhor.simple.expense.tracker.service.util.currentLocalDateTime
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
import java.time.LocalDateTime
import java.util.UUID
import java.util.stream.Stream

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
            sourceUserId = expectedSourceUserId,
            targetUserId = expectedTargetUserId,
            notification = expectedNotification,
        )

        // then
        verify(exactly = 1) { applicationEventPublisher.publishEvent(capture(notificationEvent)) }

        assertThat(notificationEvent.captured)
            .returns(expectedSourceUserId, from { it.sourceUserId })
            .returns(expectedTargetUserId, from { it.targetUserId })
            .returns(expectedNotification, from { it.notification })
    }

    @Test
    fun `should successfully send notification via SSE emitter without persisting it`() {
        // given
        val expectedSourceUserId = 1L
        val expectedTargetUserId = 2L
        val expectedNotification = NotificationDTO("test-message", NotificationDTO.Severity.SUCCESS)
        val eventBuilder = spyk(SseEmitter.event())
        val eventSource = notificationService.subscribe(expectedTargetUserId)

        mockkObject(eventSource)
        mockkStatic(SseEmitter::class)

        every { eventSource.send(any()) } just runs
        every { SseEmitter.event() } returns eventBuilder

        // when
        notificationService.sendNotification(
            sourceUserId = expectedSourceUserId,
            targetUserId = expectedTargetUserId,
            notification = expectedNotification,
        )

        // then
        verify(exactly = 1) { eventSource.send(eventBuilder) }
        verify(exactly = 1) { eventBuilder.id(any()) }
        verify(exactly = 1) { eventBuilder.name(any()) }
        verify(exactly = 1) { eventBuilder.data(expectedNotification) }
        verify { applicationEventPublisher wasNot called }
        verify { notificationRepository wasNot called }
        verify { notificationMapper wasNot called }

        // finally
        unmockkObject(eventSource)
        unmockkStatic(SseEmitter::class)
    }

    @Test
    fun `should persist notification when there are not target user within current notification subscribers`() {
        mockkStatic("com.github.arhor.simple.expense.tracker.service.util.TimeUtilsKt") {
            // given
            val expectedSourceUserId = 1L
            val expectedTargetUserId = 2L
            val expectedTimestamp = LocalDateTime.parse("2022-09-08T12:00:00")
            val expectedSeverity = NotificationDTO.Severity.SUCCESS.name
            val expectedMessage = "test-message"
            val expectedNotification = NotificationDTO(expectedMessage, NotificationDTO.Severity.valueOf(expectedSeverity))

            val notification = slot<Notification>()

            every { currentLocalDateTime() } returns expectedTimestamp
            every { notificationRepository.save(any()) } returnsArgument 0
            every { notificationMapper.mapDtoToEntity(any(), any(), any(), any()) } answers {
                Notification(
                    message = arg<NotificationDTO>(0).message!!,
                    severity = arg<NotificationDTO>(0).severity!!.name,
                    targetUserId = arg(1),
                    sourceUserId = arg(2),
                    timestamp = arg(3),
                )
            }

            // when
            notificationService.sendNotification(
                sourceUserId = expectedSourceUserId,
                targetUserId = expectedTargetUserId,
                notification = expectedNotification,
            )

            // then
            verify(exactly = 1) {
                notificationMapper.mapDtoToEntity(
                    dto = expectedNotification,
                    targetUserId = expectedTargetUserId,
                    sourceUserId = expectedSourceUserId,
                    timestamp = expectedTimestamp
                )
            }
            verify(exactly = 1) { notificationRepository.save(capture(notification)) }
            verify { applicationEventPublisher wasNot called }

            assertThat(notification.captured)
                .returns(expectedSourceUserId, from { it.sourceUserId })
                .returns(expectedTargetUserId, from { it.targetUserId })
                .returns(expectedTimestamp, from { it.timestamp })
                .returns(expectedSeverity, from { it.severity })
                .returns(expectedMessage, from { it.message })
        }
    }

    @Test
    fun `should successfully send persisted notification via SSE emitter then delete it`() {
        // given
        val expectedId = UUID.randomUUID()
        val expectedSeverity = NotificationDTO.Severity.SUCCESS.name
        val expectedMessage = "test-message"
        val expectedTargetUserId = 2L

        val compactNotificationProjection = CompactNotificationProjection(
            id = expectedId,
            severity = expectedSeverity,
            message = expectedMessage,
            targetUserId = expectedTargetUserId
        )

        val eventSource = notificationService.subscribe(expectedTargetUserId)
        val eventBuilder = spyk(SseEmitter.event())
        val notification = slot<NotificationDTO>()

        mockkObject(eventSource)
        mockkStatic(SseEmitter::class)

        every { notificationRepository.findAllByTargetUserIdIn(any()) } answers {
            Stream.of(
                compactNotificationProjection
            )
        }
        every { notificationMapper.mapProjectionToDto(any()) } answers {
            arg<CompactNotificationProjection>(0).let {
                NotificationDTO(
                    it.message,
                    NotificationDTO.Severity.valueOf(it.severity),
                )
            }
        }
        every { eventSource.send(any()) } just runs
        every { SseEmitter.event() } returns eventBuilder
        every { notificationRepository.deleteById(any()) } just runs

        // when
        notificationService.sendNotifications()

        // then
        verify(exactly = 1) { notificationRepository.findAllByTargetUserIdIn(setOf(expectedTargetUserId)) }
        verify(exactly = 1) { notificationMapper.mapProjectionToDto(compactNotificationProjection) }
        verify(exactly = 1) { notificationRepository.deleteById(expectedId) }
        verify(exactly = 1) { eventSource.send(eventBuilder) }
        verify(exactly = 1) { eventBuilder.id(any()) }
        verify(exactly = 1) { eventBuilder.name(any()) }
        verify(exactly = 1) { eventBuilder.data(capture(notification)) }
        verify { applicationEventPublisher wasNot called }

        assertThat(notification.captured)
            .isNotNull

        // finally
        unmockkObject(eventSource)
        unmockkStatic(SseEmitter::class)
    }

    @Test
    fun `should not interact with other components when there are not subscribers at all`() {
        // given
        notificationService.unsubscribeAll()

        // when
        notificationService.sendNotifications()

        // then
        verify { applicationEventPublisher wasNot called }
        verify { notificationRepository wasNot called }
        verify { notificationMapper wasNot called }
    }
}
