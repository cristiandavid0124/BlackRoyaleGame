package com.escuelagaing.edu.co;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import com.escuelagaing.edu.co.model.*;

class GameTest {

    @Test
    void testStartGame() {
        // Crear jugadores
        List<Player> players = createPlayers();
        
        // Crear la instancia del juego
        Game game = new Game(players, "room1");
        
        // Iniciar el juego
        game.startGame();
        
        // Verificar que el juego esté activo
        assertTrue(game.isActive(), "El juego debería estar activo");
        
        // Verificar que las manos de los jugadores tengan 2 cartas
        assertEquals(2, players.get(0).getHand().size(), "El jugador 1 debería tener 2 cartas");
        assertEquals(2, players.get(1).getHand().size(), "El jugador 2 debería tener 2 cartas");
        
        // Verificar que el dealer tenga 2 cartas
        assertEquals(2, game.getDealer().getHand().size(), "El dealer debería tener 2 cartas");
    }

    @Test
    void testStartBetting() {
        // Crear jugadores
        List<Player> players = createPlayers();
        
        // Crear la instancia del juego
        Game game = new Game(players, "room1");
        

        // Realizar la fase de apuestas

     
        

        // Verificar que los jugadores hayan realizado sus apuestas
        for (Player player : players) {
            assertTrue(player.getBet() > 0, "El jugador " + player.getName() + " debería haber apostado");
        }
    }

    @Test
    void testPlayerTurns() {
        // Crear jugadores
        List<Player> players = createPlayers();
        
        // Crear la instancia del juego
        Game game = new Game(players, "room1");
        
        // Iniciar el juego
        game.startGame();
        
        // Simular apuestas

        // Comenzar los turnos de los jugadores
        game.startPlayerTurns();

        // Verificar que todos los jugadores hayan terminado su turno
        for (Player player : players) {
            assertTrue(player.isFinishTurn(), "El jugador " + player.getName() + " debería haber terminado su turno");
        }
    }

    @Test
    void testDealerTurn() {
        // Crear jugadores
        List<Player> players = createPlayers();
        
        // Crear la instancia del juego
        Game game = new Game(players, "room1");
        
        // Iniciar el juego
        game.startGame();
        
        // Simular apuestas
    
        // Comenzar los turnos de los jugadores
        game.startPlayerTurns();
        
        // Ejecutar el turno del dealer
        game.dealerTurn();
        
        // Verificar que el dealer tiene al menos 17 puntos
        assertTrue(game.getDealer().calculateScore() >= 17, "El dealer debería tener al menos 17 puntos");
    }

    @Test
    void testCalculateWinners() {
        // Crear jugadores
        List<Player> players = createPlayers();
        
        // Crear la instancia del juego
        Game game = new Game(players, "room1");
        
        // Simular manos de los jugadores y el dealer
        simulateGameEnd(game);

        // Calcular los ganadores
        List<Player> winners = game.calculateWinners();
        
        // Verificar que el ganador sea el correcto
        assertTrue(winners.contains(players.get(0)), "El jugador 1 debería ser el ganador");
        assertFalse(winners.contains(players.get(1)), "El jugador 2 no debería ser el ganador");
    }

    @Test
    void testEndGame() {
        // Crear jugadores
        List<Player> players = createPlayers();
        
        // Crear la instancia del juego
        Game game = new Game(players, "room1");
        
        // Iniciar el juego
        game.startGame();
        
        // Terminar el juego
        game.endGame();
        
        // Verificar que el juego esté inactivo
        assertFalse(game.isActive(), "El juego debería estar inactivo");
    }

    private List<Player> createPlayers() {
        List<Player> players = new ArrayList<>();
        User user1 = new User("1", "user1@example.com", "Player1");
        User user2 = new User("2", "user2@example.com", "Player2");
        Player player1 = new Player(user1, "pepe",  "room1", 10000);
        Player player2 = new Player(user2, "marco","room1", 10000);
        players.add(player1);
        players.add(player2);
        ArrayList<Chip> chipsPlayer1 = new ArrayList<>();
        chipsPlayer1.add(Chip.AMARILLO);
        chipsPlayer1.add(Chip.AZUL);
        chipsPlayer1.add(Chip.ROJO);

        player1.setChips(chipsPlayer1);

        ArrayList<Chip> chipsPlayer2 = new ArrayList<>();
        chipsPlayer2.add(Chip.VERDE);
        chipsPlayer2.add(Chip.NEGRO);

        player2.setChips(chipsPlayer2);
        return players;
    }

    void simulateGameEnd(Game game) {
        // Simular las manos de los jugadores y el dealer
        Player player1 = game.getPlayers().get(0);
        Player player2 = game.getPlayers().get(1);
        
        player1.addCard(new Card("Corazones", "10", 10));
        player1.addCard(new Card("Picas", "10", 10));
        
        player2.addCard(new Card("Diamantes", "5", 5));
        player2.addCard(new Card("Tréboles", "6", 6));
        
        game.getDealer().addCard(new Card("Corazones", "9", 9));
        game.getDealer().addCard(new Card("Picas", "10", 10));
        
        game.endGame();  // Llamar a endGame para calcular ganadores
    }
}