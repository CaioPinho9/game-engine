package org.caiopinho.component;

import org.caiopinho.core.GameObject;

public abstract class Component {

	public GameObject gameObject;

	public void start() {
	}

	public abstract void update(float deltaTime);

	public void imgui() {
	}

}
