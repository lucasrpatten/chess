package ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import model.CreateGameRequest;
import model.CreateGameResult;
import model.GameData;
import model.GameListResult;

public class PostloginUI extends UserInterface {
    PostloginUI() {
        super();
        this.cmds.put("create game", new FunctionPair<>(List.of("creategame", "create", "cg", "c"),
                new Arguments(List.of("game_name")), "Create a new game.", this::createGame));
        this.cmds.put("list games",
                new FunctionPair<>(List.of("listgames", "list", "lg", "l"), "List all games.", this::listGames));
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

    private String listGames() {
        GameListResult req = Data.getInstance().getServerFacade().listGames();
        List<GameData> games = new ArrayList<>(req.games());
        if (games.size() == 0) {
            return "No games found. Create one with `creategame`.";
        }

        Comparator<GameData> comparator = new Comparator<GameData>() {
            @Override
            public int compare(GameData o1, GameData o2) {
                int emptyo1 = emptySpots(o1);
                int emptyo2 = emptySpots(o2);
                if (emptyo1 != emptyo2) {
                    return Integer.compare(emptyo1, emptyo2);
                }

                return Integer.compare(o1.gameID(), o2.gameID());
            }
        };

        games.sort(comparator);

        String output = EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_MAGENTA + "Games:\n"
                + EscapeSequences.RESET_TEXT_BOLD_FAINT;
        for (GameData game : games) {
            String gameOut = "\t";
            String white = game.whiteUsername() == null ? "Empty" : game.whiteUsername();
            String black = game.blackUsername() == null ? "Empty" : game.blackUsername();
            white = EscapeSequences.SET_TEXT_COLOR_BLACK + EscapeSequences.SET_BG_COLOR_WHITE + white
                    + EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR;
            black = EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.SET_BG_COLOR_BLACK + black
                    + EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR;

            gameOut += emptySpots(game) == 0 ? EscapeSequences.SET_TEXT_COLOR_YELLOW
                    : EscapeSequences.SET_TEXT_COLOR_GREEN;

            String gameName = EscapeSequences.SET_TEXT_BOLD + game.gameName();
            String gameID = EscapeSequences.SET_TEXT_ITALIC + "(" + game.gameID() + ")"
                    + EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_TEXT_ITALIC;
            gameOut = gameOut + String.format(" %s %s : %s vs. %s\n", gameID, gameName, white, black);

            output += gameOut;
        }

        return output;

    }

    private String logout() {
        Data.getInstance().getServerFacade().logout();
        return EscapeSequences.SET_TEXT_COLOR_RED + "Successfully logged out." + EscapeSequences.RESET_TEXT_COLOR;
    }

    private int emptySpots(GameData game) {
        int whiteUser = game.whiteUsername() == null ? 0 : 1;
        int blackUser = game.blackUsername() == null ? 0 : 1;
        return whiteUser + blackUser;
    }
}
