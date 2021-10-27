package com.study.udemy.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerticleB extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(VerticleB.class);
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        LOGGER.debug("Start {}", getClass().getName());
        startPromise.complete();
    }
}
