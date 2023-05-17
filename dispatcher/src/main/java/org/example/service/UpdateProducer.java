package org.example.service;


import org.telegram.telegrambots.meta.api.objects.Update;
//Передает ответы  в брокер сообщений RabbitMQ
//Принимает название очереди и update в виде данных
public interface UpdateProducer {
    void produce(String rabbitQueue, Update update);
}
