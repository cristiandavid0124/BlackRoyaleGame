import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiplayerService {
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public void startMultiplayerGame(Game game) {
        for (Player player : game.getPlayers()) {
            executorService.submit(() -> handlePlayerTurn(player, game));
        }
    }

    private void handlePlayerTurn(Player player, Game game) {
        // Lógica para manejar el turno del jugador
    }
}