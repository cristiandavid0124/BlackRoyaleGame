package com.escuelgaing.edu.co.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

    // Constructor: recibe la lista de jugadores al inicio del juego
    public Game(List<Player> players) {
        this.players = players;
        this.winners = new ArrayList<>();
        this.dealer = new Dealer();
        this.isActive = true;
        this.currentRound = 1;
        this.currentPlayerIndex = 0;
    }




    public void startBetting() {
        ExecutorService executor = Executors.newFixedThreadPool(players.size());
        for (Player player : players) {
            executor.submit(() -> {
                ArrayList<Chip> chipsToBet = player.getChips();
                playerBet(player,chipsToBet);
            });
        }
        executor.shutdown();
        try {
            // Esperar a que todos los jugadores terminen de apostar o se agote el tiempo de 60 segundos
            if (!executor.awaitTermination(MAX_BET_TIME, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
        startGame();
    }


    public Dealer getDealer() {
        return dealer;
    }



    
    private void playerBet(Player player, ArrayList<Chip> chips) {
       player.setChips(chips);
    }

    // Inicia el juego
    public void startGame() {
        this.isActive = true;
        this.deck = new Deck(); // Crear un nuevo mazo
        this.deck.shuffle(); // Barajar el mazo
        resetHands();
        dealInitialCards();  // Repartir cartas iniciales
    }

    // Repartir cartas a todos los jugadores y al dealer
    public void dealInitialCards() {
        System.out.println("Repartiendo cartas iniciales...");
        for (Player player : players) {
            player.addCard(deck.drawCard()); // 1ra carta
            player.addCard(deck.drawCard()); // 2da carta
            System.out.println("Jugador: " + player.getName() + " - Cartas: " + player.getHand() + " - Puntuación: " + player.calculateScore());
        }
        dealer.addCard(deck.drawCard()); // 1ra carta del dealer
        dealer.addCard(deck.drawCard()); // 2da carta del dealer
        System.out.println("Dealer - Cartas: " + dealer.getHand() + " - Puntuación: " + dealer.calculateScore());
        startPlayerTurns();
    }

    public void startPlayerTurns() {
        for (Player player : players) {
            if (!player.isFinishTurn()) {
                System.out.println("Iniciando turno de " + player.getName());
                startPlayerTurn(player);
            } else {
                System.out.println("El jugador " + player.getName() + " ya ha terminado su turno.");
            }
        }
        dealerTurn();
    }

    private void dealerTurn() {
        while (dealer.calculateScore() < 17) {
            dealer.addCard(deck.drawCard());
        }
        endGame();
    }

    private void startPlayerTurn(Player player) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            executor.submit(() -> {
                decideAction(player);
                player.setfinishTurn(true);
            });
            executor.shutdown();
            if (!executor.awaitTermination(MAX_DECISION_TIME, TimeUnit.SECONDS)) {
                // Si el tiempo se agota, por defecto el jugador "se queda"
                player.setfinishTurn(true);
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

      
    }

    public void decideAction(Player player) {
        PlayerAction action = player.getEstado();
        switch (action) {
            case HIT:  // "robar"
                player.addCard(deck.drawCard());
                break;
            case STAND:  // "quedarse"
                player.setfinishTurn(true);
                break;
            case DOUBLE:  // "doblar"
                double doubleBet = player.getBet() * 2;
                player.placeBet(doubleBet);
                player.addCard(deck.drawCard()); // Después de doblar, solo recibe una carta más
                player.setfinishTurn(true);
                break;
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

    public boolean isActive() {
        return isActive;
    }

    public void endGame() {
        this.isActive = false;
        deliverProfit(); 
    }

    // Método para calcular y entregar las ganancias a los ganadores
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
                continue; // Si el jugador se pasa de 21, no es considerado
            }
    
            // Verifica si el dealer se pasó o si el jugador tiene una puntuación más alta
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
    
        // Si no hay ganadores, el dealer puede ser el ganador si tiene puntuación válida
        if (winners.isEmpty() && dealerScore <= 21) {
            winners.add(dealer);
            System.out.println("El dealer gana con: " + dealerScore);
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

    // Método para repartir una carta al jugador actual
    public Card dealCard() {
        Player currentPlayer = getCurrentPlayer();
        Card newCard = this.deck.drawCard();
        currentPlayer.addCard(newCard);
        return newCard;
    }

    // Obtener el jugador actual
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    // Avanzar al siguiente jugador
    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    // Reiniciar los turnos de todos los jugadores
    public void resetPlayerTurns() {
        for (Player player : players) {
            player.setfinishTurn(false);
        }
    }


    public void resetHands() {
        for (Player player : players) {
            player.resetHand(); // Limpia las cartas de cada jugador
            player.setBet(0); // Resetea la apuesta
            player.setfinishTurn(false); // Resetea el estado del turno
        }
        dealer.resetHand(); // Limpia las cartas del dealer
    }

}
