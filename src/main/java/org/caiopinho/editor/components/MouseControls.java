package org.caiopinho.editor.components;

import static org.caiopinho.editor.components.DebugView.drawGameObjectSelectionSquare;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.caiopinho.component.Component;
import org.caiopinho.core.GameObject;
import org.caiopinho.core.MouseListener;
import org.caiopinho.editor.TranslateGizmos;
import org.caiopinho.renderer.Window;
import org.caiopinho.scene.Scene;
import org.joml.Vector4f;

public class MouseControls extends Component {
	public static final Vector4f SELECTION_COLOR = new Vector4f(1, 0, 0, 1);
	private transient final TranslateGizmos translateGizmos;
	private transient final GridTools gridTools;
	private transient final Scene scene;
	private transient GameObject holdingGameObject;
	private final transient List<GameObject> selectQueue;
	private transient boolean justSelected;
	private transient boolean justDropped;
	private transient boolean justDoubleClicked;
	@Getter @Setter private boolean gridModeEnabled;

	public MouseControls(TranslateGizmos translateGizmos, GridTools gridTools, Scene scene) {
		this.translateGizmos = translateGizmos;
		this.gridTools = gridTools;
		this.scene = scene;
		this.holdingGameObject = null;
		this.gridModeEnabled = false;
		this.selectQueue = new ArrayList<>();
	}

	public void setHoldingGameObject(GameObject gameObject) {
		this.holdingGameObject = gameObject;
		this.scene.activeGameObject = gameObject;
		this.translateGizmos.setTarget(gameObject);
	}

	public void placeGameObject() {
		this.holdingGameObject = null;
		this.justDropped = true;
		this.translateGizmos.endTarget();
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
			if (!this.selectQueue.isEmpty() && this.holdingGameObject == null) {
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
			this.justSelected = true;
			this.justDoubleClicked = true;
		}
	}

	@Override public void update(float deltaTime) {
		if (!this.justDropped) {
			this.selectGameObject();
			this.handleDoubleClick();
		}
		if (this.holdingGameObject != null) {
			drawGameObjectSelectionSquare(this.holdingGameObject, SELECTION_COLOR);

			if (MouseListener.isButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
				this.moveObject();
			}

			if (!this.justSelected && !this.translateGizmos.getIsDragging() && MouseListener.isButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
				this.placeGameObject();
			}

		}
		if (!MouseListener.isButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
			this.justDropped = false;
			this.translateGizmos.getGizmoHorizontal().isDragging &= this.translateGizmos.getGizmoHorizontal().isPointInsideBoxSelection(MouseListener.getOrtho());
			this.translateGizmos.getGizmoVertical().isDragging &= this.translateGizmos.getGizmoVertical().isPointInsideBoxSelection(MouseListener.getOrtho());

			if (!MouseListener.isDoubleClick(GLFW_MOUSE_BUTTON_LEFT)) {
				this.justSelected = false;
				this.justDoubleClicked = false;
			}
		}
	}

	private void moveObject() {
		this.translateGizmos.getGizmoHorizontal().isDragging |= this.translateGizmos.getGizmoHorizontal().isPointInsideBoxSelection(MouseListener.getOrtho());
		this.translateGizmos.getGizmoVertical().isDragging |= this.translateGizmos.getGizmoVertical().isPointInsideBoxSelection(MouseListener.getOrtho());

		if (this.translateGizmos.getGizmoVertical().isDragging && this.translateGizmos.getGizmoHorizontal().isDragging) {
			if (this.gridModeEnabled) {
				this.holdingGameObject.transform.position.x = (int) (MouseListener.getOrthoX() / this.gridTools.getGridSize()) * this.gridTools.getGridSize();
				this.holdingGameObject.transform.position.y = (int) (MouseListener.getOrthoY() / this.gridTools.getGridSize()) * this.gridTools.getGridSize();
			} else {
				this.holdingGameObject.transform.position = MouseListener.getOrtho();
			}
			return;
		}

		if (this.translateGizmos.getGizmoHorizontal().isDragging) {
			if (this.gridModeEnabled) {
				this.holdingGameObject.transform.position.x =
						(int) ((MouseListener.getOrthoX() - this.translateGizmos.getGizmoOffset()) / this.gridTools.getGridSize()) * this.gridTools.getGridSize();
			} else {
				this.holdingGameObject.transform.position.x = MouseListener.getOrthoX() - this.translateGizmos.getGizmoOffset();
			}
		}
		if (this.translateGizmos.getGizmoVertical().isDragging) {
			if (this.gridModeEnabled) {
				this.holdingGameObject.transform.position.y =
						(int) ((MouseListener.getOrthoY() - this.translateGizmos.getGizmoOffset()) / this.gridTools.getGridSize()) * this.gridTools.getGridSize();
			} else {
				this.holdingGameObject.transform.position.y = MouseListener.getOrthoY() - this.translateGizmos.getGizmoOffset();
			}
		}
	}
}
