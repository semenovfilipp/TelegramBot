package org.example.entity.enums;
//Хранение доступного состояния пользователя
//В зависимости от текущего State мы будет ожидать от пользователя определенных действий

public enum UserState {
    BASIC_STATE,
    WAIT_FOR_EMAIL_STATE
    //BASIC_STATE = Гость сможет ввести только разрешенные команды или отправить файл, иначе ввод некоректен
    //WAIT_FOR_EMAIL_STATE = Приложение будет считать валидным только ввод email|отмены процесса регистрации

}
