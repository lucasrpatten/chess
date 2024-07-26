package handlers;

import dataaccess.DataAccess;
import model.LoginRequest;
import service.ServerException;
import service.UserService;

public class LogoutHandler extends RequestHandler<LoginRequest> {

    public LogoutHandler(DataAccess dataAccess) {
        super(dataAccess);
    }

    @Override
    public Class<LoginRequest> getRequestClass() {
        return LoginRequest.class;
    }

    @Override
    public Object getServiceResponse(DataAccess dataAccess, LoginRequest request, String token) throws ServerException {
        new UserService(dataAccess).logout(token);
        return null;
    }
}
