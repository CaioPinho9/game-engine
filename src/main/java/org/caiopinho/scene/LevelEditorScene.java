package org.caiopinho.scene;

import lombok.NoArgsConstructor;

import org.caiopinho.assets.AssetPool;
import org.caiopinho.assets.Spritesheet;
import org.caiopinho.component.SpriteRenderer;
import org.caiopinho.core.GameObject;
import org.caiopinho.core.Transform;
import org.caiopinho.renderer.Camera;
import org.joml.Vector2f;

@NoArgsConstructor
public class LevelEditorScene extends Scene {

	@Override public void init() {
		this.camera = new Camera(new Vector2f());
		this.loadResources();

		for (int i = 0; i < 26; i++) {
			GameObject gameObject = new GameObject("Object " + i, new Transform(new Vector2f(i * 25 + 25, 25), new Vector2f(25, 25)));
			gameObject.addComponent(new SpriteRenderer(AssetPool.getSpritesheet("character").getSprite(i)));
			this.addGameObjectToScene(gameObject);
		}
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
