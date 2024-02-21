package org.caiopinho.component;

import lombok.Getter;

import org.caiopinho.core.GameObject;
import org.caiopinho.editor.imgui.ImGuiHelper;

public abstract class Component {
	@Getter private int uid = -1;
	private static int ID_COUNTER = 0;

	public transient GameObject gameObject;

	public void start() {
	}

	public abstract void update(float deltaTime);

	public void imgui() {
		ImGuiHelper.generateImguiFieldsFromObjectAttributes(this);
	}

	public void generateId() {
		if (this.uid == -1) {
			this.uid = ID_COUNTER++;
		}
	}

	public static void init(int maxId) {
		ID_COUNTER = maxId;
	}
}
