package org.example.service.impl;

import lombok.extern.log4j.Log4j;
import org.example.dao.AppUserDAO;
import org.example.dao.RawDataDAO;
import org.example.entity.AppDocument;
import org.example.entity.AppPhoto;
import org.example.entity.AppUser;
import org.example.entity.RawData;
import org.example.exception.UploadFileException;
import org.example.service.FileService;
import org.example.service.MainService;
import org.example.service.ProducerService;
import org.example.service.enums.ServiceCommands;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static org.example.entity.enums.UserState.BASIC_STATE;
import static org.example.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;
import static org.example.service.enums.ServiceCommands.*;

@Log4j
@Service
public class MainServiceImpl implements MainService {
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;
    private final FileService fileService;

//MainService является связующим звеном, между БД и ConsumerService, который будет передавать сообщения из  RabbitMQ

    public MainServiceImpl(RawDataDAO rawDataDAO, ProducerService producerService, AppUserDAO appUserDAO,FileService fileService) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
        this.fileService = fileService;
    }

    //Метод для обработки текстовых сообщений processTextMessage
    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        //Разбираем сообщение на составные части
        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var output = "";


        var serviceCommand = ServiceCommands.fromValue(text);
        //Проверка команды -> сбрасывает state пользователя к базовому состоянию
        if (CANCEL.equals(serviceCommand)) {
            output = cancelProcess(appUser);
            //Если текущий State пользователя базовый
            //То он ожидает от него ввода сервисных комманд
        } else if (BASIC_STATE.equals(userState)) {
            output = producerServiceCommand(appUser, text);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            //TODO добавить обработку в будущем
        } else {
            log.error("Unknown user state " + userState);
            output = "Неизвестная ошибка.Введите /cancel или попробуйте позже";

        }
            //Отправка output с текстом обратно в чат пользователю

            var chatId = update.getMessage().getChatId();
            sendAnswer(output, chatId);

    }

    @Override
    public void processDocMessage(Update update) {
        //Сохраняем все текущие update что бы обезопасить от потери при ошибке
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)){
            return;
        }


        try {
            AppDocument doc = fileService.processDoc(update.getMessage());
            //TODO добавить сохранение документа
            var answer = "Документ успешно загружен!"
                    +  "Ссылка для скачивания https://test.ru/get-doc/777";
            sendAnswer(answer, chatId);
        } catch (UploadFileException ex){
            log.error(ex);
            String error = "К сожалению загрузка файла не удалась. Повторите попытку позже";
            sendAnswer(error, chatId);
        }
    }

    @Override
    public void processPhotoMessage(Update update) {
        //Сохраняем все текущие update что бы обезопасить от потери при ошибке
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)){
            return;
        }
        try {
            //Получаем фото
            AppPhoto photo = fileService.processPhoto(update.getMessage());
            //TODO добавить сохранение фото
            var answer = "Фото успешно загружено.  https://test.ru/get-photo/777";
            sendAnswer(answer, chatId);
        } catch (UploadFileException ex){
            log.error(ex);
            String error = "К сожалению загрузка фото не удалась. Повторите попытку позже";
            sendAnswer(error, chatId);
        }
    }
    //Две проверки:
    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        var userState = appUser.getState();
        //1. Подтвердил ли пользователь переход по ссылке из учетного письма
        if (!appUser.getIsActive()) {
            var error = "Зарегестрируйтесь или активируйте свою учетную запись для загрузки контента";
            sendAnswer(error, chatId);
            return true;
        //2. Находится ли пользователь в базовом state
        } else if (!BASIC_STATE.equals(userState)){
            var error = "Отмените текущую комманду с помощью /cancel для отправки файлов";
            sendAnswer(error, chatId);
            return true;
            }
        return false;
        }




    //Конструктор отправки output с текстом обратно в чат пользователю

    private void sendAnswer(String output, Long chatId) {
        //Генерация ответа в микросервис dispatcher
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.produceAnswer(sendMessage);
    }
    //Логика комманд

    private String producerServiceCommand(AppUser appUser, String cmd) {
        var serviceCommands = ServiceCommands.fromValue(cmd);
        if (REGISTRATION.equals(serviceCommands)) {
            ///TODO добавить регистрацию
            return "Временно недоступно";
        } else if (HELP.equals(serviceCommands)){
            return  help();
        } else if (START.equals(serviceCommands)){
            return "Приветствую!Для списка доступных комманд нажмите /help";
        } else {
            return "Неизвестная комманда!Для списка доступных комманд нажмите /help";
        }
    }
    //Описание help
    private String help() {
        return "Список доступных комманд:\n"
                + "/cancel - отмена выполнения текущей комманды\n"
                + "/registration - регистрация пользователя";
    }


    //cancelProcess устанавливает текущему пользователю базовый state, и сохраняет обновленные данные в БД
    private String cancelProcess(AppUser appUser) {
        appUser.setState(BASIC_STATE);
        appUserDAO.save(appUser);
        return "Комманда отменена!";

    }

    //Ищем в базе текущего пользователя и называем его persistentAppUser
    //persistent - пользователь уже есть в БД, имеет ключ и связан с сессией Hibernate(под капотом SpringData)
    private AppUser findOrSaveAppUser(Update update){
        User telegramUser = update.getMessage().getFrom();//достаем объект из telegramUser

        AppUser persistentAppUser = appUserDAO.findAppUserByTelegramUserId(telegramUser.getId());
        //Если не найден, создаем transient(объект не представлен в БД и нам предстоит его сохранить)
        if (persistentAppUser == null){
            //FistLoginDate сгенерируется автоматически в БД
            AppUser transientAppUser = AppUser.builder()

                    .telegramUserId(telegramUser.getId())
                    .userName(telegramUser.getUserName())
                    .fistName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    //TODO изменить значение по умолчанию после добавления регистрации
                    .isActive(true)
                    .state(BASIC_STATE)
                    .build();
            return appUserDAO.save(transientAppUser);//сохраняем пользователя в БД и возвращаем из метода
            //save возвращает сохранненый объект с уже заполненым первичным ключом и привязкой объека к сессии Hibernate
        }
        return persistentAppUser;
    }

    //Запись в БД
    private void saveRawData(Update update) {
        RawData rawData = RawData.builder() //паттерн builder
                .event(update)  //сэттер в который мы передаем update
                .build();
        rawDataDAO.save(rawData); //Вводимые данные зависят от интерфейса RawDataDAO
    }
}
