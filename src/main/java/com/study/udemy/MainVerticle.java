package com.study.udemy;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

    public static void main(String[] args) {
        var vertx = Vertx.vertx();
        vertx.deployVerticle(new MainVerticle());
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        LOGGER.info("in start(...)");
        vertx.createHttpServer()
            .requestHandler(httpRequest ->
                httpRequest.response()
                    .putHeader("content-type", "text/plain")
                    .end("Hello from Vert.x!"))
            .listen(9999, http -> {
                if (http.succeeded()) {
                    startPromise.complete();
                    LOGGER.info("HTTP server started on port 8888");
                } else {
                    LOGGER.error("HTTP Server Failed To Start: {}", http.cause().getMessage());
                    startPromise.fail(http.cause());
                }
            });
    }
}
