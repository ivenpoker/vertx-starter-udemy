package com.study.udemy.eventbus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class PublishSubscribe {
    public static void main(String[] args) {
        var vertx = Vertx.vertx();
        vertx.deployVerticle(new Publish());
        vertx.deployVerticle(new Subscriber1());
        vertx.deployVerticle(new Subscriber2());
    }

    static class Publish extends AbstractVerticle {
        static final String ADDRESS = Publish.class.getName();

        @Override
        public void start(Promise<Void> startPromise) throws Exception {
            startPromise.complete();
            vertx.setPeriodic(Duration.ofSeconds(1).toMillis(), id -> {
                vertx.eventBus().publish(ADDRESS, "Published message for all");
            });
        }
    }

    static class Subscriber1 extends AbstractVerticle {
        private static final Logger LOGGER = LoggerFactory.getLogger(Subscriber1.class);

        @Override
        public void start(Promise<Void> startPromise) throws Exception {
            startPromise.complete();
            vertx.eventBus().<String>consumer(Publish.ADDRESS, message -> {
                LOGGER.debug("Sub1 Recieved: {}", message.body());
            });
        }
    }

    static class Subscriber2 extends AbstractVerticle {
        private static final Logger LOGGER = LoggerFactory.getLogger(Subscriber2.class);

        @Override
        public void start(Promise<Void> startPromise) throws Exception {
            startPromise.complete();
            vertx.eventBus().<String>consumer(Publish.ADDRESS, message -> {
                LOGGER.debug("Sub2 Recieved: {}", message.body());
            });
        }
    }
}
