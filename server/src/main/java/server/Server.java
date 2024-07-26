package server;

import java.net.HttpURLConnection;

import dataaccess.DataAccess;
import handlers.ClearHandler;
import handlers.CreateGameHandler;
import handlers.LoginHandler;
import handlers.LogoutHandler;
import handlers.RegisterHandler;
import handlers.ServerExceptionHandler;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.ServerException;
import service.UnauthorizedException;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        DataAccess data = new DataAccess();

        Spark.delete("/db", new ClearHandler(data));
        Spark.post("/user", new RegisterHandler(data));

        Spark.path("/game", () -> {
            Spark.post("", new CreateGameHandler(data));
        });
        Spark.path("/session", () -> {
            Spark.post("", new LoginHandler(data));
            Spark.delete("", new LogoutHandler(data));
        });

        Spark.exception(BadRequestException.class, new ServerExceptionHandler<>(HttpURLConnection.HTTP_BAD_REQUEST));
        Spark.exception(UnauthorizedException.class, new ServerExceptionHandler<>(HttpURLConnection.HTTP_UNAUTHORIZED));
        Spark.exception(AlreadyTakenException.class, new ServerExceptionHandler<>(HttpURLConnection.HTTP_FORBIDDEN));
        Spark.exception(ServerException.class, new ServerExceptionHandler<>(HttpURLConnection.HTTP_INTERNAL_ERROR));

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
