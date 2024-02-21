package org.caiopinho.editor.imgui;

import static org.lwjgl.glfw.GLFW.GLFW_ARROW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.GLFW_HAND_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_HRESIZE_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_IBEAM_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_C;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DELETE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_END;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_HOME;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_INSERT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SUPER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_PAGE_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_PAGE_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SUPER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_TAB;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_V;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Y;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Z;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_3;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_4;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_5;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_VRESIZE_CURSOR;
import static org.lwjgl.glfw.GLFW.glfwCreateStandardCursor;
import static org.lwjgl.glfw.GLFW.glfwGetClipboardString;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetCharCallback;
import static org.lwjgl.glfw.GLFW.glfwSetClipboardString;
import static org.lwjgl.glfw.GLFW.glfwSetCursor;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;

import java.util.Objects;

import org.caiopinho.core.MouseListener;
import org.caiopinho.editor.GameViewWindow;
import org.caiopinho.renderer.Window;
import org.caiopinho.scene.Scene;

import imgui.ImFontAtlas;
import imgui.ImFontConfig;
import imgui.ImGui;
import imgui.ImGuiFreeType;
import imgui.ImGuiIO;
import imgui.callback.ImStrConsumer;
import imgui.callback.ImStrSupplier;
import imgui.flag.ImGuiBackendFlags;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiKey;
import imgui.flag.ImGuiMouseCursor;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.type.ImBoolean;

public class ImGuiLayer {
	private final long glfwWindow;
	private final GameViewWindow gameViewWindow;
	// Mouse cursors provided by GLFW
	private final long[] mouseCursors = new long[ImGuiMouseCursor.COUNT];

	// LWJGL3 renderer (SHOULD be initialized)
	private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

	public ImGuiLayer(long glfwWindow) {
		this.glfwWindow = glfwWindow;
		this.gameViewWindow = new GameViewWindow();
	}

	public void update(float deltaTime, Scene currentScene) {
		this.startFrame(deltaTime);
		// Any Dear ImGui code SHOULD go between ImGui.newFrame()/ImGui.render() methods
		ImGui.newFrame();
		this.setupDockSpace();
		this.gameViewWindow.imgui();
		currentScene.sceneImgui();
		ImGui.showDemoWindow();
		ImGui.render();

		this.endFrame();
	}

