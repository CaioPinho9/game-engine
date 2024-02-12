package org.caiopinho.scene;

import lombok.NoArgsConstructor;

import org.caiopinho.assets.AssetPool;
import org.caiopinho.assets.Sprite;
import org.caiopinho.assets.Spritesheet;
import org.caiopinho.component.RigidBody;
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

		if (this.wasLoaded) {
			System.out.println("Scene was loaded");
			return;
		}

		GameObject gameObject1 = new GameObject("Object1", new Transform(new Vector2f(100, 100), new Vector2f(100, 100)), 1);
		SpriteRenderer spriteRenderer1 = new SpriteRenderer();
		spriteRenderer1.setColor(new Vector4f(1, 0, 0, .8f));
		gameObject1.addComponent(spriteRenderer1);
		gameObject1.addComponent(new RigidBody());

		GameObject gameObject2 = new GameObject("Object2", new Transform(new Vector2f(180, 100), new Vector2f(100, 100)), 0);
		SpriteRenderer spriteRenderer2 = new SpriteRenderer();
		spriteRenderer2.setColor(new Vector4f(0, 1, 0, .8f));
		gameObject2.addComponent(spriteRenderer2);

		GameObject gameObject3 = new GameObject("Object3", new Transform(new Vector2f(0, 0), new Vector2f(100, 100)), 0);
		SpriteRenderer spriteRenderer3 = new SpriteRenderer();
		Sprite sprite3 = new Sprite();
		sprite3.setTexture(AssetPool.getTexture("assets/textures/logo.png"));
		spriteRenderer3.setSprite(sprite3);
		gameObject3.addComponent(spriteRenderer3);

		GameObject gameObject4 = new GameObject("Object4", new Transform(new Vector2f(300, 0), new Vector2f(900, 168.75f)), 0);
		SpriteRenderer spriteRenderer4 = new SpriteRenderer();
		Sprite sprite4 = new Sprite();
		sprite4.setTexture(AssetPool.getTexture("assets/textures/ubuntu dices.png"));
		spriteRenderer4.setSprite(sprite4);
		gameObject4.addComponent(spriteRenderer4);

		this.addGameObjectToScene(gameObject1);
		this.addGameObjectToScene(gameObject2);
		this.addGameObjectToScene(gameObject3);
		this.addGameObjectToScene(gameObject4);
		this.activeGameObject = gameObject1;

		for (int i = 0; i < 26; i++) {
			GameObject gameObject = new GameObject("Object " + (i + 5), new Transform(new Vector2f(i * 25 + 400, 0), new Vector2f(25, 25)), 0);
			SpriteRenderer spriteRenderer5 = new SpriteRenderer();
			spriteRenderer5.setSprite(AssetPool.getSpritesheet("character").getSprite(i));
			gameObject.addComponent(spriteRenderer5);
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
