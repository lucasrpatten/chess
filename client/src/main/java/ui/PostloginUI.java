package ui;

import java.util.List;

import model.CreateGameRequest;
import model.CreateGameResult;

public class PostloginUI extends UserInterface {
    PostloginUI() {
        super();
        this.cmds.put("create game", new FunctionPair<>(List.of("creategame", "create", "cg", "c"),
                new Arguments(List.of("game_name")), "Create a new game.", this::createGame));
        this.cmds.put("logout",
                new FunctionPair<>(List.of("logout", "signout", "exit"), "Sign out of your account.", this::logout));
    }

    private String createGame(String argString) {
        String[] args = argString.split(" ");
        if (args.length != 1) {
            return "Invalid number of arguments. Use `help` for command info.";
        }

        CreateGameResult res = Data.getInstance().getServerFacade().createGame(new CreateGameRequest(argString));
        return "%sSuccessfully created game with ID %d%s".formatted(EscapeSequences.SET_TEXT_COLOR_GREEN, res.gameID(),
                EscapeSequences.RESET_TEXT_COLOR);

    }

    private String logout() {
        Data.getInstance().getServerFacade().logout();
        return EscapeSequences.SET_TEXT_COLOR_RED + "Successfully logged out." + EscapeSequences.RESET_TEXT_COLOR;
    }
}
