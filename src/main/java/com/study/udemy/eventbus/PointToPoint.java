package com.study.udemy.eventbus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class PointToPoint {
    public static void main(String[] args) {
        var vertx = Vertx.vertx();
        vertx.deployVerticle(new Sender());
        vertx.deployVerticle(new Receiver());
    }

    static class Sender extends AbstractVerticle {
        static final String ADDRESS = Sender.class.getName();

        @Override
        public void start(Promise<Void> startPromise) throws Exception {
            startPromise.complete();
            vertx.setPeriodic(Duration.ofSeconds(1).toMillis(), id -> vertx.eventBus().send(ADDRESS, "Person"));
        }
    }

    static class Receiver extends AbstractVerticle {
        private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);

        @Override
        public void start(Promise<Void> startPromise) throws Exception {
            startPromise.complete();
            vertx.eventBus().<String>consumer(Sender.ADDRESS, message -> LOGGER.debug("Received: {}", message.body()));
        }
    }
}
