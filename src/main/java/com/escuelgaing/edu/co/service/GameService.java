package com.escuelgaing.edu.co.service;

import com.escuelgaing.edu.co.model.Card;
import com.escuelgaing.edu.co.model.Game;
import com.escuelgaing.edu.co.model.Player;
import com.escuelgaing.edu.co.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    public Game startGame() {
        Game game = new Game();
        game.startGame();
        return gameRepository.save(game);
    }

    public Game addPlayerToGame(Long gameId, Player player) {
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game != null) {
            game.addPlayer(player);
            return gameRepository.save(game);
        }
        return null;
    }

    public void dealCardToPlayer(Long gameId) {
        executorService.submit(() -> {
            Game game = gameRepository.findById(gameId).orElse(null);
            if (game != null && game.isActive()) {
                Card newCard = game.dealCard();  
                if (newCard != null) {
                    gameRepository.save(game);
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

    public boolean placeBet(Long gameId, double amount) {
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game != null && game.isActive() && amount > 0) {
            game.placeBet(amount);
            gameRepository.save(game);
            return true;
        }
        return false;
    }

    public List<Player> determineWinner(Long gameId) {
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game != null && !game.isActive()) {
            return game.calculateWinners();
        }
        return null;
    }

    public int getCurrentRound(Long gameId) {
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game != null) {
            return game.getCurrentRound();
        }
        return -1;
    }

    public Player getCurrentPlayer(Long gameId) {
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game != null) {
            return game.getCurrentPlayer();
        }
        return null;
    }

    public void changeTurn(Long gameId) {
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game != null && game.isActive()) {
            game.changeTurn();
            gameRepository.save(game);
        }
    }
}
