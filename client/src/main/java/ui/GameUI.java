package ui;

import java.util.List;

public class GameUI extends GameRendererUI {
    GameUI() {
        super();
        this.cmds.put("redraw chess board", new FunctionPair<>(List.of("redrawboard", "redraw", "reload", "rb", "r"),
                "Redraw the chess board.", null));
        this.cmds.put("make move", new FunctionPair<>(List.of("move", "m"), new Arguments(List.of("from", "to")),
                "Move the selected piece from one position to another.", null));
        this.cmds.put("highlight leagal moves", new FunctionPair<>(List.of("highlight", "moves"),
                new Arguments(List.of("piece_location")), "Highlight the legal moves for the selected piece.", null));
        this.cmds.put("resign", new FunctionPair<>(List.of("resign", "r"), "Resign from the game.", null));
        this.cmds.put("leave", new FunctionPair<>(List.of("leave", "l"), "Stop viewing the game.", null));
    }
}
