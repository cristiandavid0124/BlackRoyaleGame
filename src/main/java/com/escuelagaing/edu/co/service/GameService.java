package com.escuelagaing.edu.co.service;

import com.escuelagaing.edu.co.model.Game;
import com.escuelagaing.edu.co.model.Player;
import com.escuelagaing.edu.co.model.Room;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class GameService {
    private final Map<String, Room> rooms = new HashMap<>(); 


    public Room createRoom() {
        Room room = new Room(); 
        rooms.put(room.getRoomId(), room); 
        return room;
    }


    public Room getRoom(String roomId) {
        return rooms.get(roomId); 
    }


    public Player getPlayerById(String playerId) {
        for (Room room : rooms.values()) {
            Player player = room.getPlayerById(playerId); 
            if (player != null) {
                return player;
            }
        }
        return null; 
    }

   
    public void addPlayerToRoom(String roomId, Player player) {
        Room room = getRoom(roomId);
        if (room != null) {
            room.addPlayer(player);
        }else{
            Room newRoom = new Room(); 
            newRoom.addPlayer(player);
        }


    }

    // Iniciar un juego en la sala
    public void startGame(Room room) {
        Game game = new Game(room.getPlayers(), room.getRoomId());
        room.setGame(game); // Asumiendo que Room tiene un método para establecer el juego
        game.startGame(); // Iniciar el juego
    }

    // Procesar la acción de un jugador
    public void processAction(Player player, String action) {
        Room room = getRoom(player.getRoomId());
        if (room != null && room.getGame() != null) {
            room.getGame().decideAction(player); // Pasar acción al método decideAction
            // No es necesario guardar, ya que los cambios se reflejan en el mapa
        }
    }

    // Colocar una apuesta
    public void placeBet(Player player, Double betAmount) {
        Room room = getRoom(player.getRoomId());
        if (room != null && room.getGame() != null) {
            if (player.placeBet(betAmount)) { 
            }
        }
    }

    // Finalizar el juego en la sala
    public void endGame(Room room) {
        if (room.getGame() != null) {
            room.getGame().endGame(); // Finalizar el juego
            // No es necesario guardar, ya que los cambios se reflejan en el mapa
        }
    }
}
