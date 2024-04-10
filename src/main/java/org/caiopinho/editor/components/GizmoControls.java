package org.caiopinho.editor.components;

import static org.caiopinho.editor.components.DebugView.drawGameObjectSelectionSquare;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
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
import org.caiopinho.editor.gizmos.TranslateGizmo;
import org.caiopinho.renderer.Camera;
import org.caiopinho.renderer.Window;
import org.caiopinho.scene.Scene;
import org.joml.Vector4f;

public class GizmoControls extends Component {
	public static final Vector4f SELECTION_COLOR = new Vector4f(1, 0, 0, 1);
	private transient final Scene scene;
	private final Camera camera;
	@Getter private Gizmo gizmoVertical;
	@Getter private Gizmo gizmoHorizontal;
	@Getter private Gizmo gizmoCircle;
	private final List<Gizmo> gizmos = new ArrayList<>();

	private final transient List<GameObject> selectQueue;
	private transient boolean justDropped;
	private transient boolean justDoubleClicked;
	@Getter private boolean fixedMode;
	@Getter private float gizmoOffset;
	private transient GameObject target;

	private transient GizmoMode activeGizmoMode;

	public GizmoControls(Camera camera, GridTools gridTools, Scene scene) {
		this.camera = camera;
		this.scene = scene;
		this.fixedMode = false;
		this.target = null;
		this.activeGizmoMode = GizmoMode.TRANSLATE;
		this.selectQueue = new ArrayList<>();

		this.gizmoVertical = new TranslateGizmo("GizmoVertical", true, gridTools);
		this.gizmoHorizontal = new TranslateGizmo("GizmoHorizontal", false, gridTools);
		this.gizmoCircle = new RotationGizmo("GizmoCircle");

		this.gizmos.add(this.gizmoVertical);
		this.gizmos.add(this.gizmoHorizontal);
		this.gizmos.add(this.gizmoCircle);
	}

	@Override public void start() {
		super.start();
		for (Gizmo gizmo : this.gizmos) {
			gizmo.start();
		}
	}

	@Override public void update(float deltaTime) {
		if (this.target != null) {
			this.followTarget();
			this.changeGizmoMode();
			this.changeFixedMode();
		}

		if (!this.justDropped) {
			this.selectGameObject();
			this.handleDoubleClick();
		}

		if (this.target != null) {
			drawGameObjectSelectionSquare(this.target, SELECTION_COLOR);

			this.useGizmos();

			if (!this.isAnyGizmoDragging() && MouseListener.isButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
				this.placeGameObject();
			}

		}
		this.resetFlags();
	}

	private void useGizmos() {
		if (MouseListener.isButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
			for (Gizmo gizmo : this.gizmos) {
				gizmo.use();
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
				if (gameObject.isPointInsideBoxSelection(MouseListener.getOrtho())) {
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
			if (!this.selectQueue.isEmpty() && this.target == null) {
				this.setHoldingGameObject(this.selectQueue.getFirst());
				System.out.println("Selected " + this.selectQueue.getFirst().getName());
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
			System.out.println("Double clicked " + this.selectQueue.getFirst().getName());
		}
	}

	private void resetFlags() {
		if (!MouseListener.isButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
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
		if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL)) {
			this.fixedMode = !this.fixedMode;
			for (Gizmo gizmo : this.gizmos) {
				gizmo.setFixedMode(this.fixedMode);
			}
		}
	}
}
