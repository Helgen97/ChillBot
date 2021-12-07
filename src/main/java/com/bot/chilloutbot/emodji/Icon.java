package com.bot.chilloutbot.emodji;

import com.vdurmont.emoji.EmojiParser;

public enum Icon {
    OKAY(":white_check_mark:"),
    NOT(":x:"),
    BACK(":arrow_left:"),
    PHONE(":telephone_receiver:"),
    MENU(":book:"),
    RESERVE(":pushpin:"),
    REVIEW(":memo:"),
    CONTACTS(":envelope_with_arrow:"),
    CLOCK(":clock130:"),
    CALL(":iphone:"),
    INSTA(":stars:"),
    MAP(":world_map:"),
    HI(":wave:"),
    NEWS(":newspaper:"),
    NEW(":new:"),
    IMPORTANT(":exclamation:");

    private String value;

    Icon(String value) {
        this.value = value;
    }

    public String get(){
        return EmojiParser.parseToUnicode(value);
    }
}
