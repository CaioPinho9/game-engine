package org.caiopinho.scene;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import org.caiopinho.core.GameObject;
import org.caiopinho.renderer.Camera;
import org.caiopinho.renderer.Renderer;

public abstract class Scene {
	@Getter protected Camera camera;
	Renderer renderer = new Renderer();
	private boolean isRunning;
	protected List<GameObject> gameObjects = new ArrayList<>();

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

	}

	public void addGameObjectToScene(GameObject gameObject) {
		this.gameObjects.add(gameObject);
		if (this.isRunning) {
			gameObject.start();
			this.renderer.add(gameObject);
		}
	}
}
