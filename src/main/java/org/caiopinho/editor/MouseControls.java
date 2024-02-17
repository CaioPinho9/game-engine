package org.caiopinho.editor;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

import org.caiopinho.component.Component;
import org.caiopinho.core.GameObject;
import org.caiopinho.core.MouseListener;
import org.caiopinho.renderer.Camera;
import org.caiopinho.renderer.Window;
import org.caiopinho.scene.Scene;

public class MouseControls extends Component {
	private transient GameObject holdingGameObject;
	private boolean gridModeEnabled = false;

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
			GridTools gridTools = this.gameObject.getComponent(GridTools.class);

			if (this.gridModeEnabled) {
				this.holdingGameObject.transform.position.x = (int) (MouseListener.getOrthoX() / gridTools.getGridSize()) * gridTools.getGridSize();
				this.holdingGameObject.transform.position.y = (int) (MouseListener.getOrthoY() / gridTools.getGridSize()) * gridTools.getGridSize();
			} else {
				this.holdingGameObject.transform.position.x = MouseListener.getOrthoX() - this.holdingGameObject.transform.scale.x / 2;
				this.holdingGameObject.transform.position.y = MouseListener.getOrthoY() - this.holdingGameObject.transform.scale.y * 1.1f;
			}

			if (MouseListener.isButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
				this.placeGameObject();
			}
		}

		this.moveCamera(deltaTime);
	}

	private void moveCamera(float deltaTime) {
		if (MouseListener.isButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
			float x = MouseListener.getDeltaX() * 100;
			float y = MouseListener.getDeltaY() * -100;

			Camera camera = Window.getScene().getCamera();
			camera.addDeltaMoveCamera(x, y, deltaTime);
		}
	}
}
