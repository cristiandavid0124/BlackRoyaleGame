package com.escuelgaing.edu.co.repository;

import com.escuelgaing.edu.co.model.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends CrudRepository<Player, Long> {
    Player findByName(String name);
}
