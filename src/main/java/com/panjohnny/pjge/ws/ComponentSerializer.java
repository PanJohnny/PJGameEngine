package com.panjohnny.pjge.ws;

import com.google.gson.*;
import com.panjohnny.pjgl.api.object.Component;
import com.panjohnny.pjgl.api.object.GameObject;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class ComponentSerializer implements JsonSerializer<Component> {
    @Override
    public JsonElement serialize(Component src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.addProperty("class", src.getClass().getName());

        JsonObject params = new JsonObject();
        // obj.add("parameters", context.serialize(src));
        for (Field field : src.getClass().getFields()) {
            if (field.getType() != GameObject.class) {
                try {
                    params.add(field.getName(), context.serialize(field.get(src)));
                } catch (IllegalAccessException ignored) {
                }
            }
        }
        obj.add("parameters", params);
        return obj;
    }
}
