package ui;

import java.util.List;

import chess.ChessGame;
import model.CreateGameRequest;
import model.CreateGameResult;
import model.GameData;

public class PostloginUI extends UserInterface {
    PostloginUI() {
        super();
        this.cmds.put("create game", new FunctionPair<>(List.of("creategame", "create", "cg", "c"),
                new Arguments(List.of("game_name")), "Create a new game.", this::createGame));
        this.cmds.put("list games",
                new FunctionPair<>(List.of("listgames", "list", "lg", "l"), "List all games.", this::listGames));
        this.cmds.put("join game",
                new FunctionPair<>(List.of("joingame", "join", "j"),
                        new Arguments(List.of("WHITE|BLACK", "game_number")), "Join a game with the given number.",
                        this::joinGame));
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
        List<GameData> games = Data.getInstance().getServerFacade().listGames();
        if (games.size() == 0) {
            return "No games found. Create one with `creategame`.";
        }

        String output = EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_MAGENTA + "Games:\n"
                + EscapeSequences.RESET_TEXT_BOLD_FAINT;
        for (int i = 0; i < games.size(); i++) {
            GameData game = games.get(i);
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
            String gameNumber = EscapeSequences.SET_TEXT_ITALIC + "(" + i + 1 + ")" + EscapeSequences.RESET_TEXT_COLOR
                    + EscapeSequences.RESET_TEXT_ITALIC;
            gameOut = gameOut + String.format(" %s %s : %s vs. %s\n", gameNumber, gameName, white, black);

            output += gameOut;
        }

        return output;
    }

    private String joinGame(String argString) {
        String[] args = argString.split(" ");
        if (args.length != 2) {
            return "Invalid number of arguments. Use `help` for command info.";
        }

        ChessGame.TeamColor color = args[0].toLowerCase().equals("white") ? ChessGame.TeamColor.WHITE
                : ChessGame.TeamColor.BLACK;
        int gameNumber = Integer.parseInt(args[1]);
        Data.getInstance().getServerFacade().joinGame(color, gameNumber);
        return EscapeSequences.SET_TEXT_COLOR_GREEN + "Successfully joined game" + EscapeSequences.RESET_TEXT_COLOR;
    }

    public static int emptySpots(GameData game) {
        int whiteUser = game.whiteUsername() == null ? 0 : 1;
        int blackUser = game.blackUsername() == null ? 0 : 1;
        return whiteUser + blackUser;
    }

    private String logout() {
        Data.getInstance().getServerFacade().logout();
        return EscapeSequences.SET_TEXT_COLOR_RED + "Successfully logged out." + EscapeSequences.RESET_TEXT_COLOR;
    }

}
