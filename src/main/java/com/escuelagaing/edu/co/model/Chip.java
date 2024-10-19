package com.escuelagaing.edu.co.model;

public enum Chip {
    AMARILLO(1.0),
    AZUL(5.0),
    ROJO(10.0),
    VERDE(25.0),
    NEGRO(100.0);

    private final double value;

    Chip(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public static Chip fromColor(String color) {
        switch (color.toUpperCase()) {
            case "AMARILLO":
                return AMARILLO;
            case "AZUL":
                return AZUL;
            case "ROJO":
                return ROJO;
            case "VERDE":
                return VERDE;
            case "NEGRO":
                return NEGRO;
            default:
                throw new IllegalArgumentException("Color de ficha no reconocido: " + color);
        }
    }
}