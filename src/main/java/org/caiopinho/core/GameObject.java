package org.caiopinho.core;

import java.util.ArrayList;
import java.util.List;

public class GameObject {

	private String name;
	private List<Component> components;
	public Transform transform;

	public GameObject(String name) {
		this.init(name, new Transform());
	}

	public GameObject(String name, Transform transform) {
		this.init(name, transform);
	}

	public void init(String name, Transform transform) {
		this.name = name;
		this.components = new ArrayList<>();
		this.transform = transform;
	}

	public <T extends Component> T getComponent(Class<T> componentClass) {
		for (Component component : this.components) {
			if (component.getClass().equals(componentClass)) {
				try {
					return componentClass.cast(component);
				} catch (ClassCastException e) {
					e.printStackTrace();
					assert false : "Error: Casting component " + component.getClass().getName() + " to " + componentClass.getName() + " failed";
				}
			}
		}
		return null;
	}

	public <T extends Component> void addComponent(T component) {
		this.components.add(component);
		component.gameObject = this;
	}

	public <T extends Component> void start() {
		for (Component component : this.components) {
			component.start();
		}
	}

	public <T extends Component> void update(float deltaTime) {
		for (Component component : this.components) {
			component.update(deltaTime);
		}
	}

	public <T extends Component> void removeComponent(Class<T> componentClass) {
		for (Component component : this.components) {
			if (component.getClass().equals(componentClass)) {
				this.components.remove(component);
				return;
			}
		}
	}
}
