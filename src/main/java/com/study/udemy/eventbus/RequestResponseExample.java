package com.study.udemy.eventbus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestResponseExample {
    public static void main(String[] args) {
        var vertx = Vertx.vertx();
        vertx.deployVerticle(new RequestVerticle());
        vertx.deployVerticle(new ResponseVerticle());
    }

    static class RequestVerticle extends AbstractVerticle {
        private static final Logger LOGGER = LoggerFactory.getLogger(RequestVerticle.class);
        static final String ADDRESS = "my.request.address";

        @Override
        public void start(Promise<Void> startPromise) throws Exception {
            startPromise.complete();
            var eventBus = vertx.eventBus();
            var message = "Hello World!";
            LOGGER.debug("Sending: {}", message);
            eventBus.<String>request(ADDRESS, "Hello World!")
                .onSuccess(reply -> LOGGER.debug("Response: {}", reply.body()))
                .onFailure(error -> LOGGER.error("Response failure: {}", error.getMessage()));
        }
    }

    static class ResponseVerticle extends AbstractVerticle {
        private static final Logger LOGGER = LoggerFactory.getLogger(ResponseVerticle.class);
        @Override
        public void start(Promise<Void> startPromise) throws Exception {
            startPromise.complete();
            vertx.eventBus().<String>consumer(RequestVerticle.ADDRESS, message -> {
                LOGGER.debug("Received Message: {}", message.body());
                message.reply("Received your message. Thanks!");
            });
        }
    }
}
