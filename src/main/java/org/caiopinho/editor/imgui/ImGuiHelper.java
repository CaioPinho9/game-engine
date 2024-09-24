package org.caiopinho.editor.imgui;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.caiopinho.math.MathHelper;
import org.joml.Vector3f;
import org.joml.Vector4f;

import imgui.ImGui;
import imgui.ImVec2;

public class ImGuiHelper {
	public static void genericFieldBehavior(Field field, Object object) {
		try {
			if (Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
				return;
			}

			boolean isPrivate = Modifier.isPrivate(field.getModifiers());
			if (isPrivate) {
				field.setAccessible(true);
			}

			Class<?> type = field.getType();
			Object rawValue = field.get(object);
			String name = field.getName();
			FieldConfig config = FieldConfig.findByFieldName(name, object.getClass());

			String buttonName = getWidgetNameId(name, config, object);

			if (field.isSynthetic()) {
				return;
			}

			if (type == int.class) {
				handleIntField(field, object, buttonName, rawValue, config);
			} else if (type == float.class) {
				handleFloatField(field, object, buttonName, rawValue, config);
			} else if (type == boolean.class) {
				handleBooleanField(field, object, buttonName, rawValue);
			} else if (type == Vector3f.class) {
				handleVector3fField(buttonName, rawValue, config);
			} else if (type == Vector4f.class) {
				handleVector4fField(buttonName, rawValue, config);
			} else {
				System.out.println("Component.imgui: Unknown type " + type);
			}

			if (isPrivate) {
				field.setAccessible(false);
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private static void handleIntField(Field field, Object object, String name, Object rawValue, FieldConfig config) throws IllegalAccessException {
		int value = (int) rawValue;
		int[] arr = { value };
		if (config != null) {
			int range = (int) getRange(config, value);
			if (ImGui.dragInt(name, arr, range, config.getMin(), config.getMax())) {
				arr[0] = (int) MathHelper.clamp(arr[0], config.getMin(), config.getMax());
				field.setInt(object, arr[0]);
			}
			return;
		}
		if (ImGui.dragInt(name, arr)) {
			field.setInt(object, arr[0]);
		}
	}

	private static void handleFloatField(Field field, Object object, String name, Object rawValue, FieldConfig config) throws IllegalAccessException {
		float value = (float) rawValue;
		float[] arr = { value };
		if (config != null) {
			if (ImGui.dragFloat(name, arr, getRange(config, value), config.getMin(), config.getMax())) {
				arr[0] = MathHelper.clamp(arr[0], config.getMin(), config.getMax());
				field.setFloat(object, arr[0]);
			}
			return;
		}
		if (ImGui.dragFloat(name, arr)) {
			field.setFloat(object, arr[0]);
		}
	}

	private static void handleBooleanField(Field field, Object object, String name, Object rawValue) throws IllegalAccessException {
		boolean value = (boolean) rawValue;

		if (ImGui.checkbox(name, value)) {
			field.setBoolean(object, !value);
		}
	}

	private static void handleVector3fField(String name, Object rawValue, FieldConfig config) {
		Vector3f value = (Vector3f) rawValue;
		float[] arr = new float[] { value.x, value.y, value.z };
		if (config != null) {
			if (ImGui.dragFloat3(name, arr, getRange(config), config.getMin(), config.getMax())) {
				arr = MathHelper.clamp(arr, config.getMin(), config.getMax());
				value.set(arr[0], arr[1], arr[2]);
			}
			return;
		}
		if (ImGui.dragFloat3(name, arr)) {
			value.set(arr[0], arr[1], arr[2]);
		}
	}

	private static void handleVector4fField(String name, Object rawValue, FieldConfig config) {
		Vector4f value = (Vector4f) rawValue;
		float[] arr = new float[] { value.x, value.y, value.z, value.w };
		if (config != null) {
			if (ImGui.dragFloat4(name, arr, getRange(config), config.getMin(), config.getMax())) {
				arr = MathHelper.clamp(arr, config.getMin(), config.getMax());
				value.set(arr[0], arr[1], arr[2], arr[3]);
			}
			return;
		}
		if (ImGui.dragFloat4(name, arr)) {
			value.set(arr[0], arr[1], arr[2], arr[3]);
		}
	}

	public static void generateImguiFieldsFromObjectAttributes(Object object) {
		WindowConfig config = WindowConfig.findByClass(object.getClass());
		String name = config != null ? config.getWindowName() : object.getClass().getSimpleName();
		ImGui.text(name);
		Field[] fields = object.getClass().getDeclaredFields();
		for (Field field : fields) {
			genericFieldBehavior(field, object);
		}
		ImGui.newLine();
	}

	private static float getRange(FieldConfig config) {
		return getRange(config, 0);
	}

	private static float getRange(FieldConfig config, float value) {
		return switch (config.getRangeEnum()) {
			case RangeEnum.LINEAR -> (config.getMax() - config.getMin()) * .01f;
			case RangeEnum.DECIMAL -> value != 1 ? 10 : 9;
			default -> throw new RuntimeException("Unknown range enum " + config.getRangeEnum());
		};
	}

	public static String getWidgetNameId(String name, FieldConfig config, Object object) {
		String widgetName = config != null ? config.getUserFriendlyName() : name;
		widgetName += "##" + object.hashCode();
		return widgetName;
	}

	public static ImVec2 getWindowSizeNoScroll() {
		ImVec2 windowSize = new ImVec2();
		ImGui.getContentRegionAvail(windowSize);
		windowSize.x -= ImGui.getScrollX();
		windowSize.y -= ImGui.getScrollY();
		return windowSize;
	}
}
