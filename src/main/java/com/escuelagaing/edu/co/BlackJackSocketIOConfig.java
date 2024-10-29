package com.escuelagaing.edu.co;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.escuelagaing.edu.co.model.Player;
import com.escuelagaing.edu.co.model.Room;
import com.escuelagaing.edu.co.model.RoomStatus;
import com.escuelagaing.edu.co.model.User;
import com.escuelagaing.edu.co.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    }

    private DataListener<String> joinRoomListener() {
        return (client, roomId, ackSender) -> {
            String playerName = client.getHandshakeData().getSingleUrlParam("name");
            String playerId = client.getHandshakeData().getSingleUrlParam("id"); // Obtener el id del jugador
    
            System.out.println("Player: " + playerName + " con ID: " + playerId + " se unió a la sala " + roomId);
            client.joinRoom(roomId); // Agregar al cliente a la sala
    
            Room room = rooms.computeIfAbsent(roomId, k -> new Room()); // Crear la sala si no existe
    
            if (room.getStatus() == RoomStatus.EN_JUEGO) {
                client.sendEvent("error", "No puedes unirte. El juego ya ha comenzado.");
                return;
            } else if (room.getStatus() == RoomStatus.FINALIZADO) {
                client.sendEvent("error", "El juego ha finalizado.");
                return;
            }
    
            // Buscar el usuario por ID usando UserService
            Optional<User> userOptional = userService.getUserById(playerId);
            if (!userOptional.isPresent()) {
                client.sendEvent("error", "Usuario no encontrado con ID: " + playerId);
                return;
            }
    
            User user = userOptional.get();
    
            // Crear un nuevo jugador usando el usuario encontrado
            Player player = new Player(user, playerName, roomId, 1000);
    
            // Log para imprimir la información del jugador después de ser creado
            System.out.println("Jugador creado: ID=" + playerId + ", Nombre=" + playerName + ", RoomId=" + roomId + ", Saldo=" + player.getAmount());
    
            if (room.addPlayer(player)) {
                if (room.getPlayers().size() >= room.getMinPlayers()) {
                    room.setStatus(RoomStatus.EN_ESPERA);
                }
                rooms.put(roomId, room);
    
                // Enviar la lista de jugadores actuales en lugar de gameState si game es null
                if (room.getGame() == null) {
                    client.sendEvent("loadGameState", room.getPlayers());
    
                    // Print detallado de la lista de jugadores
                    System.out.println("Enviando lista de jugadores en lugar de gameState, ya que el juego no ha iniciado.");
                    System.out.println("Lista de jugadores en la sala " + roomId + ":");
                    for (Player p : room.getPlayers()) {
                        System.out.println("Jugador - ID: " + p.getId() + ", Nombre: " + p.getName() + ", Saldo: " + p.getAmount());
                    }
                } else {
                    client.sendEvent("loadGameState", room.getGame());
                    System.out.println("Enviando gameState completo.");
                }
                
                // Emitir el estado actualizado de la sala a todos los clientes
                server.getRoomOperations(roomId).sendEvent("updateGameState." + roomId, room.getPlayers());
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

                // Si no hay suficientes jugadores, eliminar la sala o actualizar el estado del juego
                if (room.getPlayers().isEmpty()) {
                    rooms.remove(roomId);
                    System.out.println("Eliminando la sala " + roomId + " ya que no hay jugadores conectados.");
                } else if (room.getPlayers().size() < room.getMinPlayers() && room.getStatus() == RoomStatus.EN_JUEGO) {
                    room.setStatus(RoomStatus.FINALIZADO);
                    System.out.println("Cambiando el estado de la sala " + roomId + " a FINALIZADO por falta de jugadores.");
                    server.getRoomOperations(roomId).sendEvent("updateGameState." + roomId, room.getGame());
                } else {
                    server.getRoomOperations(roomId).sendEvent("updateGameState." + roomId, room.getGame());
                }
            }
        };
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
