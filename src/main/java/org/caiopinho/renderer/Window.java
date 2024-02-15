package org.caiopinho.renderer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_MAXIMIZED;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11C.GL_SRC_ALPHA;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.caiopinho.core.ImGUILayer;
import org.caiopinho.core.KeyListener;
import org.caiopinho.core.MouseListener;
import org.caiopinho.renderer.debug.DebugDraw;
import org.caiopinho.scene.LevelEditorScene;
import org.caiopinho.scene.LevelScene;
import org.caiopinho.scene.Scene;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

public class Window {
	private int width, height;
	private String title;
	private long glfwWindow;
	private ImGUILayer imguiLayer;

	public float r, g, b, a;

	private static Window instance = null;

	private static Scene currentScene;

	private Window() {
		this.width = 1920;
		this.height = 1080;
		this.title = "Hello World";
		this.r = 1;
		this.b = 1;
		this.g = 1;
		this.a = 1;
	}

	public static void changeScene(int newScene) {
		switch (newScene) {
			case 0:
				currentScene = new LevelEditorScene();
				break;
			case 1:
				currentScene = new LevelScene();
				break;
			default:
				assert false : "Unknown scene '" + newScene + "'";
				break;
		}
		currentScene.load();
		currentScene.init();
		currentScene.start();
	}

	public static Window get() {
		if (instance == null) {
			instance = new Window();
		}

		return instance;
	}

	public void run() {
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");

		this.start();
		this.loop();

		// Free the memory
		glfwFreeCallbacks(this.glfwWindow);
		glfwDestroyWindow(this.glfwWindow);

		// Terminate GLFW and the free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	public void start() {
		// Setup an error callback
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing window.
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW.");
		}

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
		glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE); // the window will be maximized

		// Create the window
		this.glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
		if (this.glfwWindow == NULL) {
			throw new IllegalStateException("Failed to create the GLFW window.");
		}

		glfwSetCursorPosCallback(this.glfwWindow, MouseListener::mousePositionCallback);
		glfwSetMouseButtonCallback(this.glfwWindow, MouseListener::mouseButtonCallback);
		glfwSetScrollCallback(this.glfwWindow, MouseListener::mouseScrollCallback);
		glfwSetKeyCallback(this.glfwWindow, KeyListener::keyCallback);
		glfwSetWindowSizeCallback(this.glfwWindow, (w, newWidth, newHeight) -> {
			Window.setWidth(newWidth);
			Window.setHeight(newHeight);
		});

		// Make the OpenGL context current
		glfwMakeContextCurrent(this.glfwWindow);
		// Enable v-sync
		glfwSwapInterval(0);

		// Make the window visible
		glfwShowWindow(this.glfwWindow);

		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		this.imguiLayer = new ImGUILayer(this.glfwWindow);
		this.imguiLayer.start();

		Window.changeScene(0);
	}

	public void loop() {
		float beginTime = (float) glfwGetTime();
		float endTime;
		float deltaTime = -1.0f;

		while (!glfwWindowShouldClose(this.glfwWindow)) {
			// Poll events
			glfwPollEvents();

			glClearColor(this.r, this.g, this.b, this.a);
			glClear(GL_COLOR_BUFFER_BIT);

			if (deltaTime >= 0) {
				DebugDraw.render();
				currentScene.update(deltaTime);
			}

			KeyListener.endFrame();
			MouseListener.endFrame();

			this.imguiLayer.update(deltaTime, currentScene);
			glfwSwapBuffers(this.glfwWindow);

			endTime = (float) glfwGetTime();
			deltaTime = endTime - beginTime;
			beginTime = endTime;

			// Poll for window events. The key callback above will only be
			// invoked during window call.
			glfwPollEvents();
		}
		currentScene.save();
	}

	public static int getWidth() {
		return get().width;
	}

	public static int getHeight() {
		return get().height;
	}

	public static String getTitle() {
		return get().title;
	}

	public static Scene getScene() {
		return currentScene;
	}

	public static void setCurrentScene(Scene currentScene) {
		Window.currentScene = currentScene;
	}

	public static void setWidth(int newWidth) {
		get().width = newWidth;
	}

	public static void setHeight(int newHeight) {
		get().height = newHeight;
	}
}
