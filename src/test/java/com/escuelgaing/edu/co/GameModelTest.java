package com.escuelgaing.edu.co;

import com.escuelgaing.edu.co.model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class GameModelTest {

    private Game game;
    private Player player1;
    private Player player2;

    @BeforeEach
    public void setUp() {
        game = new Game();
        player1 = new Player("Player 1", 100, false);
        player2 = new Player("Player 2", 200, false);
        
        game.getPlayers().add(player1);
        game.getPlayers().add(player2);
        
        game.startGame(); 
    }

    @Test
    public void testGameStart() {
        // Verifica que el juego esté activo al iniciarse
        assertTrue(game.isActive());
        // Verifica que los jugadores y el dealer tengan manos vacías
        assertEquals(0, player1.getHand().size());
        assertEquals(0, player2.getHand().size());
        assertEquals(0, game.getDealer().getHand().size());
    }

    @Test
    public void testPlaceBet() {
        Game game = new Game();
        Player playery = new Player("Playery", 100.0, false); 
        Player playerx = new Player("Playerx", 100.0, false); 
        Player playerz = new Player("Playerz", 100.0, false); 
    
        // Añadir jugadores al juego
        game.addPlayer(playery);
        game.addPlayer(playerx);
        game.addPlayer(playerz);
    
        game.placeBet(50); 
        // Verificar que 'playery' sea el jugador actual antes de realizar la apuesta
        assertEquals(playery, game.getCurrentPlayer());
    
        // Verificar que la apuesta sea de 50
        assertEquals(50.0, playery.getBet(), 0.01);
        assertEquals(50.0, playery.getAmount(), 0.01);  // Verificar el saldo restante
    }
    
    
    @Test
    public void testPlayerPlaceBetFailure() {
        // Intenta colocar una apuesta que excede el balance del jugador
         game.placeBet(150);
        assertEquals(100, player1.getAmount()); // El saldo no debería cambiar
    }

    @Test
    public void testDealCardToPlayer() {
        // Reparte una carta al jugador y verifica que la tenga en su mano
        Card dealtCard = game.dealCard();
        assertNotNull(dealtCard);
        assertEquals(1, player1.getHand().size());
        assertEquals(dealtCard, player1.getHand().get(0));
    }


    @Test
public void testTieWithDealer() {
    player1.addCard(new Card("Hearts", "10", 10));
    player1.addCard(new Card("Diamonds", "7", 7)); // Total: 17
    
    game.getDealer().addCard(new Card("Clubs", "10", 10));
    game.getDealer().addCard(new Card("Spades", "7", 7)); // Dealer total: 17
    
    List<Player> winners = game.calculateWinners();
    
    // No debería haber ganadores ya que ambos tienen el mismo puntaje
    assertEquals(0, winners.size());
}


    @Test
    public void testCalculateWinners() {
        // Configura las manos de los jugadores y del dealer
        player1.addCard(new Card("Hearts", "8", 8));
        player1.addCard(new Card("Diamonds", "9", 9)); // Total: 17
    
        player2.addCard(new Card("Clubs", "7", 7));
        player2.addCard(new Card("Spades", "10", 10)); // Total: 17
    
        game.getDealer().addCard(new Card("Hearts", "6", 6));
        game.getDealer().addCard(new Card("Diamonds", "10", 10)); // Dealer total: 16
    
        List<Player> winners = game.calculateWinners();
    
        // Verifica que haya 2 ganadores (ambos jugadores empatan con 17)
        assertEquals(2, winners.size());
        assertTrue(winners.contains(player1));
        assertTrue(winners.contains(player2));
    }


    @Test
public void testChangeTurn() {
    game.dealCard(); // Repartir una carta al jugador 1
    assertEquals(player1, game.getCurrentPlayer());
    
    game.changeTurn(); // Cambiar al siguiente jugador
    assertEquals(player2, game.getCurrentPlayer());
    
    game.changeTurn(); // Volver al primer jugador (ya que solo hay dos jugadores)
    assertEquals(player1, game.getCurrentPlayer());
}
    
@Test
public void testFullGameCycle() {
    game.placeBet(50);
    game.dealCard(); // Reparte una carta al jugador 1
    game.changeTurn(); // Cambia al siguiente jugador

    game.placeBet(50);
    game.dealCard(); // Reparte una carta al jugador 2
    game.changeTurn(); 
    assertEquals(2, game.getCurrentRound()); 
    assertFalse(player1.isFinishTurn());
    assertFalse(player2.isFinishTurn());
}


@Test
public void testResetGame() {
    game.placeBet(50);
    game.dealCard(); // Repartir una carta
    game.nextRound(); // Avanzar a la siguiente ronda
    
    game.resetGame(); // Reiniciar el juego
    
    assertEquals(1, game.getCurrentRound()); // La ronda debería ser 1
    assertEquals(0, player1.getHand().size()); // Las manos deberían estar vacías
    assertEquals(0, player2.getHand().size());
    assertTrue(game.isActive()); // El juego debería estar activo
}



    @Test
    public void testEndGame() {
        // Termina el juego y verifica que el estado cambie a inactivo
        game.endGame();
        assertFalse(game.isActive());
    }
}
