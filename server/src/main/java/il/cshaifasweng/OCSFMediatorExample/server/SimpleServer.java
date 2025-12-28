package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.ArrayList;

public class SimpleServer extends AbstractServer {

    // We need to keep track of the two players
    private static ArrayList<ConnectionToClient> players = new ArrayList<>();

    public SimpleServer(int port) {
        super(port);
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        // If we receive a string (like "add client"), handle it here
        if (msg instanceof String) {
            String request = (String) msg;
            if (request.equals("add client")) {
                handleNewPlayer(client);
            }
        }
        // If we receive a Game Message (a Move), forward it
        else if (msg instanceof Message) {
            Message gameMsg = (Message) msg;
            if (gameMsg.getType().equals("MOVE")) {
                // Forward the move to the OTHER player
                sendToOpponent(client, gameMsg);
            }
        }
    }

    private void handleNewPlayer(ConnectionToClient client) {
        if (players.size() < 2) {
            players.add(client);
            System.out.println("Player connected: " + players.size() + "/2");

            if (players.size() == 1) {
                // First player must wait
                try {
                    client.sendToClient(new Message("WAIT", "", -1));
                } catch (IOException e) { e.printStackTrace(); }
            } else {
                // Two players connected! Start the game.
                startGame();
            }
        }
    }

    private void startGame() {
        try {
            // Randomly decide who is X and who is O
            // If Math.random() < 0.5, player 0 is X, otherwise player 1 is X
            boolean p1IsX = Math.random() < 0.5;

            ConnectionToClient player1 = players.get(0);
            ConnectionToClient player2 = players.get(1);

            // Send "START" message with their assigned symbol
            player1.sendToClient(new Message("START", p1IsX ? "X" : "O", -1));
            player2.sendToClient(new Message("START", p1IsX ? "O" : "X", -1));

            System.out.println("Game Started!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendToOpponent(ConnectionToClient sender, Message msg) {
        try {
            // Find the other player
            for (ConnectionToClient player : players) {
                if (player != sender) {
                    player.sendToClient(msg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clientDisconnected(ConnectionToClient client) {
        players.remove(client);
        System.out.println("Player disconnected. Resetting game.");
        // Optional: Send a "Game Over" message to the remaining player if you want
    }
}