package com.escuelgaing.edu.co.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.escuelgaing.edu.co.model.User; // Asegúrate de tener un modelo de usuario
import com.escuelgaing.edu.co.repository.UserRepository; // Asegúrate de tener un repositorio para manejar usuarios

@CrossOrigin("*")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository; // Repositorio para acceder a los usuarios

    @GetMapping("")
    public Map<String, Object> getUserInfo(Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No estás autenticado");
        }

        // Devuelve información del usuario autenticado
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        Map<String, Object> userAttributes = oauthToken.getPrincipal().getAttributes();
        return userAttributes;
    }

    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        // Verifica si el usuario ya existe
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario ya existe");
        }

        // Guarda el nuevo usuario
        return userRepository.save(user);
    }
}

