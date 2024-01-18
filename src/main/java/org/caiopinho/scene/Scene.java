package org.caiopinho.scene;

import org.caiopinho.renderer.Camera;

public abstract class Scene {
	protected Camera camera;

	public abstract void start();

	public void update(float deltaTime) {

	}
}
