package org.example.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;


//Принимает ответы из RabbitMQ  и передает их в UpdateController
//Тип данных на вход объект sendMessage, имя очереди указывается в самом сервисе с помощью аннотации
public interface AnswerConsumer {
    void consume(SendMessage sendMessage);

}
