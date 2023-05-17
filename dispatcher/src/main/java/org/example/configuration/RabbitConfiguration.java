package org.example.configuration;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.core.Queue;

import static org.example.model.RabbitQueue.*;

//Интеграция с RabbitMQ
@Configuration
public class RabbitConfiguration {
    ///Полученный Update в виде json передается в RabbitMQ, оттуда конвертируются в приложение в виде java
    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    //Создание бинов, каждый из которых соответсвует очереди в RabbitMQ
    @Bean
    public Queue textMessageQueue(){
        return new Queue (TEXT_MESSAGE_UPDATE);
    }
    @Bean
    public Queue photoMessageQueue(){
        return new Queue (PHOTO_MESSAGE_UPDATE);
    }
    @Bean
    public Queue docMessageQueue(){
        return new Queue (DOC_MESSAGE_UPDATE);
    }

    //Ответы от нод в диспатчер, далее конечному пользователю в чат
    @Bean
    public Queue answerMessageQueue(){
        return new Queue (ANSWER_MESSAGE);
    }




}
