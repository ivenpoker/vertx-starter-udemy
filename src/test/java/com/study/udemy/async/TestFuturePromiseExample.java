package com.study.udemy.async;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

@ExtendWith(VertxExtension.class)
class TestFuturePromiseExample {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestFuturePromiseExample.class);

    @Test
    @DisplayName("Promise Succeeds")
    void promiseSucceeds(Vertx vertx, VertxTestContext context) {
        final Promise<String> promise = Promise.promise();
        LOGGER.debug("Start");

        vertx.setTimer(Duration.ofMillis(500).toMillis(), id -> {
            promise.complete("Success");
            LOGGER.debug("Success");
            context.completeNow();
        });
        LOGGER.debug("End");
    }

    @Test
    @DisplayName("Promise Fails")
    void promiseFials(Vertx vertx, VertxTestContext context) {
        final Promise<String> promise = Promise.promise();
        LOGGER.debug("Start");
        vertx.setTimer(Duration.ofMillis(500).toMillis(), id -> {
            promise.fail(new RuntimeException("Failed"));
            LOGGER.debug("Failed");
            context.completeNow();
        });
        promise.future().onFailure(error -> context.completeNow());
    }

    @Test
    @DisplayName("Future Succeeds")
    void futureSucceeds(Vertx vertx, VertxTestContext context) {
        final Promise<String> promise = Promise.promise();
        LOGGER.debug("Start");

        vertx.setTimer(Duration.ofMillis(500).toMillis(), id -> {
            promise.complete("Success");
            LOGGER.debug("Timer done.");
        });
        final Future<String> future = promise.future();
        future
            .onSuccess(result -> {
                LOGGER.debug("Result: {}", result);
                context.completeNow();
            })
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("Future Failure")
    void futureFails(Vertx vertx, VertxTestContext context) {
        final Promise<String> promise = Promise.promise();
        LOGGER.debug("Start");

        vertx.setTimer(Duration.ofMillis(500).toMillis(), id -> {
            promise.fail(new RuntimeException("Failed!"));
            LOGGER.debug("Timer done.");
        });
        final Future<String> future = promise.future();
        future
            .onSuccess(result -> {
                LOGGER.debug("Result: {}", result);
                context.failNow(new RuntimeException("Shouldn't Succeed"));
            })
            .onFailure(error -> {
                LOGGER.debug("Error: {}", error.getMessage());
                context.completeNow();
            });
    }

    @Test
    @DisplayName("Future Map")
    void futureMap(Vertx vertx, VertxTestContext context) {
        final Promise<String> promise = Promise.promise();
        LOGGER.debug("Start");

        vertx.setTimer(Duration.ofMillis(500).toMillis(), id -> {
            promise.complete("Success");
            LOGGER.debug("Timer done.");
        });
        final Future<String> future = promise.future();
        future
            .map(asString -> {
                LOGGER.debug("Map String to JsonObject");
                return new JsonObject().put("key", asString);
            })
            .map(jsonObj -> new JsonArray().add(jsonObj))
            .onSuccess(result -> {
                LOGGER.debug("Result: {} of type {}", result, result.getClass().getSimpleName());
                context.completeNow();
            })
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("Future Coordination")
    void futureCoordination(Vertx vertx, VertxTestContext context) {
        vertx.createHttpServer()
            .requestHandler(request -> LOGGER.debug("{}", request))
            .listen(10_000)
            .onSuccess(httpServer -> {
                LOGGER.debug("Server started on port {}", httpServer.actualPort());
                context.completeNow();
            })
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("Future Composition")
    void futureComposition(Vertx vertx, VertxTestContext context) {
        var one = Promise.<Void>promise();
        var two = Promise.<Void>promise();
        var three = Promise.<Void>promise();

        var futureOne = one.future();
        var futureTwo = two.future();
        var futureThree = three.future();

        CompositeFuture.all(futureOne, futureTwo, futureThree)
            .onFailure(context::failNow)
            .onSuccess(result -> {
                LOGGER.debug("Success");
                context.completeNow();
            });

        // Complete futures
        vertx.setTimer(500, id -> {
            one.complete();
            two.complete();
            three.complete();
        });
    }

}
