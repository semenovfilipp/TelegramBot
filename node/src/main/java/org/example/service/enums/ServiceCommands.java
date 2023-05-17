package org.example.service.enums;

public enum ServiceCommands {
    START("/start"),

    HELP("/help"),
    REGISTRATION("/registration"),
    CANCEL("/cancel");
    private final String value;

    ServiceCommands(String value) {
        this.value = value;
    }

    //Метод принимает на вход строку, пробегает по всем enum объектам этого класса и смотрит..

    public static ServiceCommands fromValue(String v) {
        for (ServiceCommands c : ServiceCommands.values()) {
            //что value равно входящей строке
            if (c.value.equals(v)) {
                //Цель найти объект enum с таким же value.Если он найден, то возращается этот enum
                return c;
            }
        }
        //иначе null
        return null;
    }
    //т.е. сначала мы принимаем команду, соотносим ее с нашим списком и если она есть, то приминяем к ней описанную логику
}

