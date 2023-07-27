package ru.sergeew.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static ru.sergeew.model.RabbitQueue.*;

/**
 * Конфигурационный класс для настройки очередей RabbitMQ.
 */
@Configuration
public class RabbitConfiguration {
    /**
     * Создание бина Jackson2JsonMessageConverter.
     * Метод создает бин Jackson2JsonMessageConverter, который используется для
     * сериализации и десериализации сообщений в формате JSON при работе с RabbitMQ.
     *
     * @return Объект Jackson2JsonMessageConverter.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue textMessageUpdateQueue() {
        return new Queue(TEXT_MESSAGE_UPDATE);
    }

    @Bean
    public Queue answerMessageQueue() {
        return new Queue(RESPONSE_MESSAGE);
    }

    @Bean
    public Queue callbackQueryQueue() {
        return new Queue(CALLBACK_QUERY);
    }

    @Bean
    public Queue editAnswerMessageQueue() {
        return new Queue(EDIT_RESPONSE_MESSAGE);
    }

    @Bean
    public Queue deleteAnswerMessage() {
        return new Queue(DELETE_RESPONSE_MASSAGE);
    }
}
