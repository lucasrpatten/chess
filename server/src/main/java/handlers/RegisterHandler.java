package handlers;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.UserService;

public class RegisterHandler extends RequestHandler<UserData> {
    public RegisterHandler(DataAccess dataAccess) {
        super(dataAccess);
    }

    @Override
    protected Class<UserData> getRequestClass() {
        return UserData.class;
    }

    @Override
    protected AuthData getServiceResponse(DataAccess dataAccess, UserData request, String token)
            throws DataAccessException {
        return new UserService(dataAccess).register(request);
    }

}
