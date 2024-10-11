package com.escuelgaing.edu.co.model;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private List<Card> hand;
    private double amount;
    private double bet;
    private boolean finishTurn;
    private PlayerAction estado;
    private ArrayList<Chip> availableChips;


    public Player(String name, double amount,boolean finishTurn) {
        this.name = name;
        this.amount = amount;
        this.hand = new ArrayList<>();
        this.finishTurn = false;
        this.estado = null;  
        this.availableChips = new ArrayList<>(); // Inicia como una lista vacía

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
        return name;
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
