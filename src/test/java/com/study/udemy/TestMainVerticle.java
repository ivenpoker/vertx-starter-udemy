package com.study.udemy;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(VertxExtension.class)
class TestMainVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestMainVerticle.class);

    private static WebClient webClient;
    @BeforeAll
    static void testSetup(Vertx vertx) {
        webClient = WebClient.create(vertx);
    }

    @BeforeEach
    void deployVerticle(Vertx vertx, VertxTestContext testContext) {
        vertx.deployVerticle(new MainVerticle())
                .onSuccess(id -> testContext.completeNow())
                .onFailure(testContext::failNow);
    }

    @Test
    void verticleDeployed(Vertx vertx, VertxTestContext testContext) throws Throwable {
        webClient.get(9999, "localhost", "/")
            .send()
            .onSuccess(httpResp -> {
                LOGGER.debug("http response: {}", httpResp.bodyAsString());
                testContext.completeNow();
            })
            .onFailure(testContext::failNow);
    }
}
