package com.github.arhor.simple.expense.tracker.data.repository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;

import com.github.arhor.simple.expense.tracker.data.model.InternalUser;
import com.github.arhor.simple.expense.tracker.data.model.Notification;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationRepositoryTest extends RepositoryTestBase {

    @Container
    private static final JdbcDatabaseContainer<?> db = createDatabaseContainer();

    @DynamicPropertySource
    static void registerDynamicProperties(final DynamicPropertyRegistry registry) {
        registerDatasource(registry, db);
    }

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void should_return_all_notifications_where_user_id_is_one_of_the_passed_ids() {
        // given
        var usersIds = createUsersStream()
            .map(InternalUser::getId)
            .toList();

        var expectedNotifications = usersIds.stream()
            .flatMap(this::createNotificationsStream)
            .map(notificationRepository::save)
            .toList();

        // when
        var result = notificationRepository.findAllByUserIdIn(usersIds);

        // then
        assertThat(result)
            .containsExactlyInAnyOrderElementsOf(expectedNotifications);
    }

    private Stream<InternalUser> createUsersStream() {
        return IntStream.range(0, 5).mapToObj(this::createTestUser);
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
