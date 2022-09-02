package com.github.arhor.simple.expense.tracker.data.repository

import com.github.arhor.simple.expense.tracker.data.model.InternalUser
import com.github.arhor.simple.expense.tracker.data.model.Notification
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.function.Consumer

internal class NotificationRepositoryTest : RepositoryTestBase() {

    @Test
    fun `should return all notifications where user id is one of the passed ids`() {
        // given
        val usersIds = createUsers().mapNotNull(InternalUser::id)

        val notifications = usersIds
            .flatMap { createNotifications(it) }
            .map(notificationRepository::save)
            .associateBy { it.id }

        // when
        val result = notificationRepository.findAllByTargetUserIdIn(usersIds)

        // then
        assertThat(result)
            .isNotEmpty
            .allSatisfy(Consumer { projection ->
                val currentId = projection.id
                val currentUserId = projection.targetUserId

                assertThat(notifications[currentId])
                    .describedAs("notification: $currentId, user id: $currentUserId")
                    .isNotNull
                    .satisfies(
                        Consumer {
                            assertThat(projection.id)
                                .describedAs("id")
                                .isEqualTo(it?.id)
                        },
                        {
                            assertThat(projection.targetUserId)
                                .describedAs("targetUserId")
                                .isEqualTo(it?.targetUserId)
                        },
                        {
                            assertThat(projection.message)
                                .describedAs("message")
                                .isEqualTo(it?.message)
                        },
                        {
                            assertThat(projection.severity)
                                .describedAs("severity")
                                .isEqualTo(it?.severity)
                        }
                    )
            })
    }

    private fun createUsers() = (0..5).map(::createPersistedTestUser)

    private fun createNotifications(targetUserId: Long) = (0..3).map {
        Notification(
            sourceUserId = -1,
            targetUserId = targetUserId,
            message = "test-message-$it",
            severity = "INFO",
            timestamp = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS),
        )
    }
}
