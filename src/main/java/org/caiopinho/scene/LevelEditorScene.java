package org.caiopinho.scene;

import lombok.NoArgsConstructor;

import org.caiopinho.assets.AssetPool;
import org.caiopinho.assets.Sprite;
import org.caiopinho.assets.Spritesheet;
import org.caiopinho.component.SpriteRenderer;
import org.caiopinho.core.GameObject;
import org.caiopinho.core.Transform;
import org.caiopinho.renderer.Camera;
import org.joml.Vector2f;
import org.joml.Vector4f;

import imgui.ImGui;

@NoArgsConstructor
public class LevelEditorScene extends Scene {

	@Override public void init() {
		this.camera = new Camera(new Vector2f());
		this.loadResources();

		GameObject gameObject1 = new GameObject("Object1", new Transform(new Vector2f(100, 100), new Vector2f(100, 100)), 1);
		gameObject1.addComponent(new SpriteRenderer(new Vector4f(1, 0, 0, .8f)));

		GameObject gameObject2 = new GameObject("Object2", new Transform(new Vector2f(180, 100), new Vector2f(100, 100)));
		gameObject2.addComponent(new SpriteRenderer(new Vector4f(0, 1, 0, .8f)));

		GameObject gameObject3 = new GameObject("Object3", new Transform(new Vector2f(0, 0), new Vector2f(100, 100)));
		gameObject3.addComponent(new SpriteRenderer(new Sprite(AssetPool.getTexture("assets/textures/logo.png"))));

		GameObject gameObject4 = new GameObject("Object4", new Transform(new Vector2f(300, 0), new Vector2f(900, 168.75f)));
		gameObject4.addComponent(new SpriteRenderer(new Sprite(AssetPool.getTexture("assets/textures/ubuntu dices.png"))));

		this.addGameObjectToScene(gameObject1);
		this.addGameObjectToScene(gameObject2);
		this.addGameObjectToScene(gameObject3);
		this.addGameObjectToScene(gameObject4);
		this.activeGameObject = gameObject1;

		for (int i = 0; i < 26; i++) {
			GameObject gameObject = new GameObject("Object " + (i + 5), new Transform(new Vector2f(i * 25 + 400, 0), new Vector2f(25, 25)));
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

	@Override public void imgui() {
		ImGui.begin("Test window");
		ImGui.text("Hello, world!");
		ImGui.end();
	}
}
