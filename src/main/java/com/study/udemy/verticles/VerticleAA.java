package com.study.udemy.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerticleAA extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(VerticleAA.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        LOGGER.debug("Start {}", getClass().getName());
        startPromise.complete();
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        LOGGER.debug("Stopped: {}", VerticleAA.class.getName());
        stopPromise.complete();
    }
}
