package com.escuelagaing.edu.co.controller;

import com.escuelagaing.edu.co.dto.UserDTO;
import com.escuelagaing.edu.co.model.User;
import com.escuelagaing.edu.co.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Crear un nuevo usuario
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserDTO userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        // Map other fields as needed

        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    // Obtener un usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Actualizar un usuario
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody UserDTO userDetails) {
        try {
            // Convertir el DTO a la entidad
            User user = new User();
            user.setName(userDetails.getName());
            user.setEmail(userDetails.getEmail());
            // Mapear otros campos según sea necesario
    
            // Llamar al servicio de actualización
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    

    // Eliminar un usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content
    }
}
