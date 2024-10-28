package com.escuelagaing.edu.co.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BlackjackConsoleGame {

    private Game game;
    private List<Player> players;
    private Scanner scanner;

    public BlackjackConsoleGame() {
        scanner = new Scanner(System.in);
        players = createPlayers();
        game = new Game(players, "room1");
    }

    public void play() {
        System.out.println("¡Bienvenido al juego de Blackjack!");

        // Fase de apuestas
        placeBets();

        // Iniciar el juego y repartir cartas iniciales
        game.startGame();

        // Turnos de jugadores
        for (Player player : players) {
            playTurn(player);
        }

        // Turno del dealer
        System.out.println("\n--- Turno del Dealer ---");
        game.dealerTurn();
        System.out.println("Dealer - Cartas: " + game.getDealer().getHand() + " - Puntuación: " + game.getDealer().calculateScore());

        // Finalizar el juego y mostrar ganadores
        game.endGame();
        showWinners();
    }

    private void placeBets() {
        System.out.println("\n--- Fase de Apuestas ---");
        for (Player player : players) {
            System.out.print(player.getName() + ", ingresa tu apuesta (saldo disponible: " + player.getAmount() + "): ");
            double betAmount = scanner.nextDouble();
            
            if (player.placeBet(betAmount)) {
                System.out.println(player.getName() + " ha apostado " + betAmount);
                // Estado de jugador después de la apuesta
                System.out.println("Jugador: " + player.getName() + " - Apuesta actual: " + player.getBet() + " - Saldo restante: " + player.getAmount());
            } else {
                System.out.println("Fondos insuficientes. Apuesta rechazada.");
            }
        }
        game.startBetting(); // Inicia el proceso de apuestas en el objeto Game
    }

    private void playTurn(Player player) {
        System.out.println("\nTurno de " + player.getName());
        boolean playerFinished = false;

        while (!playerFinished) {
            System.out.println("Mano actual: " + player.getHand() + " - Puntuación: " + player.calculateScore());
            System.out.println("¿Qué deseas hacer? (1: HIT, 2: STAND, 3: DOUBLE)");
            int decision = scanner.nextInt();

            switch (decision) {
                case 1:
                    player.setEstado(PlayerAction.HIT);
                    game.decideAction(player);
                    System.out.println(player.getName() + " ha elegido HIT.");
                    break;
                case 2:
                    player.setEstado(PlayerAction.STAND);
                    game.decideAction(player);
                    System.out.println(player.getName() + " ha elegido STAND.");
                    playerFinished = true;
                    break;
                case 3:
                    player.setEstado(PlayerAction.DOUBLE);
                    if (player.placeBet(player.getBet() * 2)) {
                        game.decideAction(player);
                        System.out.println(player.getName() + " ha elegido DOUBLE.");
                        playerFinished = true;
                    } else {
                        System.out.println("Fondos insuficientes para doblar la apuesta.");
                    }
                    break;
                default:
                    System.out.println("Opción no válida.");
                    break;
            }

            if (player.calculateScore() > 21) {
                System.out.println(player.getName() + " se ha pasado de 21.");
                playerFinished = true;
            }
        }
    }

    private void showWinners() {
        List<Player> winners = game.calculateWinners();
        System.out.println("\n--- Fin del Juego ---");

        if (winners.isEmpty()) {
            System.out.println("El dealer ha ganado.");
        } else {
            System.out.println("¡Ganadores!");
            for (Player winner : winners) {
                System.out.println(winner.getName() + " con puntuación: " + winner.calculateScore());
            }
        }

        for (Player player : winners) {
            System.out.println(player.getName() + ": " + player.getAmount() + ":"+ player.getBet()) ;
        }
        // Distribuir ganancias después de identificar ganadores
        game.deliverProfit(); 

      
    
        System.out.println("\nSaldos finales:");
        for (Player player : players) {
            System.out.println(player.getName() + ": " + player.getAmount() + ":"+ player.getBet()) ;
        }
    }

    private List<Player> createPlayers() {
        List<Player> players = new ArrayList<>();
        User user1 = new User("1", "player1@example.com", "Jugador 1");
        User user2 = new User("2", "player2@example.com", "Jugador 2");

        Player player1 = new Player(user1, "room1", 1000);
        Player player2 = new Player(user2, "room1", 1000);

        players.add(player1);
        players.add(player2);
        return players;
    }

    public static void main(String[] args) {
        BlackjackConsoleGame blackjackGame = new BlackjackConsoleGame();
        blackjackGame.play();
    }
}
