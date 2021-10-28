package com.study.udemy.restapi;

import com.study.udemy.commons.JsonMapper;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class AssetsRestApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssetsRestApi.class);
    private final Router _router;

    private AssetsRestApi(Router router) {
        this._router = router;
//        setupRoutes(
//            Map.of("/assets",
//                Map.of(
//                    HttpMethod.GET, this::handleGET,
//                    HttpMethod.POST, this::handleGET)),
//            Map.of("/assets/{id}",
//                Map.of(
//                    HttpMethod.GET, this::handleGET,
//                    HttpMethod.PUT, this::handleGET)));
        setupRoutes();
    }

    private void handleGET(RoutingContext routingContext) {

    }

    private void setupRoutes() {
        _router.get("/assets").handler(routingContext -> {
            var response = new JsonArray();
            var tmpJsonObj =
                new JsonObject()
                    .put("id", UUID.randomUUID().toString())
                    .put("name", "2839");
            response
                .add(new Asset(UUID.randomUUID(), "AAPL"))
                .add(new Asset(UUID.randomUUID(), "AMZN"))
                .add(new Asset(UUID.randomUUID(), "NFLX"))
                .add(new Asset(UUID.randomUUID(), "TSLA"))
                .add(JsonMapper.fromJson(tmpJsonObj, Asset.class).result());
            LOGGER.info("Path {} responds with {}", routingContext.normalizedPath(), response.encode());
            routingContext.response().end(response.toBuffer());
        });

        _router.post("/assets").handler(routingContext -> {
            var body = routingContext.getBodyAsJson();
        });
    }

    public static AssetsRestApi create(Router parent) {
        return new AssetsRestApi(parent);
    }

}