	// Initialize Dear ImGui.
	public void start() {
		// IMPORTANT!!
		// This line is critical for Dear ImGui to work.
		ImGui.createContext();

		//------------------------------------------------------------
		// Initialize ImGuiIO config
		final ImGuiIO io = ImGui.getIO();

		io.setIniFilename("imgui.ini"); // We don't want to save .ini file
		io.setConfigFlags(ImGuiConfigFlags.NavEnableKeyboard); // Navigation with keyboard
		io.setConfigFlags(ImGuiConfigFlags.DockingEnable);
		io.setBackendFlags(ImGuiBackendFlags.HasMouseCursors); // Mouse cursors to display while resizing windows etc.
		io.setBackendPlatformName("imgui_java_impl_glfw");

		//------------------------------------------------------------
		// Keyboard mapping. ImGui will use those indices to peek into the io.KeysDown[] array.
		final int[] keyMap = new int[ImGuiKey.COUNT];
		keyMap[ImGuiKey.Tab] = GLFW_KEY_TAB;
		keyMap[ImGuiKey.LeftArrow] = GLFW_KEY_LEFT;
		keyMap[ImGuiKey.RightArrow] = GLFW_KEY_RIGHT;
		keyMap[ImGuiKey.UpArrow] = GLFW_KEY_UP;
		keyMap[ImGuiKey.DownArrow] = GLFW_KEY_DOWN;
		keyMap[ImGuiKey.PageUp] = GLFW_KEY_PAGE_UP;
		keyMap[ImGuiKey.PageDown] = GLFW_KEY_PAGE_DOWN;
		keyMap[ImGuiKey.Home] = GLFW_KEY_HOME;
		keyMap[ImGuiKey.End] = GLFW_KEY_END;
		keyMap[ImGuiKey.Insert] = GLFW_KEY_INSERT;
		keyMap[ImGuiKey.Delete] = GLFW_KEY_DELETE;
		keyMap[ImGuiKey.Backspace] = GLFW_KEY_BACKSPACE;
		keyMap[ImGuiKey.Space] = GLFW_KEY_SPACE;
		keyMap[ImGuiKey.Enter] = GLFW_KEY_ENTER;
		keyMap[ImGuiKey.Escape] = GLFW_KEY_ESCAPE;
		keyMap[ImGuiKey.KeyPadEnter] = GLFW_KEY_KP_ENTER;
		keyMap[ImGuiKey.A] = GLFW_KEY_A;
		keyMap[ImGuiKey.C] = GLFW_KEY_C;
		keyMap[ImGuiKey.V] = GLFW_KEY_V;
		keyMap[ImGuiKey.X] = GLFW_KEY_X;
		keyMap[ImGuiKey.Y] = GLFW_KEY_Y;
		keyMap[ImGuiKey.Z] = GLFW_KEY_Z;
		io.setKeyMap(keyMap);

		//------------------------------------------------------------
		// Mouse cursors mapping
		this.mouseCursors[ImGuiMouseCursor.Arrow] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
		this.mouseCursors[ImGuiMouseCursor.TextInput] = glfwCreateStandardCursor(GLFW_IBEAM_CURSOR);
		this.mouseCursors[ImGuiMouseCursor.ResizeAll] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
		this.mouseCursors[ImGuiMouseCursor.ResizeNS] = glfwCreateStandardCursor(GLFW_VRESIZE_CURSOR);
		this.mouseCursors[ImGuiMouseCursor.ResizeEW] = glfwCreateStandardCursor(GLFW_HRESIZE_CURSOR);
		this.mouseCursors[ImGuiMouseCursor.ResizeNESW] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
		this.mouseCursors[ImGuiMouseCursor.ResizeNWSE] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
		this.mouseCursors[ImGuiMouseCursor.Hand] = glfwCreateStandardCursor(GLFW_HAND_CURSOR);
		this.mouseCursors[ImGuiMouseCursor.NotAllowed] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);

		//------------------------------------------------------------
		// GLFW callbacks to handle user input

		glfwSetKeyCallback(this.glfwWindow, (w, key, scancode, action, mods) -> {
			if (action == GLFW_PRESS) {
				io.setKeysDown(key, true);
			} else if (action == GLFW_RELEASE) {
				io.setKeysDown(key, false);
			}

			io.setKeyCtrl(io.getKeysDown(GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW_KEY_RIGHT_CONTROL));
			io.setKeyShift(io.getKeysDown(GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW_KEY_RIGHT_SHIFT));
			io.setKeyAlt(io.getKeysDown(GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW_KEY_RIGHT_ALT));
			io.setKeySuper(io.getKeysDown(GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW_KEY_RIGHT_SUPER));
		});

		glfwSetCharCallback(this.glfwWindow, (w, c) -> {
			if (c != GLFW_KEY_DELETE) {
				io.addInputCharacter(c);
			}
		});

		glfwSetMouseButtonCallback(this.glfwWindow, (w, button, action, mods) -> {
			final boolean[] mouseDown = new boolean[5];

			mouseDown[0] = button == GLFW_MOUSE_BUTTON_1 && action != GLFW_RELEASE;
			mouseDown[1] = button == GLFW_MOUSE_BUTTON_2 && action != GLFW_RELEASE;
			mouseDown[2] = button == GLFW_MOUSE_BUTTON_3 && action != GLFW_RELEASE;
			mouseDown[3] = button == GLFW_MOUSE_BUTTON_4 && action != GLFW_RELEASE;
			mouseDown[4] = button == GLFW_MOUSE_BUTTON_5 && action != GLFW_RELEASE;

			io.setMouseDown(mouseDown);

			if (!io.getWantCaptureMouse() && mouseDown[1]) {
				ImGui.setWindowFocus(null);
			}

			if (!io.getWantCaptureMouse() || this.gameViewWindow.getWantCaptureMouse()) {
				MouseListener.mouseButtonCallback(w, button, action, mods);
			}
		});

		glfwSetScrollCallback(this.glfwWindow, (w, xOffset, yOffset) -> {
			io.setMouseWheelH(io.getMouseWheelH() + (float) xOffset);
			io.setMouseWheel(io.getMouseWheel() + (float) yOffset);
		});

