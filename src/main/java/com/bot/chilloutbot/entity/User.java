package com.bot.chilloutbot.entity;

import com.bot.chilloutbot.bot.BotStates;

import javax.persistence.*;

@Entity
@Table(name = "Bot_Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "First_Name")
    private String firstName;

    @Column(name = "Last_Name")
    private String lastName;

    @Column(name = "Username")
    private String username;

    @Column(name = "Chat_Id", nullable = false)
    private String chatID;

    @Column(name = "isAdmin")
    private boolean isAdmin = false;

    @Column(name = "State")
    @Enumerated
    private BotStates currentState;

    public User() {
    }

    public User(String firstName, String secondName, String username, String chatID) {
        this.firstName = firstName;
        this.lastName = secondName;
        this.username = username;
        this.chatID = chatID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public BotStates getCurrentState() {
        return currentState;
    }

    public void setCurrentState(BotStates currentState) {
        this.currentState = currentState;
    }
}
