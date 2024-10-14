package com.escuelgaing.edu.co.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Game {
    private List<Player> players;
    private Dealer dealer;
    private boolean isActive;
    private int currentRound;
    private List<Player> winners;
    private Deck deck;
    private int currentPlayerIndex;
    private final int MAX_BET_TIME = 60;  // 60 segundos para apostar
    private final int MAX_DECISION_TIME = 40;  // 40 segundos por decisión
    private final Lock playerLock = new ReentrantLock();  // Lock para evitar condiciones de carrera en jugadores
    private CyclicBarrier barrier;

    // Constructor: recibe la lista de jugadores al inicio del juego
    public Game(List<Player> players,String roomId) {
        this.players = players;
        this.winners = new ArrayList<>();
        this.dealer = new Dealer(roomId);
        this.isActive = true;
        this.currentRound = 1;
        this.currentPlayerIndex = 0;
        this.barrier = new CyclicBarrier(players.size() + 1);  // +1 para incluir al coordinador
    }

    public void startBetting() {
        ExecutorService executor = Executors.newFixedThreadPool(players.size());
        for (Player player : players) {
            executor.submit(() -> {
                try {
                    ArrayList<Chip> chipsToBet = player.getChips();
                    playerBet(player, chipsToBet);
                } catch (Exception e) {
                    System.out.println("Error al realizar la apuesta para el jugador: " + player.getName());
                }
            });
        }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(MAX_BET_TIME, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                System.out.println("Se terminó el tiempo para apostar.");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            System.out.println("La espera fue interrumpida.");
        }
        startGame();
    }

    public Dealer getDealer() {
        return dealer;
    }

    private synchronized void playerBet(Player player, ArrayList<Chip> chips) {
        player.setChips(chips);
    }

    public void startGame() {
        this.isActive = true;
        this.deck = new Deck(); // Crear un nuevo mazo
        this.deck.shuffle(); // Barajar el mazo
        resetHands();
        dealInitialCards();
    }

    public List<Player> getPlayers(){
        return players;
    }

    public void dealInitialCards() {
        System.out.println("Repartiendo cartas iniciales...");
        for (Player player : players) {
            player.addCard(deck.drawCard());
            player.addCard(deck.drawCard());
            System.out.println("Jugador: " + player.getName() + " - Cartas: " + player.getHand() + " - Puntuación: " + player.calculateScore());
        }
        dealer.addCard(deck.drawCard());
        dealer.addCard(deck.drawCard());
        System.out.println("Dealer - Cartas: " + dealer.getHand() + " - Puntuación: " + dealer.calculateScore());
        startPlayerTurns();
    }

    public void startPlayerTurns() {
        ExecutorService executor = Executors.newFixedThreadPool(players.size());
        for (Player player : players) {
            executor.submit(() -> startPlayerTurn(player));
        }
        executor.shutdown();
        
        // El hilo del coordinador espera a que todos los jugadores terminen su turno
        try {
            barrier.await(); // Esperar a que todos los jugadores terminen
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
        
        // Esperar a que el executor termine
        try {
            if (!executor.awaitTermination(MAX_DECISION_TIME, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                System.out.println("Tiempo de espera agotado para los turnos de los jugadores.");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            System.out.println("La espera fue interrumpida.");
        }
        
        dealerTurn(); // Iniciar el turno del dealer
    }
    

    private void dealerTurn() {
        while (dealer.calculateScore() < 17) {
            dealer.addCard(deck.drawCard());
        }
        endGame();
    }

    


    private void startPlayerTurn(Player player) {
        try {
            decideAction(player);  // Decisión de acción del jugador
        } catch (Exception e) {
            System.out.println("Error en el turno del jugador: " + player.getName());
        } finally {
            player.setfinishTurn(true);
            try {
                barrier.await(); // Esperar en la barrera
            } catch (InterruptedException | BrokenBarrierException e) {
                System.out.println("Error al esperar en la barrera para " + player.getName());
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
                double doubleBet = player.getBet() * 2;
                player.placeBet(doubleBet);
                player.addCard(deck.drawCard());
                player.setfinishTurn(true);
                break;
            default:
            player.setfinishTurn(true);
        }
    }

    public void resetGame() {
        this.currentRound = 1;
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

    public void endGame() {
        this.isActive = false;
        deliverProfit();
    }

    public void deliverProfit() {
        List<Player> winners = calculateWinners();
        for (Player player : winners) {
            player.revenue();
        }
    }

    public List<Player> calculateWinners() {
        int dealerScore = dealer.calculateScore();
        int highestScore = 0;
        
        winners.clear();
    
        System.out.println("Puntuación del Dealer: " + dealerScore);
        
        for (Player player : players) {
            int playerScore = player.calculateScore();
            System.out.println("Jugador: " + player.getName() + " - Cartas: " + player.getHand() + " - Puntuación: " + playerScore);
    
            if (playerScore > 21) {
                System.out.println("Jugador " + player.getName() + " se pasó de 21.");
                continue;
            }
    
            if (playerScore > dealerScore || dealerScore > 21) {
                if (playerScore > highestScore) {
                    highestScore = playerScore;
                    winners.clear();
                    winners.add(player);
                    System.out.println("Nuevo máximo: " + player.getName() + " con " + playerScore);
                } else if (playerScore == highestScore) {
                    winners.add(player);
                    System.out.println("Empate entre jugadores: " + player.getName());
                }
            }
        }
    
        if (winners.isEmpty() && dealerScore <= 21) {
            winners.add(dealer);
           
        }
    
        if (!winners.isEmpty()) {
            System.out.println("Ganadores: ");
            for (Player winner : winners) {
                System.out.println(winner.getName() + " con puntuación: " + winner.calculateScore());
            }
        } else {
            System.out.println("No hay ganadores.");
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
            player.setBet(0);
            player.setfinishTurn(false);
        }
        dealer.resetHand();
    }
}
