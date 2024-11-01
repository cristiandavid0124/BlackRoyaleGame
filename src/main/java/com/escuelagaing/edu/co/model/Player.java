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


    public Player(User user, String name, String roomId, double amount) {
        
        this.user = user; 
        this.name = name;
        this.roomId = roomId; 
        this.amount = amount;
        this.hand = new ArrayList<>();
        this.finishTurn = false;
        this.estado = null;  
        this.availableChips = new ArrayList<>(); // Inicia como una lista vacía
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
        return amount;
    }

    public void decreaseBalance(double amount) {
        this.amount -= amount;
    }

    public void increaseBalance(double amount) {
        this.amount += amount;
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
        if (bet <= this.amount) {  
            this.amount -= bet; // Descontar la apuesta del saldo
            setBet(bet);
            return true; 
        }
        return false;
    }
    
    public void revenue(boolean isWinner) {
        if (isWinner) {
            this.amount += bet * 2; // Recupera la apuesta y gana el equivalente
        }
        this.bet = 0; // Reinicia la apuesta
    }
    public int calculateScore() {
        int score = 0;
        int aceCount = 0; // Contar los Ases
    
        for (Card card : hand) {
            score += card.getValue();
            if (card.getValue() == 11) { // Si es un As
                aceCount++;
            }
        }
    
        // Ajustar los Ases para no pasarse de 21
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

        // Convertir cada color en la lista de strings a un objeto Chip
        for (String color : chipColors) {
            try {
                chips.add(Chip.fromColor(color));
            } catch (IllegalArgumentException e) {
                System.out.println("Color de ficha no válido: " + color);
                return false;
            }
        }

        double totalBetValue = chips.stream().mapToDouble(Chip::getValue).sum();

        if (totalBetValue > amount) {
            System.out.println("Saldo insuficiente para realizar esta apuesta.");
            return false;
        }

        this.amount -= totalBetValue;
        this.bet += totalBetValue;
        this.availableChips.clear();
        this.availableChips.addAll(chips);
        this.hasBet = true;
        System.out.println("Apuesta realizada: " + totalBetValue + ". Saldo restante: " + this.amount);
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
