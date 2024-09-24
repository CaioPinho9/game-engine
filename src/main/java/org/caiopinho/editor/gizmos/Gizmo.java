package org.caiopinho.editor.gizmos;

import lombok.Getter;
import lombok.Setter;

import org.caiopinho.component.SpriteRenderer;
import org.caiopinho.core.GameObject;
import org.caiopinho.core.Transform;
import org.joml.Vector2f;

public abstract class Gizmo extends GameObject {
	protected static final float ASPECT_RATIO = .42f;
	protected static final float SCALE = 100f;
	protected static final int Z_INDEX = 10;
	@Setter private boolean dragging = false;
	@Getter private boolean active = true;
	@Setter protected GameObject target;
	@Setter protected boolean fixedMode;
	@Setter protected boolean aspectRatioLockMode;
	@Setter protected float gizmoOffset;
	@Getter
	protected boolean isVertical;
	protected final GizmoMode mode;

	public Gizmo(String name, Transform transform, boolean isVertical, GizmoMode mode) {
		super(name, transform);
		this.isVertical = isVertical;
		this.mode = mode;
	}

	public Gizmo(String name, Transform transform, GizmoMode mode) {
		super(name, transform);
		this.setSelectable(false);
		this.setSerializable(false);
		this.mode = mode;
	}

	public Gizmo(String name, GizmoMode mode) {
		super(name, new Transform(new Vector2f(), new Vector2f(1, 1), 0, Z_INDEX));
		this.setSelectable(false);
		this.setSerializable(false);
		this.mode = mode;
	}

	public void setActive(boolean active) {
		this.active = active;
		if (active) {
			this.getComponent(SpriteRenderer.class).setAlpha(1);
		} else {
			this.getComponent(SpriteRenderer.class).setAlpha(0);
			this.dragging = false;
		}
	}

	public void setActive(GizmoMode mode) {
		this.setActive(this.mode == mode);
	}

	public boolean isDragging() {
		return this.dragging && this.active;
	}

	public void followTarget(float cameraZoom) {
		this.target.transform.copy(this.transform);
		float scale = cameraZoom * SCALE;
		this.transform.scale = new Vector2f(scale * ASPECT_RATIO, scale);
		this.transform.position.add(this.isVertical ? 0 : this.transform.scale.y / 2, this.isVertical ? this.transform.scale.y / 2 : 0);
	}

	public abstract void use();
}
