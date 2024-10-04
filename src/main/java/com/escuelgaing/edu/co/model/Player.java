package com.escuelgaing.edu.co.model;

import java.util.List;

public class Player {
    private String name;
    private List<Card> hand;
    private double balance;
    private double bet;

    public Player(String name, double balance) {
        this.name = name;
        this.balance = balance;
        this.hand = new ArrayList<>();
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

    public double getBalance() {
        return balance;
    }

    public void decreaseBalance(double amount) {
        this.balance -= amount;
    }

    public void increaseBalance(double amount) {
        this.balance += amount;
    }

    public double getBet() {
        return bet;
    }

    public void setBet(double bet) {
        this.bet = bet;
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
}
