package ui;

import java.io.IOException;
import java.util.List;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessPiece.PieceType;
import web.WebSocketClient;

public class GameUI extends GameRendererUI {

        WebSocketClient ws;

        GameUI() {
                super();
                this.cmds.put("redraw chess board",
                                new FunctionPair<>(List.of("redrawboard", "redraw", "reload", "rb", "r"),
                                                "Redraw the chess board.", this::formatBoard));
                this.cmds.put("make move",
                                new FunctionPair<>(List.of("move", "m"),
                                                new Arguments(List.of("from", "to", "?promotion_piece?")),
                                                "Move the selected piece from one position to another.", this::move));
                this.cmds.put("highlight legal moves",
                                new FunctionPair<>(List.of("highlight", "moves"),
                                                new Arguments(List.of("piece_location")),
                                                "Highlight the legal moves for the selected piece.", this::highlight));
                this.cmds.put("resign", new FunctionPair<>(List.of("resign", "r"), "Resign from the game.", this::res));
                this.cmds.put("leave",
                                new FunctionPair<>(List.of("leave", "l"), "Stop viewing the game.", this::leave));
                System.out.println(formatBoard(Data.getInstance().getGameNumber()));

        }

        private String res() {
                try {
                        Data.getInstance().getWebSocketClient().resign();
                }
                catch (IOException e) {
                        return "Failed to resign.";
                }
                Data.getInstance().setState(Data.State.LOGGED_IN);
                return "Resigned.";
        }

        private String leave() {
                try {

                        Data.getInstance().getWebSocketClient().leave();
                }
                catch (IOException e) {
                        return "Failed to leave game.";
                }

                Data.getInstance().setState(Data.State.LOGGED_IN);
                return "Left game.";
        }

        private String move(String argString) {
                Data.getInstance().setJustMoved(false);
                String[] args = argString.split(" ");
                if (args.length < 2 || args.length > 3) {
                        return "Invalid number of arguments. Use `help` for command info.";
                }
                ChessPosition from;
                ChessPosition to;
                try {
                        from = parsePosition(args[0]);
                        to = parsePosition(args[1]);
                }
                catch (Exception e) {
                        return "Invalid position.";
                }

                PieceType promotion = null;

                if (args.length == 3) {
                        switch (args[2].charAt(0)) {
                        case 'q' -> promotion = PieceType.QUEEN;
                        case 'r' -> promotion = PieceType.ROOK;
                        case 'b' -> promotion = PieceType.BISHOP;
                        case 'n' -> promotion = PieceType.KNIGHT;
                        default -> {
                                return "Invalid promotion piece.";
                        }
                        }
                }

                ChessMove move = new ChessMove(from, to, promotion);
                try {
                        WebSocketClient wsc = Data.getInstance().getWebSocketClient();
                        wsc.move(move);
                        // return formatBoard(Data.getInstance().getGameNumber());
                        Data.getInstance().setJustMoved(true);
                        return "\n";

                }
                catch (Exception e) {
                        return "Failed to make move.";
                }
        }

        private String highlight(String argString) {
                String[] args = argString.split(" ");
                if (args.length != 1) {
                        return "Invalid number of arguments. Use `help` for command info.";
                }
                try {
                        ChessPosition pos = parsePosition(argString);
                        return highlightLegal(Data.getInstance().getGameNumber(), pos);
                }
                catch (Exception e) {
                        return "Invalid position.";
                }
        }

        private ChessPosition parsePosition(String posStr) {
                char file = posStr.charAt(0);
                char rank = posStr.charAt(1);

                int colIdx = (int) file - (int) 'a' + 1;
                int rowIdx = (int) rank - (int) '0';
                return new ChessPosition(rowIdx, colIdx);
        }

}
