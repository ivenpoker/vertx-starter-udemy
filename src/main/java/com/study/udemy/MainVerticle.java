package com.study.udemy;

import com.study.udemy.restapi.AssetsRestApi;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
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

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        LOGGER.info("in start(...)");

        final Router restApi = Router.router(vertx);

        restApi.route().failureHandler(routingContext -> {
            if (routingContext.response().ended()) {
                return;
            }
            routingContext.response()
                .setStatusCode(500)
                .end(new JsonObject().put("message", "Something went wrong :(").toBuffer());
        });

        AssetsRestApi.create(restApi);

        vertx.createHttpServer()
            .requestHandler(restApi)
            .exceptionHandler(MainVerticle::routeExceptionHandler)
            .listen(9999, http -> {
                if (http.succeeded()) {
                    startPromise.complete();
                    LOGGER.info("HTTP server started on port 9999");
                } else {
                    LOGGER.error("HTTP Server Failed To Start: {}", http.cause().getMessage());
                    startPromise.fail(http.cause());
                }
            });
    }
}
