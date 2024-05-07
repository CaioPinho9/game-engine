package org.caiopinho.editor.components;

import lombok.Getter;
import lombok.Setter;

import org.caiopinho.component.Component;
import org.caiopinho.core.GameObject;
import org.caiopinho.core.Transform;
import org.caiopinho.renderer.debug.DebugDraw;
import org.caiopinho.scene.Scene;
import org.joml.Vector2f;
import org.joml.Vector4f;

@Getter
@Setter
public class DebugView extends Component {
	private static final int SELECTION_Z_INDEX = 30;
	private static final float SELECTION_LINE_WIDTH = 2;
	private static final Vector4f SELECTION_COLOR = new Vector4f(0, 1, 0, 1);
	private transient final Scene scene;
	private boolean enabled = true;

	public DebugView(Scene scene) {
		this.scene = scene;
	}

	@Override
	public void update(float deltaTime) {
		if (this.enabled) {
			this.drawDebugView();
		}
	}

	private void drawDebugView() {
		for (GameObject gameObject : this.scene.getGameObjects()) {
			if (gameObject.isSelectable()) {
				drawGameObjectSelectionSquare(gameObject, SELECTION_COLOR);
			}
		}
	}

	public static void drawGameObjectSelectionSquare(GameObject gameObject, Vector4f color) {
		Vector2f position = new Vector2f(gameObject.transform.position.x, gameObject.transform.position.y);
		int zIndex = color == SELECTION_COLOR ? SELECTION_Z_INDEX : SELECTION_Z_INDEX + 1;
		Transform transform = new Transform(position, gameObject.getBoxSelectionScale(), 0, zIndex);
		DebugDraw.addBox2D(transform, color, 1, SELECTION_LINE_WIDTH);
	}

}
