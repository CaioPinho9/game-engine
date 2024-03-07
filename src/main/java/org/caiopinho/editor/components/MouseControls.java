package org.caiopinho.editor.components;

import static org.caiopinho.editor.components.DebugView.drawGameObjectSelectionSquare;
import static org.caiopinho.editor.components.DebugView.getBoxSelectionScale;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.caiopinho.component.Component;
import org.caiopinho.core.GameObject;
import org.caiopinho.core.Gizmo;
import org.caiopinho.core.MouseListener;
import org.caiopinho.core.Transform;
import org.caiopinho.math.SquarePhysics;
import org.caiopinho.renderer.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class MouseControls extends Component {
	public static final Vector4f SELECTION_COLOR = new Vector4f(1, 0, 0, 1);
	private transient GameObject holdingGameObject;
	private final transient List<GameObject> selectQueue = new ArrayList<>();
	private transient boolean justSelected;
	private transient boolean justDropped;
	private transient boolean justDoubleClicked;
	@Getter @Setter private boolean gridModeEnabled = false;
	@Getter @Setter private boolean moveGameObjectEnabled = true;

	public void setHoldingGameObject(GameObject gameObject) {
		this.holdingGameObject = gameObject;
		Window.getScene().activeGameObject = gameObject;
	}

	public void placeGameObject() {
		this.holdingGameObject = null;
		this.justDropped = true;
	}

	public void selectGameObject() {
		if (MouseListener.isButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
			// Check every game object in the scene to see if the mouse is inside its box
			for (GameObject gameObject : Window.getScene().getGameObjects()) {
				if (gameObject instanceof Gizmo) {
					this.useGizmo((Gizmo) gameObject);
					continue;
				}

				// Check if is inside the box
				if (SquarePhysics.isPointInsideRectangle(
						new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY()),
						new Transform(new Vector2f(gameObject.transform.position).add(new Vector2f(gameObject.transform.scale).mul(-.5f)), getBoxSelectionScale(gameObject))
				)) {
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

		// When double clicked the first object in the queue is moved to the end, and the holding object is set to the first
		if (!this.justDoubleClicked && MouseListener.isDoubleClick(GLFW_MOUSE_BUTTON_LEFT) && this.selectQueue.size() >= 2) {
			GameObject gameObject = this.selectQueue.removeFirst();
			this.selectQueue.add(gameObject);
			this.setHoldingGameObject(this.selectQueue.getFirst());
			this.justSelected = true;
			this.justDoubleClicked = true;
		}
	}

	private void useGizmo(Gizmo gizmo) {

	}

	@Override public void update(float deltaTime) {
		if (!this.justDropped) {
			this.selectGameObject();
		}
		if (this.holdingGameObject != null) {
			drawGameObjectSelectionSquare(this.holdingGameObject, SELECTION_COLOR);

			GridTools gridTools = this.gameObject.getComponent(GridTools.class);

			if (this.moveGameObjectEnabled) {
				if (this.gridModeEnabled) {
					this.holdingGameObject.transform.position.x =
							(int) (MouseListener.getOrthoX() / gridTools.getGridSize()) * gridTools.getGridSize() + this.holdingGameObject.transform.scale.x / 2;
					this.holdingGameObject.transform.position.y =
							(int) (MouseListener.getOrthoY() / gridTools.getGridSize()) * gridTools.getGridSize() + this.holdingGameObject.transform.scale.y / 2;
				} else {
					this.holdingGameObject.transform.position.x = MouseListener.getOrthoX();
					this.holdingGameObject.transform.position.y = MouseListener.getOrthoY();
				}
			}

			if (!this.justSelected && MouseListener.isButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
				this.placeGameObject();
			}

		}
		if (!MouseListener.isButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
			this.justDropped = false;
			if (!MouseListener.isDoubleClick(GLFW_MOUSE_BUTTON_LEFT)) {
				this.justSelected = false;
				this.justDoubleClicked = false;
			}
		}
	}
}
