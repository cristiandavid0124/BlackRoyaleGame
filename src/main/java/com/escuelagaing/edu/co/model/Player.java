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


    public Player(User user,String roomId, double amount,boolean finishTurn) {
        
        this.user = user; 
        this.name = name;
        this.roomId = roomId; 
        this.amount = amount;
        this.hand = new ArrayList<>();
        this.finishTurn = false;
        this.estado = null;  
        this.availableChips = new ArrayList<>(); // Inicia como una lista vacía

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


    public void setChips(ArrayList<Chip> chips) {
        availableChips.clear(); // Limpia las fichas actuales
        for (Chip chip : chips) {
            if (calculateTotalChipsValue() + chip.getValue() <= amount) {
                availableChips.add(chip); 
                setBetChip(chip);
            }
        }
    }

    public ArrayList<Chip> getChips() {
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
        this.bet = bet;
    }
    public void setfinishTurn(boolean estado){
         finishTurn = estado;
    }


    public boolean placeBet(double bet) {
        if (bet <= this.amount) {  
            setBet(bet);
            decreaseBalance(bet);
            return true; 
        }
        return false;
    }

    

    public void  revenue() {
       
           amount += bet*2;
           
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

   


     public boolean isFinishTurn() {
        return finishTurn;
    }

}
