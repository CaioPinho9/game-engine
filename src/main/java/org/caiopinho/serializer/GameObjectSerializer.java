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

	@Override
	public GameObject deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		String name = jsonObject.get("name").getAsString();
		JsonArray components = jsonObject.getAsJsonArray("components");

		GameObject gameObject = new GameObject(name);

		for (JsonElement componentElement : components) {
			Component component = jsonDeserializationContext.deserialize(componentElement, Component.class);
			gameObject.addComponent(component);
		}
		gameObject.transform = gameObject.getComponent(Transform.class);

		return gameObject;
	}
}
