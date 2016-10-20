package net.wezu.jxg.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class StringConverter implements JsonSerializer<String>, JsonDeserializer<String> {
    @Override
    public String deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return jsonElement.getAsJsonPrimitive().getAsString();
    }

    @Override
    public JsonElement serialize(String s, Type type, JsonSerializationContext jsonSerializationContext) {
        if (s == null) {
            return new JsonPrimitive("");
        } else {
            return new JsonPrimitive(s);
        }
    }
}
