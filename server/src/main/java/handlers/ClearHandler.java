package handlers;

import dataaccess.DataAccess;
import service.ClearService;
import service.ServerException;

public class ClearHandler extends RequestHandler<Void> {

    public ClearHandler(DataAccess dataAccess) {
        super(dataAccess);
    }

    @Override
    protected Class<Void> getRequestClass() {
        return null;
    }

    @Override
    protected Object getServiceResponse(DataAccess dataAccess, Void request, String token) throws ServerException {
        new ClearService(dataAccess).clear();
        return null;
    }
}
