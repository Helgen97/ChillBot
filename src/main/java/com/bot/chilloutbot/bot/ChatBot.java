package com.bot.chilloutbot.bot;

import com.bot.chilloutbot.emodji.Icon;
import com.bot.chilloutbot.entity.User;
import com.bot.chilloutbot.services.UserService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@PropertySource("classpath:telegram.properties")
public class ChatBot extends TelegramLongPollingBot {

    private static final Logger LOGGER = LogManager.getLogger(ChatBot.class);
    private final UserService userService;
    private String news = "Поки все тихо. Працюемо в звичайному режимі";

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    public ChatBot(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() && !update.hasCallbackQuery()) {
            return;
        }
        User user = null;
        if (update.hasMessage()) {
            user = userExist(update);
            String message = update.getMessage().getText();
            messageOptions(this, user, message);
        } else if (update.hasCallbackQuery()) {
            user = userService.findUserByChatId(update.getCallbackQuery().getMessage().getChatId());
            String queryData = update.getCallbackQuery().getData();
            queryOptions(this, user, queryData);
        }
        userService.updateUser(user);
    }

    private void messageOptions(ChatBot bot, User user, String message){
        if(user.getCurrentState() == null){
            BotStates.START.init(bot, user);
        } else {
            user.getCurrentState().userInput(bot, user, userService.findAdmin(), message);
        }
    }

    private void queryOptions(ChatBot bot, User user, String queryData) {
        if(queryData.startsWith("/confirm")){
            String chatID = queryData.substring(queryData.indexOf(" ")+1);
            BotStates.CONFIRM.init(bot, user, chatID);
        } else if(queryData.startsWith("/decline")){
            String chatID = queryData.substring(queryData.indexOf(" ")+1);
            BotStates.DECLINE.init(bot, user, chatID);
        } else {
            switch (queryData) {
                case "/start" -> BotStates.START.init(bot, user);
                case "/news" -> BotStates.NEWS.init(bot, user);
                case "/menu" -> BotStates.MENU.init(bot, user);
                case "/reserve" -> BotStates.RESERVE.init(bot, user);
                case "/review" -> BotStates.REVIEW.init(bot, user);
                case "/contacts" -> BotStates.CONTACTS.init(bot, user);
                case "/number" -> BotStates.NUMBER.init(bot, user);
                case "/addNews" -> BotStates.ADDNEWS.init(bot, user);
            }
        }
    }

    private User userExist(Update update) {
        User user;
        if (!userService.userExist(update.getMessage().getChatId())) {
            user = new User(update.getMessage().getChat().getFirstName(), update.getMessage().getChat().getLastName(), update.getMessage().getChat().getUserName(), update.getMessage().getChatId());
            userService.addUser(user);
            LOGGER.log(Level.INFO, "New user register: " + user.getChatID());
        } else user = userService.findUserByChatId(update.getMessage().getChatId());
        return user;
    }

    public String getBotName() {
        return botName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public String getNews() {
        return news;
    }

    public void setNews(String news) {
        String emptyText = "Поки все тихо. Працюемо в звичайному режимі";
        if(news.equals(" ")) this.news = emptyText;
        else {
            String newNews = Icon.IMPORTANT.get() + " Оголошення " + Icon.IMPORTANT.get() +"\n"
                    + "Новина за " + new SimpleDateFormat("dd.MM.yyyy").format(new Date()) + "\n"
                    + news;
            this.news = newNews;
        }
    }
}
