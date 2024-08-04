import chess.*;
import ui.UserREPL;

public class Main {
    public static void main(String[] args) {
        UserREPL ui = new UserREPL();
        String host = "localhost";
        int port = 8080;
        if (args.length > 0) {
            host = args[0];
        }
        if (args.length > 1) {
            port = Integer.parseInt(args[1]);
        }
    }
}