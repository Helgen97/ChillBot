package com.bot.chilloutbot.bot;

import com.bot.chilloutbot.emodji.Icon;
import com.bot.chilloutbot.entity.User;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum BotStates {
    START {
        @Override
        public void init(ChatBot bot, User user) {
            String userText = Icon.HI.get() +
                    """
                    Привіт!\s
                    Вітаю в нашому чат боті!\s
                    Обери одну з послуг, та натисни на неї.\s
                    Обов'язково ознайомся с останніми новинами закладу!
                    """;
            String error = "BotState/Start/Init: Executing message to user error";

            START.sendMessage(bot, user.getChatID(), userText, START.getDefaultButtons(user.isAdmin()), error );
        }
    },
    NEWS{
        @Override
        public void init(ChatBot bot, User user) {
            String userText = bot.getNews();
            String errorUserMessage = "BotStates/News/Init: Executing message to user error";
            NEWS.sendMessage(bot, user.getChatID(), userText, NEWS.getBackButton(), errorUserMessage);
        }
    },
    MENU {
        @Override
        public void init(ChatBot bot, User user) {
            String userText = "Відправляемо меню...";
            String errorUserMessage = "BotState/Menu/Init: Executing message to user error";
            MENU.sendMessage(bot, String.valueOf(user.getChatID()), userText, null, errorUserMessage);

            String errorDocumentMessage = "BotState/Menu/Init: Executing pdf to user error";
            MENU.sendDocument(bot, String.valueOf(user.getChatID()), MENU.getBackButton(), errorDocumentMessage);

        }
    },
    RESERVE{
        @Override
        public void init(ChatBot bot, User user) {
            user.setCurrentState(this);

            String userText = """
                    Щоб забронювати столик:\s
                    Напиши бажаний час, на кого бронювати столик та скільки вас буде.\s
                    І в найближчий час тобі прийде оповіщення стосовно твоеї броні.\s
                    Чекаемо на тебе!""";
            String errorUserMessage = "BotState/Reserve/Init: Executing message to user error";
            RESERVE.sendMessage(bot, user.getChatID(), userText, null, errorUserMessage);

        }

        @Override
        public void userInput(ChatBot bot, User user, User userAdmin, String userInput) {
            String userText = "Дякую! Ми обов'язково перевіримо і повідомимо тебе про твою бронь.";
            String errorUserMessage = "BotState/Reserve/UserInput: Executing message to user error";
            RESERVE.sendMessage(bot, user.getChatID(),userText, RESERVE.getBackButton(), errorUserMessage);

            String adminText = "Бажана бронь: \n" + userInput;
            String errorAdminMessage = "BotState/Reserve/UserInput: Executing message to admin error";
            RESERVE.sendAdminMessage(bot, userAdmin.getChatID(), adminText, RESERVE.getReserveButtons(user.getChatID()), errorAdminMessage);

            user.setCurrentState(null);
        }
    },
    NUMBER {
        @Override
        public void init(ChatBot bot, User user) {
            user.setCurrentState(this);

            String userText = "Напиши нам свій номер і за декілька хвилин чекай нашого дзвінка.";
            String errorUserMessage = "BotState/Number/Init: Executing message to user error";
            NUMBER.sendMessage(bot, user.getChatID(), userText, null, errorUserMessage);
        }

        @Override
        public void userInput(ChatBot bot, User user, User userAdmin, String userInput) {
            String userText = "Дякую! За декілька хвилин ми наберемо тебе!";
            String errorUserMessage = "BotState/Number/UserInput: Executing message to user error";
            NUMBER.sendMessage(bot, user.getChatID(), userText, RESERVE.getBackButton(), errorUserMessage);

            String adminText = user.getFirstName() + " " + user.getLastName() + " залишив свій номер:\n" + userInput;
            String errorAdminMessage = "BotState/Number/UserInput: Executing message to admin error";
            NUMBER.sendAdminMessage(bot, userAdmin.getChatID(), adminText, null, errorAdminMessage);

            user.setCurrentState(null);
        }
    },
    CONFIRM {
        @Override
        public void init(ChatBot bot, User user, String chatID) {
            String userText = """
                    Твоя бронь підверджена!\s
                    Чекаемо з нетерпінням на тебе!\s
                    До зустрічі!
                    """;
            String errorUserMessage = "BotState/Confirm/Init: Executing message to user error";
            CONFIRM.sendMessage(bot, chatID, userText, CONFIRM.getBackButton(), errorUserMessage);
        }
    },
    DECLINE {
        @Override
        public void init(ChatBot bot, User user, String chatID) {
            String userText = """
                    На жаль, ми не можемо підтвердити твою бронь. \s
                    Ти можеш залишити свій номер і ми з радістю допоможемо тобі!""";
            String errorUserMessage = "BotState/Decline/Init: Executing message to user error";
            DECLINE.sendMessage(bot, user.getChatID(), userText, DECLINE.getNumberButton(), errorUserMessage);
        }
    },
    REVIEW {
        @Override
        public void init(ChatBot bot, User user) {
            user.setCurrentState(this);

            String userText = """
                    Дякую, що знайшов час, щоб залишити відгук або побажання\s
                    Ми обов'язково ознайомимося з ним!\s
                    Відправ відгук окремим повідомленням:\s
                    """;
            String errorUserMessage = "BotState/Review/Init: Executing message to user error";
            REVIEW.sendMessage(bot, user.getChatID(), userText, null, errorUserMessage);
        }

        @Override
        public void userInput(ChatBot bot, User user, User userAdmin, String userInput) {
            String userText = """
                    Дякую за відгук!\s
                    Чекаемо тебе в гості!
                    """;
            String errorUserMessage = "BotState/Review/UserInput: Executing message to user error";
            REVIEW.sendMessage(bot, user.getChatID(), userText, REVIEW.getBackButton(), errorUserMessage);

            String adminText = "Відгук від: " + user.getFirstName() + " " + user.getLastName() +
                    "\n" + "Відгук: \n" + userInput;
            String errorAdminMessage = "BotState/Review/UserInput: Executing message to admin error";
            REVIEW.sendAdminMessage(bot, userAdmin.getChatID(), adminText, null, errorAdminMessage);

            user.setCurrentState(null);
        }
    },
    CONTACTS {
        @Override
        public void init(ChatBot bot, User user) {
            String userText =
                    Icon.CLOCK.get() +
                             """
                             Часи роботи: \s
                                    Неділя - Четверг: 13:00 - 1:00 \s
                                    П'ятниця - Субота: 13:00 - 2:00 \s
                            """
                    +
                    Icon.CALL.get()
                    +
                    """
                     Наш номер: 0734454415\s
                    """
                    +
                    Icon.INSTA.get()
                    +
                    """               
                     Наш інстаграм: <a href = 'https://instagram.com/chillouthookah'>тиць</a> \s
                    """
                    +
                    Icon.MAP.get()
                    +
                    """
                     Адреса: Дніпровська Набережна, 25а\s
                    """;
            String errorUserMessage = "BotState/Contacts/Init: Executing message to user error";
            CONTACTS.sendMessage(bot, user.getChatID(), userText, CONTACTS.getBackButton(), errorUserMessage);
        }
    },
    ADDNEWS{
        @Override
        public void init(ChatBot bot, User user) {
            user.setCurrentState(this);
            String userText = "Відправ новий текст новини: ";
            String errorUserText = "BotStates/AddNews/Init: Executing message to user error.";
            ADDNEWS.sendMessage(bot, user.getChatID(), userText, null, errorUserText);
        }

        @Override
        public void userInput(ChatBot bot, User user, User userAdmin, String userInput) {
            bot.setNews(userInput);

            String userText = "Новина успішно змінена!";
            String errorUserText = "BotStates/AddNews/UserInput: Executing message to user error.";
            ADDNEWS.sendMessage(bot, user.getChatID(), userText, null, errorUserText);

            user.setCurrentState(null);
        }
    };

    private static final Logger LOGGER = LogManager.getLogger(BotStates.class);
    private final InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

    public void init(ChatBot bot, User user) {
    }

    public void init(ChatBot bot, User user, String chatID) {
    }

    public void userInput(ChatBot bot, User user, User userAdmin, String userInput) {
    }

    private void sendMessage(ChatBot bot, String chatId, String text, InlineKeyboardMarkup buttons, String error){
        SendMessage message = new SendMessage();
        message.enableHtml(true);
        message.setChatId(chatId);
        message.setText(text);
        message.setReplyMarkup(buttons);
        try {
            bot.execute(message);
        } catch (TelegramApiException ex) {
            LOGGER.log(Level.ERROR, error);
        }
    }

    private void sendDocument(ChatBot bot, String chatID, InlineKeyboardMarkup buttons, String error){
        InputFile file = new InputFile();
        File menuFile = new File("src/main/resources/chillout.pdf");
        file.setMedia(menuFile, "Menu.pdf");

        SendDocument menu = new SendDocument();
        menu.setChatId(chatID);
        menu.setDocument(file);
        menu.setReplyMarkup(buttons);

        try {
            bot.execute(menu);
        } catch (TelegramApiException ex) {
            LOGGER.log(Level.ERROR, error);
        }
    }

    private void sendAdminMessage(ChatBot bot, String adminChatID, String text, InlineKeyboardMarkup buttons, String error){
        SendMessage message = new SendMessage();
        message.setChatId(adminChatID);
        message.setText(text);
        message.setReplyMarkup(buttons);
        try {
            bot.execute(message);
        } catch (TelegramApiException ex) {
            LOGGER.log(Level.ERROR, error);
        }
    }

    private void deleteMessage(){

    }

    private InlineKeyboardMarkup getDefaultButtons(boolean isAdmin) {
        List<List<InlineKeyboardButton>> allRows = new ArrayList<>();

        List<InlineKeyboardButton> zeroRow = new ArrayList<>();
        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        List<InlineKeyboardButton> thirdRow = new ArrayList<>();
        List<InlineKeyboardButton> fourthRow = new ArrayList<>();

        InlineKeyboardButton zeroButton = new InlineKeyboardButton();
        zeroButton.setText(Icon.NEWS.get() + " Наші новини");
        zeroButton.setCallbackData("/news");
        InlineKeyboardButton firstButton = new InlineKeyboardButton();
        firstButton.setText(Icon.MENU.get() + " Наше меню");
        firstButton.setCallbackData("/menu");
        InlineKeyboardButton secondButton = new InlineKeyboardButton();
        secondButton.setText(Icon.RESERVE.get() + " Забронювати столик");
        secondButton.setCallbackData("/reserve");
        InlineKeyboardButton thirdButton = new InlineKeyboardButton();
        thirdButton.setText(Icon.REVIEW.get() + " Побажання та відгуки");
        thirdButton.setCallbackData("/review");
        InlineKeyboardButton fourthButton = new InlineKeyboardButton();
        fourthButton.setText(Icon.CONTACTS.get() + " Наші контакти");
        fourthButton.setCallbackData("/contacts");

        zeroRow.add(zeroButton);
        firstRow.add(firstButton);
        secondRow.add(secondButton);
        thirdRow.add(thirdButton);
        fourthRow.add(fourthButton);

        allRows.add(zeroRow);
        allRows.add(firstRow);
        allRows.add(secondRow);
        allRows.add(thirdRow);
        allRows.add(fourthRow);

        if(isAdmin){
            List<InlineKeyboardButton> fiveRow = new ArrayList<>();
            InlineKeyboardButton fiveButton = new InlineKeyboardButton();
            fiveButton.setText(Icon.NEW.get() + " Змінити новину");
            fiveButton.setCallbackData("/addNews");
            firstRow.add(fiveButton);
            allRows.add(firstRow);
        }
        keyboardMarkup.setKeyboard(allRows);
        return keyboardMarkup;
    }

    private InlineKeyboardMarkup getBackButton() {
        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(Icon.BACK.get() + " Повернутися назад");
        button.setCallbackData("/start");
        firstRow.add(button);
        keyboardMarkup.setKeyboard(Collections.singletonList(firstRow));
        return keyboardMarkup;
    }

    private InlineKeyboardMarkup getReserveButtons(String chatID) {
        List<List<InlineKeyboardButton>> allRows = new ArrayList<>();
        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        InlineKeyboardButton buttonConfirm = new InlineKeyboardButton();
        InlineKeyboardButton buttonDecline = new InlineKeyboardButton();

        buttonConfirm.setText(Icon.OKAY.get() + " Підтвердити бронь");
        buttonConfirm.setCallbackData("/confirm " + chatID);

        buttonDecline.setText(Icon.NOT.get() + " Відмінити бронь");
        buttonDecline.setCallbackData("/decline " + chatID);


        firstRow.add(buttonConfirm);
        secondRow.add(buttonDecline);

        allRows.add(firstRow);
        allRows.add(secondRow);

        keyboardMarkup.setKeyboard(allRows);
        return keyboardMarkup;
    }

    private InlineKeyboardMarkup getNumberButton() {
        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton buttonNumber = new InlineKeyboardButton();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(Icon.BACK.get() + " Повернутися назад");
        button.setCallbackData("/start");

        buttonNumber.setText(Icon.PHONE.get() + " Залишити номер");
        buttonNumber.setCallbackData("/number");
        firstRow.add(buttonNumber);
        firstRow.add(button);
        keyboardMarkup.setKeyboard(Collections.singletonList(firstRow));
        return keyboardMarkup;
    }
}
