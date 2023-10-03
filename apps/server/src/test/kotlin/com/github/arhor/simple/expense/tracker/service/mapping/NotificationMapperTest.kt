package com.github.arhor.simple.expense.tracker.service.mapping

import com.github.arhor.simple.expense.tracker.data.model.projection.CompactNotificationProjection
import com.github.arhor.simple.expense.tracker.model.NotificationDTO
import com.github.arhor.simple.expense.tracker.model.NotificationDTO.Severity
import com.github.arhor.simple.expense.tracker.util.currentLocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID
import java.util.function.Consumer

internal class NotificationMapperTest : MapperTestBase() {

    @Autowired
    private lateinit var notificationMapper: NotificationMapper

    @ParameterizedTest
    @ValueSource(strings = ["SUCCESS", "FAILURE"])
    fun `should correctly map notification entity to dto`(severity: String) {
        // given
        val id = UUID.randomUUID()
        val userId = -1L
        val message = "test-notification-message"

        val notification = CompactNotificationProjection(
            id = id,
            severity = severity,
            message = message,
            targetUserId = userId,
        )

        // when
        val result = notificationMapper.mapProjectionToDto(notification)

        // then
        assertThat(result)
            .isNotNull
            .satisfies(
                Consumer {
                    assertThat(it.severity!!.name)
                        .describedAs("severity")
                        .isEqualTo(notification.severity)
                },
                {
                    assertThat(it.message)
                        .describedAs("message")
                        .isEqualTo(notification.message)
                }
            )
    }

    @ParameterizedTest
    @EnumSource(Severity::class)
    fun `should correctly map notification dto to entity`(severity: Severity) {
        // given
        val message = "test-notification-message"
        val timestamp = currentLocalDateTime()
        val sourceUserId = -1L
        val targetUserId = -2L

        val dto = NotificationDTO().apply {
            this.severity = severity
            this.message = message
        }

        // when
        val result = notificationMapper.mapDtoToEntity(dto, targetUserId, sourceUserId, timestamp)

        // then
        assertThat(result)
            .isNotNull
            .satisfies(
                Consumer {
                    assertThat(it.severity)
                        .describedAs("severity")
                        .isNotNull
                        .isEqualTo(severity.name)
                },
                {
                    assertThat(it.message)
                        .describedAs("message")
                        .isNotEmpty
                        .contains(message)
                },
                {
                    assertThat(it.targetUserId)
                        .describedAs("targetUserId")
                        .isEqualTo(targetUserId)
                },
                {
                    assertThat(it.sourceUserId)
                        .describedAs("sourceUserId")
                        .isEqualTo(sourceUserId)
                },
                {
                    assertThat(it.timestamp)
                        .describedAs("timestamp")
                        .isEqualTo(timestamp)
                }
            )
    }
}
