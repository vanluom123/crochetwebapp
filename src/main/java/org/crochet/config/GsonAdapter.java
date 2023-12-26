package org.crochet.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.swagger.v3.core.util.Json;

import java.lang.reflect.Type;

public class GsonAdapter implements JsonSerializer<Json> {
    @Override
    public JsonElement serialize(Json src, Type typeOfSrc, JsonSerializationContext context) {
        return JsonParser.parseString(src.toString());
    }
}
