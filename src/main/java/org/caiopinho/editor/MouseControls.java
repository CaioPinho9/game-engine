package org.caiopinho.editor;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

import org.caiopinho.component.Component;
import org.caiopinho.core.GameObject;
import org.caiopinho.core.MouseListener;
import org.caiopinho.renderer.Window;
import org.caiopinho.scene.Scene;

public class MouseControls extends Component {
	GameObject holdingGameObject;

	public void pickGameObject(GameObject gameObject) {
		this.holdingGameObject = gameObject;
		Scene scene = Window.getScene();
		scene.addGameObjectToScene(gameObject);
		scene.activeGameObject = gameObject;
	}

	public void placeGameObject() {
		this.holdingGameObject = null;
	}

	@Override public void update(float deltaTime) {
		if (this.holdingGameObject != null) {
			this.holdingGameObject.transform.position.x = MouseListener.getOrthoX() - this.holdingGameObject.transform.scale.x / 2;
			this.holdingGameObject.transform.position.y = MouseListener.getOrthoY() - this.holdingGameObject.transform.scale.y * 1.1f;
		}

		if (MouseListener.isButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
			this.placeGameObject();
		}
	}
}
