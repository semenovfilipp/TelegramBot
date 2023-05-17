package org.example.dao;

import org.example.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserDAO extends JpaRepository<AppUser, Long> {
    AppUser findAppUserByTelegramUserId(Long id); //Проверка того что текущий пользователь от которого пришло сообщение уже есть в нашей базе
}
