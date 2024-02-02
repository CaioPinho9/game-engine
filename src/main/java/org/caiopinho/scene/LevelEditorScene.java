package org.caiopinho.scene;

import lombok.NoArgsConstructor;

import org.caiopinho.component.SpriteRenderer;
import org.caiopinho.core.AssetPool;
import org.caiopinho.core.GameObject;
import org.caiopinho.core.Transform;
import org.caiopinho.renderer.Camera;
import org.joml.Vector2f;

@NoArgsConstructor public class LevelEditorScene extends Scene {

	@Override public void init() {
		this.camera = new Camera(new Vector2f());

		GameObject gameObject = new GameObject("Test", new Transform(new Vector2f(100, 100), new Vector2f(300, 300)));
		gameObject.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/textures/logo.png")));
		this.addGameObjectToScene(gameObject);

	}

	@Override public void update(float deltaTime) {
		System.out.print("FPS: " + 1 / deltaTime + "\r");
		for (GameObject gameObject : this.gameObjects) {
			gameObject.update(deltaTime);
		}
		this.renderer.render();
	}
}
