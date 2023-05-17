package org.example.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

//Сервис для отправки ответов с ноды в брокер
public interface ProducerService {
    ///тк все сообщения однотипные, то будет только один метод
    void produceAnswer(SendMessage sendMessage);
}
