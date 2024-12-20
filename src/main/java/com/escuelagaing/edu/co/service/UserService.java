package com.escuelagaing.edu.co.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.escuelagaing.edu.co.repository.UserRepository;
import com.escuelagaing.edu.co.exception.UserServiceException;
import com.escuelagaing.edu.co.dto.RoomStateDTO;
import com.escuelagaing.edu.co.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Crear un nuevo usuario
    public User createUser(User user) {
        if (userRepository.existsById(user.getEmail())) {
            throw new UserServiceException.UserAlreadyExistsException(user.getEmail());
        }
        return userRepository.save(user);
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public void saveGameToUserHistory(String userId, RoomStateDTO gameState) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.addGameToHistory(gameState); 
            userRepository.save(user);
        } else {
            throw new UserServiceException.UserNotFoundException(userId);
        }
    }

      public List<Map<String, Object>> getUserGameHistory(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            return userOptional.get().getGameHistory();
        } else {
            throw new UserServiceException.UserNotFoundException(userId);
        }
    }
    

    public User updateUser(String id, User userDetails) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setNickName(userDetails.getNickName());
                    user.setAmount(userDetails.getAmount());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new UserServiceException.UserNotFoundException(id));
    }


    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new UserServiceException.UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}