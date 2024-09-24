package org.caiopinho.core;

import org.caiopinho.assets.Sprite;
import org.caiopinho.component.SpriteRenderer;
import org.joml.Vector2f;

public class Prefabs {
	public static GameObject createSpriteObject(Sprite sprite, float sizeX, float sizeY) {
		GameObject gameObject = new GameObject("Sprite", new Transform(new Vector2f(), new Vector2f(sizeX, sizeY)));
		SpriteRenderer spriteRenderer = new SpriteRenderer();
		spriteRenderer.setSprite(sprite);
		gameObject.addComponent(spriteRenderer);
		return gameObject;
	}
}
