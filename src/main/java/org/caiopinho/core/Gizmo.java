package org.caiopinho.core;

import org.caiopinho.component.SpriteRenderer;

public class Gizmo extends GameObject {
	private static final int Z_INDEX = 5;
	private GameObject target;

	public Gizmo(String name, Transform transform) {
		super(name, transform, Z_INDEX);
	}

	public void setTarget(GameObject target) {
		this.target = target;
		this.transform = target.transform;
		this.getComponent(SpriteRenderer.class).setAlpha(1);
	}

	public void endTarget() {
		this.target = null;
		this.getComponent(SpriteRenderer.class).setAlpha(0);
	}
}
