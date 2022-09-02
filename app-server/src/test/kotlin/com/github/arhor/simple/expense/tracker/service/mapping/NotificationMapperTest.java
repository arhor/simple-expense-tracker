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
        val id = (UUID) null;
        val userId = (Long) null;
        val message = "test-notification-message";

        val notification = new Notification.Projection(id, userId, message, severity);

        // when
        val result = notificationMapper.mapProjectionToDto(notification);

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
        val expectedDTO = new NotificationDTO();

        // when
        val result = notificationMapper.mapProjectionToDto(null);

        // then
        assertThat(result)
            .isNotNull()
            .isEqualTo(expectedDTO);
    }

    @ParameterizedTest
    @EnumSource(NotificationDTO.Severity.class)
    void should_correctly_map_notification_dto_to_entity(final NotificationDTO.Severity severity) {
        // given
        val message = "test-notification-message";
        val userId = -1L;
        val createdBy = -2L;

        val dto = new NotificationDTO();
        dto.setSeverity(severity);
        dto.setMessage(message);

        // when
        val result = notificationMapper.mapDtoToEntity(dto, userId, createdBy);

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
        val expectedEntity = Notification.builder().build();

        // when
        val result = notificationMapper.mapDtoToEntity(null, null, null);

        // then
        assertThat(result)
            .isNotNull()
            .isEqualTo(expectedEntity);
    }
}
