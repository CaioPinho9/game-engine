package org.caiopinho.editor.gizmos;

import lombok.Getter;
import lombok.Setter;

import org.caiopinho.component.SpriteRenderer;
import org.caiopinho.core.GameObject;
import org.caiopinho.core.Transform;
import org.joml.Vector2f;

public abstract class Gizmo extends GameObject {
	protected static final float SCALE = .5f;
	private static final int Z_INDEX = 10;
	@Setter private boolean dragging = false;
	@Getter private boolean active = true;
	@Setter protected GameObject target;
	@Setter protected boolean fixedMode;
	@Setter protected float gizmoOffset;
	protected GizmoMode mode;

	public Gizmo(String name, Transform transform, GizmoMode mode) {
		super(name, transform, Z_INDEX);
		this.setSelectable(false);
		this.setSerializable(false);
		this.mode = mode;
	}

	public Gizmo(String name, GizmoMode mode) {
		super(name, new Transform(new Vector2f(), new Vector2f(1, 1)), Z_INDEX);
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

	public abstract void followTarget(float cameraZoom);

	public abstract void use();
}
