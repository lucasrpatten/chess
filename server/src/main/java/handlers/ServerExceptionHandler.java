package handlers;

import java.util.Map;

import com.google.gson.Gson;

import spark.ExceptionHandler;
import spark.Request;
import spark.Response;

public class ServerExceptionHandler<T extends Exception> implements ExceptionHandler<T> {

    private final int code;

    /**
     * @param code The HTTP response code
     */
    public ServerExceptionHandler(int code) {
        this.code = code;
    }

    @Override
    public void handle(T type, Request request, Response response) {
        if (type.getCause() != null) {
            type.printStackTrace();
        }
        response.status(code);
        response.body(new Gson().toJson(Map.of("message", type.getMessage())));
    }
}
