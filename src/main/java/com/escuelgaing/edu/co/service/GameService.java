package com.escuelgaing.edu.co.service;

import com.escuelgaing.edu.co.model.Card;
import com.escuelgaing.edu.co.model.Game;
import com.escuelgaing.edu.co.model.Player;
import com.escuelgaing.edu.co.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    public Game startGame(List<Player> players) {
        Game game = new Game(players);
        game.startGame();
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

    public void dealCardToPlayer(Long gameId) {
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game != null && game.isActive()) {
            Card newCard = game.dealCard();  
            if (newCard != null) {
                gameRepository.save(game);
            }
        }
    }

    public Game endGame(Long gameId) {
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game != null) {
            game.endGame();
            return gameRepository.save(game);
        }
        return null;
    }

    public List<Player> determineWinner(Long gameId) {
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game != null && !game.isActive()) {
            return game.calculateWinners();
        }
        return null;
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
            game.nextPlayer();
            gameRepository.save(game);
        }
    }
}
