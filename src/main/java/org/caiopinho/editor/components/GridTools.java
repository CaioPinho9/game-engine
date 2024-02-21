package org.caiopinho.editor.components;

import lombok.Getter;
import lombok.Setter;

import org.caiopinho.component.Component;
import org.caiopinho.renderer.Camera;
import org.caiopinho.renderer.Window;
import org.caiopinho.renderer.debug.DebugDraw;
import org.joml.Vector2f;
import org.joml.Vector4f;

@Getter
@Setter
public class GridTools extends Component {
	private float width = 3;
	private int zIndex = -1;
	private int gridSize = 30;
	private boolean enabled = true;
	private Vector4f color = new Vector4f(.4f, .4f, .4f, 1f);

	@Override public void update(float deltaTime) {
		if (this.enabled) {
			this.drawGrid();
		}
	}

	public void drawGrid() {
		Camera camera = Window.getScene().getCamera();
		Vector2f cameraPosition = camera.getPosition();
		Vector2f projectionSize = camera.getProjectionSize();

		int firstX = (((int) cameraPosition.x / this.gridSize) - 1) * this.gridSize;
		int firstY = (((int) cameraPosition.y / this.gridSize) - 1) * this.gridSize;

		int lineVerticalCount = (int) (projectionSize.x / this.gridSize) + 2;
		int lineHorizontalCount = (int) (projectionSize.y / this.gridSize) + 2;
		int maxLineCount = Math.max(lineHorizontalCount, lineVerticalCount);

		float width = projectionSize.x + this.gridSize * 2;
		float height = projectionSize.y + this.gridSize * 2;

		int x = firstX;
		int y = firstY;
		for (int i = 0; i < maxLineCount; i++) {
			if (i < lineVerticalCount) {
				DebugDraw.addLine2D(new Vector2f(x, firstY), new Vector2f(x, firstY + height), this.color, 1, this.width, this.zIndex);
			}

			if (i < lineHorizontalCount) {
				DebugDraw.addLine2D(new Vector2f(firstX, y), new Vector2f(firstX + width, y), this.color, 1, this.width, this.zIndex);
			}

			x += this.gridSize;
			y += this.gridSize;
		}
	}
}
