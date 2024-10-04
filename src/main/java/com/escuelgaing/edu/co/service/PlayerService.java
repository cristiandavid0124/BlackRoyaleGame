package com.escuelgaing.edu.co.service;

import com.escuelgaing.edu.co.model.Player;
import com.escuelgaing.edu.co.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    public List<Player> getAllPlayers() {
        return (List<Player>) playerRepository.findAll();
    }

    public Player getPlayerByName(String name) {
        return playerRepository.findByName(name);
    }

    public Player createPlayer(Player player) {
        return playerRepository.save(player);
    }

    public Player updateBalance(String name, double amount) {
        Player player = playerRepository.findByName(name);
        if (player != null) {
            player.increaseBalance(amount); // O decreaseBalance(amount) según sea necesario
            return playerRepository.save(player);
        }
        return null;
    }
}
