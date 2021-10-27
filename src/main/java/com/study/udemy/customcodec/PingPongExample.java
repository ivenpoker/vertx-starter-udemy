package com.study.udemy.customcodec;

import com.study.udemy.commons.JsonMapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
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
            eventBus.<JsonObject>request(ADDRESS, JsonObject.mapFrom(message))
                .onSuccess(reply -> JsonMapper.fromJson(reply.body(), Pong.class)
                    .flatMap(pong -> {
                        LOGGER.debug("Response: {}", pong);
                        return Future.succeededFuture(pong);
                    }))
                .onFailure(error -> LOGGER.error("Response failure: {}", error.getMessage()));
        }
    }

    static class PongVerticle extends AbstractVerticle {
        private static final Logger LOGGER = LoggerFactory.getLogger(PongVerticle.class);

        @Override
        public void start(Promise<Void> startPromise) throws Exception {
            startPromise.complete();
            vertx.eventBus().<JsonObject>consumer(PingVerticle.ADDRESS, message ->
                    JsonMapper.fromJson(message.body(), Ping.class)
                        .flatMap(ping -> {
                            LOGGER.debug("Received Message: {}", ping);
                            return Future.succeededFuture(ping);
                        })
                        .onSuccess(_noUse -> message.reply(JsonObject.mapFrom(new Pong(0)))))
                .exceptionHandler(error -> LOGGER.error("Consumption at {} failed: {}",
                    PingVerticle.ADDRESS, error.getMessage()));
        }
    }

    private static <T extends AbstractVerticle> void handleDeploymentFailure(Class<T> clazz, Throwable throwable) {
        LOGGER.error("Failed deploying %s".formatted(clazz.getSimpleName()), throwable);
    }
}
