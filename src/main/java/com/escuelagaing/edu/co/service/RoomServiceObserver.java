
package com.escuelagaing.edu.co.service;

import com.escuelagaing.edu.co.model.RoomObserver;
import com.escuelagaing.edu.co.BlackJackSocketIOConfig;
import org.springframework.stereotype.Service;

@Service
public class RoomServiceObserver implements RoomObserver {
    private final BlackJackSocketIOConfig socketConfig;

    public RoomServiceObserver(BlackJackSocketIOConfig socketConfig) {
        this.socketConfig = socketConfig;
    }

    @Override
    public void onRoomUpdate(String roomId) {
        socketConfig.sendRoomUpdate(roomId); // Enviar la actualización al frontend
    }
}
