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
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;

import lombok.Setter;

import org.caiopinho.core.KeyListener;
import org.caiopinho.core.MouseListener;
import org.caiopinho.scene.LevelEditorScene;
import org.caiopinho.scene.Scene;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

@Setter
public class Window {
	public int width = 1920, height = 1080;
	private String title = "Hello World!";

	private Scene currentScene = new LevelEditorScene();

	private static Window instance = null;
	private long glfwWindow;

	private Window() {
	}

	public static synchronized Window get() {
		if (instance == null) {
			instance = new Window();
		}
		return instance;
	}

	public static void run() {
		Window window = Window.get();

		System.out.println("Hello LWJGL " + Version.getVersion() + "!");

		start();
		loop();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window.glfwWindow);
		glfwDestroyWindow(window.glfwWindow);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private static void start() {
		Window window = Window.get();

		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing window.
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
		glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE); // the window will be maximized

		// Create the window
		window.glfwWindow = glfwCreateWindow(window.width, window.height, window.title, NULL, NULL);
		if (window.glfwWindow == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		glfwSetCursorPosCallback(window.glfwWindow, MouseListener::mousePositionCallback);
		glfwSetMouseButtonCallback(window.glfwWindow, MouseListener::mouseButtonCallback);
		glfwSetScrollCallback(window.glfwWindow, MouseListener::mouseScrollCallback);
		glfwSetKeyCallback(window.glfwWindow, KeyListener::keyCallback);

		// Get the thread stack and push a new frame
		try (MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window.glfwWindow, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
					window.glfwWindow,
					(vidmode.width() - pWidth.get(0)) / 2,
					(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window.glfwWindow);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window.glfwWindow);
	}

	private static void loop() {
		Window window = Window.get();

		float beginTime = (float) glfwGetTime();
		float endTime;
		float deltaTime = -1;

		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		window.currentScene.init();
		window.currentScene.start();

		// Set the clear color
		glClearColor(1, 1, 1, 1);

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while (!glfwWindowShouldClose(window.glfwWindow)) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

			if (deltaTime >= 0 && window.currentScene != null) {
				window.currentScene.update(deltaTime);
			}

			KeyListener.endFrame();
			MouseListener.endFrame();
			glfwSwapBuffers(window.glfwWindow); // swap the color buffers

			endTime = (float) glfwGetTime();
			deltaTime = endTime - beginTime;
			beginTime = endTime;

			// Poll for window events. The key callback above will only be
			// invoked during window call.
			glfwPollEvents();
		}
	}

	public static int getWidth() {
		Window instance = get();
		int[] widthPointer = new int[1];
		glfwGetWindowSize(instance.glfwWindow, widthPointer, new int[1]);
		instance.width = widthPointer[0];
		return instance.width;
	}

	public static int getHeight() {
		Window instance = get();
		int[] heightPointer = new int[1];
		glfwGetWindowSize(instance.glfwWindow, new int[1], heightPointer);
		instance.height = heightPointer[0];
		return instance.height;
	}

	public static String getTitle() {
		return get().title;
	}

	public static Scene getScene() {
		return get().currentScene;
	}

	public static void setCurrentScene(Scene currentScene) {
		get().currentScene = currentScene;
	}
}
