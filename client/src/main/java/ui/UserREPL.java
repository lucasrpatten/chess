package ui;

import java.util.Scanner;

import javax.websocket.OnMessage;

import com.google.gson.Gson;

import web.WebSocketObserver;
import websocket.messages.ErrorMsg;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

public class UserREPL implements WebSocketObserver {

    @Override
    @OnMessage
    public void receiveMessage(String msg) {
        ServerMessage message = new Gson().fromJson(msg, ServerMessage.class);
        switch (message.getServerMessageType()) {
        case NOTIFICATION -> {
            Notification notif = new Gson().fromJson(msg, Notification.class);
            System.out.println("%s%s%s".formatted(EscapeSequences.SET_TEXT_COLOR_GREEN, notif.getMessage(),
                    EscapeSequences.RESET_TEXT_COLOR));
            break;

        }
        case ERROR -> {
            ErrorMsg error = new Gson().fromJson(msg, ErrorMsg.class);
            System.out.println("%s%s%s%s%s".formatted(EscapeSequences.SET_TEXT_COLOR_RED,
                    EscapeSequences.SET_TEXT_BLINKING, error.getErrorMessage(), EscapeSequences.RESET_TEXT_COLOR,
                    EscapeSequences.RESET_TEXT_BLINKING));
            break;
        }
        case LOAD_GAME -> {
            LoadGame gameMsg = new Gson().fromJson(msg, LoadGame.class);
            Data.getInstance().setGame(gameMsg.getGame());
            System.out.println(((GameUI) Data.getInstance().getUi()).formatBoard());
            break;
        }
        default -> System.out.println("Unknown message type: " + message.getServerMessageType());
        }

        printPrompt();
    }

    private void printPrompt() {
        System.out.print("\r" + EscapeSequences.SET_TEXT_COLOR_YELLOW + Data.getInstance().getPrompt()
                + EscapeSequences.RESET_TEXT_COLOR);
        System.out.flush();
    }

    public void run() {
        // Print the welcome message
        System.out.printf("%sWelcome to Chess240! Type 'help' for a list of commands. Login to get started!\n",
                EscapeSequences.SET_TEXT_COLOR_WHITE); // + EscapeSequences.SET_BG_COLOR_DARK_GREY);

        try (Scanner scanner = new Scanner(System.in)) {
            String res = "";

            while (!res.equals("quit")) {
                // Print the initial prompt and stay on the same line
                System.out.print("\r" + EscapeSequences.SET_TEXT_COLOR_YELLOW + Data.getInstance().getPrompt()
                        + EscapeSequences.RESET_TEXT_COLOR);
                System.out.flush(); // Ensure the prompt is visible
                // Read user input
                String line = scanner.nextLine();

                // Process the input
                String[] tokens = line.toLowerCase().split(" ");
                res = tokens[0].toLowerCase();
                try {
                    String result = Data.getInstance().getUi().runCmd(line);
                    System.out.println(result);
                }
                catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                System.out.print(EscapeSequences.RESET_TEXT_COLOR);
            }
        }
    }
}
