package org.example.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;

@Component
@Log4j
public class TelegramBot extends TelegramLongPollingBot {
    //Данные, которые ссылаются на application.properties
    @Value("${bot.name}")

    private String botName;
    @Value("${bot.token}")

    private String botToken;
    private UpdateController updateController;

    public TelegramBot(UpdateController updateController) {
        this.updateController = updateController;
    }
    //Метод для создания моста "запрос <--> обработка <---> ответ типа (Файл получен)"
    @PostConstruct
    public void init(){
        updateController.registerBot(this);
    }



    @Override
    public String getBotUsername() {

        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    //Получаем сообщение.Интеграция UpdateController и TelegramBot
    @Override
    public void onUpdateReceived(Update update) {
         updateController.processUpdate(update);




    }
    public void sendAnswerMessage(SendMessage message){
        if (message != null){
            try {
                //Метод execute делает распарс входящего  json  и преобразует его в java объект
                execute(message);
                } catch (TelegramApiException e){
                log.error(e);
            }
        }

    }
}
