package com.escuelagaing.edu.co;

import java.util.HashMap;
import java.util.Map;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.escuelagaing.edu.co.model.Player;
import com.escuelagaing.edu.co.model.Room;
import com.escuelagaing.edu.co.model.RoomStatus;
import com.escuelagaing.edu.co.service.UserService;

public class BlackJackSocketIOConfig {
    private SocketIOServer server;
    private Map<String, Room> rooms = new HashMap<>(); // Almacenar las salas
    private UserService userService;

    public BlackJackSocketIOConfig(SocketIOServer server, UserService userService) {
        this.server = server;
        this.userService = userService;  // Asignar UserService proporcionado por el contexto de Spring

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
            System.out.println("Player: " + playerName + " se unió a la sala " + roomId);
            client.joinRoom(roomId); // Agregar al cliente a la sala
    
            Room room = rooms.computeIfAbsent(roomId, k -> new Room()); // Crear la sala si no existe
    
            if (room.getStatus() == RoomStatus.EN_JUEGO) {
                client.sendEvent("error", "No puedes unirte. El juego ya ha comenzado.");
                return;
            } else if (room.getStatus() == RoomStatus.FINALIZADO) {
                client.sendEvent("error", "El juego ha finalizado.");
                return;
            }
    
            Player player = new Player(null, playerName, roomId, 1000); // Crear un nuevo jugador con un saldo inicial de 1000
    
            // Log para imprimir la información del jugador después de ser creado
            System.out.println("Jugador creado: Nombre=" + playerName + ", RoomId=" + roomId + ", Saldo=" + player.getAmount());
    
            if (room.addPlayer(player)) {
                if (room.getPlayers().size() >= room.getMinPlayers()) {
                    room.setStatus(RoomStatus.EN_ESPERA);
                }
                rooms.put(roomId, room);
    
                // Enviar el estado del juego al cliente que se unió
                client.sendEvent("loadGameState", room.getGame());
    
                // Emitir el estado actualizado del juego a todos los clientes en la sala
                server.getRoomOperations(roomId).sendEvent("updateGameState." + roomId, room.getGame());
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
