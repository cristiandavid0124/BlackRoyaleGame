package com.escuelagaing.edu.co.controller;

import com.escuelagaing.edu.co.model.Player;
import com.escuelagaing.edu.co.model.Room;
import com.escuelagaing.edu.co.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class STOMPMessagesHandler {

    @Autowired
    private GameService gameService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Cuando un jugador se une a la sala
    @MessageMapping("/{roomId}/{playerId}/join")
    @SendTo("/topic/game/{roomId}")
    public Room joinGame(@DestinationVariable String roomId, @DestinationVariable String playerId) {
        Player player = gameService.getPlayerById(playerId);
        gameService.addPlayerToRoom(roomId, player);
        Room room = gameService.getRoom(roomId);
        return room; // Envía la sala actualizada a todos los suscriptores
    }

    // Cuando se inicia el juego
    @MessageMapping("/{roomId}/start")
    @SendTo("/topic/game/{roomId}")
    public Room startGame(@DestinationVariable String roomId) {
        Room room = gameService.getRoom(roomId);
        gameService.startGame(room);
        return room; // Envía el estado inicial del juego a todos
    }

    // Cuando un jugador hace una apuesta
    @MessageMapping("/{roomId}/{playerId}/bet")
    public void placeBet(@DestinationVariable String roomId, @DestinationVariable String playerId, Double betAmount) {
        Player player = gameService.getPlayerById(playerId);
        gameService.placeBet(player, betAmount);
        Room room = gameService.getRoom(roomId);
        messagingTemplate.convertAndSend("/topic/game/" + roomId, room); // Actualiza la sala
    }

    // Cuando un jugador realiza una acción (hit, stand, etc.)
    @MessageMapping("/{roomId}/{playerId}/{action}")
    public void playerAction(@DestinationVariable String roomId, @DestinationVariable String playerId, @DestinationVariable String action) {
        Player player = gameService.getPlayerById(playerId);
        gameService.processAction(player, action);
        Room room = gameService.getRoom(roomId);
        messagingTemplate.convertAndSend("/topic/game/" + roomId, room); // Actualiza la sala
    }

    // Obtener los ganadores al final del juego
    @MessageMapping("/{roomId}/end")
    @SendTo("/topic/winners/{roomId}")
    public Room endGame(@DestinationVariable String roomId) {
        Room room = gameService.getRoom(roomId);
        gameService.endGame(room);
        return room; // Envía los ganadores al final del juego
    }
}
