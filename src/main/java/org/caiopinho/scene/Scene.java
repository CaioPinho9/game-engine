package org.caiopinho.scene;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import org.caiopinho.core.GameObject;
import org.caiopinho.renderer.Camera;
import org.caiopinho.renderer.Renderer;

import imgui.ImGui;

public abstract class Scene {
	@Getter protected Camera camera;
	Renderer renderer = new Renderer();
	private boolean isRunning;
	protected List<GameObject> gameObjects = new ArrayList<>();

	protected GameObject activeGameObject = null;

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

	public void addGameObjectsToScene(List<GameObject> gameObjects) {
		for (GameObject gameObject : gameObjects) {
			this.addGameObjectToScene(gameObject);
		}
	}
}
