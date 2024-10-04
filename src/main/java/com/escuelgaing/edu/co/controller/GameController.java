package com.escuelgaing.edu.co.controller;

import com.escuelgaing.edu.co.model.Game;
import com.escuelgaing.edu.co.model.Player;
import com.escuelgaing.edu.co.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameService gameService;

    @PostMapping("/start")
    public ResponseEntity<Game> startGame() {
        Game newGame = gameService.startGame();
        return ResponseEntity.ok(newGame);
    }

    @PostMapping("/{gameId}/addPlayer")
    public ResponseEntity<Game> addPlayerToGame(@PathVariable Long gameId, @RequestBody Player player) {
        Game updatedGame = gameService.addPlayerToGame(gameId, player);
        return updatedGame != null ? ResponseEntity.ok(updatedGame) : ResponseEntity.notFound().build();
    }

    @PostMapping("/{gameId}/dealCard")
    public ResponseEntity<Void> dealCardToPlayer(@PathVariable Long gameId, @RequestParam String playerName) {
        gameService.dealCardToPlayer(gameId, playerName);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{gameId}/end")
    public ResponseEntity<Game> endGame(@PathVariable Long gameId) {
        Game endedGame = gameService.endGame(gameId);
        return ResponseEntity.ok(endedGame);
    }
}
