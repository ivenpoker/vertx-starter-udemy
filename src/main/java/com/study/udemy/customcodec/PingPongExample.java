package com.study.udemy.customcodec;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingPongExample {
    private static final Logger LOGGER = LoggerFactory.getLogger(PingPongExample.class);

    public static void main(String[] args) {
        var vertx = Vertx.vertx();
        vertx.deployVerticle(new PingVerticle()).onFailure(throwable -> handleDeploymentFailure(PingVerticle.class, throwable));
        vertx.deployVerticle(new PongVerticle()).onFailure(throwable -> handleDeploymentFailure(PongVerticle.class, throwable));
    }

    static class PingVerticle extends AbstractVerticle {
        private static final Logger LOGGER = LoggerFactory.getLogger(PingVerticle.class);
        static final String ADDRESS = PingVerticle.class.getName();

        @Override
        public void start(Promise<Void> startPromise) throws Exception {
            startPromise.complete();
            var eventBus = vertx.eventBus();
            var message = new Ping("Hello", true);
            LOGGER.debug("Sending: {}", message);
            eventBus.<Pong>request(ADDRESS, message)
                .onSuccess(reply -> LOGGER.debug("Response: {}", reply.body()))
                .onFailure(error -> LOGGER.error("Response failure: {}", error.getMessage()));
        }
    }

    static class PongVerticle extends AbstractVerticle {
        private static final Logger LOGGER = LoggerFactory.getLogger(PongVerticle.class);

        @Override
        public void start(Promise<Void> startPromise) throws Exception {
            startPromise.complete();
            vertx.eventBus().<Ping>consumer(PingVerticle.ADDRESS, message -> {
                    LOGGER.debug("Received Message: {}", message.body());
                    message.reply(new Pong(0));
                })
                .exceptionHandler(error -> LOGGER.error("Consumption at {} failed: {}",
                    PingVerticle.ADDRESS, error.getMessage()));
        }
    }

    private static <T extends AbstractVerticle> void handleDeploymentFailure(Class<T> clazz, Throwable throwable) {
        LOGGER.error("Failed deploying %s".formatted(clazz.getSimpleName()), throwable);
    }
}
