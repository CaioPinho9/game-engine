package org.caiopinho.editor.components;

import static org.caiopinho.editor.components.DebugView.drawGameObjectSelectionSquare;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import org.caiopinho.component.Component;
import org.caiopinho.core.GameObject;
import org.caiopinho.core.KeyListener;
import org.caiopinho.core.MouseListener;
import org.caiopinho.editor.gizmos.Gizmo;
import org.caiopinho.editor.gizmos.GizmoMode;
import org.caiopinho.editor.gizmos.RotationGizmo;
import org.caiopinho.editor.gizmos.ScaleGizmo;
import org.caiopinho.editor.gizmos.TranslateGizmo;
import org.caiopinho.renderer.Camera;
import org.caiopinho.renderer.Window;
import org.caiopinho.scene.Scene;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class GizmoControls extends Component {
	public static final Vector4f SELECTION_COLOR = new Vector4f(1, 0, 0, 1);

	private transient final Scene scene;
	private transient final Camera camera;
	@Getter private transient final List<Gizmo> gizmos = new ArrayList<>();

	@Getter private transient final Gizmo gizmoTranslateVertical;
	@Getter private transient final Gizmo gizmoTranslateHorizontal;
	@Getter private transient final Gizmo gizmoScaleVertical;
	@Getter private transient final Gizmo gizmoScaleHorizontal;
	@Getter private transient final Gizmo gizmoCircle;

	private transient boolean justSelected;
	private transient boolean justDropped;
	private transient boolean justDoubleClicked;
	private transient boolean wasControlPressed = false;
	private transient boolean wasShiftPressed = false;
	private transient long lastControlToggleTime = 0;
	private transient long lastShiftToggleTime = 0;
	private static final long TOGGLE_DELAY = 100;


	private transient GameObject target;
	private transient final List<GameObject> selectQueue;

	private boolean fixedMode;
	private boolean aspectRatioLockMode;
	private transient GizmoMode activeGizmoMode;

	public GizmoControls(Camera camera, GridTools gridTools, Scene scene) {
		this.camera = camera;
		this.scene = scene;
		this.fixedMode = false;
		this.aspectRatioLockMode = false;
		this.target = null;
		this.activeGizmoMode = GizmoMode.TRANSLATE;
		this.selectQueue = new ArrayList<>();

		this.gizmoTranslateVertical = new TranslateGizmo("GizmoTranslateVertical", true, gridTools);
		this.gizmoTranslateHorizontal = new TranslateGizmo("GizmoTranslateHorizontal", false, gridTools);
		this.gizmoScaleVertical = new ScaleGizmo("GizmoScaleVertical", true, gridTools);
		this.gizmoScaleHorizontal = new ScaleGizmo("GizmoScaleHorizontal", false, gridTools);
		this.gizmoCircle = new RotationGizmo("GizmoCircle");

		this.gizmos.add(this.gizmoTranslateVertical);
		this.gizmos.add(this.gizmoTranslateHorizontal);
		this.gizmos.add(this.gizmoScaleVertical);
		this.gizmos.add(this.gizmoScaleHorizontal);
		this.gizmos.add(this.gizmoCircle);
	}

	@Override
	public void start() {
		super.start();
		for (Gizmo gizmo : this.gizmos) {
			gizmo.start();
		}
	}

	@Override
	public void update(float deltaTime) {
		if (this.target != null) {
			this.followTarget();
			this.changeGizmoMode();
			this.changeFixedMode();
			this.changeAspectRatioLockMode();
		}

		if (!this.justDropped) {
			this.selectGameObject();
			this.handleDoubleClick();
		}

		if (this.target != null) {
			drawGameObjectSelectionSquare(this.target, SELECTION_COLOR);

			this.checkActiveGizmo();

			this.useGizmos();

			if (!this.justSelected && MouseListener.isButtonDown(GLFW_MOUSE_BUTTON_LEFT) && !this.isAnyGizmoDragging()) {
				this.placeGameObject();
			}
		}

		this.resetFlags();
	}

	private void checkActiveGizmo() {
		if (MouseListener.isButtonDown(GLFW_MOUSE_BUTTON_LEFT) && !isAnyGizmoDragging()) {
			for (Gizmo gizmo : this.gizmos) {
				boolean wasDragging = gizmo.isDragging();
				gizmo.setDragging(gizmo.isActive() && gizmo.isPointInsideBoxSelection(new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY())));
				if (wasDragging || !gizmo.isDragging() || !(gizmo instanceof TranslateGizmo)) {
					continue;
				}

				if (((TranslateGizmo) gizmo).isVertical()) {
					gizmo.setGizmoOffset(MouseListener.getOrthoY() - gizmo.transform.position.y + gizmo.transform.scale.y / 2);
				} else {
					gizmo.setGizmoOffset(MouseListener.getOrthoX() - gizmo.transform.position.x + gizmo.transform.scale.x / 2);
				}
			}
		}

		if (MouseListener.isButtonReleased(GLFW_MOUSE_BUTTON_LEFT)) {
			for (Gizmo gizmo : this.gizmos) {
				gizmo.setDragging(false);
			}
		}
	}

	private void useGizmos() {
		if (MouseListener.isButtonPressed(GLFW_MOUSE_BUTTON_LEFT)) {
			for (Gizmo gizmo : this.gizmos) {
				if (gizmo.isDragging()) {
					gizmo.use();
				}
			}
		}
	}

	private void toggleGizmos() {
		for (Gizmo gizmo : this.gizmos) {
			gizmo.setActive(this.activeGizmoMode);
		}
	}

	private void followTarget() {
		for (Gizmo gizmo : this.gizmos) {
			gizmo.followTarget(this.camera.getZoom());
		}
	}

	public void setTarget(GameObject target) {
		this.target = target;
		for (Gizmo gizmo : this.gizmos) {
			gizmo.setTarget(this.target);
		}

		this.toggleGizmos();
		this.followTarget();
	}

	public void endTarget() {
		this.target = null;
		for (Gizmo gizmo : this.gizmos) {
			gizmo.setTarget(null);
			gizmo.setActive(false);
		}
	}

	public boolean isAnyGizmoDragging() {
		for (Gizmo gizmo : this.gizmos) {
			if (gizmo.isDragging()) {
				return true;
			}
		}
		return false;
	}

	public void setHoldingGameObject(GameObject gameObject) {
		this.target = gameObject;
		this.scene.activeGameObject = gameObject;
		this.setTarget(gameObject);
	}

	public void placeGameObject() {
		this.target = null;
		this.justDropped = true;
		this.endTarget();
	}

	public void selectGameObject() {
		if (MouseListener.isButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
			// Check every game object in the scene to see if the mouse is inside its box
			for (GameObject gameObject : Window.getScene().getGameObjects()) {
				if (!gameObject.isSelectable()) {
					continue;
				}

				// Check if is inside the box
				if (gameObject.isPointInsideBoxSelection(new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY()))) {
					// If the game object is not in the select queue, add it, this is done so the queue remains in the same order as before
					if (this.selectQueue.contains(gameObject)) {
						continue;
					}
					// Insert in the queue in the correct position, according to the z-index
					boolean added = false;
					for (int i = 0; i < this.selectQueue.size(); i++) {
						GameObject object = this.selectQueue.get(i);
						if (object.getZIndex() <= gameObject.getZIndex()) {
							this.selectQueue.add(i, gameObject);
							added = true;
							break;
						}
					}
					if (!added) {
						this.selectQueue.add(gameObject);
					}
				} else {
					// If the game object is not inside the box anymore, remove it from the queue
					this.selectQueue.remove(gameObject);
				}
			}
			// The holding object is the first of the queue
			if (!this.justSelected && !this.selectQueue.isEmpty() && this.target == null) {
				this.setHoldingGameObject(this.selectQueue.getFirst());
				this.justSelected = true;
			}
		}
	}

	private void handleDoubleClick() {
		// When double clicked the first object in the queue is moved to the end, and the holding object is set to the first
		if (!this.justDoubleClicked && MouseListener.isDoubleClick(GLFW_MOUSE_BUTTON_LEFT) && this.selectQueue.size() >= 2) {
			GameObject gameObject = this.selectQueue.removeFirst();
			this.selectQueue.add(gameObject); // Move the first object to the end.
			this.setHoldingGameObject(this.selectQueue.getFirst()); // Set the new first object as the holding object.
			this.justDoubleClicked = true;
		}
	}

	private void resetFlags() {
		if (!MouseListener.isButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
			this.justSelected = false;
			this.justDropped = false;

			if (!MouseListener.isDoubleClick(GLFW_MOUSE_BUTTON_LEFT)) {
				this.justDoubleClicked = false;
			}
		}
	}

	private void changeGizmoMode() {
		if (KeyListener.isKeyPressed(GLFW_KEY_1)) {
			this.activeGizmoMode = GizmoMode.TRANSLATE;
		} else if (KeyListener.isKeyPressed(GLFW_KEY_2)) {
			this.activeGizmoMode = GizmoMode.ROTATE;
		} else if (KeyListener.isKeyPressed(GLFW_KEY_3)) {
			this.activeGizmoMode = GizmoMode.SCALE;
		}
		this.toggleGizmos();
	}

	private void changeFixedMode() {
		long currentTime = System.currentTimeMillis();
		boolean isControlPressed = KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL);

		// Check if the key state has changed from not pressed to pressed
		if (isControlPressed && !wasControlPressed) {
			if (currentTime - lastControlToggleTime > TOGGLE_DELAY) {
				this.fixedMode = !this.fixedMode;
				this.toggleFixedMode();
				lastControlToggleTime = currentTime;
			}
		}

		// Update the previously pressed state
		wasControlPressed = isControlPressed;
	}

	private void changeAspectRatioLockMode() {
		long currentTime = System.currentTimeMillis();
		boolean isShiftPressed = KeyListener.isKeyPressed(GLFW_KEY_LEFT_SHIFT);

		// Check if the key state has changed from not pressed to pressed
		if (isShiftPressed && !wasShiftPressed) {
			if (currentTime - lastShiftToggleTime > TOGGLE_DELAY) {
				this.aspectRatioLockMode = !this.aspectRatioLockMode;
				this.toggleAspectRatioLockMode();
				lastShiftToggleTime = currentTime;
			}
		}

		// Update the previously pressed state
		wasShiftPressed = isShiftPressed;
	}

	private void toggleFixedMode() {
		for (Gizmo gizmo : this.gizmos) {
			gizmo.setFixedMode(this.fixedMode);
		}
	}

	private void toggleAspectRatioLockMode() {
		for (Gizmo gizmo : this.gizmos) {
			gizmo.setAspectRatioLockMode(this.aspectRatioLockMode);
		}
	}
}
