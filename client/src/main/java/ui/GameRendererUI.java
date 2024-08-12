package ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessGame.TeamColor;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPiece.PieceType;
import chess.ChessPosition;

public abstract class GameRendererUI extends UserInterface {
    private static ChessPiece whiteRook = new ChessPiece(TeamColor.WHITE, PieceType.ROOK);
    private static ChessPiece whiteKnight = new ChessPiece(TeamColor.WHITE, PieceType.KNIGHT);
    private static ChessPiece whiteBishop = new ChessPiece(TeamColor.WHITE, PieceType.BISHOP);
    private static ChessPiece whiteQueen = new ChessPiece(TeamColor.WHITE, PieceType.QUEEN);
    private static ChessPiece whiteKing = new ChessPiece(TeamColor.WHITE, PieceType.KING);
    private static ChessPiece whitePawn = new ChessPiece(TeamColor.WHITE, PieceType.PAWN);

    private static ChessPiece blackRook = new ChessPiece(TeamColor.BLACK, PieceType.ROOK);
    private static ChessPiece blackKnight = new ChessPiece(TeamColor.BLACK, PieceType.KNIGHT);
    private static ChessPiece blackBishop = new ChessPiece(TeamColor.BLACK, PieceType.BISHOP);
    private static ChessPiece blackQueen = new ChessPiece(TeamColor.BLACK, PieceType.QUEEN);
    private static ChessPiece blackKing = new ChessPiece(TeamColor.BLACK, PieceType.KING);
    private static ChessPiece blackPawn = new ChessPiece(TeamColor.BLACK, PieceType.PAWN);

    private ChessBoard getBoard(int gameNumber) {
        return getGame(gameNumber).getBoard();
    }

    private ChessGame getGame(int gameNumber) {
        return Data.getInstance().getGameList().get(gameNumber - 1).game();
    }

    private String whiteStringColorize(String piece) {
        return EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + piece + EscapeSequences.RESET_TEXT_COLOR;
    }

    private String blackStringColorize(String piece) {
        return EscapeSequences.SET_TEXT_COLOR_DARK_GREY + piece + EscapeSequences.RESET_TEXT_COLOR;
    }

    private String pieceToString(ChessPiece piece) {

        if (piece == null) {
            return "   ";
        }
        if (piece.equals(whitePawn)) {
            return whiteStringColorize(EscapeSequences.WHITE_PAWN);
        }
        if (piece.equals(whiteRook)) {
            return whiteStringColorize(EscapeSequences.WHITE_ROOK);
        }
        if (piece.equals(whiteKnight)) {
            return whiteStringColorize(EscapeSequences.WHITE_KNIGHT);
        }
        if (piece.equals(whiteBishop)) {
            return whiteStringColorize(EscapeSequences.WHITE_BISHOP);
        }
        if (piece.equals(whiteQueen)) {
            return whiteStringColorize(EscapeSequences.WHITE_QUEEN);
        }
        if (piece.equals(whiteKing)) {
            return whiteStringColorize(EscapeSequences.WHITE_KING);
        }
        if (piece.equals(blackPawn)) {
            return blackStringColorize(EscapeSequences.BLACK_PAWN);
        }
        if (piece.equals(blackRook)) {
            return blackStringColorize(EscapeSequences.BLACK_ROOK);
        }
        if (piece.equals(blackKnight)) {
            return blackStringColorize(EscapeSequences.BLACK_KNIGHT);
        }
        if (piece.equals(blackBishop)) {
            return blackStringColorize(EscapeSequences.BLACK_BISHOP);
        }
        if (piece.equals(blackQueen)) {
            return blackStringColorize(EscapeSequences.BLACK_QUEEN);
        }
        if (piece.equals(blackKing)) {
            return blackStringColorize(EscapeSequences.BLACK_KING);
        }
        return "    ";
    }

