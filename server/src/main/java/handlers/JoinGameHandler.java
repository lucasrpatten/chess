package handlers;

import dataaccess.DataAccess;
import model.CreateGameRequest;
import model.JoinGameRequest;
import service.GameService;
import service.ServerException;

public class JoinGameHandler extends RequestHandler<JoinGameRequest> {

    public JoinGameHandler(DataAccess dataAccess) {
        super(dataAccess);
    }

    @Override
    protected Class<JoinGameRequest> getRequestClass() {
        return JoinGameRequest.class;
    }

    @Override
    protected Object getServiceResponse(DataAccess dataAccess, JoinGameRequest request, String token)
            throws ServerException {
        return new GameService(dataAccess).join(request.playerColor(), request.gameID(), token);
    }

}
