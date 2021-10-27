package com.study.udemy.json;

import com.study.udemy.json.domain.Person;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.Map;

class TestJsonObjectExample {
    @Test
    @DisplayName("Ensure Json Objects Can Be Mapped")
    void jsonObjectCanBeMapped() {
        var jsonObj = new JsonObject();
        jsonObj.put("id", 1);
        jsonObj.put("name", "Alice");
        jsonObj.put("loves_vertx", true);

        var encoded = jsonObj.encode();
        Assertions.assertEquals("{\"id\":1,\"name\":\"Alice\",\"loves_vertx\":true}", encoded);

        var decodedJsonObject = new JsonObject(encoded);
        Assertions.assertEquals(jsonObj, decodedJsonObject);
    }

    @Test
    @DisplayName("Ensure Json Object Can Be Created From Map")
    void jsonObjectCanBeCreatedFromMap() {
        final Map<String, Object> myMap = new HashMap<>();
        myMap.put("id", 1);
        myMap.put("name", "Alice");
        myMap.put("loves_vertx", true);

        var jsonObj = new JsonObject(myMap);
        Assertions.assertEquals(myMap, jsonObj.getMap());
        Assertions.assertEquals("Alice", jsonObj.getString("name"));
        Assertions.assertEquals(true, jsonObj.getBoolean("loves_vertx"));
    }

    @Test
    @DisplayName("Ensure JsonArray Can Be Mapped")
    void jsonArrayCanBeMapped() {
        var jsonArray = new JsonArray();
        jsonArray
            .add(new JsonObject().put("id", 1))
            .add(new JsonObject().put("id", 2))
            .add(new JsonObject().put("id", 3))
            .add("randomValue");
        Assertions.assertEquals("[{\"id\":1},{\"id\":2},{\"id\":3},\"randomValue\"]", jsonArray.encode());
    }

    @Test
    @DisplayName("Can Map Java Object To Json Object")
    void canMapJavaObjects() {
        var alice = new Person(1, "Alice", true);
        var aliceJsonObj = JsonObject.mapFrom(alice);

        Assertions.assertEquals(alice.getId(), aliceJsonObj.getInteger("id"));
        Assertions.assertEquals(alice.getName(), aliceJsonObj.getString("name"));
        Assertions.assertEquals(alice.isLovesVertx(), aliceJsonObj.getBoolean("lovesVertx"));
    }

    @Test
    @DisplayName("Can Convert Json Object To Java Object")
    void canMapJsonObjectToJavaObject() {
        var alice = new Person(1, "Alice", true);
        var aliceJsonObj = JsonObject.mapFrom(alice);

        var person = aliceJsonObj.mapTo(Person.class);

        Assertions.assertEquals(person.getId(), alice.getId());
        Assertions.assertEquals(person.getName(), alice.getName());
        Assertions.assertEquals(person.isLovesVertx(), alice.isLovesVertx());
    }
}
