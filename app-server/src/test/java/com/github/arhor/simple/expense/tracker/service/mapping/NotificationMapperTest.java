package com.github.arhor.simple.expense.tracker.service.mapping;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.arhor.simple.expense.tracker.data.model.Notification;
import com.github.arhor.simple.expense.tracker.model.NotificationDTO;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationMapperTest extends MapperTestBase {

    @Autowired
    private NotificationMapper notificationMapper;

    @ParameterizedTest
    @ValueSource(strings = {"SUCCESS", "FAILURE"})
    void should_correctly_map_notification_entity_to_dto(final String severity) {
        // given
        var id = (UUID) null;
        var userId = (Long) null;
        var message = "test-notification-message";

        var notification = new Notification.CompactProjection(id, userId, message, severity);

        // when
        var result = notificationMapper.mapProjectionToDto(notification);

        // then
        assertThat(result)
            .isNotNull()
            .satisfies(
                dto -> {
                    assertThat(dto.getSeverity())
                        .as("severity")
                        .map(Enum::name)
                        .isNotEmpty()
                        .contains(notification.severity());
                },
                dto -> {
                    assertThat(dto.getMessage())
                        .as("message")
                        .isNotEmpty()
                        .contains(notification.message());
                }
            );
    }

    @Test
    void should_map_null_to_an_empty_notification_dto() {
        // given
        var expectedDTO = new NotificationDTO();

        // when
        var result = notificationMapper.mapProjectionToDto(null);

        // then
        assertThat(result)
            .isNotNull()
            .isEqualTo(expectedDTO);
    }

    @ParameterizedTest
    @EnumSource(NotificationDTO.Severity.class)
    void should_correctly_map_notification_dto_to_entity(final NotificationDTO.Severity severity) {
        // given
        var message = "test-notification-message";
        var userId = -1L;
        var createdBy = -2L;

        var dto = new NotificationDTO();
        dto.setSeverity(severity);
        dto.setMessage(message);

        // when
        var result = notificationMapper.mapDtoToEntity(dto, userId, createdBy);

        // then
        assertThat(result)
            .isNotNull()
            .satisfies(
                notification -> {
                    assertThat(notification.severity())
                        .as("severity")
                        .isNotNull()
                        .isEqualTo(severity.name());
                },
                notification -> {
                    assertThat(notification.message())
                        .as("message")
                        .isNotEmpty()
                        .contains(message);
                },
                notification -> {
                    assertThat(notification.targetUserId())
                        .as("targetUserId")
                        .isEqualTo(userId);
                },
                notification -> {
                    assertThat(notification.sourceUserId())
                        .as("sourceUserId")
                        .isEqualTo(createdBy);
                }
            );
    }

    @Test
    void should_map_null_to_an_empty_notification_entity() {
        // given
        var expectedEntity = Notification.builder().build();

        // when
        var result = notificationMapper.mapDtoToEntity(null, null, null);

        // then
        assertThat(result)
            .isNotNull()
            .isEqualTo(expectedEntity);
    }
}
