package handlers;

import dataaccess.DataAccess;
import model.CreateGameRequest;
import service.GameService;
import service.ServerException;

public class CreateGameHandler extends RequestHandler<CreateGameRequest> {

    public CreateGameHandler(DataAccess dataAccess) {
        super(dataAccess);
    }

    @Override
    protected Class<CreateGameRequest> getRequestClass() {
        return CreateGameRequest.class;
    }

    @Override
    protected Object getServiceResponse(DataAccess dataAccess, CreateGameRequest request, String token)
            throws ServerException {
        return new GameService(dataAccess).create(request);
    }

}
