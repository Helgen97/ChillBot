package com.bot.chilloutbot.services;

import com.bot.chilloutbot.entity.User;
import com.bot.chilloutbot.repo.UserRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Transactional
    public void addUser(User user){
        if(user.getFirstName().equals("Дмитрий")){
            user.setAdmin(true);
        }
        userRepo.save(user);
    }

    @Transactional
    public void updateUser(User user){
        userRepo.save(user);
    }

    @Transactional
    public boolean userExist(long chatId){
        return userRepo.existsUserByChatID(chatId);
    }

    @Transactional
    public User findUserByChatId(long chatID){
        return userRepo.findUserByChatID(chatID);
    }

    @Transactional(readOnly = true)
    public User findAdmin(){
        return userRepo.findUserByIsAdmin(true);
    }

}
