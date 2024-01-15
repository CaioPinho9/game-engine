package org.caiopinho.core;

import lombok.Setter;

import org.caiopinho.scene.LevelEditorScene;
import org.caiopinho.scene.Scene;
import org.lwjgl.Version;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

@Setter
public class Window {
	private int width = 1366, height = 720;
	private String title = "Hello World!";

	private static Scene currentScene = new LevelEditorScene();

	private static Window instance = null;
	private long glfwWindow;

	private Window() {
	}

	public static synchronized Window get() {
		if (instance == null)
			instance = new Window();
		return instance;
	}

	public void run() {
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");

		start();
		loop();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(glfwWindow);
		glfwDestroyWindow(glfwWindow);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private void start() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
		glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE); // the window will be maximized

		// Create the window
		glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
		if (glfwWindow == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePositionCallback);
		glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
		glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
		glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

		// Get the thread stack and push a new frame
		try (MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(glfwWindow, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
					glfwWindow,
					(vidmode.width() - pWidth.get(0)) / 2,
					(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(glfwWindow);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(glfwWindow);

	}

	private void loop() {
		float beginTime = (float) glfwGetTime();
		float endTime;
		float deltaTime = -1.0f;

		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		currentScene.start();

		// Set the clear color
		glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while (!glfwWindowShouldClose(glfwWindow)) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

			if (deltaTime >= 0 && currentScene != null)
				currentScene.update(deltaTime);

			KeyListener.endFrame();
			MouseListener.endFrame();
			glfwSwapBuffers(glfwWindow); // swap the color buffers

			endTime = (float) glfwGetTime();
			deltaTime = endTime - beginTime;
			beginTime = endTime;

			// Poll for window events. The key callback above will only be
			// invoked during this call.
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

	public static void setCurrentScene(Scene currentScene) {
		Window.currentScene = currentScene;
	}
}
