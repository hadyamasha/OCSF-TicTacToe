package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class PrimaryController {

    @FXML
    private Label statusLabel;
    @FXML
    private GridPane gameBoard;

    private char mySymbol = ' '; // Will be 'X' or 'O'
    private boolean isMyTurn = false;

    @FXML
    void initialize() {
        EventBus.getDefault().register(this);
        setBoardEnabled(false); // Disable board until game starts
        try {
            SimpleClient.getClient().sendToServer("add client");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onGameEvent(GameEvent event) {
        Platform.runLater(() -> {
            Message msg = event.getMessage();
            switch (msg.getType()) {
                case "WAIT":
                    statusLabel.setText("Waiting for opponent...");
                    setBoardEnabled(false);
                    break;

                case "START":
                    mySymbol = msg.getContent().charAt(0);
                    isMyTurn = (mySymbol == 'X'); // X always goes first

                    if (isMyTurn) {
                        statusLabel.setText("Game Started! You are " + mySymbol + " (Your Turn)");
                        setBoardEnabled(true);
                    } else {
                        statusLabel.setText("Game Started! You are " + mySymbol + " (Opponent's Turn)");
                        setBoardEnabled(false);
                    }
                    break;

                case "MOVE":
                    // 1. Update board with Opponent's move
                    int index = msg.getCellIndex();
                    String symbol = msg.getContent();
                    updateBoardUI(index, symbol);

                    // 2. Check if Opponent won
                    if (checkWinner()) {
                        break; // Stop here if game is over
                    }

                    // 3. It's my turn now
                    isMyTurn = true;
                    statusLabel.setText("Your Turn (" + mySymbol + ")");
                    setBoardEnabled(true);
                    break;
            }
        });
    }

    @FXML
    void onCellClick(ActionEvent event) {
        if (!isMyTurn) return;

        Button btn = (Button) event.getSource();
        if (!btn.getText().isEmpty()) return;

        // 1. Update my screen
        btn.setText(String.valueOf(mySymbol));

        // 2. Check if I won
        if (checkWinner()) {
            // Send the move so the other player sees it, then stop
            sendMove(btn);
            return;
        }

        // 3. Send move to server and end my turn
        sendMove(btn);
        isMyTurn = false;
        setBoardEnabled(false);
        statusLabel.setText("Opponent's Turn");
    }

    private void sendMove(Button btn) {
        int index = Integer.parseInt(btn.getId().replace("btn", ""));
        try {
            SimpleClient.getClient().sendToServer(new Message("MOVE", String.valueOf(mySymbol), index));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateBoardUI(int index, String symbol) {
        Button btn = (Button) gameBoard.lookup("#btn" + index);
        if (btn != null) {
            btn.setText(symbol);
        }
    }

    private void setBoardEnabled(boolean enabled) {
        gameBoard.setDisable(!enabled);
    }

    /**
     * Checks all 8 winning combinations.
     * Returns TRUE if the game is over (Win or Draw).
     */
    private boolean checkWinner() {
        // All 8 winning lines (indices)
        int[][] lines = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // Rows
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // Cols
                {0, 4, 8}, {2, 4, 6}             // Diagonals
        };

        for (int[] line : lines) {
            String b1 = getBtnText(line[0]);
            String b2 = getBtnText(line[1]);
            String b3 = getBtnText(line[2]);

            // If all 3 are the same and not empty -> WIN
            if (!b1.isEmpty() && b1.equals(b2) && b1.equals(b3)) {
                endGame(b1 + " Won!");
                return true;
            }
        }

        // Check for Draw (Board full)
        boolean isFull = true;
        for(int i=0; i<9; i++) {
            if(getBtnText(i).isEmpty()) isFull = false;
        }
        if(isFull) {
            endGame("Draw!");
            return true;
        }

        return false;
    }

    private String getBtnText(int index) {
        Button btn = (Button) gameBoard.lookup("#btn" + index);
        return btn.getText();
    }

    private void endGame(String message) {
        setBoardEnabled(false); // Stop clicks
        statusLabel.setText("Game Over: " + message);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}