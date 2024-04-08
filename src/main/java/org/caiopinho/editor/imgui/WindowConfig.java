package org.caiopinho.editor.imgui;

import java.util.EnumSet;

import lombok.Getter;

import org.caiopinho.editor.components.DebugView;
import org.caiopinho.editor.components.GridTools;

@Getter
public enum WindowConfig {
	GRID_TOOLS("Grid", GridTools.class),
	DEBUG_VIEW("Debug View", DebugView.class);

	private final String windowName;
	private final Class clazz;

	WindowConfig(String windowName, Class clazz) {
		this.windowName = windowName;
		this.clazz = clazz;
	}

	public static WindowConfig findByClass(Class clazz) {
		for (WindowConfig config : EnumSet.allOf(WindowConfig.class)) {
			if (config.clazz.equals(clazz)) {
				return config;
			}
		}
		return null;
	}
}
