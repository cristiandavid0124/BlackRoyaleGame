package com.escuelagaing.edu.co;
import com.escuelagaing.edu.co.model.Player;
import com.escuelagaing.edu.co.model.Room;
import com.escuelagaing.edu.co.model.User;
import com.escuelagaing.edu.co.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameService = new GameService(); // Inicializa el GameService antes de cada prueba
    }

    @Test
    void testCreateRoom() {
        Room room = gameService.createRoom();
        assertNotNull(room, "La sala no debería ser null.");
        assertNotNull(room.getRoomId(), "La sala debería tener un ID.");
        assertEquals(0, room.getPlayers().size(), "La sala debería tener cero jugadores inicialmente.");
    }

    @Test
    void testAddPlayerToExistingRoom_Success() {
        // Crear una sala y agregarla al servicio
        Room room = gameService.createRoom();
        String roomId = room.getRoomId();
             User user = new User(); // Asegúrate de que el constructor es correcto
        user.setId("playerId");
        user.setName("PlayerName");
        // Crear un jugador
        Player player = new Player(user, roomId, 1000.0, false);

        // Agregar el jugador a la sala existente
        gameService.addPlayerToRoom(roomId, player);

        // Verificar que el jugador fue añadido correctamente
        Room updatedRoom = gameService.getRoom(roomId);
        assertNotNull(updatedRoom, "La sala no debería ser null.");
        assertEquals(1, updatedRoom.getPlayers().size(), "La sala debería tener un jugador.");
        assertTrue(updatedRoom.getPlayers().contains(player), "La sala debería contener al jugador.");
    }

    @Test
    void testAddPlayerToNewRoom_Success() {
        // Crear un nuevo ID de sala
        String roomId = "new-room";

        User user = new User(); 
        user.setId("playerId");
        user.setName("PlayerName");
        // Crear un jugador
        Player player = new Player(user, roomId, 1000.0, false);
        // Agregar el jugador a una sala nueva
        gameService.addPlayerToRoom(roomId, player);

        // Verificar que la nueva sala fue creada y el jugador fue añadido
        Room newRoom = gameService.getRoom(roomId);
        assertNotNull(newRoom, "La nueva sala debería haber sido creada.");
        assertEquals(1, newRoom.getPlayers().size(), "La nueva sala debería tener un jugador.");
        assertTrue(newRoom.getPlayers().contains(player), "La nueva sala debería contener al jugador.");
    }

    @Test
    void testGetPlayerById_Success() {
        // Crear una sala y agregar un jugador
        Room room = gameService.createRoom();
        String roomId = room.getRoomId();

        User user = new User(); 
        user.setId("playerId");
        user.setName("PlayerName");
        // Crear un jugador
        Player player = new Player(user, roomId, 1000.0, false);
  
        gameService.addPlayerToRoom(roomId, player);

        // Recuperar el jugador por ID
        Player retrievedPlayer = gameService.getPlayerById("playerId");
        assertNotNull(retrievedPlayer, "El jugador debería ser encontrado.");
        assertEquals("playerId", retrievedPlayer.getId(), "El ID del jugador debería coincidir.");
    }

    @Test
    void testGetNonExistingPlayerById_ReturnsNull() {
        // Intenta recuperar un jugador que no existe
        Player retrievedPlayer = gameService.getPlayerById("non-existing-player");
        assertNull(retrievedPlayer, "El jugador no debería ser encontrado.");
    }
}
