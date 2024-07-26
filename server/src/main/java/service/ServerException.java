package service;

public class ServerException extends Exception {
    public ServerException() {
    }

    public ServerException(String message) {
        super(message);
    }

    public ServerException(Throwable cause) {
        super(cause);
    }
}
