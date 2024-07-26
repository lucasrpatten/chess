package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;

public class ClearService {
    private final DataAccess dataAccess;

    public ClearService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clear() throws ServerException {
        try {
            dataAccess.getAuthDAO().clear();
            dataAccess.getUserDAO().clear();
            dataAccess.getGameDAO().clear();
        }
        catch (DataAccessException e) {
            throw new ServerException(e);
        }
    }
}
