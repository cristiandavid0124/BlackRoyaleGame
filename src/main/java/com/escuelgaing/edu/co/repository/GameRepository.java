package com.escuelgaing.edu.co.repository;

import com.escuelgaing.edu.co.model.Room;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends MongoRepository<Room, String> {
    // Puedes agregar métodos de consulta personalizados si es necesario
}