package com.study.udemy.commons;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vertx.core.Future;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public interface JsonMapper {
    Logger LOGGER = LoggerFactory.getLogger(JsonMapper.class);
    String EXCEPTION_THROWN_MESSAGE_TEMPLATE = "The following exception was thrown: {}";

    static void loadModules() {
        DatabindCodec.mapper().registerModule(new JavaTimeModule());
        DatabindCodec.mapper().configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        DatabindCodec.mapper()
            .configure(MapperFeature.INFER_CREATOR_FROM_CONSTRUCTOR_PROPERTIES, false);
        DatabindCodec.mapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        DatabindCodec.mapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    static ObjectMapper getMapper() {
        return DatabindCodec.mapper();
    }

    static <T> Future<T> fromJson(@NotNull String jsonString, @NotNull Class<T> clazz) {
        LOGGER.trace("fromJson(..) => jsonString: {}, clazz: {}", clazz, jsonString);
        if (nonNulls(List.of(clazz, jsonString))) {
            LOGGER.debug("One or more of the following parameters is null jsonString or clazz");
            return Future.failedFuture(new Exception("Deserialization Error"));
        }

        JsonObject jsonObject;
        try {
            jsonObject = new JsonObject(jsonString);
        } catch (DecodeException ex) {
            LOGGER.debug("Invalid jsonString object value");
            LOGGER.trace(EXCEPTION_THROWN_MESSAGE_TEMPLATE, ex.getMessage(), ex);
            return Future.failedFuture(new Exception("Deserialization Error"));
        }

        return fromJson(jsonObject, clazz);
    }

    static <T> Future<T> fromJson(@NotNull JsonObject json, @NotNull Class<T> clazz) {
        LOGGER.trace("fromJson(..) => json: {}, clazz: {}", clazz, json);
        if (nonNulls(List.of(clazz, json))) {
            LOGGER.debug("One or more of the following parameters is null json or clazz");
            return Future.failedFuture(new Exception("Deserialization Error"));
        }

        try {
            return Future.succeededFuture(json.mapTo(clazz));
        } catch (IllegalArgumentException ex) {
            LOGGER.debug("Unable to deserialize {} to {}", json, clazz);
            LOGGER.trace(EXCEPTION_THROWN_MESSAGE_TEMPLATE, ex.getMessage(), ex);
            return Future.failedFuture(new Exception("Deserialization Error"));
        }
    }

    static <T> Future<JsonObject> fromPojo(T pojo) {
        LOGGER.trace("fromPojo(..) => pojo: {}", pojo);
        try {
            return Future.succeededFuture(JsonObject.mapFrom(pojo));
        } catch (IllegalArgumentException ex) {
            LOGGER.debug("Unable to serialize {} to JsonObject", pojo);
            LOGGER.trace(EXCEPTION_THROWN_MESSAGE_TEMPLATE, ex.getMessage(), ex);
            return Future.failedFuture(new Exception("Deserialization Error"));
        }
    }

    static <T> Future<List<T>> fromJsonArray(@NotNull String json, @NotNull Class<T> clazz) {
        LOGGER.trace("fromJsonArray(..) => json: {}, clazz: {}", clazz, json);
        if (nonNulls(List.of(clazz, json))) {
            LOGGER.debug("One or more of the following parameters is null json or clazz");
            return Future.failedFuture(new Exception("Deserialization Error"));
        }

        JsonArray jsonArray;
        try {
            jsonArray = new JsonArray(json);
        } catch (DecodeException ex) {
            LOGGER.debug("Invalid json array value");
            LOGGER.trace(EXCEPTION_THROWN_MESSAGE_TEMPLATE, ex.getMessage(), ex);
            return Future.failedFuture(new Exception("Deserialization Error"));
        }

        return fromJsonArray(jsonArray, clazz);
    }

    static <T> Future<List<T>> fromJsonArray(@NotNull JsonArray json, @NotNull Class<T> clazz) {
        LOGGER.trace("fromJsonArray(..) => json: {}, clazz: {}", json, clazz);
        if (nonNulls(List.of(clazz, json))) {
            LOGGER.debug("One or more of the following parameters is null json or clazz");
            return Future.failedFuture(new Exception("Deserialization Error"));
        }

        try {
            ObjectMapper mapper = getMapper();
            CollectionType collectionType =
                mapper.getTypeFactory().constructCollectionType(List.class, clazz);
            return Future.succeededFuture(mapper.readValue(json.encode(), collectionType));
        } catch (IllegalArgumentException | JsonProcessingException ex) {
            LOGGER.debug("Unable to deserialize {} to {}", json, clazz);
            LOGGER.trace(EXCEPTION_THROWN_MESSAGE_TEMPLATE, ex.getMessage(), ex);
            return Future.failedFuture(new Exception("Deserialization Error"));
        }
    }

    static <T> Future<JsonArray> fromList(List<T> pojos) {
        LOGGER.trace("fromList(..)");

        if (pojos == null || pojos.isEmpty()) {
            return Future.succeededFuture(new JsonArray());
        }

        try {
            String jsonArrayAsString = getMapper().writeValueAsString(pojos);
            return Future.succeededFuture(new JsonArray(jsonArrayAsString));
        } catch (IllegalArgumentException | JsonProcessingException ex) {
            LOGGER.debug("Unable to serialize to JsonArray");
            LOGGER.trace(EXCEPTION_THROWN_MESSAGE_TEMPLATE, ex.getMessage(), ex);
            return Future.failedFuture(new Exception("Deserialization Error"));
        }
    }

    private static boolean nonNulls(Collection<Object> objects) {
        if (objects == null) {
            return true;
        }

        return !objects.stream().allMatch(Objects::nonNull);
    }

}
