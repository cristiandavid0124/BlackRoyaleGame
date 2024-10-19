package com.escuelagaing.edu.co;

import com.escuelagaing.edu.co.controller.STOMPMessagesHandler;
import com.escuelagaing.edu.co.model.Player;
import com.escuelagaing.edu.co.model.Room;
import com.escuelagaing.edu.co.model.User;
import com.escuelagaing.edu.co.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class STOMPMessagesHandlerTest {

    @InjectMocks
    private STOMPMessagesHandler stompMessagesHandler;

    @Mock
    private GameService gameService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private Room room;
    private Player player;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        User user = new User(); // Asegúrate de que el constructor es correcto
        user.setId("playerId");
        user.setName("PlayerName");
        
        room = new Room(); // Inicializa la sala
        room.setId("roomId"); // Asigna un ID a la sala
        
        player = new Player(user, room.getRoomId(), 1000.0, false); // Inicializa el jugador
    }

    @Test
    void testJoinGame_SuccessfulJoin() {
        String roomId = "roomId";
        String playerId = "playerId";

        when(gameService.getPlayerById(playerId)).thenReturn(player);
        when(gameService.getRoom(roomId)).thenReturn(room);

        Room result = stompMessagesHandler.joinGame(roomId, playerId);

        assertSame(room, result);
        verify(gameService).addPlayerToRoom(roomId, player);
        verify(gameService).getRoom(roomId);
    }

    @Test
    void testStartGame() {
        String roomId = "roomId";

        when(gameService.getRoom(roomId)).thenReturn(room);

        Room result = stompMessagesHandler.startGame(roomId);

        assertSame(room, result);
        verify(gameService).startGame(room);
    }

    @Test
    void testPlaceBet() {
        String roomId = "roomId";
        String playerId = "playerId";
        Double betAmount = 100.0;

        when(gameService.getPlayerById(playerId)).thenReturn(player);
        when(gameService.getRoom(roomId)).thenReturn(room);

        stompMessagesHandler.placeBet(roomId, playerId, betAmount);

        verify(gameService).placeBet(player, betAmount);
        verify(messagingTemplate).convertAndSend("/topic/game/" + roomId, room);
    }

    @Test
    void testPlayerAction() {
        String roomId = "roomId";
        String playerId = "playerId";
        String action = "hit";

        when(gameService.getPlayerById(playerId)).thenReturn(player);
        when(gameService.getRoom(roomId)).thenReturn(room);

        stompMessagesHandler.playerAction(roomId, playerId, action);

        verify(gameService).processAction(player, action);
        verify(messagingTemplate).convertAndSend("/topic/game/" + roomId, room);
    }

    @Test
    void testJoinGame_MultiplePlayers() {
        String roomId = "roomId";
        String[] playerIds = {"playerId1", "playerId2", "playerId3", "playerId4", "playerId5"};
        String[] playerNames = {"PlayerName1", "PlayerName2", "PlayerName3", "PlayerName4", "PlayerName5"};
        Player[] players = new Player[5];
    
        when(gameService.getRoom(roomId)).thenReturn(room);
    
        // Simular la creación de 5 jugadores
        for (int i = 0; i < playerIds.length; i++) {
            User user = new User();
            user.setId(playerIds[i]);
            user.setName(playerNames[i]);
            players[i] = new Player(user, room.getRoomId(), 1000.0, false);
    
            when(gameService.getPlayerById(playerIds[i])).thenReturn(players[i]);
        }
    
        // Unir a los jugadores al juego
        for (String playerId : playerIds) {
            stompMessagesHandler.joinGame(roomId, playerId);
        }
    
        // Verificar que cada jugador se haya añadido a la sala
        for (Player player : players) {
            verify(gameService).addPlayerToRoom(roomId, player);
        }
        
        // Asegúrate de que hay 5 jugadores en la sala
        assertEquals(5, room.getPlayers().size()); // Asegúrate de que hay cinco jugadores en la sala
    }
    


    @Test
    void testEndGame() {
        String roomId = "roomId";

        when(gameService.getRoom(roomId)).thenReturn(room);

        Room result = stompMessagesHandler.endGame(roomId);

        assertSame(room, result);
        verify(gameService).endGame(room);
    }
}
