package com.devsu.person_service.adapter.out.messaging;

import com.devsu.person_service.adapter.out.messaging.dto.ClientUpdatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RabbitMQClientEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private RabbitMQClientEventPublisher publisher;

    private static final String TEST_EXCHANGE = "client.exchange";
    private static final String TEST_ROUTING_KEY = "client.updated";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(publisher, "exchange", TEST_EXCHANGE);
        ReflectionTestUtils.setField(publisher, "routingKey", TEST_ROUTING_KEY);
    }

    @Test
    void publish_withValidData_shouldPublishEventSuccessfully() {
        String clientId = "CLI-12345";
        String clientName = "John Doe";
        Boolean clientStatus = true;
        String eventType = "CLIENT_UPDATED";

        ArgumentCaptor<ClientUpdatedEvent> eventCaptor = ArgumentCaptor.forClass(ClientUpdatedEvent.class);

        StepVerifier.create(publisher.publish(clientId, clientName, clientStatus, eventType))
                .verifyComplete();

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq(TEST_EXCHANGE),
                eq(TEST_ROUTING_KEY),
                eventCaptor.capture()
        );

        ClientUpdatedEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getClientId()).isEqualTo(clientId);
        assertThat(capturedEvent.getClientName()).isEqualTo(clientName);
        assertThat(capturedEvent.getClientStatus()).isEqualTo(clientStatus);
        assertThat(capturedEvent.getEventType()).isEqualTo(eventType);
        assertThat(capturedEvent.getTimestamp()).isNotNull();
        assertThat(capturedEvent.getTimestamp()).isBefore(LocalDateTime.now().plusSeconds(1));
    }

    @Test
    void publish_shouldSetTimestampOnEventCreation() {
        String clientId = "CLI-12345";
        String clientName = "John Doe";
        Boolean clientStatus = true;
        String eventType = "CLIENT_UPDATED";

        LocalDateTime beforePublish = LocalDateTime.now();

        ArgumentCaptor<ClientUpdatedEvent> eventCaptor = ArgumentCaptor.forClass(ClientUpdatedEvent.class);

        StepVerifier.create(publisher.publish(clientId, clientName, clientStatus, eventType))
                .verifyComplete();

        verify(rabbitTemplate).convertAndSend(
                eq(TEST_EXCHANGE),
                eq(TEST_ROUTING_KEY),
                eventCaptor.capture()
        );

        ClientUpdatedEvent capturedEvent = eventCaptor.getValue();
        LocalDateTime afterPublish = LocalDateTime.now();

        assertThat(capturedEvent.getTimestamp()).isNotNull();
        assertThat(capturedEvent.getTimestamp()).isAfterOrEqualTo(beforePublish);
        assertThat(capturedEvent.getTimestamp()).isBeforeOrEqualTo(afterPublish);
    }

    @Test
    void publish_shouldCreateEventWithCorrectEventType() {
        String clientId = "CLI-12345";
        String clientName = "John Doe";
        Boolean clientStatus = false;
        String eventType = "CLIENT_UPDATED";

        ArgumentCaptor<ClientUpdatedEvent> eventCaptor = ArgumentCaptor.forClass(ClientUpdatedEvent.class);

        StepVerifier.create(publisher.publish(clientId, clientName, clientStatus, eventType))
                .verifyComplete();

        verify(rabbitTemplate).convertAndSend(
                eq(TEST_EXCHANGE),
                eq(TEST_ROUTING_KEY),
                eventCaptor.capture()
        );

        ClientUpdatedEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getEventType()).isEqualTo("CLIENT_UPDATED");
    }

    @Test
    void publish_whenRabbitTemplateThrowsException_shouldHandleErrorGracefully() {
        String clientId = "CLI-12345";
        String clientName = "John Doe";
        Boolean clientStatus = true;
        String eventType = "CLIENT_UPDATED";

        doThrow(new RuntimeException("RabbitMQ connection failed"))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(ClientUpdatedEvent.class));

        StepVerifier.create(publisher.publish(clientId, clientName, clientStatus, eventType))
                .verifyComplete();

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq(TEST_EXCHANGE),
                eq(TEST_ROUTING_KEY),
                any(ClientUpdatedEvent.class)
        );
    }

    @Test
    void publish_whenErrorOccurs_shouldReturnMonoEmptyWithoutThrowing() {
        String clientId = "CLI-99999";
        String clientName = "Error Client";
        Boolean clientStatus = true;
        String eventType = "CLIENT_UPDATED";

        doThrow(new RuntimeException("Serialization error"))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(ClientUpdatedEvent.class));

        StepVerifier.create(publisher.publish(clientId, clientName, clientStatus, eventType))
                .expectComplete()
                .verify();
    }

    @Test
    void publish_shouldVerifyClientUpdatedEventStructure() {
        String clientId = "CLI-54321";
        String clientName = "Jane Smith";
        Boolean clientStatus = false;
        String eventType = "CLIENT_UPDATED";

        ArgumentCaptor<ClientUpdatedEvent> eventCaptor = ArgumentCaptor.forClass(ClientUpdatedEvent.class);

        StepVerifier.create(publisher.publish(clientId, clientName, clientStatus, eventType))
                .verifyComplete();

        verify(rabbitTemplate).convertAndSend(
                eq(TEST_EXCHANGE),
                eq(TEST_ROUTING_KEY),
                eventCaptor.capture()
        );

        ClientUpdatedEvent event = eventCaptor.getValue();
        assertThat(event).isNotNull();
        assertThat(event.getClientId()).isEqualTo(clientId);
        assertThat(event.getClientName()).isEqualTo(clientName);
        assertThat(event.getClientStatus()).isEqualTo(clientStatus);
        assertThat(event.getEventType()).isEqualTo(eventType);
        assertThat(event.getTimestamp()).isInstanceOf(LocalDateTime.class);
    }
}
