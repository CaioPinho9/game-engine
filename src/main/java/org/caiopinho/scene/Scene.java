package org.caiopinho.scene;

import java.util.ArrayList;
import java.util.List;

import org.caiopinho.core.GameObject;
import org.caiopinho.renderer.Camera;

public abstract class Scene {
	protected Camera camera;
	private boolean isRunning;
	protected List<GameObject> gameObjects = new ArrayList<>();

	public void init() {
	}

	public void start() {
		for (GameObject gameObject : this.gameObjects) {
			gameObject.start();
		}
		this.isRunning = true;
	}

	public void update(float deltaTime) {

	}

	public void addGameObjectToScene(GameObject gameObject) {
		this.gameObjects.add(gameObject);
		if (this.isRunning) {
			gameObject.start();
		}
	}
}
