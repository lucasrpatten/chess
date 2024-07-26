package handlers;

import dataaccess.DataAccess;
import model.LoginRequest;
import service.ServerException;
import service.UserService;

public class LoginHandler extends RequestHandler<LoginRequest> {

    public LoginHandler(DataAccess dataAccess) {
        super(dataAccess);
    }

    @Override
    public Class<LoginRequest> getRequestClass() {
        return LoginRequest.class;
    }

    @Override
    public Object getServiceResponse(DataAccess dataAccess, LoginRequest request, String token) throws ServerException {
        return new UserService(dataAccess).login(request);
    }
}
