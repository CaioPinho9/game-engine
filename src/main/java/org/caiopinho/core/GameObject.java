package org.caiopinho.core;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import org.caiopinho.component.Component;

public class GameObject {

	private String name;
	private List<Component> components;
	public Transform transform;
	@Getter private int zIndex;

	public GameObject(String name) {
		this.init(name, new Transform(), 0);
	}

	public GameObject(String name, Transform transform) {
		this.init(name, transform, 0);
	}

	public GameObject(String name, Transform transform, int zIndex) {
		this.init(name, transform, zIndex);
	}

	public void init(String name, Transform transform, int zIndex) {
		this.name = name;
		this.components = new ArrayList<>();
		this.transform = transform;
		this.zIndex = zIndex;
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

	public void imgui() {
		for (Component component : this.components) {
			component.imgui();
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
