import ui.Data;
import ui.UserREPL;

public class Main {
    public static void main(String[] args) {
        UserREPL repl = new UserREPL();
        String host = "localhost";
        int port = 8080;
        if (args.length > 0) {
            host = args[0];
        }
        if (args.length > 1) {
            port = Integer.parseInt(args[1]);
        }
        try {
            Data.getInstance().initializeRun(host, port, repl);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        repl.run();
    }
}