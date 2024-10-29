package com.escuelagaing.edu.co.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;

public class Game {

    private static final Logger logger = LoggerFactory.getLogger(Game.class);

    private String id;
    private List<Player> players;
    private Dealer dealer;
    private boolean isActive;
    private List<Player> winners;
    private int currentPlayerIndex;
    private static final int MAX_BET_TIME = 60;  // 60 segundos para apostar
    private static final int MAX_DECISION_TIME = 40;  // 40 segundos para decisiones
    private static final String SCORE_TEXT = " - Puntuación: ";  // Constant for repeated string literal

    @Transient
    private Deck deck;

    @Transient
    private CyclicBarrier barrier;

    public Game(List<Player> players, String roomId) {
        this.id = ObjectId.get().toHexString();
        this.players = players;
        this.winners = new ArrayList<>();
        this.dealer = new Dealer(roomId);
        this.isActive = true;
        this.currentPlayerIndex = 0;
        this.barrier = new CyclicBarrier(players.size() + 1); 
    }

    // FASE 2: Fase de Apuestas
    public void startBetting() {
        ExecutorService executor = Executors.newFixedThreadPool(players.size());
        for (Player player : players) {
            executor.submit(() -> {
                try {
                    List<Chip> chipsToBet = player.getChips();
                    playerBet(player, chipsToBet);
                } catch (Exception e) {
                    logger.error("Error al realizar la apuesta para el jugador: " + player.getName(), e);
                }
            });
        }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(MAX_BET_TIME, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                logger.info("Se terminó el tiempo para apostar.");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            logger.error("La espera fue interrumpida.", e);
            Thread.currentThread().interrupt(); // Re-interrumpe el hilo actual
        }
    }

    public Dealer getDealer() {
        return dealer;
    }

    private synchronized void playerBet(Player player, List<Chip> chipsToBet) {
        player.setChips(chipsToBet);
    }

    // FASE 3: Fase de Reparto de Cartas Iniciales
    public void startGame() {
        this.isActive = true;
        this.deck = new Deck(); // Crear un nuevo mazo
        this.deck.shuffle(); // Barajar el mazo
        resetHands();
        dealInitialCards();
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void dealInitialCards() {
        logger.info("Repartiendo cartas iniciales...");
        for (Player player : players) {
            player.addCard(deck.drawCard());
            player.addCard(deck.drawCard());
            logger.info("Jugador: " + player.getName() + " - Cartas: " + player.getHand() + SCORE_TEXT + player.calculateScore());
        }
        dealer.addCard(deck.drawCard());
        dealer.addCard(deck.drawCard());
        logger.info("Dealer - Cartas: " + dealer.getHand() + SCORE_TEXT + dealer.calculateScore());
    }

    // FASE 4: Fase de Decisiones de Jugadores
    public void startPlayerTurns() {
        ExecutorService executor = Executors.newFixedThreadPool(players.size());
        for (Player player : players) {
            executor.submit(() -> startPlayerTurn(player));
        }
        executor.shutdown();

        try {
            barrier.await(); // Esperar a que todos los jugadores terminen
        } catch (InterruptedException | BrokenBarrierException e) {
            logger.error("Error en la barrera durante los turnos de los jugadores.", e);
            Thread.currentThread().interrupt(); // Re-interrumpe el hilo actual
        }

        try {
            if (!executor.awaitTermination(MAX_DECISION_TIME, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                logger.info("Tiempo de espera agotado para los turnos de los jugadores.");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            logger.error("La espera fue interrumpida.", e);
            Thread.currentThread().interrupt(); // Re-interrumpe el hilo actual
        }
    }

    private void startPlayerTurn(Player player) {
        try {
            decideAction(player);  // Decisión de acción del jugador
        } catch (Exception e) {
            logger.error("Error en el turno del jugador: " + player.getName(), e);
        } finally {
            player.setfinishTurn(true);
            try {
                barrier.await(); // Esperar en la barrera
            } catch (InterruptedException | BrokenBarrierException e) {
                logger.error("Error al esperar en la barrera para " + player.getName(), e);
                Thread.currentThread().interrupt(); // Re-interrumpe el hilo actual
            }
        }
    }

    public void decideAction(Player player) {
        PlayerAction action = player.getEstado();
        switch (action) {
            case HIT:
                player.addCard(deck.drawCard());
                break;
            case STAND:
                player.setfinishTurn(true);
                break;
                case DOUBLE:
                double additionalBet = player.getBet(); // Tomar solo la cantidad inicial
                player.placeBet(additionalBet); // Apostar adicionalmente la cantidad inicial
                player.addCard(deck.drawCard());
                player.setfinishTurn(true);
                break;
            default:
                player.setfinishTurn(true);
        }
    }

    // FASE 5: Turno del Dealer
    public void dealerTurn() {
        while (dealer.calculateScore() < 17) {
            dealer.addCard(deck.drawCard());
        }
    }

    public void resetGame() {
        for (Player player : players) {
            player.getHand().clear();
            player.setBet(0);
            player.setfinishTurn(false);
        }
        dealer.getHand().clear();
        this.currentPlayerIndex = 0;
        this.winners.clear();
        this.isActive = true;
    }

    // FASE 6: Fin del Juego y Distribución de Ganancias
    public void endGame() {
        this.isActive = false;
        deliverProfit(); 
    }

    public void deliverProfit() {
        List<Player> winners = calculateWinners();
        for (Player player : players) {
            if (winners.contains(player)) {
                player.revenue(true); // Si el jugador es ganador, aplica revenue(true)
            } else {
                player.revenue(false); // Si no es ganador, aplica revenue(false)
            }
        }
    }
    public List<Player> calculateWinners() {
        int dealerScore = dealer.calculateScore();
        int highestScore = 0;
    
        // Limpiar la lista de ganadores al inicio para evitar conflictos
        winners.clear();
    
        logger.info("Puntuación del Dealer: " + dealerScore);
    
        // Verificar si el dealer tiene una puntuación válida para competir
        if (dealerScore <= 21) {
            highestScore = dealerScore;
            winners.add(dealer);
        }
    
        for (Player player : players) {
            int playerScore = player.calculateScore();
            logger.info("Jugador: " + player.getName() + " - Cartas: " + player.getHand() + SCORE_TEXT + playerScore);
    
            if (playerScore > 21) {
                logger.info("Jugador " + player.getName() + " se pasó de 21.");
                continue; // Si el jugador se pasó de 21, no puede ganar
            }
    
            if (playerScore > highestScore) {
                highestScore = playerScore;
                winners.clear(); // Actualizar la lista de ganadores con un nuevo puntaje más alto
                winners.add(player);
            } else if (playerScore == highestScore) {
                winners.add(player);
            }
        }
    
        return winners;
    }

    public Card dealCard() {
        Player currentPlayer = getCurrentPlayer();
        Card newCard = this.deck.drawCard();
        currentPlayer.addCard(newCard);
        return newCard;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public void resetPlayerTurns() {
        for (Player player : players) {
            player.setfinishTurn(false);
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void resetHands() {
        for (Player player : players) {
            player.resetHand();
            player.setfinishTurn(false);
        }
        dealer.resetHand();
    }

    public Deck getDeck() {
        return deck;
    }
}
