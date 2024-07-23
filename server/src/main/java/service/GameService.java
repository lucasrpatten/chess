package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import model.GameData;
import model.UserData;

public class GameService {
    private static final AuthDAO authDAO = new AuthDAO();

    public GameData[] list(UserData user) {
    }

    public GameData create(UserData user, String gameName) {
    }

    public GameData join(int gameID, UserData user, ChessGame.TeamColor color) {
    }
}
