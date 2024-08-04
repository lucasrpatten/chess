package ui;

import ui.EscapeSequences;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class UserInterface implements BaseUI {

    private Map<String, FunctionPair<String>> cmds;

    public UserInterface() {
        this.cmds = new LinkedHashMap<>();
        this.cmds.put("help", new FunctionPair<>(List.of("help", "h"), "Displays this help message.", this::help));
    }

    @Override
    public String help() {
        List<String> commands = new ArrayList<>();
        for (String key : cmds.keySet()) {
            FunctionPair<String> f = cmds.get(key);
            String callers;
            String helpMsg;

            String args = "";

            callers = String.join(", ", f.getKeys());
            callers = String.format("%s(%s)%s", EscapeSequences.SET_TEXT_ITALIC, callers,
                    EscapeSequences.RESET_TEXT_ITALIC);
            if (f.getArgs() != null) {
                args = f.getArgs().toString();
            }

            helpMsg = String.format("%s%s:%s\n\t%s%s%s%s- %s",
                    EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_MAGENTA, key,
                    EscapeSequences.RESET_TEXT_BOLD_FAINT, EscapeSequences.SET_TEXT_COLOR_BLUE, callers, args,
                    EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY, f.getHelp());

            commands.add(helpMsg);
        }
        return String.join("\n", commands);
    }
}
