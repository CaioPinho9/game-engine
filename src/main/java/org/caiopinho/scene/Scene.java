package org.caiopinho.scene;

import static org.caiopinho.core.MouseListener.getOrthoX;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import org.caiopinho.assets.AssetPool;
import org.caiopinho.component.Component;
import org.caiopinho.component.SpriteRenderer;
import org.caiopinho.core.GameObject;
import org.caiopinho.renderer.Camera;
import org.caiopinho.renderer.Renderer;
import org.caiopinho.serializer.ComponentSerializer;
import org.caiopinho.serializer.GameObjectSerializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import imgui.ImGui;

public abstract class Scene {
	@Getter protected Camera camera;
	protected Renderer renderer = new Renderer();
	private boolean isRunning;
	protected boolean wasLoaded;
	protected List<GameObject> gameObjects = new ArrayList<>();

	public GameObject activeGameObject = null;

	private final Gson gson = new GsonBuilder()
			.setPrettyPrinting()
			.registerTypeAdapter(GameObject.class, new GameObjectSerializer())
			.registerTypeAdapter(Component.class, new ComponentSerializer())
			.create();

	public void init() {
	}

	public void start() {
		for (GameObject gameObject : this.gameObjects) {
			gameObject.start();
			this.renderer.add(gameObject);
		}
		this.isRunning = true;
	}

	public void update(float deltaTime) {
		System.out.print("FPS: " + 1 / deltaTime + "\r");
		getOrthoX();
		for (GameObject gameObject : this.gameObjects) {
			gameObject.update(deltaTime);
		}
		this.renderer.render();
	}

	public void sceneImgui() {
		if (this.activeGameObject != null) {
			ImGui.begin("Inspector");
			this.activeGameObject.imgui();
			ImGui.end();
		}

		this.imgui();
	}

	public void imgui() {
	}

	public void addGameObjectToScene(GameObject gameObject) {
		this.gameObjects.add(gameObject);
		if (this.isRunning) {
			gameObject.start();
			this.renderer.add(gameObject);
		}
	}

	public void save() {
		try {
			FileWriter writer = new FileWriter("saves/" + this.getClass().getCanonicalName() + ".txt");
			writer.write(this.gson.toJson(this.gameObjects));
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected void loadResources() {
	}

	public void load() {
		String text = "";

		this.loadResources();

		try {
			text = new String(Files.readAllBytes(Paths.get("saves/" + this.getClass().getCanonicalName() + ".txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (!text.isEmpty()) {
			int maxGameObjectId = -1;
			int maxComponentId = -1;
			GameObject[] gameObjects = this.gson.fromJson(text, GameObject[].class);
			this.gameObjects.clear();
			this.renderer.clear();
			for (GameObject gameObject : gameObjects) {
				this.addGameObjectToScene(gameObject);
				if (gameObject.getUid() > maxGameObjectId) {
					maxGameObjectId = gameObject.getUid();
				}
				for (Component component : gameObject.getComponents()) {
					if (component.getUid() > maxComponentId) {
						maxComponentId = component.getUid();
					}
				}
			}
			GameObject.init(++maxGameObjectId);
			Component.init(++maxComponentId);
			this.wasLoaded = true;

			if (gameObjects.length > 0) {
				this.activeGameObject = gameObjects[0];
			}
		}

		this.loadGameObjectTextures();
	}

	private void loadGameObjectTextures() {
		for (GameObject gameObject : this.gameObjects) {
			SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);
			if (spriteRenderer != null && spriteRenderer.getTexture() != null) {
				spriteRenderer.setTexture(AssetPool.getTexture(spriteRenderer.getTexture().getFilepath()));
			}
		}
	}
}
