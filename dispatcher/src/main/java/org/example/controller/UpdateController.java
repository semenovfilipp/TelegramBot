package org.example.controller;

import lombok.extern.log4j.Log4j;
import org.example.service.UpdateProducer;
import org.example.utils.MessageUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.example.model.RabbitQueue.*;

@Log4j
@Component
public class UpdateController {
    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;
    private final UpdateProducer updateProducer;

    //Создание зависисости в классе UpdateController
    public UpdateController(MessageUtils messageUtils, UpdateProducer updateProducer) {
        this.messageUtils = messageUtils;
        this.updateProducer = updateProducer;
    }

    ///Способ создания моста для соединения получения запросов и отправления на них
    // ответов через метод init
    public void registerBot(TelegramBot telegramBot){
        this.telegramBot = telegramBot;
    }

    ///Анализ полученного update.Разбиение его на:
    public void processUpdate(Update update) {
        // 1. Запись в лог и ответ на запрос при типе запроса null или инном пустом

        if (update == null) {
            log.error("Received update is null");
            return;
        }
        // 2. При != null -> отправка на сортировку по типу сообщения

        if (update.hasMessage()) {
            distributeMessagesByType(update);
        } else {
            log.error("Received unsupported message" + update);
        }
    }
    ///Разбиение полученного сообщения на типы
    private void distributeMessagesByType(Update update) {
        var message = update.getMessage();
        if (message.hasText()){
            processTextMessage(update);
        } else if (message.hasDocument()) {
            processDocMessage(update);
        } else if (message.hasPhoto()){
            processPhotoMessage(update);

        } else {
            setUnsupportedMessageTypeView(update);

        }
    }
///Обратная связь при получении запросов

    private void setUnsupportedMessageTypeView(Update update) {
        var sendMessage = messageUtils.generateMessageWithText(update,
                " Неподдерживаемый тип сообщения!");
        setView(sendMessage);
    }
    private void setFileIsReceivedView(Update update) {
        var sendMessage = messageUtils.generateMessageWithText(update,
                "Файл получен, обрабатывается...");
        setView(sendMessage);
    }

    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }




////Методы обработки информации исходя из ее типа
    private void processDocMessage(Update update) {
        updateProducer.produce(DOC_MESSAGE_UPDATE, update);
        setFileIsReceivedView(update);
    }
    private void processTextMessage(Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
    }
    private void processPhotoMessage(Update update) {
        updateProducer.produce(PHOTO_MESSAGE_UPDATE, update);
        setFileIsReceivedView(update);
    }
}
