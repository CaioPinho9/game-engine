package org.caiopinho.editor;

import lombok.Getter;

import org.caiopinho.core.MouseListener;
import org.caiopinho.editor.imgui.ImGuiHelper;
import org.caiopinho.renderer.Window;
import org.joml.Vector2f;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;

public class GameViewWindow {
	private float leftX, rightX, topY, bottomY;
	@Getter private Vector2f gameViewportPosition = new Vector2f();
	@Getter private Vector2f gameViewportSize = new Vector2f();

	public void imgui() {
		int windowFlags = ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse;
		ImGui.begin("Game Viewport", windowFlags);

		ImVec2 viewportPanelSize = getViewportPanelSize();
		ImVec2 viewportPosition = this.getCenteredPositionViewport(viewportPanelSize);

		ImGui.setCursorPos(viewportPosition.x, viewportPosition.y);

		ImVec2 topLeft = new ImVec2();
		ImGui.getCursorScreenPos(topLeft);
		topLeft.x -= ImGui.getScrollX();
		topLeft.y -= ImGui.getScrollY();

		this.gameViewportPosition = new Vector2f(topLeft.x, topLeft.y);
		this.gameViewportSize = new Vector2f(viewportPanelSize.x, viewportPanelSize.y);
		MouseListener.setGameViewportPosition(this.gameViewportPosition);
		MouseListener.setGameViewportSize(this.gameViewportSize);

		this.leftX = topLeft.x;
		this.bottomY = topLeft.y;
		this.rightX = topLeft.x + viewportPanelSize.x;
		this.topY = topLeft.y + viewportPanelSize.y;

		int textureId = Window.getFramebuffer().getTextureId();
		ImGui.image(textureId, viewportPanelSize.x, viewportPanelSize.y, 0, 1, 1, 0);

		ImGui.end();
	}

	private static ImVec2 getViewportPanelSize() {
		ImVec2 windowSize = ImGuiHelper.getWindowSizeNoScroll();

		float aspectWidth = windowSize.x;

		float aspectHeight = aspectWidth / Window.getAspectRatio();
		if (aspectHeight > windowSize.y) {
			aspectHeight = windowSize.y;
			aspectWidth = aspectHeight * Window.getAspectRatio();
		}

		return new ImVec2(aspectWidth, aspectHeight);
	}

	private ImVec2 getCenteredPositionViewport(ImVec2 viewportSize) {
		ImVec2 windowSize = ImGuiHelper.getWindowSizeNoScroll();

		float xPos = (windowSize.x - viewportSize.x) * 0.5f + ImGui.getCursorPosX();
		float yPos = (windowSize.y - viewportSize.y) * 0.5f + ImGui.getCursorPosY();

		return new ImVec2(xPos, yPos);
	}

	public boolean getWantCaptureMouse() {
		return MouseListener.getX() >= this.leftX && MouseListener.getX() <= this.rightX && MouseListener.getY() >= this.bottomY && MouseListener.getY() <= this.topY;
	}
}
