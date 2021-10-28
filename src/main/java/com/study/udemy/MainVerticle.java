package com.study.udemy;

import com.study.udemy.restapi.AssetsRestApi;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

    public static void main(String[] args) {
        var vertx = Vertx.vertx();
        vertx.deployVerticle(new MainVerticle());

        vertx.exceptionHandler(MainVerticle::handleException);
        vertx.deployVerticle(new MainVerticle())
            .onFailure(MainVerticle::handleDeploymentFailure)
            .onSuccess(id -> handleSuccessfullDeployment(MainVerticle.class, id));

    }

    static <T extends AbstractVerticle> void handleSuccessfullDeployment(Class<T> clazz, String deployedId) {
        var message = "Deployed %s successfully (id: %s)".formatted(clazz.getSimpleName(), deployedId);
        LOGGER.debug(message);
    }

    static void handleException(Throwable throwable) {
        LOGGER.error("Unhandled Exception", throwable);
    }

    static void handleDeploymentFailure(Throwable throwable) {
        LOGGER.error("Verticle deployment failed", throwable);
    }

    static void handleDeploymentFailure(AbstractVerticle verticle, Throwable throwable) {
        var message = "Failed to deploy %s".formatted(verticle.getClass().getSimpleName());
        LOGGER.error(message, throwable);
    }

    static void routeExceptionHandler(Throwable throwable) {
        LOGGER.debug("Exception on route", throwable);
    }

    static void apiRequestFailureHandler(RoutingContext routingContext) {
        if (routingContext.response().ended()) {
            return;
        }
        routingContext.response()
            .setStatusCode(500)
            .end(new JsonObject().put("message", "Something went wrong :(").toBuffer());
    }

    static void handleServerFailure(Throwable throwable) {
        LOGGER.error("Failed to start server", throwable);
    }



    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        LOGGER.info("in start(...)");

        final Router restApi = Router.router(vertx);

        AssetsRestApi.create(restApi);

        restApi.route().handler(routingContext ->
                routingContext.response().end(
                        new JsonObject().put("error", "NotFound").encode()));

        // default failure handler for all unhandle requests
        restApi.route().failureHandler(MainVerticle::apiRequestFailureHandler);

        vertx.createHttpServer()
            .requestHandler(restApi)
            .exceptionHandler(MainVerticle::routeExceptionHandler)
            .listen(9999)
            .onSuccess(_httpServer -> {
                LOGGER.info("HTTP Server started on port 9999");
                startPromise.complete();
            })
            .onFailure(MainVerticle::handleServerFailure);
    }
}
