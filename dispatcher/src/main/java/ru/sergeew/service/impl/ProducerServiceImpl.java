package ru.sergeew.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.sergeew.service.ProducerService;

/**
 * Реализация интерфейса {@link ProducerService} для отправки объекта {@link Update} в RabbitMQ очередь.
 */
@Service
@Log4j
@AllArgsConstructor
public class ProducerServiceImpl implements ProducerService {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void produce(String rabbitQueue, Update update) {
        log.info("Received update and sent it to reminder-service. Update: " + update);
        rabbitTemplate.convertAndSend(rabbitQueue, update);
    }
}
