package com.escuelagaing.edu.co;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.escuelagaing.edu.co.dto.BetPayload;
import com.escuelagaing.edu.co.dto.RoomStateDTO;
import com.escuelagaing.edu.co.model.Player;
import com.escuelagaing.edu.co.model.Room;
import com.escuelagaing.edu.co.model.RoomStatus;
import com.escuelagaing.edu.co.model.User;
import com.escuelagaing.edu.co.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class BlackJackSocketIOConfig {
    private final SocketIOServer server;
    private final UserService userService;
    private final Map<String, Room> rooms = new HashMap<>(); // Almacenar las salas

    @Autowired
    public BlackJackSocketIOConfig(SocketIOServer server, UserService userService) {
        this.server = server;
        this.userService = userService;

        // Configuración de eventos del servidor
        server.addConnectListener(onConnected());
        server.addDisconnectListener(onDisconnected());

        // Evento para unirse a una sala específica
        server.addEventListener("joinRoom", String.class, joinRoomListener());
        // Evento para salir de una sala específica
        server.addEventListener("leaveRoom", String.class, leaveRoomListener());
        // Evento de apuesta
        server.addEventListener("playerBet", BetPayload.class, playerBetListener());

    }

    private DataListener<String> joinRoomListener() {
        return (client, roomId, ackSender) -> {
            String playerName = client.getHandshakeData().getSingleUrlParam("name");
            String playerId = client.getHandshakeData().getSingleUrlParam("id");
    
            System.out.println("Player: " + playerName + " con ID: " + playerId + " se unió a la sala " + roomId);
            client.joinRoom(roomId);
    
            Room room = rooms.computeIfAbsent(roomId, k -> new Room());
    
            if (room.getStatus() == RoomStatus.EN_JUEGO) {
                client.sendEvent("error", "No puedes unirte. El juego ya ha comenzado.");
                return;
            } else if (room.getStatus() == RoomStatus.FINALIZADO) {
                client.sendEvent("error", "El juego ha finalizado.");
                return;
            }
    
            Optional<User> userOptional = userService.getUserById(playerId);
            if (!userOptional.isPresent()) {
                client.sendEvent("error", "Usuario no encontrado con ID: " + playerId);
                return;
            }
    
            User user = userOptional.get();
            String playerNickName = user.getNickName();
            Player player = new Player(user, playerNickName, roomId, 1000);
    
            System.out.println("Jugador creado: ID=" + playerId + ", Nombre=" + playerName + ", RoomId=" + roomId + ", Saldo=" + player.getAmount());
    
            if (room.addPlayer(player)) {
                rooms.put(roomId, room);
                sendRoomUpdate(roomId); // Asegúrate de que esta línea se ejecuta
                System.out.println("Enviando actualización de la sala después de que el jugador se una");
            } else {
                client.sendEvent("error", "La sala ya está llena o no se puede unir.");
            }
        };
    }
    
    private DataListener<String> leaveRoomListener() {
        return (client, roomId, ackSender) -> {
            System.out.println("Client left Blackjack room: " + roomId);
            client.leaveRoom(roomId); // Eliminar al cliente de la sala

            Room room = rooms.get(roomId);
            if (room != null) {
                Player player = room.getPlayerByName(client.getHandshakeData().getSingleUrlParam("name"));
                if (player != null) {
                    room.removePlayer(player); // Eliminar el jugador de la sala
                }

                
                if (room.getPlayers().isEmpty()) {
                    rooms.remove(roomId);
                    System.out.println("Eliminando la sala " + roomId + " ya que no hay jugadores conectados.");
                } else if (room.getPlayers().size() < room.getMinPlayers() && room.getStatus() == RoomStatus.EN_JUEGO) {
                    room.setStatus(RoomStatus.FINALIZADO);
                }
                sendRoomUpdate(roomId); // Enviar la actualización del estado de la sala
            }
        };
    }

    private DataListener<BetPayload> playerBetListener() {
        return (client, data, ackSender) -> {
            // Extrae `roomId` y `fichas` del objeto `BetPayload`
            String roomId = data.getRoomId();
            List<String> chipColors = data.getFichas();
    
            System.out.println("Evento playerBet recibido con las fichas: " + chipColors);
            System.out.println("roomId recibido: " + roomId);
    
            String playerId = client.getHandshakeData().getSingleUrlParam("id");
    
            Room room = rooms.get(roomId);
            if (room != null && room.getStatus() == RoomStatus.EN_APUESTAS) {
                Player player = room.getPlayerById(playerId);
                if (player != null) {
                    if (player.placeBet(chipColors)) {
                        System.out.println("Apuesta realizada por " + player.getName() + ": " + player.getBet());
                        System.out.println("Saldo después de apuesta: " + player.getAmount());
                        sendRoomUpdate(roomId);
                        
                       
                    } else {
                        client.sendEvent("betError", "Saldo insuficiente o color de ficha no válido para esta apuesta.");
                    }
                } else {
                    client.sendEvent("betError", "Jugador no encontrado en la sala.");
                }
            } else {
                client.sendEvent("betError", "No se puede realizar la apuesta en este momento.");
            }
        };
    }
    public void sendRoomUpdate(String roomId) {
        Room room = rooms.get(roomId);
        if (room != null) {
            RoomStateDTO roomState = room.buildRoomState(); // Construir el estado de la sala
            
            // Convertir roomState a JSON y hacer un print
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonString = objectMapper.writeValueAsString(roomState);
                System.out.println("JSON enviado a roomUpdate: " + jsonString);
            } catch (Exception e) {
                System.err.println("Error al convertir RoomStateDTO a JSON: " + e.getMessage());
            }
    
            // Enviar la actualización de la sala a través del socket
            server.getRoomOperations(roomId).sendEvent("roomUpdate", roomState);
        }
    }

    private ConnectListener onConnected() {
        return client -> System.out.println("New connection!");
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            System.out.println("Disconnected!");
            // Aquí podrías añadir lógica adicional si es necesario, como actualizar roomConnections
        };
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop();
    }
}
