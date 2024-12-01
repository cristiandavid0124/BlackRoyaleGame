package com.escuelagaing.edu.co.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ChatMessage {
    private String sender;
    private String message;
    private String roomId;

    public ChatMessage() {
    }

    @JsonCreator
    public ChatMessage(
            @JsonProperty("sender") String sender,
            @JsonProperty("message") String message,
            @JsonProperty("roomId") String roomId) {
        this.sender = sender;
        this.message = message;
        this.roomId = roomId;
    }

    // Getters y Setters
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}

