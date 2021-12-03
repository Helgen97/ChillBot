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
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(user.getChatID()));
            message.setText(
                    Icon.HI.get() +
                    """
                    Привіт!\s
                    Вітаю в нашому чат боті!\s
                    Обери одну з послуг, та натисни на неї.""");
            message.enableHtml(true);
            message.setReplyMarkup(START.getDefaultButtons());
            try {
                bot.execute(message);
            } catch (TelegramApiException ex) {
                LOGGER.log(Level.ERROR, "BotState/Start: Executing message error");
            }
        }
    },
    MENU {
        @Override
        public void init(ChatBot bot, User user) {
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(user.getChatID()));
            message.setText("Відправляемо меню...");

            SendDocument menu = new SendDocument();
            menu.setChatId(String.valueOf(user.getChatID()));
            menu.setDocument(new InputFile(new File("src/main/resources/chillout.pdf"), "Menu.pdf"));
            menu.setReplyMarkup(MENU.getBackButton());

            try {
                bot.execute(message);
                bot.execute(menu);
            } catch (TelegramApiException ex) {
                LOGGER.log(Level.ERROR, "BotState/Menu: Executing message or pdf error");
            }
        }
    },
    RESERVE() {
        @Override
        public void init(ChatBot bot, User user) {
            user.setCurrentState(this);
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(user.getChatID()));
            message.setText("""
                    Щоб забронювати столик:\s
                    Напиши бажаний час, на кого бронювати столик та скільки вас буде. \s
                    І в найближчий час тобі прийде оповіщення стосовно твоеї броні.\s
                    Чекаемо на тебе!""");
            try {
                bot.execute(message);
            } catch (TelegramApiException ex) {
                LOGGER.log(Level.ERROR, "BotState/RESERVE: Executing message error");
            }
        }

        @Override
        public void userInput(ChatBot bot, User user, User userAdmin, String userInput) {
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(user.getChatID()));
            message.setText("""
                    Дякую! Ми обов'язково перевіримо і повідомимо тебе про твою бронь.\s
                    """);
            message.setReplyMarkup(RESERVE.getBackButton());
            SendMessage message1 = new SendMessage();
            message1.setChatId(String.valueOf(userAdmin.getChatID()));
            message1.setText(userInput);
            message1.setReplyMarkup(RESERVE.getReserveButtons(String.valueOf(user.getChatID())));
            try {
                bot.execute(message);
                bot.execute(message1);
            } catch (TelegramApiException ex) {
                LOGGER.log(Level.ERROR, "BotState/RESERVE: Executing message error");
                ex.printStackTrace();
            }
            user.setCurrentState(null);
        }
    },
    NUMBER {
        @Override
        public void init(ChatBot bot, User user) {
            user.setCurrentState(this);
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(user.getChatID()));
            message.setText("""
                    Напиши нам свій номер і за декілька хвилин чекай нашого дзвінка.""");
            try {
                bot.execute(message);
            } catch (TelegramApiException ex) {
                LOGGER.log(Level.ERROR, "BotState/RESERVE: Executing message error");
            }
        }

        @Override
        public void userInput(ChatBot bot, User user, User userAdmin, String userInput) {
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(user.getChatID()));
            message.setText("""
                    Дякую! За декілька хвилин ми наберемо тебе!\s
                    """);
            message.setReplyMarkup(RESERVE.getBackButton());
            SendMessage message1 = new SendMessage();
            message1.setChatId(String.valueOf(userAdmin.getChatID()));
            message1.setText(userInput);
            try {
                bot.execute(message);
                bot.execute(message1);
            } catch (TelegramApiException ex) {
                LOGGER.log(Level.ERROR, "BotState/NUMBER: Executing message error");
                ex.printStackTrace();
            }
            user.setCurrentState(null);
        }
    },
    CONFIRM {
        @Override
        public void init(ChatBot bot, User user, String chatID) {
            SendMessage message = new SendMessage();
            message.setChatId(chatID);
            message.setText("""
                    Твоя бронь підверджена! \s
                    Чекаемо з нетерпінням на тебе! \s
                    До зустрічі!
                    """);
            try {
                bot.execute(message);
            } catch (TelegramApiException ex) {
                LOGGER.log(Level.ERROR, "BotState/CONFIRM: Executing message error");
            }
        }
    },
    DECLINE {
        @Override
        public void init(ChatBot bot, User user, String chatID) {
            SendMessage message = new SendMessage();
            message.setChatId(chatID);
            message.setText("""
                    На жаль, ми не можемо підтвердити твою бронь. \s
                    Ти можеш залишити свій номер і ми з радістю допоможемо тобі!
                    """);
            message.setReplyMarkup(DECLINE.getNumberButton());
            try {
                bot.execute(message);
            } catch (TelegramApiException ex) {
                LOGGER.log(Level.ERROR, "BotState/DECLINE: Executing message error");
            }
        }
    },
    REVIEW {
        @Override
        public void init(ChatBot bot, User user) {
            user.setCurrentState(this);
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(user.getChatID()));
            message.setText("""
                    Дякую, що знайшов час, щоб залишити відгук або побажання \s
                    Ми обов'язково ознайомимося з ним! \s
                    Відправ відгук окремим повідомленням: \s
                    """);
            try {
                bot.execute(message);
            } catch (TelegramApiException ex) {
                LOGGER.log(Level.ERROR, "BotState/REVIEW: Executing message error");
            }
        }

        @Override
        public void userInput(ChatBot bot, User user, User userAdmin, String userInput) {
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(user.getChatID()));
            message.setText("""
                    Дякую за відгук!\s
                    Чекаемо тебе в гості!
                    """);
            message.setReplyMarkup(RESERVE.getBackButton());
            SendMessage message1 = new SendMessage();
            message1.setChatId(String.valueOf(userAdmin.getChatID()));
            message1.setText("Відгук від: " + user.getFirstName() + " " + user.getLastName() +
                    "\n" + "Відгук: \n" + userInput);
            try {
                bot.execute(message);
                bot.execute(message1);
            } catch (TelegramApiException ex) {
                LOGGER.log(Level.ERROR, "BotState/REVIEW: Executing message error");
                ex.printStackTrace();
            }
            user.setCurrentState(null);
        }
    },
    CONTACTS {
        @Override
        public void init(ChatBot bot, User user) {
            SendMessage message = new SendMessage();
            message.enableHtml(true);
            message.setChatId(String.valueOf(user.getChatID()));
            message.setText(
                    Icon.CLOCK.get() + """
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
                            """);
            try {
                bot.execute(message);
            } catch (TelegramApiException ex) {
                LOGGER.log(Level.ERROR, "BotState/CONTACTS: Executing message error");
            }
        }
    };

    private static Logger LOGGER = LogManager.getLogger(BotStates.class);

    public void init(ChatBot bot, User user) {
    }

    public void init(ChatBot bot, User user, String chatID) {
    }

    public void userInput(ChatBot bot, User user, User userAdmin, String userInput) {
    }

    private InlineKeyboardMarkup getDefaultButtons() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> allRows = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        List<InlineKeyboardButton> thirdRow = new ArrayList<>();
        List<InlineKeyboardButton> fourthRow = new ArrayList<>();

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

        firstRow.add(firstButton);
        secondRow.add(secondButton);
        thirdRow.add(thirdButton);
        fourthRow.add(fourthButton);

        allRows.add(firstRow);
        allRows.add(secondRow);
        allRows.add(thirdRow);
        allRows.add(fourthRow);

        keyboardMarkup.setKeyboard(allRows);
        return keyboardMarkup;
    }

    private InlineKeyboardMarkup getBackButton() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(Icon.BACK.get() + " Повернутися назад");
        button.setCallbackData("/start");
        firstRow.add(button);
        keyboardMarkup.setKeyboard(Collections.singletonList(firstRow));
        return keyboardMarkup;
    }

    private InlineKeyboardMarkup getReserveButtons(String chatID) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton buttonConfirm = new InlineKeyboardButton();
        InlineKeyboardButton buttonDecline = new InlineKeyboardButton();

        buttonConfirm.setText(Icon.OKAY.get() + " Підтвердити бронь");
        buttonConfirm.setCallbackData("/confirm " + chatID);

        buttonDecline.setText(Icon.NOT.get() + " Відмінити бронь");
        buttonDecline.setCallbackData("/decline " + chatID);

        firstRow.add(buttonConfirm);
        firstRow.add(buttonDecline);
        keyboardMarkup.setKeyboard(Collections.singletonList(firstRow));
        return keyboardMarkup;
    }

    private InlineKeyboardMarkup getNumberButton() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
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