		io.setSetClipboardTextFn(new ImStrConsumer() {
			@Override
			public void accept(final String s) {
				glfwSetClipboardString(ImGuiLayer.this.glfwWindow, s);
			}
		});

		io.setGetClipboardTextFn(new ImStrSupplier() {
			@Override
			public String get() {
				final String clipboardString = glfwGetClipboardString(ImGuiLayer.this.glfwWindow);
				return Objects.requireNonNullElse(clipboardString, "");
			}
		});

		//------------------------------------------------------------
		// Fonts configuration
		// Read: https://raw.githubusercontent.com/ocornut/imgui/master/docs/FONTS.txt

		final ImFontAtlas fontAtlas = io.getFonts();
		final ImFontConfig fontConfig = new ImFontConfig(); // Natively allocated object, should be explicitly destroyed

		// Glyphs could be added per-font as well as per config used globally like here
		fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());

		// Fonts merge example
		fontConfig.setPixelSnapH(true);

		fontAtlas.addFontFromFileTTF("assets/fonts/Roboto-Regular.ttf", 16, fontConfig);

		fontConfig.setMergeMode(false);
		fontConfig.setPixelSnapH(false);

		fontConfig.destroy(); // After all fonts were added we don't need this config more

		//------------------------------------------------------------
		// Use freetype instead of stb_truetype to build a fonts texture
		ImGuiFreeType.buildFontAtlas(fontAtlas, ImGuiFreeType.RasterizerFlags.LightHinting);

		// Method initializes LWJGL3 renderer.
		// This method SHOULD be called after you've initialized your ImGui configuration (fonts and so on).
		// ImGui context should be created as well.
		this.imGuiGl3.init("#version 330 core");
	}

	private void startFrame(final float deltaTime) {
		double[] mousePosX = { 0 };
		double[] mousePosY = { 0 };

		float windowWidth = Window.getWidth();
		float windowHeight = Window.getHeight();

		// Get window properties and mouse position
		glfwGetCursorPos(this.glfwWindow, mousePosX, mousePosY);

		// We SHOULD call those methods to update Dear ImGui state for the current frame
		final ImGuiIO io = ImGui.getIO();
		io.setDisplaySize(windowWidth, windowHeight);
		io.setDisplayFramebufferScale(1, 1);
		io.setMousePos((float) mousePosX[0], (float) mousePosY[0]);
		io.setDeltaTime(deltaTime);

		// Update the mouse cursor
		final int imguiCursor = ImGui.getMouseCursor();
		glfwSetCursor(this.glfwWindow, this.mouseCursors[imguiCursor]);
		glfwSetInputMode(this.glfwWindow, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
	}

	private void endFrame() {
		// After Dear ImGui prepared a draw data, we use it in the LWJGL3 renderer.
		// At that moment ImGui will be rendered to the current OpenGL context.
		this.imGuiGl3.render(ImGui.getDrawData());
	}

	// If you want to clean a room after yourself - do it by yourself
	private void destroyImGui() {
		this.imGuiGl3.dispose();
		ImGui.destroyContext();
	}

	private void setupDockSpace() {
		int windowFlags = ImGuiWindowFlags.MenuBar;
		windowFlags |= ImGuiWindowFlags.NoDocking;
		windowFlags |= ImGuiWindowFlags.NoTitleBar;
		windowFlags |= ImGuiWindowFlags.NoCollapse;
		windowFlags |= ImGuiWindowFlags.NoResize;
		windowFlags |= ImGuiWindowFlags.NoMove;
		windowFlags |= ImGuiWindowFlags.NoBringToFrontOnFocus;
		windowFlags |= ImGuiWindowFlags.NoNavFocus;

		ImGui.setNextWindowPos(0, 0, ImGuiCond.Always);
		ImGui.setNextWindowSize(Window.getWidth(), Window.getHeight());
		ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
		ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);

		ImGui.begin("DockSpace", new ImBoolean(true), windowFlags);
		ImGui.popStyleVar(2);

		// DockSpace
		ImGui.dockSpace(ImGui.getID("DockSpace"));
		ImGui.end();
	}

}
