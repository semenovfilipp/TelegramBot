package org.example.entity;
//Сохранение пользователей в БД postgres
//AppUser будет привязана к аналогичной таблице в БД

import lombok.*;
import org.example.entity.enums.UserState;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;


@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
//Конструктор по умолчанию - без параметров
@NoArgsConstructor
//Конструктор, который содержит все поля этого класса
@AllArgsConstructor
///Entity говорит что наш класс является сущностью, которая связана с таблицей в нашей БД
@Entity
//Table Задает имя таблицы
@Table(name = "app_user")

public class AppUser {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    private Long telegram;
    private Long telegramUserId;
    @CreationTimestamp //Аннотация для автоматического сохранения подключения пользователя
    private LocalDateTime fistLoginDate;//Дата первого подключения к боту
    private String lastName;
    private String fistName;
    private String userName;
    private String email;
    private Boolean isActive;//Флаг, который изменит значение при активации аккаунта
    @Enumerated(EnumType.STRING) //говорит для SpringData как именно этот enum будет транслироватся в БД
    private UserState state;




}
