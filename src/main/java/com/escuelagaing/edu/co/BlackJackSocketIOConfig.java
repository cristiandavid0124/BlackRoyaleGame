package com.escuelagaing.edu.co;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.escuelagaing.edu.co.dto.BetPayload;
import com.escuelagaing.edu.co.dto.PlayerActionPayload;
import com.escuelagaing.edu.co.dto.RestartGamePayload;
import com.escuelagaing.edu.co.dto.RoomStateDTO;
import com.escuelagaing.edu.co.model.ChatMessage;
import com.escuelagaing.edu.co.model.Game;
import com.escuelagaing.edu.co.model.Player;
import com.escuelagaing.edu.co.model.Room;
import com.escuelagaing.edu.co.model.RoomStatus;
import com.escuelagaing.edu.co.model.User;
import com.escuelagaing.edu.co.service.RoomService;
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
    private final Map<String, Room> rooms = new HashMap<>(); 

    private final Map<String, String> socketToPlayerId = new HashMap<>();
private final Map<String, String> socketToRoomId = new HashMap<>();




    @Autowired
    public BlackJackSocketIOConfig(SocketIOServer server, UserService userService) {
        this.server = server;
        this.userService = userService;
       


        server.addConnectListener(onConnected());
        server.addDisconnectListener(onDisconnected());
        server.addEventListener("joinRoom", String.class, joinRoomListener());
        server.addEventListener("leaveRoom", String.class, leaveRoomListener());
        server.addEventListener("playerBet", BetPayload.class, playerBetListener());
        server.addEventListener("playerAction", PlayerActionPayload.class, playerActionListener());
        server.addEventListener("restartGame", RestartGamePayload.class, restartGameListener());
        server.addEventListener("sendMessage", ChatMessage.class, chatMessageListener());


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
            Player player = new Player(user, playerNickName, roomId, user.getAmount());
    
            System.out.println("Jugador creado: ID=" + playerId + ", Nombre=" + playerName + ", RoomId=" + roomId + ", Saldo=" + player.getAmount());
    
            if (room.addPlayer(player)) {
                rooms.put(roomId, room);
                sendRoomUpdate(roomId); 
                System.out.println("Enviando actualización de la sala después de que el jugador se una");
            } else {
                client.sendEvent("error", "La sala ya está llena o no se puede unir.");
            }
        };
    }
    private DataListener<String> leaveRoomListener() {
        return (client, roomId, ackSender) -> {
            System.out.println("Client left Blackjack room: " + roomId);
            client.leaveRoom(roomId); 

            Room room = rooms.get(roomId);
            if (room != null) {
                Player player = room.getPlayerByName(client.getHandshakeData().getSingleUrlParam("name"));
                if (player != null) {
                    room.removePlayer(player); 
                }

                
                if (room.getPlayers().isEmpty()) {
                    rooms.remove(roomId);
                    System.out.println("Eliminando la sala " + roomId + " ya que no hay jugadores conectados.");
                } else if (room.getPlayers().size() < room.getMinPlayers() && room.getStatus() == RoomStatus.EN_JUEGO) {
                    room.setStatus(RoomStatus.FINALIZADO);
                }
                sendRoomUpdate(roomId); 
            }
        };
    }
    private DataListener<BetPayload> playerBetListener() {
        return (client, data, ackSender) -> {
            String roomId = data.getRoomId();
            List<String> chipColors = data.getFichas();
        
            System.out.println("Evento playerBet recibido con las fichas: " + chipColors);
            System.out.println("roomId recibido: " + roomId);
        
            String playerId = client.getHandshakeData().getSingleUrlParam("id");
        
            Room room = rooms.get(roomId);
            if (room != null && room.getStatus() == RoomStatus.EN_APUESTAS) {
                Player player = room.getPlayerById(playerId);
                System.out.println("revisando player id " + player.getId());
                if (player != null) {
                    if (player.placeBet(chipColors)) {
                        player.setHasCompletedBet(true);
                        System.out.println("Apuesta realizada por " + player.getName() + ": " + player.getBet());
                        System.out.println("Saldo después de apuesta: " + player.getAmount());
                        sendRoomUpdate(roomId);
                        boolean allPlayersCompletedBet = room.getPlayers().stream().allMatch(Player::hasCompletedBet);
                        if (allPlayersCompletedBet) {
                            room.endBetting();
                            sendRoomUpdate(roomId); 
                        }
                        
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




    private DataListener<RestartGamePayload> restartGameListener() {
        return (client, data, ackSender) -> {
            String roomId = data.getRoomId();
            Room room = rooms.get(roomId);
            if (room != null) {
                room.resetRoom();
                sendRoomUpdate(roomId); 
            } else {
                client.sendEvent("restartError", "Sala no encontrada o inválida.");
            }
        };
    }


    

  

    private void saveGameStateForPlayers(RoomStateDTO gameState, List<Player> players) {
        if (gameState != null) {
            for (Player player : players) {
                String userId = player.getUser().getEmail();
                userService.saveGameToUserHistory(userId, gameState);
                userService.updateUser(userId, player.getUser());
            }
        }
    }
    
    public void sendRoomUpdate(String roomId) {
        Room room = rooms.get(roomId);
        if (room != null) {
            RoomStateDTO roomState = room.buildRoomState(); 
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonString = objectMapper.writeValueAsString(roomState);
                System.out.println("JSON enviado a roomUpdate: " + jsonString);
            } catch (Exception e) {
                System.err.println("Error al convertir RoomStateDTO a JSON: " + e.getMessage());
            }
            server.getRoomOperations(roomId).sendEvent("roomUpdate", roomState);
        }
    }

    private DataListener<PlayerActionPayload> playerActionListener() {
        return (client, data, ackSender) -> {
            String roomId = data.getRoomId();
            String actionType = data.getType();
            String playerId = client.getHandshakeData().getSingleUrlParam("id");

            System.out.println("Acción del jugador recibida: " + actionType + " en la sala " + roomId);

            Room room = rooms.get(roomId);
            if (room != null && room.getStatus() == RoomStatus.EN_JUEGO) {
                System.out.println("esta en juego y encontro la sala");
                Player player = room.getPlayerById(playerId);  
                System.out.println("encontro el jugador");
                if (player != null && player.getInTurn()) {
                    System.out.println("sigue en turno");
                    Game game = room.getGame();
                    game.startPlayerTurn(player, actionType);
                    if (!room.getGame().isActive()) {
                        RoomStateDTO gameState = room.buildRoomState();
                        saveGameStateForPlayers(gameState, room.getPlayers());
                    }
                    sendRoomUpdate(roomId); 
                } else {
                    client.sendEvent("actionError", "No es el turno del jugador o el jugador no existe en la sala.");
                }
            } else {
                client.sendEvent("actionError", "La sala no está en el estado de juego.");
            }
        };
    }

    private ConnectListener onConnected() {
        return client -> {
            String playerId = client.getHandshakeData().getSingleUrlParam("id");
            String roomId = client.getHandshakeData().getSingleUrlParam("roomId");

            System.out.println("New connection! Player ID: " + playerId + ", Room ID: " + roomId);

            socketToPlayerId.put(client.getSessionId().toString(), playerId);
            socketToRoomId.put(client.getSessionId().toString(), roomId);
        };
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            String sessionId = client.getSessionId().toString();
            String playerId = socketToPlayerId.get(sessionId);
            String roomId = socketToRoomId.get(sessionId);

            if (playerId == null || roomId == null) {
                System.out.println("[ERROR] No se encontró información para el session ID: " + sessionId);
                return;
            }

            System.out.println("Player disconnected. Player ID: " + playerId + ", Room ID: " + roomId);

            Room room = rooms.get(roomId);
            if (room != null) {
                Player player = room.getPlayerById(playerId);
            
                if (player != null && room.getGame() != null) {
                    player.setDisconnected(true);
                    System.out.println("Player " + player.getId()+ " marked as disconnected.");
                    if (room.getStatus() == RoomStatus.EN_APUESTAS) {
                        System.out.println("Eliminando jugador desconectado durante la fase de apuestas: " + playerId);
                        room.removePlayer(player);
                    }

                    if (room.getStatus() == RoomStatus.EN_JUEGO && player.getInTurn()) {
                        System.out.println("Player was in turn. Passing to next player.");
                        player.setInTurn(false);
                        room.getGame().nextPlayer(); // Llama al método ajustado
                    }

                    sendRoomUpdate(roomId);
                } else {
                    System.err.println("[ERROR] Jugador no encontrado en la sala. Player ID: " + playerId);
                }
            } else {
                System.err.println("[ERROR] Sala no encontrada para Room ID: " + roomId);
            }

            // Limpia los mapeos
            socketToPlayerId.remove(sessionId);
            socketToRoomId.remove(sessionId);
        };
    }

    private DataListener<ChatMessage> chatMessageListener() {
        return (client, message, ackSender) -> {
            String roomId = message.getRoomId(); // ID de la sala del mensaje
            String sender = message.getSender(); // Nombre del remitente
    
            // Verificar que la sala existe
            Room room = rooms.get(roomId);
            if (room == null) {
                client.sendEvent("error", "La sala no existe.");
                return;
            }
    
            // Construir el mensaje de chat y retransmitirlo a todos en la sala
            ChatMessage chatMessage = new ChatMessage(sender, message.getMessage(), roomId);
            server.getRoomOperations(roomId).sendEvent("receiveMessage", chatMessage);
    
            System.out.println("Mensaje retransmitido en sala " + roomId + " por " + sender + ": " + message.getMessage());
        };
    }
    
    public void start() {
        server.start();
    }

    public void stop() {
        server.stop();
    }
}