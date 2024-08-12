package ui;

import java.util.List;

import chess.ChessPosition;

public class GameUI extends GameRendererUI {
        GameUI() {
                super();
                this.cmds.put("redraw chess board",
                                new FunctionPair<>(List.of("redrawboard", "redraw", "reload", "rb", "r"),
                                                "Redraw the chess board.", null));
                this.cmds.put("make move",
                                new FunctionPair<>(List.of("move", "m"), new Arguments(List.of("from", "to")),
                                                "Move the selected piece from one position to another.", null));
                this.cmds.put("highlight legal moves",
                                new FunctionPair<>(List.of("highlight", "moves"),
                                                new Arguments(List.of("piece_location")),
                                                "Highlight the legal moves for the selected piece.", this::highlight));
                this.cmds.put("resign", new FunctionPair<>(List.of("resign", "r"), "Resign from the game.", null));
                this.cmds.put("leave", new FunctionPair<>(List.of("leave", "l"), "Stop viewing the game.", null));
                formatBoard(Data.getInstance().getGameNumber());
        }

        private String highlight(String argString) {
                String[] args = argString.split(" ");
                if (args.length != 1) {
                        return "Invalid number of arguments. Use `help` for command info.";
                }
                ChessPosition pos = parsePosition(argString);
                return highlightLegal(Data.getInstance().getGameNumber(), pos);
        }

        private ChessPosition parsePosition(String posStr) {
                char file = posStr.charAt(0);
                char rank = posStr.charAt(1);

                int colIdx = (int) file - (int) 'a' + 1;
                int rowIdx = (int) rank - (int) '0';
                System.out.println("Row: " + rowIdx + " Col: " + colIdx);
                return new ChessPosition(rowIdx, colIdx);
        }

}
