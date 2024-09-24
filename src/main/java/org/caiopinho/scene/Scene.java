package org.caiopinho.scene;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import org.caiopinho.assets.AssetPool;
import org.caiopinho.component.Component;
import org.caiopinho.component.SpriteRenderer;
import org.caiopinho.core.GameObject;
import org.caiopinho.core.Settings;
import org.caiopinho.file.FileController;
import org.caiopinho.renderer.Camera;
import org.caiopinho.renderer.Renderer;

import imgui.ImGui;

public abstract class Scene {
	@Getter protected Camera camera;
	@Getter protected final Renderer renderer = new Renderer();
	private boolean isRunning;
	protected boolean wasLoaded;
	@Getter protected final List<GameObject> gameObjects = new ArrayList<>();

	public GameObject activeGameObject = null;
	private final FileController<GameObject> fileController = new FileController<>();

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

	public <T extends GameObject> void addGameObjectToScene(List<T> gameObjects) {
		for (T go : gameObjects) {
			this.addGameObjectToScene(go);
		}
	}

	public void save() {
		List<GameObject> serializableGameObjects = this.gameObjects.stream()
				.filter(GameObject::isSerializable)
				.toList();
		this.fileController.writeGSON("saves/", this.getClass().getCanonicalName() + ".txt", serializableGameObjects);
	}

	protected void loadResources() {
	}

	public void load() {
		this.loadResources();

		if (Settings.HARD_LEVEL_RELOAD) {
			return;
		}

		int maxGameObjectId = -1;
		int maxComponentId = -1;

		GameObject[] gameObjects = this.fileController.readGSON("saves/" + this.getClass().getCanonicalName() + ".txt", GameObject[].class);

		if (gameObjects != null) {
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
