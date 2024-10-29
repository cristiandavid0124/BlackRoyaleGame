package com.escuelagaing.edu.co;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.beans.factory.annotation.Autowired;

import com.corundumstudio.socketio.SocketIOServer;
import com.escuelagaing.edu.co.service.UserService;
import com.corundumstudio.socketio.Configuration;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.escuelagaing.edu.co.repository")
public class App {

    @Autowired
    private UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);  // Inicia el contexto de Spring Boot
    }

    // Configuración del servidor Socket.IO
    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(8081);  // Utiliza un puerto diferente para Socket.IO
        return new SocketIOServer(config);
    }

    // Comenzar el servidor Socket.IO cuando la aplicación Spring Boot arranca
    @Bean
    public CommandLineRunner commandLineRunner(SocketIOServer socketIOServer) {
        return args -> {
            BlackJackSocketIOConfig module = new BlackJackSocketIOConfig(socketIOServer, userService);
            module.start();
        };
    }
}

