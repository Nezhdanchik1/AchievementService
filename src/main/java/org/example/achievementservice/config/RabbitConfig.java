package org.example.achievementservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_STATISTICS = "statistics-exchange";
    public static final String QUEUE_ACHIEVEMENTS = "achievement-service-queue";
    public static final String ROUTING_KEY_ACTIONS = "user.action.#";

    @Bean
    public Queue achievementQueue() {
        return new Queue(QUEUE_ACHIEVEMENTS, true);
    }

    @Bean
    public TopicExchange statisticsExchange() {
        return new TopicExchange(EXCHANGE_STATISTICS);
    }

    @Bean
    public Binding binding(Queue achievementQueue, TopicExchange statisticsExchange) {
        return BindingBuilder.bind(achievementQueue).to(statisticsExchange).with(ROUTING_KEY_ACTIONS);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
