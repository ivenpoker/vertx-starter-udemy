package com.study.udemy.worker;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerVerticle.class);

    public static void main(String[] args) {
        var vertx = Vertx.vertx();
        vertx.deployVerticle(new WorkerVerticle());
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        vertx.deployVerticle(new WorkerVerticle(),
            new DeploymentOptions()
                .setWorker(true)
                .setWorkerPoolSize(1)
                .setWorkerPoolName("my-worker-verticle"));
        startPromise.complete();
        executeBlockingCode();
    }

    private void executeBlockingCode() {
        vertx.executeBlocking(promise -> {
                LOGGER.debug("Executing blocking code");
                try {
                    Thread.sleep(5000);
                    promise.complete();
                } catch (InterruptedException exc) {
                    LOGGER.error("Failed: ", exc);
                    promise.fail(exc);
                }
            })
            .onSuccess(result -> LOGGER.debug("Blocking call done."))
            .onFailure(error -> LOGGER.error("Blocking call failed due to: {}", error.getMessage()));
    }
}
