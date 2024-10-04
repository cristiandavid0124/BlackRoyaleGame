package com.escuelgaing.edu.co.model;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private List<Player> players;
    private Dealer dealer;
    private boolean isActive;
    private int currentRound;

    public Game() {
        this.players = new ArrayList<>();
        this.dealer = new Dealer();
        this.isActive = true;
        this.currentRound = 1;
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
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void nextRound() {
        this.currentRound++;
        for (Player player : players) {
            player.finishTurn();  // Reset para la siguiente ronda
        }
    }
}
