package org.example.service.impl;

import lombok.extern.log4j.Log4j;
import org.example.service.ConsumerService;
import org.example.service.MainService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.example.model.RabbitQueue.*;

@Service
@Log4j
//Методы имплементированны каждый под свою очередь
public class ConsumerServiceImpl implements ConsumerService {

    //Делаем вызов MainService чтобы вызвать на нем processTextMessage и передать туда update (Сообщение)
    private final MainService mainService;

    public ConsumerServiceImpl(MainService mainService) {
        this.mainService = mainService;
    }


    @Override
    //Аннотация RabbitListener указывается на очередь, которую этот метод будет слушать
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumeTextMessageUpdates(Update update) {
        log.debug("NODE : text");
        mainService.processTextMessage(update);
        ///Отправка ответа из ноды в диспатчер
    }

    @Override
    @RabbitListener(queues = DOC_MESSAGE_UPDATE)
    public void consumeDocMessageUpdates(Update update) {
        log.debug("NODE : doc");
        mainService.processDocMessage(update);
    }

    @Override
    @RabbitListener(queues = PHOTO_MESSAGE_UPDATE)
    public void consumePhotoMessageUpdates(Update update) {
        log.debug("NODE : photo");
        mainService.processPhotoMessage(update);
    }
}
