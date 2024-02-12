package org.caiopinho.serializer;

import java.lang.reflect.Type;

import org.caiopinho.component.Component;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ComponentSerializer implements JsonSerializer<Component>, JsonDeserializer<Component> {
	@Override public JsonElement serialize(Component component, Type type, JsonSerializationContext jsonSerializationContext) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("type", new JsonPrimitive(component.getClass().getCanonicalName()));
		jsonObject.add("properties", jsonSerializationContext.serialize(component, component.getClass()));
		return jsonObject;
	}

	@Override public Component deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		String objectType = jsonObject.get("type").getAsString();
		JsonElement element = jsonObject.get("properties");
		try {
			return jsonDeserializationContext.deserialize(element, Class.forName(objectType));
		} catch (ClassNotFoundException e) {
			throw new JsonParseException(e);
		}
	}
}
