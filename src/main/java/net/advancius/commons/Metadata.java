package net.advancius.commons;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import net.advancius.AdvanciusClient;

import java.util.HashMap;
import java.util.Map;

public class Metadata {

    @Getter
    private final Map<String, Object> internal = new HashMap<>();

    public <T> Object setMetadata(String key, T value) {
        return internal.put(key, value);
    }

    public Object unsetMetadata(String key) {
        return internal.remove(key);
    }

    public boolean hasMetadata(String key) {
        return internal.containsKey(key) && internal.get(key) != null;
    }

    public boolean hasMetadata(String key, Object value) {
        return hasMetadata(key) && getMetadata(key).equals(value);
    }

    public <T> T getMetadata(String key) {
        return (T) internal.get(key);
    }

    public <T> T castMetadata(String key, Class<T> type) {
        if (!hasMetadata(key)) return null;
        return AdvanciusClient.GSON.fromJson(AdvanciusClient.GSON.toJson(getMetadata(key)), type);
    }

    @SuppressWarnings("unchecked")
    public <T> T getMetadata(String key, Class<T> type) {
        return (T) internal.get(key);
    }

    public <T> T getMetadataOr(String key, T def) {
        return internal.containsKey(key) ? getMetadata(key) : def;
    }

    public <T> boolean isMetadataOf(String key, Class<T> clazz) {
        return hasMetadata(key) && clazz.isInstance(getMetadata(key));
    }

    public JsonObject serialize() {
        JsonParser parser = new JsonParser();
        return parser.parse(AdvanciusClient.GSON.toJson(internal)).getAsJsonObject();
    }

    public void deserialize(Map<String, Object> data) {
        internal.putAll(data);
    }

    public void deserialize(JsonObject json) {
        internal.putAll(AdvanciusClient.GSON.fromJson(json, Map.class));
    }
}
