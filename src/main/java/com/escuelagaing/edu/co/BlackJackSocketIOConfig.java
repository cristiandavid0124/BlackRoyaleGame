package com.escuelagaing.edu.co;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.escuelagaing.edu.co.model.Game;
import com.escuelagaing.edu.co.model.PlayerAction;

public class BlackJackSocketIOConfig {
    private SocketIOServer server;
    private Map<String, Game> roomGames = new HashMap<>(); // Almacenar el estado del juego por sala
    private Map<String, Integer> roomConnections = new HashMap<>(); // Contar conexiones por sala

    public BlackJackSocketIOConfig(SocketIOServer server) {
        this.server = server;
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
            System.out.println("Player: " + playerName + " se unió al Room " + roomId);
            subscribeToGameEvents(roomId);

            // Aumentar el contador de conexiones para la sala
            roomConnections.put(roomId, roomConnections.getOrDefault(roomId, 0) + 1);

            // Crear un nuevo juego si no existe para esta sala
            roomGames.computeIfAbsent(roomId, k -> new Game(new ArrayList<>(), roomId));

            // Enviar el estado actual del juego al cliente que se une a la sala
            client.sendEvent("loadGameState", roomGames.get(roomId));
        };
    }

    private DataListener<String> leaveRoomListener() {
        return (client, roomId, ackSender) -> {
            System.out.println("Client left Blackjack room: " + roomId);
            String eventName = "playerAction." + roomId;
            client.leaveRoom(eventName);

            // Reducir el contador de conexiones para la sala y eliminar si no hay usuarios
            int connections = roomConnections.getOrDefault(roomId, 0) - 1;
            if (connections <= 0) {
                roomConnections.remove(roomId);
                roomGames.remove(roomId); // Eliminar el juego de la sala
                System.out.println("Eliminando juego de la sala " + roomId + " ya que no hay usuarios conectados.");
            } else {
                roomConnections.put(roomId, connections);
            }
        };
    }

    private void subscribeToGameEvents(String roomId) {
        String eventName = "playerAction." + roomId;

        // Registrar el evento para manejar las acciones de los jugadores y actualizar el estado del juego
        server.addEventListener(eventName, PlayerAction.class, (client, action, ackSender) -> {
            System.out.println("Received player action in " + roomId + ": " + action);

            // Obtener el juego de la sala y aplicar la acción del jugador
            Game game = roomGames.get(roomId);
            if (game != null) {
                game.decideAction(game.getCurrentPlayer());

                // Emitir el estado actualizado del juego a todos los clientes en esta sala
                server.getRoomOperations(roomId).sendEvent("updateGameState." + roomId, game);
            }
        });
    }

    private ConnectListener onConnected() {
        return client -> System.out.println("New connection!");
    }

    private DisconnectListener onDisconnected() {
        return client -> System.out.println("Disconnected!");
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop();
    }
}