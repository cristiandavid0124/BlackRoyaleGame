package com.escuelgaing.edu.co.model;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private List<Player> players;
    private Dealer dealer;
    private boolean isActive;
    private int currentRound;
    private List<Player> winers;
    private Deck deck;
    private int currentPlayerIndex; 

    public Game() {
        this.players = new ArrayList<>();
        this.winers = new ArrayList<>();
        this.dealer = new Dealer();
        this.isActive = true;
        this.currentRound = 1;
        this.currentPlayerIndex = 0; 
    }


    public void addPlayer(Player player) {
        players.add(player);
    }

    public void resetGame() {
        this.deck = new Deck();
        this.deck.shuffle();
    
        this.currentRound = 1;
        for (Player player : players) {
            player.getHand().clear(); 
            player.setBet(0); 
            player.setfinishTurn(false);
        }
        dealer.getHand().clear();
        this.currentPlayerIndex = 0;
        this.winers.clear();
        this.isActive = true;
    }


    public void startGame() {
        this.deck = new Deck();
        this.deck.shuffle(); 
        this.isActive = true; 
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Dealer getDealer() {
        return dealer;
    }

    public boolean isActive() {
        return isActive;
    }

    public void endGame() {
        this.isActive = false;
        Deliverprofit();
    }


    public void Deliverprofit(){
        List<Player> winers = calculateWinners();

        for (Player player : winers) {
            player.revenue();
        }

    }

    public int getCurrentRound() {
        return currentRound;
    }

   
    public void nextRound() {
        this.currentRound++;
        for (Player player : players) {
            player.setfinishTurn(true);  
        }
    }


    public List<Player> calculateWinners() {
        int dealerScore = dealer.calculateScore();
        int highestScore = 0;
        winers.clear();
    
        for (Player player : players) {
            int playerScore = player.calculateScore();
    
            if (playerScore > 21) {
                continue; 
            }
            
            if (playerScore > dealerScore || dealerScore > 21) {
                if (playerScore > highestScore) {
                    highestScore = playerScore;
                    winers.clear(); 
                    winers.add(player);
                } else if (playerScore == highestScore) {
                    winers.add(player); 
                }
            }
        }
    
        if (winers.isEmpty() && dealerScore <= 21) {
            winers.add(dealer); 
        }
    
        if (winers.size() == 1 && winers.contains(dealer)) {
            for (Player player : players) {
                if (player.calculateScore() == dealerScore) {
                    winers.clear(); 
                    break;
                }
            }
        }
        return winers;
    }




    public void placeBet(double amount) {
        Player currentPlayer = getCurrentPlayer(); 
        currentPlayer.placeBet(amount);
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


    public void decideAction(String result) {
        if(result == "robar"){
            dealCard();
        }else if(result == "quedarse"){
            changeTurn();
        }else if(result == "doblar"){
            double doblebet = getCurrentPlayer().getBet() * 2;
            placeBet(doblebet);
        }
    }


    public void changeTurn() {
        getCurrentPlayer().setfinishTurn(true);
    
        // Aumentamos el índice del jugador actual para pasar al siguiente turno
        currentPlayerIndex++;
    

        if (currentPlayerIndex >= players.size()) {
            currentPlayerIndex = 0; // Volver al primer jugador
            nextRound(); // Avanzar a la siguiente ronda
            resetPlayerTurns(); // Restablecer el estado de los turnos
        }
    }

}
