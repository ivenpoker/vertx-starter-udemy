package com.study.udemy.commons;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.ReadContext;
import com.jayway.jsonpath.WriteContext;
import io.vertx.core.json.JsonObject;

import java.util.Map;
import java.util.Optional;

public interface JsonUtils {
    static boolean containsKey(JsonObject json, String keyPath) {
        ReadContext readContext = JsonPath.parse(json.encode());
        return containsKey(readContext, keyPath);
    }

    static boolean containsKey(ReadContext readContext, String keyPath) {
        try {
            return Optional.ofNullable(readContext.read(keyPath)).isPresent();
        } catch (PathNotFoundException ex) {
            return false;
        }
    }

    static <T> Optional<T> value(JsonObject json, String keyPath) {
        return value(JsonPath.parse(json.encode()), keyPath);
    }

    static <T> Optional<T> value(ReadContext readContext, String keyPath) {
        return containsKey(readContext, keyPath) ? Optional.ofNullable(readContext.read(keyPath)) :
            Optional.empty();
    }

    static JsonObject write(JsonObject json, String jsonPath, Object value) {
        WriteContext writeContext = JsonPath.parse(json.encode());
        DocumentContext documentContext = writeContext.set(jsonPath, value);
        return new JsonObject(documentContext.jsonString());
    }

    static JsonObject write(JsonObject json, Map<String, Object> keyValues) {
        WriteContext writeContext = JsonPath.parse(json.encode());
        keyValues.forEach(writeContext::set);
        return new JsonObject(writeContext.jsonString());
    }

    static JsonObject put(JsonObject json, String path, String key,  Object value) {
        WriteContext writeContext = JsonPath.parse(json.encode());
        DocumentContext documentContext = writeContext.put(JsonPath.compile(path), key, value);
        return new JsonObject(documentContext.jsonString());
    }
}
