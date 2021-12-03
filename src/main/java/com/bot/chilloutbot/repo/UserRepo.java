package com.bot.chilloutbot.repo;

import com.bot.chilloutbot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    User findUserByChatID(Long chatId);

    boolean existsUserByChatID(Long chatId);

    @Query("select u from User u where u.isAdmin = ?1")
    User findUserByIsAdmin(boolean isAdmin);
}
