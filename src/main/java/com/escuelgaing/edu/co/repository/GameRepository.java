package com.escuelgaing.edu.co.repository;

import com.escuelgaing.edu.co.model.Game;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends CrudRepository<Game, Long> {
    Game findByIsActiveTrue();
}
