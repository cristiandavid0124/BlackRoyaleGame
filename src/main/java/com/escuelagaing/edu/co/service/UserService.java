package com.escuelagaing.edu.co.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.escuelagaing.edu.co.repository.UserRepository;
import com.escuelagaing.edu.co.model.User;

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
        // Verificar si el usuario ya existe
        if (userRepository.existsById(user.getEmail())) {
            throw new RuntimeException("El usuario con email " + user.getEmail() + " ya existe.");
        }
        return userRepository.save(user);
    }

    // Obtener un usuario por ID
    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    // Actualizar un usuario
    public User updateUser(String id, User userDetails) {
        // Verificar si el usuario existe
        return userRepository.findById(id)
                .map(user -> {
                    user.setNickName(userDetails.getNickName());
                    user.setName(userDetails.getName());
                    // Actualizar otros campos según sea necesario
                    return userRepository.save(user); // Guardar los cambios en la base de datos
                })
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
    }

    // Eliminar un usuario
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con id: " + id);
        }
        userRepository.deleteById(id);
    }
}
