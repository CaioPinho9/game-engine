package org.caiopinho.editor.components;

import lombok.Getter;
import lombok.Setter;

import org.caiopinho.component.SpriteRenderer;
import org.caiopinho.core.GameObject;
import org.caiopinho.core.Transform;

public class Gizmo extends GameObject {
	@Setter private boolean dragging = false;
	@Getter private boolean active = true;
	public GizmoMode mode;

	public Gizmo(String name, Transform transform, int zIndex, GizmoMode mode) {
		super(name, transform, zIndex);
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
}
