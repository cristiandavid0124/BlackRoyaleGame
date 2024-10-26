package com.escuelagaing.edu.co;

import com.escuelagaing.edu.co.controller.UserController;
import com.escuelagaing.edu.co.model.User;
import com.escuelagaing.edu.co.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser_Success() {
        User user = new User("user@example.com", "Juan Pérez", "Juanito");
        when(userService.createUser(any(User.class))).thenReturn(user);

        ResponseEntity<User> response = userController.createUser(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void testCreateUser_Failure() {
        User user = new User("user@example.com", "Juan Pérez", "Juanito");
        when(userService.createUser(any(User.class))).thenThrow(new RuntimeException("User already exists"));

        ResponseEntity<User> response = userController.createUser(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void testGetUserById_Success() {
        User user = new User("user@example.com", "Juan Pérez", "Juanito");
        when(userService.getUserById("user@example.com")).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.getUserById("user@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(userService, times(1)).getUserById("user@example.com");
    }

    @Test
    void testGetUserById_NotFound() {
        when(userService.getUserById("user@example.com")).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.getUserById("user@example.com");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userService, times(1)).getUserById("user@example.com");
    }

    @Test
    void testUpdateUser_Success() {
        User user = new User("user@example.com", "Juan Pérez", "Juanito");
        when(userService.updateUser(eq("user@example.com"), any(User.class))).thenReturn(user);

        ResponseEntity<User> response = userController.updateUser("user@example.com", user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(userService, times(1)).updateUser(eq("user@example.com"), any(User.class));
    }

    @Test
    void testUpdateUser_NotFound() {
        User user = new User("user@example.com", "Juan Pérez", "Juanito");
        when(userService.updateUser(eq("user@example.com"), any(User.class))).thenThrow(new RuntimeException("User not found"));

        ResponseEntity<User> response = userController.updateUser("user@example.com", user);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userService, times(1)).updateUser(eq("user@example.com"), any(User.class));
    }

    @Test
    void testDeleteUser_Success() {
        doNothing().when(userService).deleteUser("user@example.com");

        ResponseEntity<Void> response = userController.deleteUser("user@example.com");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).deleteUser("user@example.com");
    }

    @Test
    void testDeleteUser_NotFound() {
        doThrow(new RuntimeException("User not found")).when(userService).deleteUser("user@example.com");

        ResponseEntity<Void> response = userController.deleteUser("user@example.com");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userService, times(1)).deleteUser("user@example.com");
    }
}

