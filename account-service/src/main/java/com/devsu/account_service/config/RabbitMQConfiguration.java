package com.devsu.account_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {
    
    @Value("${rabbitmq.queue.client-updated}")
    private String clientUpdatedQueue;
    
    @Value("${rabbitmq.exchange.client}")
    private String clientExchange;
    
    @Value("${rabbitmq.routing-key.client-updated}")
    private String clientUpdatedRoutingKey;
    
    @Bean
    public Queue clientUpdatedQueue() {
        return new Queue(clientUpdatedQueue, true);
    }
    
    @Bean
    public TopicExchange clientExchange() {
        return new TopicExchange(clientExchange);
    }
    
    @Bean
    public Binding clientUpdatedBinding() {
        return BindingBuilder
            .bind(clientUpdatedQueue())
            .to(clientExchange())
            .with(clientUpdatedRoutingKey);
    }
    
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
