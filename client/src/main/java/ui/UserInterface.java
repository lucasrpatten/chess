package ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserInterface implements BaseUI {

    protected Map<String, FunctionPair<String>> cmds;

    UserInterface() {
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

            callers = String.join("|", f.getKeys());
            callers = String.format("%s[%s]%s", EscapeSequences.SET_TEXT_BOLD, callers,
                    EscapeSequences.RESET_TEXT_BOLD_FAINT);
            if (f.getArgs() != null) {
                args = EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_ITALIC + f.getArgs().toString()
                        + EscapeSequences.RESET_TEXT_BOLD_FAINT + EscapeSequences.RESET_TEXT_ITALIC;
            }

            helpMsg = String.format("%s%s:%s\n\t%s%s %s%s- %s",
                    EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_MAGENTA, key,
                    EscapeSequences.RESET_TEXT_BOLD_FAINT, EscapeSequences.SET_TEXT_COLOR_BLUE, callers, args,
                    EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY, f.getHelp());

            commands.add(helpMsg);
        }
        return String.join("\n", commands);
    }

    @Override
    public String runCmd(String cmd) {
        String cmdCaller;
        String args;

        // Split the command string into command and arguments
        String[] parts = cmd.split(" ", 2);

        if (parts.length == 1) {
            cmdCaller = parts[0];
            args = "";
        }
        else {
            cmdCaller = parts[0];
            args = parts[1];
        }

        cmdCaller = cmdCaller.toLowerCase();

        // Iterate over available commands and execute the matching one
        for (FunctionPair<String> f : cmds.values()) {
            if (f.getKeys().contains(cmdCaller)) {
                return f.apply(args);
            }
        }

        return help();
    }

}
