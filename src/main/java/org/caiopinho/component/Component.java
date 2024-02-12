package org.caiopinho.component;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.caiopinho.core.GameObject;
import org.joml.Vector3f;
import org.joml.Vector4f;

import imgui.ImGui;

public abstract class Component {

	public transient GameObject gameObject;

	public void start() {
	}

	public abstract void update(float deltaTime);

	public void imgui() {
		try {
			ImGui.text(this.getClass().getSimpleName());
			Field[] fields = this.getClass().getDeclaredFields();
			for (Field field : fields) {
				if (Modifier.isTransient(field.getModifiers())) {
					continue;
				}

				boolean isPrivate = Modifier.isPrivate(field.getModifiers());
				if (isPrivate) {
					field.setAccessible(true);
				}

				Class<?> type = field.getType();
				Object rawValue = field.get(this);
				String name = field.getName();

				if (field.isSynthetic()) {
					continue;
				}

				if (type == int.class) {
					int value = (int) rawValue;
					int[] arr = { value };
					if (ImGui.dragInt(name, arr)) {
						field.setInt(this, arr[0]);
					}
				} else if (type == float.class) {
					float value = (float) rawValue;
					float[] arr = { value };
					if (ImGui.dragFloat(name, arr)) {
						field.setFloat(this, arr[0]);
					}
				} else if (type == boolean.class) {
					boolean value = (boolean) rawValue;
					if (ImGui.checkbox(name, value)) {
						field.setBoolean(this, !value);
					}
				} else if (type == Vector3f.class) {
					Vector3f value = (Vector3f) rawValue;
					float[] arr = { value.x, value.y, value.z };
					if (ImGui.dragFloat3(name, arr)) {
						value.set(arr[0], arr[1], arr[2]);
					}
				} else if (type == Vector4f.class) {
					Vector4f value = (Vector4f) rawValue;
					float[] arr = { value.x, value.y, value.z, value.w };
					if (ImGui.dragFloat4(name, arr)) {
						value.set(arr[0], arr[1], arr[2], arr[3]);
					}

				} else {
					System.out.println("Component.imgui: Unknown type " + type);
				}

				if (isPrivate) {
					field.setAccessible(false);
				}
			}
			ImGui.newLine();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
