package handlers;

import java.net.HttpURLConnection;

import com.google.gson.Gson;

import dataaccess.DataAccess;
import service.ServerException;
import spark.Request;
import spark.Response;
import spark.Route;

public abstract class RequestHandler<T> implements Route {
    private final DataAccess dataAccess;

    public RequestHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    /**
     * Handles the HTTP request and generates a response.
     *
     * @param request  The HTTP request object.
     * @param response The HTTP response object.
     * @return A JSON string representing the response object.
     * @throws ServerException If there is an error processing the request.
     */
    @Override
    public Object handle(Request request, Response response) throws ServerException {
        Gson serializer = new Gson();
        String token = request.headers("Authorization");

        T reqObj = null;
        Class<T> reqCls = getRequestClass();
        if (reqCls != null) {
            reqObj = serializer.fromJson(request.body(), reqCls);
        }
        Object res = getServiceResponse(dataAccess, reqObj, token);
        response.status(HttpURLConnection.HTTP_OK);
        return serializer.toJson(res);

    }

    /**
     * Gets the class of the request object that this handler will process
     * 
     * @return The class of the request object that this handler will process
     */
    protected abstract Class<T> getRequestClass();

    /**
     * Processes the request and returns the service response
     * 
     * @param dataAccess Data access object used to interact with the database
     * @param request    The request object
     * @param token      The authorization token
     * @return The response object
     * @throws ServerException If there is an error processing the request
     */
    protected abstract Object getServiceResponse(DataAccess dataAccess, T request, String token) throws ServerException;
}
