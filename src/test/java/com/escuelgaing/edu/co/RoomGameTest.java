package com.escuelgaing.edu.co;

import org.junit.jupiter.api.Test;
import com.escuelgaing.edu.co.model.*;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RoomGameTest {

    private static final PlayerAction STAND = null;

    @Test
    public void testAddPlayer() {
        Room room = new Room();
        Player player1 = new Player("Alice", 100, false);
        Player player2 = new Player("Bob", 100, false);
        Player player3 = new Player("Charlie", 100, false);
        Player player4 = new Player("Dave", 100, false);

        assertTrue(room.addPlayer(player1));
        assertEquals(1, room.getPlayers().size());

        assertTrue(room.addPlayer(player2));
        assertEquals(2, room.getPlayers().size());

        assertTrue(room.addPlayer(player3));
        assertEquals(3, room.getPlayers().size());
        assertEquals(RoomStatus.EN_JUEGO, room.getStatus());  // El juego debe comenzar

        assertTrue(room.addPlayer(player4));
        assertEquals(4, room.getPlayers().size());

        // Probar que no se pueden agregar más de 5 jugadores
        Player player5 = new Player("Eve", 100, false);
        Player player6 = new Player("Frank", 100, false);
        assertTrue(room.addPlayer(player5));
        assertFalse(room.addPlayer(player6));  // No debería poder agregar
        assertEquals(5, room.getPlayers().size());
    }

    @Test
    public void testRemovePlayer() {
        Room room = new Room();
        Player player1 = new Player("Alice", 100, false);
        Player player2 = new Player("Bob", 100, false);
        Player player3 = new Player("Charlie", 100, false);
        Player player4 = new Player("Dave", 100, false);

        room.addPlayer(player1);
        room.addPlayer(player2);
        room.addPlayer(player3);

        // Remover un jugador y comprobar que la cantidad disminuye
        assertTrue(room.removePlayer(player2));
        assertEquals(2, room.getPlayers().size());

        // Eliminar a un jugador que no está en la sala
        assertFalse(room.removePlayer(player4));  // No está en la sala
    }

    @Test
public void testStartAndEndGame() {
    Room room = new Room();
    Player player1 = new Player("Alice", 100, false);
    Player player2 = new Player("Bob", 100, false);
    Player player3 = new Player("Charlie", 100, false);

    room.addPlayer(player1);
    room.addPlayer(player2);
    room.addPlayer(player3);
    
    room.startGame();  // Asegúrate de llamar al método startGame() aquí

    assertEquals(RoomStatus.EN_JUEGO, room.getStatus());  // El juego debería estar en progreso

    room.endGame();
    assertEquals(RoomStatus.FINALIZADO, room.getStatus());  // Después de finalizar
}

@Test
public void testGameFlowWithSmallBets() throws InterruptedException {
    // Crear una sala y agregar jugadores
    Room room = new Room();
    Player player1 = new Player("Alice", 100, false);
    Player player2 = new Player("Bob", 100, false);
    Player player3 = new Player("Charlie", 100, false);

    room.addPlayer(player1);
    room.addPlayer(player2);
    room.addPlayer(player3);

    // Iniciar el juego pasando los jugadores de la sala
    Game game = new Game(room.getPlayers());

    // Establecer apuestas pequeñas para cada jugador
    double smallBet = 10.0;
    for (Player player : room.getPlayers()) {
        player.setBet(smallBet);  // Asignar una pequeña apuesta a cada jugador
    }

    // Asignar una acción por defecto (e.g., STAND) para evitar que esperen los 40 segundos
    for (Player player : room.getPlayers()) {
        player.setEstado(PlayerAction.STAND);  // Los jugadores se quedarán en su lugar
    }

    // Comenzar el proceso de apuestas (esto también inicia el juego)
    game.startBetting();

    // Verificar que cada jugador tiene 2 cartas después de repartir
    for (Player player : room.getPlayers()) {
        assertEquals(2, player.getHand().size());  // Verifica que el jugador tiene 2 cartas
    }

   

    // Probar los turnos de los jugadores
    game.startPlayerTurns();

    // Verificar que cada jugador ha terminado su turno
    for (Player player : room.getPlayers()) {
        assertTrue(player.isFinishTurn(), "El jugador " + player.getName() + " no ha terminado su turno.");
    }

    // Verificar que el dealer ha jugado su turno

   

    // Verificar que el juego ha finalizado
    game.endGame();
    assertFalse(game.isActive(), "El juego debería estar inactivo después de finalizar.");

    // Verificar los ganadores
    List<Player> winners = game.calculateWinners();
    assertNotNull(winners, "La lista de ganadores no debería ser nula.");
    if (!winners.isEmpty()) {
        for (Player winner : winners) {
            assertTrue(winner.calculateScore() <= 21, "El ganador no debería pasarse de 21.");
        }
    } 
     
    }

    @Test
    public void testPlayerBet() {
        Room room = new Room();
        Player player1 = new Player("Alice", 100, false);
        Player player2 = new Player("Bob", 100, false);
        Player player3 = new Player("Charlie", 100, false);

        room.addPlayer(player1);
        room.addPlayer(player2);
        room.addPlayer(player3);

        Game game = new Game(room.getPlayers());
        game.startBetting();  // Asegúrate de que las apuestas comiencen

        player1.placeBet(20);
        player2.placeBet(50);
        player3.placeBet(30);

        assertEquals(80, player1.getAmount());
        assertEquals(50, player2.getAmount());
        assertEquals(70, player3.getAmount());
    }

    @Test
    public void testCalculateWinners() {
        Room room = new Room();
        Player player1 = new Player("Alicew", 100, false);
        Player player2 = new Player("Bobb", 100, false);
        Player player3 = new Player("Charliew", 100, false);
        
        room.addPlayer(player1);
        room.addPlayer(player2);
        room.addPlayer(player3);
        
        // Crear el dealer y el juego
        Game game = new Game(room.getPlayers());
        
        // Asegúrate de resetear las manos antes de agregar cartas
        game.resetHands();
    
        // Asignar cartas manualmente (asegurando que sean únicas)
        player1.addCard(new Card("Hearts", "10", 10));
        player1.addCard(new Card("Spades", "6", 6)); // 16 puntos, no se pasa
        
        player2.addCard(new Card("Hearts", "9", 9));
        player2.addCard(new Card("Diamonds", "5", 5)); // 14 puntos
        
        player3.addCard(new Card("Clubs", "Ace", 11));  // 11 puntos
        player3.addCard(new Card("Hearts", "King", 10)); // 21 puntos
        
        // Asignar cartas al dealer (asegurando que sean únicas)
        Dealer dealer = game.getDealer();
        dealer.addCard(new Card("Hearts", "8", 8));
        dealer.addCard(new Card("Clubs", "8", 8)); // 16 puntos
        
        // Calculamos los ganadores sin finalizar el juego
        List<Player> winners = game.calculateWinners();
    
        game.endGame();
        
        // Verificar que el ganador es el jugador 3 con 21 puntos
        assertEquals(1, winners.size());
        assertEquals("Charliew", winners.get(0).getName());
    }


    @Test
public void testPlayerChips() {
    Player player = new Player("Alice", 100, false);
    
    ArrayList<Chip> initialChips = new ArrayList<>();
    initialChips.add(Chip.AMARILLO);
    initialChips.add(Chip.ROJO);
    
    player.setChips(initialChips);
    
    assertEquals(2, player.getChips().size()); 
    assertTrue(player.getChips().contains(Chip.AMARILLO));
    assertTrue(player.getChips().contains(Chip.ROJO));
}
    
    
    
    
}
