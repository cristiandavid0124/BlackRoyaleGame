package com.escuelagaing.edu.co.service;


import com.escuelagaing.edu.co.model.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final Map<String, User> userMap = new HashMap<>(); // Almacena los usuarios en memoria

    // Crear un nuevo usuario
    public User createUser(User user) {
        userMap.put(user.getId(), user); // Suponiendo que el usuario tiene un método getId() que devuelve su ID
        return user;
    }

    // Obtener un usuario por ID
    public Optional<User> getUserById(String id) {
        return Optional.ofNullable(userMap.get(id)); // Retorna el usuario si existe, o un Optional vacío
    }

    // Actualizar un usuario
    public User updateUser(String id, User userDetails) {
        // Verificar si el usuario existe
        User user = userMap.get(id);
        if (user == null) {
            throw new RuntimeException("Usuario no encontrado con id: " + id);
        }
        
        // Actualizar los detalles del usuario
        user.setEmail(userDetails.getEmail());
        user.setName(userDetails.getName());
        user.setDisplayName(userDetails.getDisplayName());
        user.setGivenName(userDetails.getGivenName());
        user.setFamilyName(userDetails.getFamilyName());

        return user; // Retornar el usuario actualizado
    }

    // Eliminar un usuario
    public void deleteUser(String id) {
        userMap.remove(id); // Eliminar el usuario del mapa
    }

    // Otras operaciones personalizadas pueden ser agregadas aquí
}
