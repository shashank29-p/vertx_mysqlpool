package com.torryharris.sample.controller;

import com.torryharris.sample.App;
import com.torryharris.sample.dao.UserDAO;
import com.torryharris.sample.model.Response;
import com.torryharris.sample.model.User;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Usercontroller {
    private static final Logger LOGGER = LogManager.getLogger(Usercontroller.class);
    private final UserDAO userDAO;

    public Usercontroller(Vertx vertx) {
        userDAO = new UserDAO(vertx);
    }

    public Promise<Response> getuser() {
        Promise<Response> promise = Promise.promise();
        Response response = new Response();
        userDAO.getuser()
                .onSuccess(user -> {
                    response.setStatusCode(200)
                            .setResponseBody(Json.encode(user));
                    promise.tryComplete(response);
                }).onFailure(throwable -> {
                    response.setStatusCode(500);
                });
        return promise;
    }

    public Promise<Response> insertuser(User user) {
        Promise<Response> responsePromise = Promise.promise();
        userDAO.insertuser(user);
        return responsePromise;
    }
    public Promise<Response> updateUser(long id){
        Promise<Response> responsePromise=Promise.promise();
        userDAO.updateUser(id);
        return responsePromise;
    }
}