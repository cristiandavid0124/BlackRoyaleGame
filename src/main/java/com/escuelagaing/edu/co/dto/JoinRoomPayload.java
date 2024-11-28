package com.escuelagaing.edu.co.dto;


public class JoinRoomPayload {
    private String userId;
    private String roomId;

    // Getters y setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
}
