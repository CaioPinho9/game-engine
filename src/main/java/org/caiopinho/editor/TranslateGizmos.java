package org.caiopinho.editor;

import lombok.Getter;

import org.caiopinho.assets.AssetPool;
import org.caiopinho.assets.Spritesheet;
import org.caiopinho.component.Component;
import org.caiopinho.component.SpriteRenderer;
import org.caiopinho.core.GameObject;
import org.caiopinho.core.Transform;
import org.caiopinho.renderer.Camera;
import org.joml.Vector2f;

public class TranslateGizmos extends Gizmo {
	public static final float GIZMO_SENSIBILITY = 500;
	private static final int Z_INDEX = 10;
	private static final float SCALE = .5f;
	public static String name = "TranslateGizmos";
	private final Camera camera;
	@Getter private Gizmo gizmoVertical;
	@Getter private Gizmo gizmoHorizontal;
	@Getter private float gizmoOffset;
	private GameObject target;

	public TranslateGizmos(Camera camera) {
		super(name, new Transform(), Z_INDEX);
		this.camera = camera;

		Spritesheet gizmoSpritesheets = AssetPool.getSpritesheet("assets/images/gizmos.png");
		this.gizmoVertical = new Gizmo("GizmoVertical", new Transform(new Vector2f(), new Vector2f(1, 1), 180), Z_INDEX);
		SpriteRenderer spriteRendererVertical = new SpriteRenderer();
		spriteRendererVertical.setColor(1, 0, 0, 1);
		spriteRendererVertical.setSprite(gizmoSpritesheets.getSprite(1));
		this.gizmoVertical.setSelectable(false);
		this.gizmoVertical.setSerializable(false);
		this.gizmoVertical.addComponent(spriteRendererVertical);

		this.gizmoHorizontal = new Gizmo("GizmoHorizontal", new Transform(new Vector2f(), new Vector2f(1, 1), 90), Z_INDEX);
		SpriteRenderer spriteRendererHorizontal = new SpriteRenderer();
		spriteRendererHorizontal.setColor(0, 1, 0, 1);
		spriteRendererHorizontal.setSprite(gizmoSpritesheets.getSprite(1));
		this.gizmoHorizontal.setSelectable(false);
		this.gizmoHorizontal.setSerializable(false);
		this.gizmoHorizontal.addComponent(spriteRendererHorizontal);
	}

	@Override public <T extends Component> void start() {
		super.start();
		this.gizmoVertical.start();
		this.gizmoHorizontal.start();
	}

	@Override public <T extends Component> void update(float deltaTime) {
		super.update(deltaTime);

		if (this.target != null) {
			this.followTarget();
		}
	}

	private void followTarget() {
		this.target.transform.copy(this.gizmoVertical.transform);
		this.target.transform.copy(this.gizmoHorizontal.transform);
		float scale = Math.min(this.target.transform.scale.x, this.target.transform.scale.y) * SCALE * this.camera.getZoom();
		this.gizmoVertical.transform.scale = new Vector2f(scale, scale);
		this.gizmoHorizontal.transform.scale = new Vector2f(scale, scale);
		this.gizmoVertical.transform.position.add(0, this.gizmoHorizontal.transform.scale.y / 2);
		this.gizmoHorizontal.transform.position.add(this.gizmoVertical.transform.scale.x / 2, 0);
		this.gizmoOffset = this.gizmoHorizontal.transform.scale.x / 1.5f;
	}

	public void setTarget(GameObject target) {
		this.target = target;
		this.gizmoVertical.getComponent(SpriteRenderer.class).setAlpha(1);
		this.gizmoHorizontal.getComponent(SpriteRenderer.class).setAlpha(1);
		this.followTarget();
	}

	public void endTarget() {
		this.target = null;
		this.transform = new Transform();
		this.gizmoVertical.getComponent(SpriteRenderer.class).setAlpha(0);
		this.gizmoHorizontal.getComponent(SpriteRenderer.class).setAlpha(0);
	}

	public boolean getIsDragging() {
		return this.gizmoVertical.isDragging || this.gizmoHorizontal.isDragging;
	}

}
