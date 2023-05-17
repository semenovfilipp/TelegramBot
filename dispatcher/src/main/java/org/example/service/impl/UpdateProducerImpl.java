package org.example.service.impl;

import lombok.extern.log4j.Log4j;
import org.example.service.UpdateProducer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

///Осуществляет реализации сервисов UpdateProducer|AnswerConsumer
//Формат програмирования интерфейсами для упрощения удобства тестирования

@Component
@Log4j
public class UpdateProducerImpl implements UpdateProducer {
    private final RabbitTemplate rabbitTemplate;

    //Передача сообщений в RabbitMQ
    public UpdateProducerImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void produce(String rabbitQueue, Update update) {
        log.debug(update.getMessage().getText());
        ///Создание бина с именем очереди и update, который преобразуется в json
        rabbitTemplate.convertAndSend(rabbitQueue, update);

    }
}
