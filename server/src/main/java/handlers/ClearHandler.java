package handlers;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import service.ClearService;

public class ClearHandler extends RequestHandler<Void> {

    public ClearHandler(DataAccess dataAccess) {
        super(dataAccess);
    }

    @Override
    protected Class<Void> getRequestClass() {
        return null;
    }

    @Override
    protected Object getServiceResponse(DataAccess dataAccess, Void request, String token) throws DataAccessException {
        new ClearService(dataAccess).clear();
        return null;
    }
}
