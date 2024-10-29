package com.escuelagaing.edu.co.repository;

import com.escuelagaing.edu.co.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
}
