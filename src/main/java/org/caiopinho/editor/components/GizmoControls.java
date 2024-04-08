package org.caiopinho.editor.components;

import static org.caiopinho.editor.components.DebugView.drawGameObjectSelectionSquare;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_3;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.caiopinho.assets.AssetPool;
import org.caiopinho.component.Component;
import org.caiopinho.component.SpriteRenderer;
import org.caiopinho.core.GameObject;
import org.caiopinho.core.KeyListener;
import org.caiopinho.core.MouseListener;
import org.caiopinho.core.Transform;
import org.caiopinho.renderer.Camera;
import org.caiopinho.renderer.Window;
import org.caiopinho.scene.Scene;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class GizmoControls extends Component {
	private static final int Z_INDEX = 10;
	public static final Vector4f SELECTION_COLOR = new Vector4f(1, 0, 0, 1);
	private static final float SCALE = .5f;
	private transient final GridTools gridTools;
	private transient final Scene scene;
	private final Camera camera;
	@Getter private Gizmo gizmoVertical;
	@Getter private Gizmo gizmoHorizontal;
	@Getter private Gizmo gizmoCircle;

	private final transient List<GameObject> selectQueue;
	private transient boolean justDropped;
	private transient boolean justDoubleClicked;
	@Getter @Setter private boolean gridModeEnabled;
	@Getter private float gizmoOffset;
	private transient GameObject target;

	private transient GizmoMode activeGizmoMode;

	public GizmoControls(Camera camera, GridTools gridTools, Scene scene) {
		this.camera = camera;
		this.gridTools = gridTools;
		this.scene = scene;
		this.gridModeEnabled = false;
		this.target = null;
		this.activeGizmoMode = GizmoMode.TRANSLATE;
		this.selectQueue = new ArrayList<>();

		this.gizmoVertical = new Gizmo("GizmoVertical", new Transform(new Vector2f(), new Vector2f(1, 1), 180), Z_INDEX, GizmoMode.TRANSLATE);
		SpriteRenderer spriteRendererVertical = new SpriteRenderer();
		spriteRendererVertical.setColor(1, 0, 0, 1);
		spriteRendererVertical.setTexture(AssetPool.getTexture("assets/textures/gizmo_translation.png"));
		this.gizmoVertical.addComponent(spriteRendererVertical);

		this.gizmoHorizontal = new Gizmo("GizmoHorizontal", new Transform(new Vector2f(), new Vector2f(1, 1), 90), Z_INDEX, GizmoMode.TRANSLATE);
		SpriteRenderer spriteRendererHorizontal = new SpriteRenderer();
		spriteRendererHorizontal.setColor(0, 1, 0, 1);
		spriteRendererHorizontal.setTexture(AssetPool.getTexture("assets/textures/gizmo_translation.png"));
		this.gizmoHorizontal.addComponent(spriteRendererHorizontal);

		this.gizmoCircle = new Gizmo("GizmoCircle", new Transform(new Vector2f(), new Vector2f(1, 1)), Z_INDEX, GizmoMode.ROTATE);
		SpriteRenderer spriteRendererCircle = new SpriteRenderer();
		spriteRendererCircle.setColor(0, .3f, 1, 1);
		spriteRendererCircle.setTexture(AssetPool.getTexture("assets/textures/gizmo_rotation.png"));
		this.gizmoCircle.addComponent(spriteRendererCircle);
		this.gizmoCircle.setActive(false);
	}

	@Override public void start() {
		super.start();
		this.gizmoVertical.start();
		this.gizmoHorizontal.start();
		this.gizmoCircle.start();
	}

	@Override public void update(float deltaTime) {
		if (this.target != null) {
			this.followTarget();
			this.changeGizmo();
		}

		if (!this.justDropped) {
			this.selectGameObject();
			this.handleDoubleClick();
		}
		if (this.target != null) {
			drawGameObjectSelectionSquare(this.target, SELECTION_COLOR);

			if (MouseListener.isButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
				this.moveObject();
			}

			this.gizmoHorizontal.setDragging(this.gizmoHorizontal.isDragging() && this.gizmoHorizontal.isPointInsideBoxSelection(MouseListener.getOrtho()));
			this.gizmoVertical.setDragging(this.gizmoVertical.isDragging() && this.gizmoVertical.isPointInsideBoxSelection(MouseListener.getOrtho()));

			if (!this.getIsDragging() && MouseListener.isButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
				this.placeGameObject();
			}

		}
		this.resetFlags();
	}

	private void toggleGizmos() {
		this.gizmoHorizontal.setActive(this.activeGizmoMode);
		this.gizmoVertical.setActive(this.activeGizmoMode);
		this.gizmoCircle.setActive(this.activeGizmoMode);
	}

	private void followTarget() {
		this.target.transform.copy(this.gizmoVertical.transform);
		this.target.transform.copy(this.gizmoHorizontal.transform);
		this.target.transform.copy(this.gizmoCircle.transform);
		float scale = Math.min(this.target.transform.scale.x, this.target.transform.scale.y) * SCALE * this.camera.getZoom();
		this.gizmoVertical.transform.scale = new Vector2f(scale, scale);
		this.gizmoHorizontal.transform.scale = new Vector2f(scale, scale);
		this.gizmoCircle.transform.scale = new Vector2f(scale * 3, scale * 3);
		this.gizmoVertical.transform.position.add(0, this.gizmoHorizontal.transform.scale.y / 2);
		this.gizmoHorizontal.transform.position.add(this.gizmoVertical.transform.scale.x / 2, 0);
		this.gizmoOffset = this.gizmoHorizontal.transform.scale.x / 1.5f;
	}

	public void setTarget(GameObject target) {
		this.target = target;
		this.toggleGizmos();
		this.followTarget();
	}

	public void endTarget() {
		this.target = null;
		this.gizmoVertical.setActive(false);
		this.gizmoHorizontal.setActive(false);
		this.gizmoCircle.setActive(false);
	}

	public boolean getIsDragging() {
		return this.gizmoVertical.isDragging() || this.gizmoHorizontal.isDragging() || this.gizmoCircle.isDragging();
	}

	public void resetDragging() {
		this.gizmoVertical.setDragging(false);
		this.gizmoHorizontal.setDragging(false);
		this.gizmoCircle.setDragging(false);
	}

	public boolean isAnyTranslateGizmoDragging() {
		return this.gizmoVertical.isDragging() || this.gizmoHorizontal.isDragging();
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
			this.resetDragging();

			if (!MouseListener.isDoubleClick(GLFW_MOUSE_BUTTON_LEFT)) {
				this.justDoubleClicked = false;
			}
		}
	}

	private void changeGizmo() {
		if (KeyListener.isKeyPressed(GLFW_KEY_1)) {
			this.activeGizmoMode = GizmoMode.TRANSLATE;
		} else if (KeyListener.isKeyPressed(GLFW_KEY_2)) {
			this.activeGizmoMode = GizmoMode.ROTATE;
		} else if (KeyListener.isKeyPressed(GLFW_KEY_3)) {
			this.activeGizmoMode = GizmoMode.SCALE;
		}
		this.toggleGizmos();
	}

	private void moveObject() {
		this.gizmoHorizontal.setDragging(this.gizmoHorizontal.isDragging() || this.gizmoHorizontal.isPointInsideBoxSelection(MouseListener.getOrtho()));
		this.gizmoVertical.setDragging(this.gizmoVertical.isDragging() || this.gizmoVertical.isPointInsideBoxSelection(MouseListener.getOrtho()));

		if (this.isAnyTranslateGizmoDragging()) {
			if (this.gridModeEnabled) {
				this.target.transform.position = this.calculateGridPosition();
			} else {
				if (this.gizmoHorizontal.isDragging()) {
					this.target.transform.position.x = MouseListener.getOrthoX() - this.gizmoOffset;
				}
				if (this.gizmoVertical.isDragging()) {
					this.target.transform.position.y = MouseListener.getOrthoY() - this.gizmoOffset;
				}
			}
		}
	}

	private Vector2f calculateGridPosition() {
		float x = this.gizmoHorizontal.isDragging() ? this.calculateGridCoordinate(MouseListener.getOrthoX()) : this.target.transform.position.x;
		float y = this.gizmoVertical.isDragging() ? this.calculateGridCoordinate(MouseListener.getOrthoY()) : this.target.transform.position.y;
		return new Vector2f(x, y);
	}

	private float calculateGridCoordinate(float coordinate) {
		return (int) ((coordinate - this.getGizmoOffset()) / this.gridTools.getGridSize()) * this.gridTools.getGridSize();
	}
}
