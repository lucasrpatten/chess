package ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PreloginUI {
    private ArrayList<FunctionPair> cmds = new ArrayList<>();

    PreloginUI() {
        cmds.add(new FunctionPair(new ArgHelpPair(List.of("help", "h"), "Displays this help message."), this::help));
    }

    public void runCmd(String cmd, String[] args) {
        for (FunctionPair f : cmds) {
            if (f.keys.contains(cmd)) {
                f.function.run();
                return;
            }
        }
    }

    private String help() {

    }
}
