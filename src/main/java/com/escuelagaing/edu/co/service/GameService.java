package com.escuelagaing.edu.co.service;

import com.escuelagaing.edu.co.BlackJackSocketIOConfig;
import com.escuelagaing.edu.co.model.Game;
import com.escuelagaing.edu.co.model.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final BlackJackSocketIOConfig socketConfig;

    @Autowired
    public GameService(BlackJackSocketIOConfig socketConfig) {
        this.socketConfig = socketConfig;
    }

    public void startGame(Room room) {
        Game game = new Game(room.getPlayers(), room.getRoomId());
        room.setGame(game);
        game.startGame();
        sendRoomUpdate(room);  // Notificar al socket cuando inicia el juego
    }

    public void endGame(Room room) {
        room.getGame().endGame();
        sendRoomUpdate(room);  // Notificar al socket cuando termina el juego
    }

    public void sendRoomUpdate(Room room) {
        socketConfig.sendRoomUpdate(room.getRoomId());  // Notificar al socket
    }
}
 
  
