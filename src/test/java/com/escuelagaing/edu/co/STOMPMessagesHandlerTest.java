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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import java.util.ArrayList;


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
        // Crear un nuevo ID de sala y un objeto de sala
        String roomId1 = "roomId1";
        Room room1 = new Room();
        room1.setId(roomId1);
        room1.setPlayers(new ArrayList<>()); // Inicializar la lista de jugadores
    
   
        User user12 = new User();
        user12.setId("playerId12");

        user12.setName("PlayerName1");
        Player player = new Player(user12, roomId1, 1000.0, false);
        gameService.setRooms(room1, roomId1);
    
        // Llamar al método joinGame
        Room result = stompMessagesHandler.joinGame(roomId1, "playerId");
    
        // Verificar que el jugador se haya añadido a la sala

    
    
        // Asegurarse de que la sala tiene el jugador correcto
        assertNotNull(result, "La sala no debería ser nula");
        assertTrue(result.getPlayers().contains(player), "El jugador debería haber sido añadido a la sala");
        assertEquals(1, result.getPlayers().size(), "La sala debería tener un jugador");
    }
    
    @Test
    void testStartGame() {
        String roomId = "roomId";
        String playerId = "playerId";

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
        Room room = new Room();
        room.setId(roomId);
        // Inicializa la sala en el mock
        when(gameService.getRoom(roomId)).thenReturn(room);
        // Simular la creación de 5 jugadores
        String[] playerIds = {"playerId1", "playerId2", "playerId3"};
        for (String playerId : playerIds) {
            User user = new User();
            user.setId(playerId);
            Player player = new Player(user, roomId, 1000.0, false);
            when(gameService.getPlayerById(playerId)).thenReturn(player);
            stompMessagesHandler.joinGame(roomId, playerId);
        }
    
        // Verificar que cada jugador se haya añadido a la sala
        for (String playerId : playerIds) {
            verify(gameService).addPlayerToRoom(roomId, gameService.getPlayerById(playerId));
        }
    
        // Asegúrate de que hay 5 jugadores en la sala
        assertEquals(5, room.getPlayers().size());
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
