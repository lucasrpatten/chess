package ui;

import java.util.List;

import model.LoginRequest;
import model.UserData;

public class PreloginUI extends UserInterface {
    PreloginUI() {
        super();

        this.cmds.put("register", new FunctionPair<>(List.of("register", "reg", "r"),
                new Arguments(List.of("username", "password", "email")), "Register a new user.", this::register));
        this.cmds.put("login", new FunctionPair<>(List.of("login", "signin", "l"),
                new Arguments(List.of("username", "password")), "Login to an existing account.", this::login));
        this.cmds.put("quit", new FunctionPair<>(List.of("quit", "exit"), "Quit the program.", this::quit));
    }

    private String quit() {
        return EscapeSequences.SET_TEXT_COLOR_RED + "Quitting...";
    }

    private String register(String argString) {
        String[] args = argString.split(" ");
        if (args.length != 3) {
            return "Invalid number of arguments. Use `help` for command info.";
        }

        String username = args[0];
        String password = args[1];
        String email = args[2];
        Data.getInstance().getServerFacade().register(new UserData(username, password, email));
        return "Successfully registered.";
    }

    private String login(String argString) {
        String[] args = argString.split(" ");
        if (args.length != 2) {
            return "Invalid number of arguments. Use `help` for command info.";
        }

        String username = args[0];
        String password = args[1];
        Data.getInstance().getServerFacade().login(new LoginRequest(username, password));
        return "Successfully logged in.";
    }
}
