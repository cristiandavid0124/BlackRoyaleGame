package com.escuelgaing.edu.co.service;

import com.escuelgaing.edu.co.model.Game;
import com.escuelgaing.edu.co.model.Player;
import com.escuelgaing.edu.co.model.Room;
import com.escuelgaing.edu.co.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GameService {
    @Autowired
    private GameRepository gameRepository;

    // Crear una nueva sala de juego
    public Room createRoom() {
        Room room = new Room(); // Crear una nueva sala sin ID
        return gameRepository.save(room); // Guardar la sala en la base de datos
    }

    // Obtener una sala de juego por ID
    public Room getRoom(String roomId) {
        return gameRepository.findById(roomId).orElse(null); // Retorna null si no se encuentra
    }

    // Agregar un jugador a la sala
    public void addPlayerToRoom(String roomId, Player player) {
        Room room = getRoom(roomId);
        if (room != null) {
            room.addPlayer(player);
            gameRepository.save(room); // Guardar los cambios en la sala
        }
    }

    // Iniciar un juego en la sala
    public void startGame(Room room) {
        Game game = new Game(room.getPlayers(),room.getRoomId());
        room.setGame(game); // Asumiendo que Room tiene un método para establecer el juego
        gameRepository.save(room); // Guardar la sala con el juego asociado
        game.startGame(); // Iniciar el juego
    }

    // Procesar la acción de un jugador
    public void processAction(Player player, String action) {
        Room room = getRoom(player.getRoomId());
        if (room != null && room.getGame() != null) {
            room.getGame().decideAction(player); // Pasar acción al método decideAction
            gameRepository.save(room); // Guardar el estado actualizado de la sala
        }
    }

    // Colocar una apuesta
    public void placeBet(Player player, Double betAmount) {
        Room room = getRoom(player.getRoomId());
        if (room != null && room.getGame() != null) {
            if (player.placeBet(betAmount)) { // Asegúrate de que placeBet retorne booleano
                gameRepository.save(room); // Guardar el estado actualizado de la sala
            }
        }
    }

    // Finalizar el juego en la sala
    public void endGame(Room room) {
        if (room.getGame() != null) {
            room.getGame().endGame(); // Finalizar el juego
            gameRepository.save(room); // Guardar el estado final de la sala
        }
    }
}
