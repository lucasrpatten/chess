import java.io.IOException;
import java.net.URISyntaxException;

import javax.websocket.DeploymentException;

import ui.Data;
import ui.UserREPL;

public class Main {
    public static void main(String[] args) throws URISyntaxException, DeploymentException, IOException {
        UserREPL repl = new UserREPL();
        String host = "localhost";
        int port = 8080;
        if (args.length > 0) {
            host = args[0];
        }
        if (args.length > 1) {
            port = Integer.parseInt(args[1]);
        }
        Data.getInstance().initializeRun(host, port, repl);

        repl.run();
    }
}