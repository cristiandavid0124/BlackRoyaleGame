package com.escuelgaing.edu.co.controller;

import com.escuelgaing.edu.co.model.Game;
import com.escuelgaing.edu.co.model.Player;
import com.escuelgaing.edu.co.model.Room;
import com.escuelgaing.edu.co.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class BlackjackController {

    @Autowired
    private GameService gameService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Manejar una nueva apuesta
    @MessageMapping("/bet.{roomId}")
    public void handleBet(@DestinationVariable String roomId, Map<String, Object> betRequestData) {
        Room room = gameService.getRoom(roomId);
        String playerId = (String) betRequestData.get("playerId");
        Double betAmount = ((Number) betRequestData.get("betAmount")).doubleValue();

        Player player = room.getPlayer(playerId);
        gameService.placeBet(player, betAmount);
        
        messagingTemplate.convertAndSend("/topic/gameUpdate." + roomId, room); // Notificar a todos los jugadores
    }

    // Manejar acción de un jugador (Hit, Stand, Double)
    @MessageMapping("/action.{roomId}")
    public void handleAction(@DestinationVariable String roomId, Map<String, Object> actionRequestData) {
        Room room = gameService.getRoom(roomId);
        String playerId = (String) actionRequestData.get("playerId");
        String action = (String) actionRequestData.get("action");

        Player player = room.getPlayer(playerId);
        gameService.processAction(player, action);

        messagingTemplate.convertAndSend("/topic/gameUpdate." + roomId, room); // Notificar a todos los jugadores
    }

    // Manejar el inicio del juego
    @MessageMapping("/startGame.{roomId}")
    public void handleStartGame(@DestinationVariable String roomId) {
        Room room = gameService.getRoom(roomId);
        gameService.startGame(room);

        messagingTemplate.convertAndSend("/topic/gameUpdate." + roomId, room); // Notificar a todos los jugadores
    }

    // Manejar la finalización del juego
    @MessageMapping("/endGame.{roomId}")
    public void handleEndGame(@DestinationVariable String roomId) {
        Room room = gameService.getRoom(roomId);
        gameService.endGame(room);
        
        messagingTemplate.convertAndSend("/topic/gameUpdate." + roomId, room); // Notificar a todos los jugadores
    }

    // Obtener estado actual del juego
    @MessageMapping("/getGameState.{roomId}")
    public void getGameState(@DestinationVariable String roomId) {
        Room room = gameService.getRoom(roomId);
        
        messagingTemplate.convertAndSend("/topic/gameState." + roomId, room); // Enviar el estado actual del juego
    }
}
