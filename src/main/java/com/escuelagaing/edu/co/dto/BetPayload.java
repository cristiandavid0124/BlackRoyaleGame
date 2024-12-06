package com.escuelagaing.edu.co.dto;

import java.util.List;

public class BetPayload {
    private String roomId;
    private List<String> fichas;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public List<String> getFichas() {
        return fichas;
    }

    public void setFichas(List<String> fichas) {
        this.fichas = fichas;
    }
}