package com.escuelagaing.edu.co.controller;

import com.escuelagaing.edu.co.dto.UserDTO;
import com.escuelagaing.edu.co.model.User;
import com.escuelagaing.edu.co.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin("*")
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
        try {
            System.out.println("Recibida solicitud para crear un nuevo usuario con datos: " + userDto);

            User user = new User();
            user.setName(userDto.getName());
            user.setEmail(userDto.getEmail());
            // Mapear otros campos según sea necesario

            System.out.println("Intentando crear el usuario: Nombre=" + user.getName() + ", Email=" + user.getEmail());

            User createdUser = userService.createUser(user);

            System.out.println("Usuario creado exitosamente: " + createdUser);

            return ResponseEntity.ok(createdUser);
        } catch (RuntimeException e) {
            System.err.println("Error al crear el usuario: " + e.getMessage());
            e.printStackTrace(); // Esto imprimirá la traza completa del error en los logs
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
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
            System.out.println("Recibida solicitud para actualizar usuario con ID: " + id);
            
            // Convertir el DTO a la entidad
            User user = new User();
            user.setName(userDetails.getName());
            user.setEmail(userDetails.getEmail());
            // Mapear otros campos según sea necesario

            System.out.println("Intentando actualizar el usuario con ID: " + id + ", Nombre=" + user.getName() + ", Email=" + user.getEmail());
    
            // Llamar al servicio de actualización
            User updatedUser = userService.updateUser(id, user);
            System.out.println("Usuario actualizado exitosamente: " + updatedUser);

            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            System.err.println("Error al actualizar el usuario con ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar un usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        try {
            System.out.println("Recibida solicitud para eliminar usuario con ID: " + id);
            userService.deleteUser(id);
            System.out.println("Usuario con ID " + id + " eliminado exitosamente.");
            return ResponseEntity.noContent().build(); // Retorna 204 No Content
        } catch (RuntimeException e) {
            System.err.println("Error al eliminar el usuario con ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }
}
