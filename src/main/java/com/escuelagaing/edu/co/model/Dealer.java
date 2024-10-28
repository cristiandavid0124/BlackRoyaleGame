package com.escuelagaing.edu.co.model;

import java.util.ArrayList;


public class Dealer extends Player {
    public Dealer(String roomId) {
        super(null, "Dealer", roomId, Double.MAX_VALUE); 
        this.hand = new ArrayList<>(); // Inicializa la mano del dealer (esto ya se hace en Player, así que es opcional)
    }
}
