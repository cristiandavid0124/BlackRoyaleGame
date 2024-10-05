package com.escuelgaing.edu.co.controller;

import com.escuelgaing.edu.co.model.Game;
import com.escuelgaing.edu.co.model.Player;
import com.escuelgaing.edu.co.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game")
public class GameController {

    @Autowired
    private GameService gameService;

    @PostMapping("/start")
    public Game startGame() {
        return gameService.startGame();
    }

    @PostMapping("/{gameId}/addPlayer")
    public Game addPlayerToGame(@PathVariable Long gameId, @RequestBody Player player) {
        return gameService.addPlayerToGame(gameId, player);
    }

    @PostMapping("/{gameId}/deal")
    public void dealCardToPlayer(@PathVariable Long gameId) {
        gameService.dealCardToPlayer(gameId);
    }

    @PostMapping("/{gameId}/bet")
    public boolean placeBet(@PathVariable Long gameId, @RequestParam double amount) {
        return gameService.placeBet(gameId, amount);
    }

    @PostMapping("/{gameId}/changeTurn")
    public void changeTurn(@PathVariable Long gameId) {
        gameService.changeTurn(gameId);
    }

    @GetMapping("/{gameId}/winners")
    public List<Player> determineWinner(@PathVariable Long gameId) {
        return gameService.determineWinner(gameId);
    }

    @GetMapping("/{gameId}/round")
    public int getCurrentRound(@PathVariable Long gameId) {
        return gameService.getCurrentRound(gameId);
    }

    @GetMapping("/{gameId}/currentPlayer")
    public Player getCurrentPlayer(@PathVariable Long gameId) {
        return gameService.getCurrentPlayer(gameId);
    }

    @PostMapping("/{gameId}/end")
    public Game endGame(@PathVariable Long gameId) {
        return gameService.endGame(gameId);
    }
}
