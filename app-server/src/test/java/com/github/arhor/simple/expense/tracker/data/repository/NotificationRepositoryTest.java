package com.github.arhor.simple.expense.tracker.data.repository;

import lombok.val;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.github.arhor.simple.expense.tracker.data.model.InternalUser;
import com.github.arhor.simple.expense.tracker.data.model.Notification;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;

class NotificationRepositoryTest extends RepositoryTestBase {

    @Test
    void should_return_all_notifications_where_user_id_is_one_of_the_passed_ids() {
        // given
        val usersIds = createUsersStream()
            .map(InternalUser::id)
            .toList();

        val notifications = usersIds.stream()
            .flatMap(this::createNotificationsStream)
            .map(notificationRepository::save)
            .collect(toMap(Notification::id, Function.identity()));

        // when
        val result = notificationRepository.findAllByTargetUserIdIn(usersIds);

        // then
        assertThat(result)
            .isNotEmpty()
            .allSatisfy(projection -> {
                val currentId = projection.id();
                val currentUserId = projection.targetUserId();

                assertThat(notifications.get(currentId))
                    .as(() -> "notification: " + currentId + ", user id: " + currentUserId)
                    .isNotNull()
                    .satisfies(
                        notification -> {
                            assertThat(projection.id())
                                .as("id")
                                .isEqualTo(notification.id());
                        },
                        notification -> {
                            assertThat(projection.targetUserId())
                                .as("targetUserId")
                                .isEqualTo(notification.targetUserId());
                        },
                        notification -> {
                            assertThat(projection.message())
                                .as("message")
                                .isEqualTo(notification.message());
                        },
                        notification -> {
                            assertThat(projection.severity())
                                .as("severity")
                                .isEqualTo(notification.severity());
                        }
                    );
            });
    }

    private Stream<InternalUser> createUsersStream() {
        return IntStream.range(0, 5).mapToObj(this::createPersistedTestUser);
    }

    private Stream<Notification> createNotificationsStream(final Long targetUserId) {
        return IntStream.range(0, 3).mapToObj(number ->
            Notification.builder()
                .targetUserId(targetUserId)
                .message("test-message-" + number)
                .severity("INFO")
                .timestamp(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS))
                .build()
        );
    }
}
