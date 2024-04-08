package org.caiopinho.editor.imgui;

import java.util.EnumSet;

import lombok.Getter;

import org.caiopinho.editor.components.GridTools;

@Getter
public enum FieldConfig {
	// Generic names
	ENABLED("enabled", "Enabled"),

	// GridTools
	GRID_TOOLS_WIDTH("width", "Width", GridTools.class, 1, 7),
	GRID_TOOLS_GRID_SIZE("gridSize", "Grid Size", GridTools.class, 1, 100, RangeEnum.DECIMAL),
	GRID_TOOLS_COLOR("color", "Color", GridTools.class, 0, 1),
	GRID_TOOLS_Z_INDEX("zIndex", "Z-Index", GridTools.class, -5, 5);

	private final String fieldName;
	private final String userFriendlyName;
	private final Class clazz;
	private final float min;
	private final float max;
	private final String rangeEnum;

	FieldConfig(String fieldName, String userFriendlyName, Class clazz, float min, float max, String rangeEnum) {
		this.fieldName = fieldName;
		this.userFriendlyName = userFriendlyName;
		this.clazz = clazz;
		this.min = min;
		this.max = max;
		this.rangeEnum = rangeEnum;
	}

	FieldConfig(String fieldName, String userFriendlyName, Class clazz, float min, float max) {
		this.fieldName = fieldName;
		this.userFriendlyName = userFriendlyName;
		this.clazz = clazz;
		this.min = min;
		this.max = max;
		this.rangeEnum = RangeEnum.LINEAR;
	}

	FieldConfig(String fieldName, String userFriendlyName) {
		this.fieldName = fieldName;
		this.userFriendlyName = userFriendlyName;
		this.clazz = null;
		this.min = 0;
		this.max = 0;
		this.rangeEnum = RangeEnum.LINEAR;
	}

	FieldConfig(String fieldName, String userFriendlyName, Class clazz) {
		this.fieldName = fieldName;
		this.userFriendlyName = userFriendlyName;
		this.clazz = clazz;
		this.min = 0;
		this.max = 0;
		this.rangeEnum = RangeEnum.LINEAR;
	}

	public static FieldConfig findByFieldName(String fieldName, Class clazz) {
		for (FieldConfig config : EnumSet.allOf(FieldConfig.class)) {
			if (config.fieldName.equals(fieldName) && (config.clazz == null || config.clazz.equals(clazz))) {
				return config;
			}
		}
		return null;
	}
}