    private String[][] boardToList(ChessBoard board) {
        String[][] output = new String[8][8];
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                output[i - 1][j - 1] = pieceToString(piece);
            }
        }
        return output;
    }

    private static String generateBlackView(String[][] boardList, List<ChessPosition> highlightSquares) {
        StringBuilder blackView = new StringBuilder();
        blackView.append("%s%s    h  g  f  e  d  c  b  a     %s\n".formatted(EscapeSequences.SET_BG_COLOR_DARK_GREEN,
                EscapeSequences.SET_TEXT_COLOR_WHITE, EscapeSequences.RESET_BG_COLOR));
        for (int i = 0; i < 8; i++) {
            blackView.append("%s %d ".formatted(EscapeSequences.SET_BG_COLOR_DARK_GREEN, i + 1));
            for (int j = 7; j >= 0; --j) {
                if ((i + j) % 2 == 1) {
                    if (highlightSquares.contains(new ChessPosition(i + 1, j + 1))) {
                        blackView.append(EscapeSequences.SET_BG_COLOR_MAGENTA);
                    }
                    else {
                        blackView.append(EscapeSequences.SET_BG_COLOR_WHITE);
                    }
                }
                else {
                    if (highlightSquares.contains(new ChessPosition(i + 1, j + 1))) {
                        blackView.append(EscapeSequences.SET_BG_COLOR_RED);
                    }
                    else {
                        blackView.append(EscapeSequences.SET_BG_COLOR_BLACK);
                    }
                }
                blackView.append(boardList[i][j]);
            }
            blackView.append("%s%s %d  %s\n".formatted(EscapeSequences.SET_BG_COLOR_DARK_GREEN,
                    EscapeSequences.SET_TEXT_COLOR_WHITE, i + 1, EscapeSequences.RESET_BG_COLOR));
        }
        blackView.append("%s%s    h  g  f  e  d  c  b  a     %s\n".formatted(EscapeSequences.SET_BG_COLOR_DARK_GREEN,
                EscapeSequences.SET_TEXT_COLOR_WHITE, EscapeSequences.RESET_BG_COLOR));
        return blackView.toString();
    }

    private static String generateWhiteView(String[][] boardList, List<ChessPosition> highlightSquares) {
        StringBuilder whiteView = new StringBuilder();
        whiteView.append("%s%s    a  b  c  d  e  f  g  h     %s\n".formatted(EscapeSequences.SET_BG_COLOR_DARK_GREEN,
                EscapeSequences.SET_BG_COLOR_DARK_GREEN, EscapeSequences.RESET_BG_COLOR));
        for (int i = 7; i >= 0; i--) {
            whiteView.append("%s %d ".formatted(EscapeSequences.SET_BG_COLOR_DARK_GREEN, i + 1));
            for (int j = 0; j < 8; j++) {
                if (((7 - i) + (7 - j)) % 2 == 1) {
                    if (highlightSquares.contains(new ChessPosition(i + 1, j + 1))) {
                        whiteView.append(EscapeSequences.SET_BG_COLOR_MAGENTA);
                    }
                    else {
                        whiteView.append(EscapeSequences.SET_BG_COLOR_WHITE);
                    }
                }
                else {
                    if (highlightSquares.contains(new ChessPosition(i + 1, j + 1))) {
                        whiteView.append(EscapeSequences.SET_BG_COLOR_RED);
                    }
                    else {
                        whiteView.append(EscapeSequences.SET_BG_COLOR_BLACK);
                    }
                }
                whiteView.append(boardList[i][j]);
            }
            whiteView.append("%s%s %d  %s\n".formatted(EscapeSequences.SET_BG_COLOR_DARK_GREEN,
                    EscapeSequences.SET_TEXT_COLOR_WHITE, i + 1, EscapeSequences.RESET_BG_COLOR));
        }
        whiteView.append("%s%s    a  b  c  d  e  f  g  h     %s\n".formatted(EscapeSequences.SET_BG_COLOR_DARK_GREEN,
                EscapeSequences.SET_BG_COLOR_DARK_GREEN, EscapeSequences.RESET_BG_COLOR));
        return whiteView.toString();
    }

    public String highlightLegal(int gameNumber, ChessPosition position) {
        ChessGame game = getGame(gameNumber);
        Collection<ChessMove> allowedMoves = game.validMoves(position);
        List<ChessPosition> highlightSquares = new ArrayList<>(allowedMoves.size());
        System.out.println(highlightSquares.toString());
        for (ChessMove move : allowedMoves) {
            highlightSquares.add(move.getEndPosition());
        }
        return formatBoard(gameNumber, highlightSquares);

    }

    private String formatBoard(int gameNumber, List<ChessPosition> highlightSquares) {
        ChessBoard board = getBoard(gameNumber);
        String[][] boardList = boardToList(board);
        String whiteView = generateWhiteView(boardList, highlightSquares);
        String blackView = generateBlackView(boardList, highlightSquares);

        return "%s%s\n\n%s%s%s\n".formatted(blackView, EscapeSequences.RESET_BG_COLOR, whiteView,
                EscapeSequences.RESET_BG_COLOR, EscapeSequences.RESET_TEXT_COLOR);
    }

    public String formatBoard(int gameNumber) {
        return formatBoard(gameNumber, List.of());
    }

    public String formatBoard() {
        Data.getInstance().getGame();
        ChessBoard board = Data.getInstance().getGame().getBoard();
        String[][] boardList = boardToList(board);
        String whiteView = generateWhiteView(boardList, List.of());
        String blackView = generateBlackView(boardList, List.of());
        return "%s%s\n\n%s%s%s\n".formatted(blackView, EscapeSequences.RESET_BG_COLOR, whiteView,
                EscapeSequences.RESET_BG_COLOR, EscapeSequences.RESET_TEXT_COLOR);
    }
}
