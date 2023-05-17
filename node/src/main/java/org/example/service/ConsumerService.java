package org.example.service;

///Для считывания сообщений из брокера сообщений

import org.telegram.telegrambots.meta.api.objects.Update;


public interface ConsumerService {
    ///Для каждой очереди свой метод
    void consumeTextMessageUpdates(Update update);
    void consumeDocMessageUpdates(Update update);
    void consumePhotoMessageUpdates(Update update);
}
