package com.escuelagaing.edu.co;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.escuelagaing.edu.co.repository")
public class App {
    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(8080);

        SocketIOServer server = new SocketIOServer(config);
        BlackJackSocketIOConfig module = new BlackJackSocketIOConfig(server);
        
        module.start();
    }
}
