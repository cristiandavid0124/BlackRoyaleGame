package com.escuelagaing.edu.co.dto;

import com.escuelagaing.edu.co.model.Card;
import com.escuelagaing.edu.co.model.Player;
import com.escuelagaing.edu.co.model.Chip;
import com.escuelagaing.edu.co.model.Dealer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RoomStateDTO {

    private List<PlayerStateDTO> players;
    private List<String> winners;
    private List<Card> dealerHand;

   public RoomStateDTO(List<Player> players, List<Player> winners, List<Card> dealerHand) {
    this.players = (players != null) ? players.stream().map(PlayerStateDTO::new).collect(Collectors.toList()) : List.of();
    this.winners = (winners != null) 
        ? winners.stream().map(player -> {
            if (player instanceof Dealer) {
                return "Dealer"; // Usa "Dealer" si el ganador es el dealer
            } else {
                return player.getUser().getNickName();
            }
        }).collect(Collectors.toList())
        : List.of();
    this.dealerHand = (dealerHand != null) ? dealerHand : List.of();
}


    public List<PlayerStateDTO> getPlayers() {
        return players;
    }

    public List<String> getWinners() {
        return winners;
    }

    public List<Card> getDealerHand() {
        return dealerHand;
    }
}
