package server;

import dataaccess.DataAccess;
import handlers.ClearHandler;
import handlers.RegisterHandler;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        DataAccess data = new DataAccess();

        Spark.delete("/db", new ClearHandler(data));
        Spark.post("/user", new RegisterHandler(data));

        // This line initializes the server and can be removed once you have a
        // functioning endpoint
        // Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
