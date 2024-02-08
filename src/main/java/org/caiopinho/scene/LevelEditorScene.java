package org.caiopinho.scene;

import lombok.NoArgsConstructor;

import org.caiopinho.assets.AssetPool;
import org.caiopinho.assets.Spritesheet;
import org.caiopinho.component.SpriteRenderer;
import org.caiopinho.core.GameObject;
import org.caiopinho.core.Transform;
import org.caiopinho.renderer.Camera;
import org.joml.Vector2f;
import org.joml.Vector4f;

@NoArgsConstructor
public class LevelEditorScene extends Scene {

	@Override public void init() {
		this.camera = new Camera(new Vector2f());
		this.loadResources();

		GameObject gameObject1 = new GameObject("Test Object1", new Transform(new Vector2f(100, 100), new Vector2f(100, 100)), 1);
		gameObject1.addComponent(new SpriteRenderer(new Vector4f(1, 0, 0, .8f)));

		GameObject gameObject2 = new GameObject("Test Object2", new Transform(new Vector2f(180, 100), new Vector2f(100, 100)));
		gameObject2.addComponent(new SpriteRenderer(new Vector4f(0, 1, 0, .8f)));

		this.addGameObjectToScene(gameObject1);
		this.addGameObjectToScene(gameObject2);
	}

	private void loadResources() {
		AssetPool.getShader("assets/shaders/default.glsl");
		AssetPool.getTexture("assets/textures/logo.png");
		AssetPool.getTexture("assets/textures/ubuntu dices.png");
		Spritesheet spritesheet = new Spritesheet(AssetPool.getTexture("assets/textures/spritesheet.png"), 16, 16, 26, 0);
		AssetPool.addSpriteSheet("character", spritesheet);
	}

	@Override public void update(float deltaTime) {
		System.out.print("FPS: " + 1 / deltaTime + "\r");
		for (GameObject gameObject : this.gameObjects) {
			gameObject.update(deltaTime);
		}
		this.renderer.render();
	}
}
