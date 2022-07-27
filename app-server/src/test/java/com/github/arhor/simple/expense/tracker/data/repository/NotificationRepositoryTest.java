package com.github.arhor.simple.expense.tracker.data.repository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.arhor.simple.expense.tracker.data.model.InternalUser;
import com.github.arhor.simple.expense.tracker.data.model.Notification;

import static com.github.arhor.simple.expense.tracker.data.repository.TestUtils.createPersistedTestUser;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;

class NotificationRepositoryTest extends RepositoryTestBase {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void should_return_all_notifications_where_user_id_is_one_of_the_passed_ids() {
        // given
        var usersIds = createUsersStream()
            .map(InternalUser::getId)
            .toList();

        var notifications = usersIds.stream()
            .flatMap(this::createNotificationsStream)
            .map(notificationRepository::save)
            .collect(toMap(Notification::getId, Function.identity()));

        // when
        var result = notificationRepository.findAllByUserIdIn(usersIds);

        // then
        assertThat(result)
            .isNotEmpty()
            .allSatisfy(projection -> {
                var currentId = projection.id();
                var currentUserId = projection.userId();

                assertThat(notifications.get(currentId))
                    .as(() -> "notification: " + currentId + ", user id: " + currentUserId)
                    .isNotNull()
                    .satisfies(
                        notification -> {
                            assertThat(projection.id())
                                .as("id")
                                .isEqualTo(notification.getId());
                        },
                        notification -> {
                            assertThat(projection.userId())
                                .as("userId")
                                .isEqualTo(notification.getUserId());
                        },
                        notification -> {
                            assertThat(projection.message())
                                .as("message")
                                .isEqualTo(notification.getMessage());
                        },
                        notification -> {
                            assertThat(projection.severity())
                                .as("severity")
                                .isEqualTo(notification.getSeverity());
                        }
                    );
            });
    }

    private Stream<InternalUser> createUsersStream() {
        return IntStream.range(0, 5).mapToObj(number -> createPersistedTestUser(userRepository, number));
    }

    private Stream<Notification> createNotificationsStream(final Long userId) {
        return IntStream.range(0, 3).mapToObj(number ->
            Notification.builder()
                .userId(userId)
                .message("test-message-" + number)
                .severity("INFO")
                .timestamp(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS))
                .build()
        );
    }
}
