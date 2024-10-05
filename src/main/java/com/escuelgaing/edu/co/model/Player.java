package com.escuelgaing.edu.co.model;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private List<Card> hand;
    private double amount;
    private double bet;
    private boolean finishTurn;
    private double balance;

    public Player(String name, double balance,boolean finishTurn) {
        this.name = name;
        this.amount = balance;
        this.hand = new ArrayList<>();
        this.finishTurn = false;
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
        int acesCount = 0; 

        for (Card card : hand) {
            score += card.getValue();
            if (card.getRank().equals("Ace")) {
                acesCount++; 
            }
        }

     
        while (score > 21 && acesCount > 0) {
            score -= 10; 
            acesCount--;
        }

        return score;
    }



     public boolean isFinishTurn() {
        return finishTurn;
    }

}
