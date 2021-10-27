package com.study.udemy.eventloop;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class EventLoopExample extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventLoopExample.class);

    public static void main(String[] args) {
        var vertx = Vertx.vertx(
            new VertxOptions()
                .setMaxEventLoopExecuteTime(500)
                .setMaxEventLoopExecuteTimeUnit(TimeUnit.MICROSECONDS)
                .setBlockedThreadCheckInterval(1)
                .setBlockedThreadCheckIntervalUnit(TimeUnit.SECONDS)
                .setEventLoopPoolSize(4));

        vertx.deployVerticle(EventLoopExample.class,
            new DeploymentOptions()
                .setInstances(4));
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        LOGGER.debug("Start {}", getClass().getName());
        // Do not ido this inside a verticle
        // Thread.sleep(5000);
    }
}
