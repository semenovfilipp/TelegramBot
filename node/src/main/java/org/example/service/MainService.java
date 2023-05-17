package org.example.service;

import org.telegram.telegrambots.meta.api.objects.Update;

//Сервис, через который происходит обработка всех сообщений и далее вызывается специфические сервисы
//.. в зависимости от типа сообщения
public interface MainService {
    //Методы для обработки входящих update
    void processTextMessage(Update update);
    void processDocMessage(Update update);
    void processPhotoMessage(Update update);
}
