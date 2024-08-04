package ui;

public class UserREPL {

    public void run() {
        System.out.printf("%sWelcome to Chess240! Type 'help' for a list of commands. Login to get started!\n",
                EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.SET_BG_COLOR_DARK_GREY);

    }
}
