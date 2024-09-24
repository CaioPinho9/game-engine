package org.caiopinho.core;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import java.util.Arrays;

import lombok.Setter;

import org.caiopinho.renderer.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class MouseListener {

	private double scrollX = 0, scrollY = 0;
	private double positionX = 0;
	private double positionY = 0;
	private double lastPositionX = 0;
	private double lastPositionY = 0;
	private double worldPositionX = 0;
	private double worldPositionY = 0;
	private final boolean[] mouseButtonPressed = new boolean[5];
	private boolean[] isDragging = new boolean[5];

	// Variables for float-click detection
	private final long[] lastClickTime = new long[5];
	private final static long DOUBLE_CLICK_INTERVAL = 300; // 300 milliseconds for float-click
	private final boolean[] mouseClicked = new boolean[5];

	@Setter private static Vector2f gameViewportSize = new Vector2f();
	@Setter private static Vector2f gameViewportPosition = new Vector2f();

	private static MouseListener instance = null;

	private MouseListener() {
		// Initialize lastClickTime and mouseClicked arrays
		Arrays.fill(this.lastClickTime, -1); // Initialize with -1 to indicate no clicks yet
		Arrays.fill(this.mouseClicked, false);
	}

	public static synchronized MouseListener get() {
		if (instance == null) {
			instance = new MouseListener();
		}
		return instance;
	}

	public static void mousePositionCallback(long glfwWindow, double newPositionX, double newPositionY) {
		MouseListener mouseListener = get();
		mouseListener.lastPositionX = mouseListener.positionX;
		mouseListener.lastPositionY = mouseListener.positionY;
		mouseListener.positionX = newPositionX;
		mouseListener.positionY = newPositionY;

		mouseListener.worldPositionX = mouseListener.calculateOrthoX();
		mouseListener.worldPositionY = mouseListener.calculateOrthoY();

		mouseListener.cleanDragging();
		for (int button = 0; button < mouseListener.mouseButtonPressed.length; button++) {
			if (mouseListener.mouseButtonPressed[button]) {
				mouseListener.isDragging[button] = true;
			}
		}
	}

	public static void mouseButtonCallback(long glfwWindow, int button, int action, int mods) {
		MouseListener mouseListener = get();

		if (button < mouseListener.mouseButtonPressed.length) {
			if (action == GLFW_PRESS) {
				long currentTime = System.currentTimeMillis();// Reset the float-click flag for this button

				// Check for float-click
				mouseListener.mouseClicked[button] = currentTime - mouseListener.lastClickTime[button] <= DOUBLE_CLICK_INTERVAL; // Double-click detected
				mouseListener.lastClickTime[button] = currentTime; // Update last click time
				mouseListener.mouseButtonPressed[button] = true;
			} else if (action == GLFW_RELEASE) {
				mouseListener.mouseButtonPressed[button] = false;
				mouseListener.isDragging[button] = false;
			}
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
		Arrays.fill(mouseListener.mouseClicked, false);
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

	public static float getOrthoX() {
		return (float) get().worldPositionX;
	}

	public static float getOrthoY() {
		return (float) get().worldPositionY;
	}

	public static Vector2f getOrtho() {
		MouseListener mouseListener = get();
		return new Vector2f((float) mouseListener.worldPositionX, (float) mouseListener.worldPositionY);
	}

	private float calculateOrthoX() {
		float currentX = ((getX() - gameViewportPosition.x) / gameViewportSize.x) * 2 - 1;
		Vector4f tmpX = new Vector4f(currentX, 0, 0, 1);
		Matrix4f viewProjectionX = new Matrix4f();
		Window.getScene().getCamera().getInverseView().mul(Window.getScene().getCamera().getInverseProjection(), viewProjectionX);
		tmpX.mul(viewProjectionX);
		return tmpX.x;
	}

	private float calculateOrthoY() {
		float currentY = -(((getY() - gameViewportPosition.y) / gameViewportSize.y) * 2 - 1);
		Vector4f tmpY = new Vector4f(0, currentY, 0, 1);
		Matrix4f viewProjectionY = new Matrix4f();
		Window.getScene().getCamera().getInverseView().mul(Window.getScene().getCamera().getInverseProjection(), viewProjectionY);
		tmpY.mul(viewProjectionY);
		return tmpY.y;
	}

	public static float getScrollX() {
		return (float) get().scrollX;
	}

	public static float getScrollY() {
		return (float) get().scrollY;
	}

	private void cleanDragging() {
		this.isDragging = new boolean[5];
	}

	public static boolean isButtonDown(int button) {
		if (button >= get().mouseButtonPressed.length) {
			return false;
		}
		return get().mouseButtonPressed[button];
	}

	public static boolean isButtonUp(int button) {
		return !isButtonDown(button);
	}

	public static boolean isButtonPressed(int button) {
		return isButtonDown(button) && get().isDragging[button];
	}

	public static boolean isButtonReleased(int button) {
		return isButtonUp(button) && !get().isDragging[button];
	}

	public static boolean isDoubleClick(int button) {
		return get().mouseClicked[button];
	}
}
