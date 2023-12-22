package org.caiopinho.core;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {

	private double scrollX = 0, scrollY = 0;
	private double positionX = 0, positionY = 0, lastPositionX = 0, lastPositionY = 0;
	private final boolean[] mouseButtonPressed = new boolean[5];
	private boolean isDragging = false;

	private static MouseListener instance = null;

	private MouseListener() {
	}

	public static synchronized MouseListener get() {
		if (instance == null)
			instance = new MouseListener();
		return instance;
	}

	public static void mousePositionCallback(long glfwWindow, double newPositionX, double newPositionY) {
		MouseListener mouseListener = get();
		mouseListener.lastPositionX = mouseListener.positionX;
		mouseListener.lastPositionY = mouseListener.positionY;
		mouseListener.positionX = newPositionX;
		mouseListener.positionY = newPositionY;

		mouseListener.isDragging = false;
		for (boolean button : mouseListener.mouseButtonPressed) {
			if (button) {
				mouseListener.isDragging = true;
				break;
			}
		}
	}

	public static void mouseButtonCallback(long glfwWindow, int button, int action, int mods) {
		MouseListener mouseListener = get();

		if (action == GLFW_PRESS) {
			mouseListener.mouseButtonPressed[button] = true;
		} else if (action == GLFW_RELEASE) {
			mouseListener.mouseButtonPressed[button] = false;
			mouseListener.isDragging = false;
		}
	}

	public static void mouseScrollCallback(long glfwWindow, double offsetX, double offsetY) {
		MouseListener mouseListener = get();
		mouseListener.scrollX = offsetX;
		mouseListener.scrollY = offsetY;
	}

	public static void endFrame() {
		MouseListener mouseListener = get();
		mouseListener.scrollX = 0;
		mouseListener.scrollY = 0;
		mouseListener.lastPositionX = mouseListener.positionX;
		mouseListener.lastPositionY = mouseListener.positionY;
	}

	public static float getX() {
		return (float) get().positionX;
	}

	public static float getY() {
		return (float) get().positionY;
	}

	public static float getDeltaX() {
		return (float) (get().lastPositionX - get().positionX);
	}

	public static float getDeltaY() {
		return (float) (get().lastPositionY - get().positionY);
	}

	public static float getScrollX() {
		return (float) get().scrollX;
	}

	public static float getScrollY() {
		return (float) get().scrollY;
	}

	public static boolean isDragging() {
		return get().isDragging;
	}

	public static boolean isButtonDown(int button) {
		if (button >= get().mouseButtonPressed.length)
			return false;
		return get().mouseButtonPressed[button];
	}
}
