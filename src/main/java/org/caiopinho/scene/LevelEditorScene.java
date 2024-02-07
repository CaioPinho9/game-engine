package org.caiopinho.scene;

import lombok.NoArgsConstructor;

import org.caiopinho.component.Sprite;
import org.caiopinho.component.SpriteRenderer;
import org.caiopinho.core.AssetPool;
import org.caiopinho.core.GameObject;
import org.caiopinho.core.Transform;
import org.caiopinho.renderer.Camera;
import org.joml.Vector2f;

@NoArgsConstructor
public class LevelEditorScene extends Scene {

	@Override public void init() {
		this.camera = new Camera(new Vector2f());

		GameObject gameObject1 = new GameObject("Test", new Transform(new Vector2f(0, 0), new Vector2f(100, 100)));
		GameObject gameObject2 = new GameObject("Test1", new Transform(new Vector2f(300, 0), new Vector2f(900, 168.75f)));
		gameObject1.addComponent(new SpriteRenderer(new Sprite(AssetPool.getTexture("assets/textures/logo.png"))));
		gameObject2.addComponent(new SpriteRenderer(new Sprite(AssetPool.getTexture("assets/textures/ubuntu dices.png"))));
		this.addGameObjectToScene(gameObject1);
		this.addGameObjectToScene(gameObject2);
	}

	@Override public void update(float deltaTime) {
		System.out.print("FPS: " + 1 / deltaTime + "\r");
		for (GameObject gameObject : this.gameObjects) {
			gameObject.update(deltaTime);
		}
		this.renderer.render();
	}
}
