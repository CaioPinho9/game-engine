package org.caiopinho.core;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LAST;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import java.util.Arrays;

public class KeyListener {
	private final boolean[] keyPressed = new boolean[350];
	private final boolean[] keyBeginPress = new boolean[GLFW_KEY_LAST + 1];

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

	public static boolean keyBeginPress(int keyCode) {
		if (keyCode <= GLFW_KEY_LAST && keyCode >= 0) {
			return get().keyBeginPress[keyCode];
		}

		return false;
	}

	public static void endFrame() {
		Arrays.fill(get().keyBeginPress, false);
	}
}
