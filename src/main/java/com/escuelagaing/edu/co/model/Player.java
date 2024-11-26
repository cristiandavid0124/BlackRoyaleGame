package com.escuelagaing.edu.co.model;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private User user; 
    public String name;
    private String roomId; // ID de la sala a la que pertenece el jugador
    public List<Card> hand;
    private double amount;
    private double bet;
    private boolean finishTurn;
    private PlayerAction estado;
    private ArrayList<Chip> availableChips;
    private String NickName;
    private boolean hasBet = false;
    private boolean isTurn;
    private boolean disconnected = false;

    public Player() {
        this.hand = new ArrayList<>();
        this.availableChips = new ArrayList<>();
    } 

    public void setHand(List<Card> hand) {
        this.hand = hand;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
    public boolean isDisconnected() {
        return disconnected;
    }

    public void setDisconnected(boolean disconnected) {
        this.disconnected = disconnected;
    }



    public Player(User user, String name, String roomId, double amount) {
        
        this.user = user; 
        this.name = name;
        this.roomId = roomId; 
        this.amount = amount;
        this.hand = new ArrayList<>();
        this.finishTurn = false;
        this.estado = null;  
        this.availableChips = new ArrayList<>(); 
        this.NickName = null;
        this.isTurn = false;

    }

    public boolean hasCompletedBet(){
        return hasBet;
    }
    public void setHasCompletedBet(boolean state) {
        this.hasBet = state;
    }



    public void setNickName(String name){
        this.NickName = name;

    }
    public String getNickName(){
        return this.NickName;
    }

    public String getId(){
        return user.getEmail();
    }
        
    public String getRoomId() {
        return roomId; // Getter para el roomId
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId; // Setter para el roomId
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public void setChips(List<Chip> chips) {
        availableChips.clear(); // Limpia las fichas actuales
        for (Chip chip : chips) {
            if (calculateTotalChipsValue() + chip.getValue() <= amount) {
                availableChips.add(chip); 
                setBetChip(chip);
            }
        }
    }
    
    public List<Chip> getChips() {
        return availableChips; 
    }


    private double calculateTotalChipsValue() {
        double totalValue = 0.0;
        for (Chip chip : availableChips) {
            totalValue += chip.getValue();
        }
        return totalValue;
    }



    public PlayerAction getEstado() {
        return estado;
    }


    public void setEstado(PlayerAction estado) {
        this.estado = estado;
    }

    public String getName() {
        return user.getName(); // Puedes obtener el nombre del usuario a través del objeto User
    }

    public List<Card> getHand() {
        return hand;
    }

    public void addCard(Card card) {
        hand.add(card);

    }

    public double getAmount() {
        return user.getAmount(); 
    }

    public void decreaseBalance(double amount) {
        user.decreaseAmount(amount); // Actualiza el saldo del usuario
    }

    public void increaseBalance(double amount) {
        user.increaseAmount(amount); // Actualiza el saldo del usuario
    }

    public double getBet() {
        return bet;
    }

    public void setBetChip(Chip chip) {
        double chipValue = chip.getValue();
        this.bet += chipValue;
    }


    public void setBet(double bet) {
        this.bet += bet;
    }
    public void setfinishTurn(boolean estado){
         finishTurn = estado;
    }


    public boolean placeBet(double bet) {
        if (bet <= user.getAmount()) {  
            user.decreaseAmount(bet);
            this.bet = bet; 
            return true; 
        }
        return false;
    }

    
    public void revenue(boolean isWinner) {
        if (isWinner) {
            increaseBalance(bet * 2); 
        }
        this.bet = 0; 
    }
    public int calculateScore() {
        int score = 0;
        int aceCount = 0; 
    
        for (Card card : hand) {
            score += card.getValue();
            if (card.getValue() == 11) { 
                aceCount++;
            }
        }
        while (score > 21 && aceCount > 0) {
            score -= 10; // Cambia un As de 11 a 1
            aceCount--;
        }
    
        System.out.println("Puntuación calculada: " + score + " con cartas: " + hand);
        return score;
    }


    public void resetHand() {
        hand.clear(); // Limpia las cartas
    }

    public boolean placeBet(List<String> chipColors) {
        List<Chip> chips = new ArrayList<>();
    
        for (String color : chipColors) {
            try {
                chips.add(Chip.fromColor(color));
            } catch (IllegalArgumentException e) {
                System.out.println("Color de ficha no válido: " + color);
                return false;
            }
        }
    
        double totalBetValue = chips.stream().mapToDouble(Chip::getValue).sum();
    
        if (totalBetValue > user.getAmount()) { 
            System.out.println("Saldo insuficiente para realizar esta apuesta.");
            return false;
        }
    
        user.decreaseAmount(totalBetValue); 
        this.bet += totalBetValue;
        this.availableChips.clear();
        this.availableChips.addAll(chips);
        this.hasBet = true;
        System.out.println("Apuesta realizada: " + totalBetValue + ". Saldo restante: " + user.getAmount());
        return true;
    }

    public boolean getInTurn(){
        return isTurn;
    }
    public void setInTurn(boolean state){
        isTurn = state;

    }
     public boolean isFinishTurn() {
        return finishTurn;
    }

}
