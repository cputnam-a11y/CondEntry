package io.github.cputnama11y.conditionalentrypoints.impl.v1.utils;

import fish.cichlidmc.tinyjson.value.JsonValue;
import fish.cichlidmc.tinyjson.value.composite.JsonArray;
import fish.cichlidmc.tinyjson.value.composite.JsonObject;
import fish.cichlidmc.tinyjson.value.primitive.JsonBool;
import fish.cichlidmc.tinyjson.value.primitive.JsonNull;
import fish.cichlidmc.tinyjson.value.primitive.JsonNumber;
import fish.cichlidmc.tinyjson.value.primitive.JsonString;
import net.fabricmc.loader.api.metadata.CustomValue;

public class CustomValueUtil {
    public static JsonValue toTinyJson(CustomValue value) {
        return switch (value.getType()) {
            case BOOLEAN -> new JsonBool(value.getAsBoolean());
            case NUMBER -> new JsonNumber(value.getAsNumber().doubleValue());
            case OBJECT -> {
                var object = new JsonObject();
                for (var entry : value.getAsObject())
                    object.put(entry.getKey(), toTinyJson(entry.getValue()));
                yield object;
            }
            case STRING -> new JsonString(value.getAsString());
            case NULL -> new JsonNull();
            case ARRAY -> {
                var a = new JsonArray();
                for (var item : value.getAsArray()) {
                    a.add(toTinyJson(item));
                }
                yield a;
            }
        };
    }
}
