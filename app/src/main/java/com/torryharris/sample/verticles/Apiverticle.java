package com.torryharris.sample.verticles;

import com.torryharris.sample.controller.Usercontroller;
import com.torryharris.sample.model.Response;
import com.torryharris.sample.model.User;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Apiverticle extends AbstractVerticle {
    private static final Logger Logger = LogManager.getLogger(Apiverticle.class);
    private static Router router;
    private Usercontroller usercontroller;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        usercontroller = new Usercontroller(vertx);
        router = Router.router(vertx);
        router.route().handler(BodyHandler.create(false));
        router.get("/user").produces("*/json")
                .handler(routingContext -> {
                    usercontroller.getuser().future().onSuccess(response -> Response(routingContext, response));
                });

        router.post("/user").produces("application/json")
                .handler(routingContext -> {
                    User user = Json.decodeValue(routingContext.body().buffer(), User.class);
                    usercontroller.insertuser(user).future().onSuccess(response -> Response(routingContext, response));
                    Logger.info(routingContext.body().asJsonObject().encode());
                    JsonObject jsonObject = new JsonObject().put("message", "user record added");
                    routingContext.response().putHeader("content-type", "application/json").end(jsonObject.encode());
                });
        router.put("/user/:id").produces("application/json").handler(routingContext->{
           User user=Json.decodeValue(routingContext.body().buffer(),User.class);
           long id= Long.parseLong(routingContext.request().getParam("id"));
           usercontroller.updateUser(id).future().onSuccess(response -> Response(routingContext, response));
        });

        HttpServerOptions options = new HttpServerOptions().setTcpKeepAlive(true);
        vertx.createHttpServer(options)
                .exceptionHandler(Logger::catching)
                .requestHandler(router)
                .listen(8082)
                .onSuccess(httpServer -> {
                    Logger.info("Server started on port 8082");
                    startPromise.tryComplete();
                })
                .onFailure(startPromise::tryFail);
    }

    private void Response(RoutingContext routingContext, Response response) {
        response.getHeaders().stream()
                .forEach(entry -> routingContext.response().putHeader(entry.getKey(), entry.getValue().toString()));
        routingContext.response().setStatusCode(response.getStatusCode())
                .end(response.getResponseBody());
    }
}



