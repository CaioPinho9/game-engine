package org.caiopinho.serializer;

import java.lang.reflect.Type;

import org.caiopinho.component.Component;
import org.caiopinho.core.GameObject;
import org.caiopinho.core.Transform;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class GameObjectSerializer implements JsonDeserializer<GameObject> {

	@Override public GameObject deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		String name = jsonObject.get("name").getAsString();
		JsonArray components = jsonObject.getAsJsonArray("components");
		int zIndex = jsonObject.get("zIndex").getAsInt();
		Transform transform = jsonDeserializationContext.deserialize(jsonObject.get("transform"), Transform.class);

		GameObject gameObject = new GameObject(name, transform, zIndex);

		for (JsonElement componentElement : components) {
			Component component = jsonDeserializationContext.deserialize(componentElement, Component.class);
			gameObject.addComponent(component);
		}

		return gameObject;
	}
}
