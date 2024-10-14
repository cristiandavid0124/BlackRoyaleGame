package com.escuelgaing.edu.co.service;

import com.escuelgaing.edu.co.model.User;
import com.escuelgaing.edu.co.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return userRepository.save(user);
    }

    // Obtener un usuario por ID
    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    // Actualizar un usuario
    public User updateUser(String id, User userDetails) {
        // Verificar si el usuario existe
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        
        // Actualizar los detalles del usuario
        user.setEmail(userDetails.getEmail());
        user.setName(userDetails.getName());
        user.setDisplayName(userDetails.getDisplayName());
        user.setGivenName(userDetails.getGivenName());
        user.setFamilyName(userDetails.getFamilyName());

        return userRepository.save(user);
    }

    // Eliminar un usuario
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    // Otras operaciones personalizadas pueden ser agregadas aquí
}
