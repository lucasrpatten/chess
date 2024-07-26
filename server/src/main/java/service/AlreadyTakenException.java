package service;

public class AlreadyTakenException extends ServerException {
    public AlreadyTakenException(String msg) {
        super(msg);
    }
}
