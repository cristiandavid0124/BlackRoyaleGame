package com.escuelgaing.edu.co.service;

import com.escuelgaing.edu.co.model.Card;
import com.escuelgaing.edu.co.model.Deck;
import com.escuelgaing.edu.co.model.Game;
import com.escuelgaing.edu.co.model.Player;
import com.escuelgaing.edu.co.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    private ExecutorService executorService = Executors.newFixedThreadPool(10);
    private Deck deck; // Para gestionar las cartas en el juego

    public Game startGame() {
        Game game = new Game();
        deck = new Deck(); // Inicializa el mazo al comenzar un nuevo juego
        deck.shuffle(); // Mezcla el mazo
        return gameRepository.save(game);
    }

    public Game addPlayerToGame(Long gameId, Player player) {
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game != null) {
            game.getPlayers().add(player);
            return gameRepository.save(game);
        }
        return null;
    }

    public void dealCardToPlayer(Long gameId, String playerName) {
        executorService.submit(() -> {
            Game game = gameRepository.findById(gameId).orElse(null);
            if (game != null && game.isActive()) {
                Player player = game.getPlayers().stream()
                    .filter(p -> p.getName().equals(playerName))
                    .findFirst()
                    .orElse(null);
                if (player != null) {
                    // Lógica para repartir carta
                    Card newCard = deck.drawCard(); // Método para sacar una carta del mazo
                    player.addCard(newCard);
                    gameRepository.save(game); // Guarda el estado actualizado del juego
                }
            }
        });
    }

    public Game endGame(Long gameId) {
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game != null) {
            game.endGame();
            return gameRepository.save(game);
        }
        return null;
    }

    // Agregar lógica para manejar las apuestas y determinar el ganador
    public void placeBet(Long gameId, String playerName, double amount) {
        executorService.submit(() -> {
            Game game = gameRepository.findById(gameId).orElse(null);
            if (game != null && game.isActive()) {
                Player player = game.getPlayers().stream()
                    .filter(p -> p.getName().equals(playerName))
                    .findFirst()
                    .orElse(null);
                if (player != null) {
                    if (amount <= player.getBalance()) {
                        player.setBet(amount); // Establece la apuesta
                        player.decreaseBalance(amount); // Disminuye el balance del jugador
                        gameRepository.save(game); // Guarda el estado actualizado del juego
                    }
                }
            }
        });
    }

    // Método para determinar el ganador al final del juego
    public Player determineWinner(Long gameId) {
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game != null && !game.isActive()) {
            // Lógica para determinar el ganador (ejemplo simplificado)
            Player winner = game.getPlayers().get(0); // Cambia esta lógica según tu criterio de ganador
            return winner;
        }
        return null;
    }
}
