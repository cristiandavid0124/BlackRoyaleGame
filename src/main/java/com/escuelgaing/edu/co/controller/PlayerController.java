package com.escuelgaing.edu.co.controller;

import com.escuelgaing.edu.co.model.Player;
import com.escuelgaing.edu.co.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/players")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @GetMapping
    public List<Player> getAllPlayers() {
        return playerService.getAllPlayers();
    }

    @GetMapping("/{name}")
    public ResponseEntity<Player> getPlayerByName(@PathVariable String name) {
        Player player = playerService.getPlayerByName(name);
        return player != null ? ResponseEntity.ok(player) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {
        Player newPlayer = playerService.createPlayer(player);
        return ResponseEntity.ok(newPlayer);
    }

    @PutMapping("/{name}/balance")
    public ResponseEntity<Player> updateBalance(@PathVariable String name, @RequestParam double amount) {
        Player updatedPlayer = playerService.updateBalance(name, amount);
        return updatedPlayer != null ? ResponseEntity.ok(updatedPlayer) : ResponseEntity.notFound().build();
    }
}
