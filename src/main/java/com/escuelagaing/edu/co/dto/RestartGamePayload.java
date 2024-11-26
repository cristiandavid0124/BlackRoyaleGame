package com.escuelagaing.edu.co.dto;

import java.util.List;
public class RestartGamePayload {
    private String roomId;
    private List<String> playerNickNames;

    // Getters y Setters
    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public List<String> getPlayerNickNames() {
        return playerNickNames;
    }

    public void setPlayerNickNames(List<String> playerNickNames) {
        this.playerNickNames = playerNickNames;
    }
}
