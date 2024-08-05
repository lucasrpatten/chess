package ui;

import java.util.Scanner;

public class UserREPL {

    public void run() {
        // Print the welcome message
        System.out.printf("%sWelcome to Chess240! Type 'help' for a list of commands. Login to get started!\n",
                EscapeSequences.SET_TEXT_COLOR_WHITE); // + EscapeSequences.SET_BG_COLOR_DARK_GREY);

        // Set initial state
        Data.getInstance().setState(Data.State.LOGGED_OUT);
        try (Scanner scanner = new Scanner(System.in)) {
            String res = "";

            while (!res.equals("quit") && !res.equals("exit")) {
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
