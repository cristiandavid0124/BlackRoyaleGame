// PlayerStateDTO.java
package com.escuelagaing.edu.co.dto;

import com.escuelagaing.edu.co.model.Card;
import com.escuelagaing.edu.co.model.Chip;
import com.escuelagaing.edu.co.model.Player;

import java.util.List;

public class PlayerStateDTO {

    private String nickName;
    private double amount;
    private List<Card> hand;
    private List<Chip> chips;
    private double bet;
    private boolean isInTurn; // Campo para indicar si el jugador está en turno

    public PlayerStateDTO(Player player) {
        this.nickName = player.getUser().getNickName();
        this.amount = player.getAmount();
        this.bet = (player.getBet() != 0) ? player.getBet() : 0.0;
        this.hand = (player.getHand() != null) ? player.getHand() : List.of();
        this.chips = (player.getChips() != null) ? player.getChips() : List.of();
        this.isInTurn = player.getInTurn(); 
    }

    // Getters
    public String getNickName() {
        return nickName;
    }

    public double getAmount() {
        return amount;
    }

    public double getBet() {
        return bet;
    }

    public List<Card> getHand() {
        return hand;
    }

    public List<Chip> getChips() {
        return chips;
    }

    public boolean isInTurn() {
        return isInTurn;
    }
}
