package com.escuelagaing.edu.co.model;

import com.escuelagaing.edu.co.dto.RoomStateDTO;
import java.util.ArrayList;
import java.util.List;



import org.apache.el.stream.Optional;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



import java.util.concurrent.*;
@Document(collection = "Room") 

public class Room {
    @Id
    private String id;
    private List<Player> players;
    private Game game;
    private RoomStatus status;  
    private final int maxPlayers = 5;
    private final int minPlayers = 2;
    private final int MAX_BET_TIME = 60; // Tiempo máximo para la fase de apuestas en segundos
    private List<RoomObserver> observers = new ArrayList<>(); // Lista de observadores
    // Constructor
    public Room() {
        this.players = new ArrayList<>();
        this.status = RoomStatus.EN_ESPERA;  // Estado inicial con enum
    }
     // Método para obtener un jugador por ID
    public Player getPlayer(String playerId) {
        for (Player player : players) {
            if (player.getId().equals(playerId)) {
                return player;
            }
        }
        return null; 
    }





    public RoomStateDTO buildRoomState() {
        List<Player> winners = (game != null) ? game.calculateWinners() : List.of();
        List<Card> dealerHand = (game != null) ? game.getDealer().getHand() : List.of();
        return new RoomStateDTO(players, winners, dealerHand);
    }

    public String getRoomId() {  
        return id;
    }

    public void setId(String id) {
        this.id = id; 
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

  
    public boolean addPlayer(Player player) {
        if (players.size() < maxPlayers) {
            players.add(player);

            // Cambia el estado de la sala a `EN_APUESTAS` si el mínimo de jugadores está listo
            if (players.size() == minPlayers) {
                System.out.println("Iniciando proceso de apuestas. Hay suficientes jugadores en la sala.");
                status = RoomStatus.EN_APUESTAS;
                startBetting();
            }

            // Cambia el estado de la sala si alcanza el máximo de jugadores
            if (players.size() == maxPlayers) {
                System.out.println("La sala está llena.");
                status = RoomStatus.EN_ESPERA; // Indica que la sala ya tiene el máximo de jugadores
            }

            return true;
        } else {
            System.out.println("La sala ya está llena.");
            return false;
        }
    }


    public Player getPlayerById(String playerId) {
        for (Player player : players) {
            if (player.getId().equals(playerId)) {
                return player;
            }
        }
        return null; // Retorna null si no se encuentra el jugador con el ID dado
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

    public void startGame() {
        if (players.size() >= minPlayers) {
            this.game = new Game(players, id);
            this.status = RoomStatus.EN_JUEGO;
            game.startGame();
        }
    }

    public void endGame() {
        if (game != null) {
            game.endGame();
            this.status = RoomStatus.FINALIZADO;
        }
    }

    public void startBetting() {
        this.status = RoomStatus.EN_APUESTAS;
        System.out.println("Fase de apuestas iniciada.");
    
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            endBetting();
            scheduler.shutdown();
        }, MAX_BET_TIME, TimeUnit.SECONDS);
    }



    private void endBetting() {
        System.out.println("Fase de apuestas finalizada.");
        this.status = RoomStatus.EN_JUEGO;
        startGame(); // Iniciar el juego después de la fase de apuestas
    }

   
    public void resetRoom() {
        if (game != null) {
            game.resetGame();  
        }
        this.status = RoomStatus.EN_ESPERA;  // Cambiar el estado a "EN_ESPERA"
        players.clear();  // Limpiar los jugadores de la sala
    }

    // Método para saber si la sala está llena
    public boolean isFull() {
        return players.size() == maxPlayers;
    }

    public Player getPlayerByName(String playerName) {
        for (Player player : players) {
            if (player.getName().equals(playerName)) {
                return player; // Retornar el jugador si se encuentra el nombre
            }
        }
        return null; // Retornar null si no se encuentra el jugador con el nombre dado
    }
    
    public int getMinPlayers() {
        return minPlayers;
    }
}
