package com.escuelgaing.edu.co.model;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private List<Player> players;
    private Game game;
    private RoomStatus status;  // Usamos el enum RoomStatus
    private final int maxPlayers = 5;
    private final int minPlayers = 3;

    public Room() {
        this.players = new ArrayList<>();
        this.status = RoomStatus.EN_ESPERA;  // Estado inicial con enum
    }

    // Método para agregar un jugador a la sala
    public boolean addPlayer(Player player) {
        if (players.size() < maxPlayers) {
            players.add(player);
            if (players.size() >= minPlayers) {
                startGame(); // Comprobar y empezar el juego si hay suficientes jugadores
            }
            return true;
        } else {
            System.out.println("La sala ya está llena.");
            return false;
        }
    }

    // Método para eliminar un jugador de la sala
    public boolean removePlayer(Player player) {
        if (players.contains(player)) {
            players.remove(player);
            // Si se elimina un jugador y ya no hay suficientes, finalizar el juego si está en progreso
            if (players.size() < minPlayers && status == RoomStatus.EN_JUEGO) {
                endGame();
            }
            return true;
        } else {
            System.out.println("El jugador no está en la sala.");
            return false;
        }
    }

    // Método para iniciar el juego
    public void startGame() {
        if (players.size() >= minPlayers && players.size() <= maxPlayers) {
            this.game = new Game(players);  // Crear una nueva instancia de Game con los jugadores de la sala
            this.status = RoomStatus.EN_JUEGO;  // Cambiar el estado de la sala a "EN_JUEGO"
            game.startGame();  // Iniciar el juego
        } else {
            System.out.println("No hay suficientes jugadores para iniciar el juego.");
        }
    }

    // Método para finalizar el juego
    public void endGame() {
        if (game != null) {
            game.endGame();  // Finalizar el juego
            this.status = RoomStatus.FINALIZADO;  // Cambiar el estado de la sala a "FINALIZADO"
        } else {
            System.out.println("El juego no ha comenzado.");
        }
    }

    // Método para reiniciar la sala
    public void resetRoom() {
        if (game != null) {
            game.resetGame();  
        }
        this.status = RoomStatus.EN_ESPERA;  // Cambiar el estado a "EN_ESPERA"
        players.clear();  // Limpiar los jugadores de la sala
    }

    // Obtener el estado de la sala
    public RoomStatus getStatus() {
        return status;
    }

    // Obtener la lista de jugadores de la sala
    public List<Player> getPlayers() {
        return players;
    }

    // Método para saber si la sala está llena
    public boolean isFull() {
        return players.size() == maxPlayers;
    }
}
