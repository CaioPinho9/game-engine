package org.caiopinho.editor.components;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_HOME;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

import org.caiopinho.component.Component;
import org.caiopinho.core.KeyListener;
import org.caiopinho.core.MouseListener;
import org.caiopinho.renderer.Camera;
import org.joml.Vector2f;

public class CameraControls extends Component {
	public static final int DRAG_SENSITIVITY = 200;
	private static final float SCROLL_SENSITIVITY = .1f;
	private static final float MOUSE_SHIFT_SENSITIVITY = 50;
	private transient final Camera camera;
	private transient boolean isGoingToStartPosition;
	private transient float lerpTime = 0;

	public CameraControls(Camera camera) {
		this.camera = camera;
	}

	@Override
	public void update(float deltaTime) {
		if (MouseListener.isButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
			this.dragCamera(deltaTime);
		}

		if (MouseListener.getScrollY() != 0.0f) {
			float zoom = this.camera.getZoom();

			float addValue = -MouseListener.getScrollY() * SCROLL_SENSITIVITY * zoom;

			Vector2f viewCenterBeforeZoom = this.camera.calculateViewCenter();

			this.camera.addZoom(addValue);
			this.centralizeTowardMouse(viewCenterBeforeZoom);
		}

		if (KeyListener.isKeyPressed(GLFW_KEY_HOME)) {
			this.isGoingToStartPosition = true;
		}

		if (this.isGoingToStartPosition) {
			this.camera.position.lerp(new Vector2f(), this.lerpTime);
			this.camera.setZoom(this.camera.getZoom() + ((1.0f - this.camera.getZoom()) * this.lerpTime));
			this.lerpTime += 0.1f * deltaTime;
			if (Math.abs(this.camera.position.x) <= 5.0f && Math.abs(this.camera.position.y) <= 5.0f) {
				this.lerpTime = 0.0f;
				this.camera.position.set(0f, 0f);
				this.camera.setZoom(1.0f);
				this.isGoingToStartPosition = false;
			}
		}

	}

	private void centralizeTowardMouse(Vector2f viewCenterBeforeZoom) {
		Vector2f position = this.camera.getPosition();
		float zoom = this.camera.getZoom();

		Vector2f mousePosition = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());

		Vector2f viewCenterAfterZoom = this.camera.calculateViewCenter();

		Vector2f centerShift = viewCenterBeforeZoom.sub(viewCenterAfterZoom);

		Vector2f mouseShift = this.calculateMouseShift(mousePosition, zoom);

		position.add(centerShift.add(mouseShift));
	}

	private Vector2f calculateMouseShift(Vector2f mousePosition, float zoom) {
		Vector2f mouseShift = this.camera.normalizeScreenCoordinates(mousePosition.x, mousePosition.y);
		return mouseShift.mul(MOUSE_SHIFT_SENSITIVITY * zoom);
	}

	private void dragCamera(float deltaTime) {
		float x = MouseListener.getDeltaX() * DRAG_SENSITIVITY * this.camera.getZoom();
		float y = MouseListener.getDeltaY() * -DRAG_SENSITIVITY * this.camera.getZoom();

		Vector2f delta = new Vector2f(x, y);
		this.camera.position.add(delta.x * deltaTime, delta.y * deltaTime);
	}

}
