package com.escuelagaing.edu.co;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Transport;
import com.corundumstudio.socketio.Configuration;
@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.escuelagaing.edu.co.repository")
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(9092);  
        config.setTransports(Transport.WEBSOCKET); // Forzar WebSocket

        return new SocketIOServer(config);
    }
    @Bean
    public CommandLineRunner commandLineRunner(SocketIOServer socketIOServer, BlackJackSocketIOConfig blackJackSocketIOConfig) {
        return args -> {
            blackJackSocketIOConfig.start();
        };
    }
}
