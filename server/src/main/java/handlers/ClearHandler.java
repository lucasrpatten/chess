package handlers;

import dataaccess.DataAccess;
import model.EmptyRequest;
import service.ClearService;
import service.ServerException;

public class ClearHandler extends RequestHandler<EmptyRequest> {

    public ClearHandler(DataAccess dataAccess) {
        super(dataAccess);
    }

    @Override
    protected Class<EmptyRequest> getRequestClass() {
        return null;
    }

    @Override
    protected Object getServiceResponse(DataAccess dataAccess, EmptyRequest request, String token)
            throws ServerException {
        new ClearService(dataAccess).clear();
        return null;
    }
}
