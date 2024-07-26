package handlers;

import dataaccess.DataAccess;
import model.EmptyRequest;
import service.GameService;
import service.ServerException;

public class ListGamesHandler extends RequestHandler<EmptyRequest> {

    public ListGamesHandler(DataAccess dataAccess) {
        super(dataAccess);
    }

    @Override
    protected Class<EmptyRequest> getRequestClass() {
        return EmptyRequest.class;
    }

    @Override
    protected Object getServiceResponse(DataAccess dataAccess, EmptyRequest request, String token)
            throws ServerException {
        return new GameService(dataAccess).list(token);
    }

}
