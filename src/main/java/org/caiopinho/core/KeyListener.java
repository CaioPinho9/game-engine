package org.caiopinho.core;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyListener {
	private final boolean[] keyPressed = new boolean[350];

	private static KeyListener instance = null;

	private KeyListener() {
	}

	public static synchronized KeyListener get() {
		if (instance == null)
			instance = new KeyListener();
		return instance;
	}

	public static void keyCallback(long glfwWindow, int key, int scancode, int action, int mods) {
		if (action == GLFW_PRESS) {
			get().keyPressed[key] = true;
		} else if (action == GLFW_RELEASE) {
			get().keyPressed[key] = false;
		}
	}

	public static boolean isKeyPressed(int keyCode) {
		return get().keyPressed[keyCode];
	}
}
