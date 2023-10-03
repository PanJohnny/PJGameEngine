package com.panjohnny.pjge.ws;

import com.google.gson.*;
import com.panjohnny.pjgl.api.object.Component;
import com.panjohnny.pjgl.api.object.GameObject;

import java.lang.reflect.Type;

public class GameObjectSerializer implements JsonSerializer<GameObject> {
    @Override
    public JsonElement serialize(GameObject src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.addProperty("class", src.getClass().getName());
        obj.addProperty("hash", src.hashCode());

        JsonArray comps = new JsonArray();
        for (Component c : src.components) {
            comps.add(context.serialize(c, Component.class));
        }
        obj.add("components", comps);

        return obj;
    }
}
