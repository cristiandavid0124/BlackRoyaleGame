package com.escuelgaing.edu.co.config; // Asegúrate de que el paquete sea el correcto

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@EnableScheduling
public class BlackjackRoyaleWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // Habilitar el broker de mensajes en /topic
        config.setApplicationDestinationPrefixes("/app"); // Establecer el prefijo para los destinos de aplicación
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/blackjack-endpoint") // Cambia el nombre del endpoint a algo relacionado con Blackjack
                .setAllowedOrigins("http://localhost:3000") // Solo permitir conexiones desde localhost
                .withSockJS(); // Habilitar SockJS para fallback
    }
}
