package org.caiopinho.editor;

import lombok.Getter;

import org.caiopinho.core.MouseListener;
import org.caiopinho.renderer.Window;
import org.joml.Vector2f;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;

public class GameViewWindow {
	private static float leftX, rightX, topY, bottomY;
	@Getter private static Vector2f gameViewportPosition = new Vector2f();
	@Getter private static Vector2f gameViewportSize = new Vector2f();

	public static void imgui() {
		int windowFlags = ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse;
		ImGui.begin("Game Viewport", windowFlags);

		ImVec2 viewportPanelSize = getViewportPanelSize();
		ImVec2 viewportPosition = getCenteredPositionViewport(viewportPanelSize);

		ImGui.setCursorPos(viewportPosition.x, viewportPosition.y);

		ImVec2 topLeft = new ImVec2();
		ImGui.getCursorScreenPos(topLeft);
		topLeft.x -= ImGui.getScrollX();
		topLeft.y -= ImGui.getScrollY();

		gameViewportPosition = new Vector2f(topLeft.x, topLeft.y);
		gameViewportSize = new Vector2f(viewportPanelSize.x, viewportPanelSize.y);
		MouseListener.setGameViewportPosition(gameViewportPosition);
		MouseListener.setGameViewportSize(gameViewportSize);

		leftX = topLeft.x;
		bottomY = topLeft.y;
		rightX = topLeft.x + viewportPanelSize.x;
		topY = topLeft.y + viewportPanelSize.y;

		int textureId = Window.getFramebuffer().getTextureId();
		ImGui.image(textureId, viewportPanelSize.x, viewportPanelSize.y, 0, 1, 1, 0);

		ImGui.end();
	}

	private static ImVec2 getViewportPanelSize() {
		ImVec2 windowSize = ImGUILayer.getWindowSizeNoScroll();

		float aspectWidth = windowSize.x;

		float aspectHeight = aspectWidth / Window.getAspectRatio();
		if (aspectHeight > windowSize.y) {
			aspectHeight = windowSize.y;
			aspectWidth = aspectHeight * Window.getAspectRatio();
		}

		return new ImVec2(aspectWidth, aspectHeight);
	}

	private static ImVec2 getCenteredPositionViewport(ImVec2 viewportSize) {
		ImVec2 windowSize = ImGUILayer.getWindowSizeNoScroll();

		float xPos = (windowSize.x - viewportSize.x) * 0.5f + ImGui.getCursorPosX();
		float yPos = (windowSize.y - viewportSize.y) * 0.5f + ImGui.getCursorPosY();

		return new ImVec2(xPos, yPos);
	}

	public static boolean getWantCaptureMouse() {
		return MouseListener.getX() >= leftX && MouseListener.getX() <= rightX && MouseListener.getY() >= bottomY && MouseListener.getY() <= topY;
	}
}
