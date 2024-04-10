package org.caiopinho.editor.gizmos;

import org.caiopinho.assets.AssetPool;
import org.caiopinho.component.SpriteRenderer;
import org.caiopinho.core.Transform;
import org.joml.Vector2f;

public class RotationGizmo extends Gizmo {

	public RotationGizmo(String name) {
		super(name, new Transform(new Vector2f(), new Vector2f(1, 1)), GizmoMode.ROTATE);

		SpriteRenderer spriteRenderer = new SpriteRenderer();
		spriteRenderer.setColor(0, .3f, 1, 1);
		spriteRenderer.setTexture(AssetPool.getTexture("assets/textures/gizmo_rotation.png"));
		this.addComponent(spriteRenderer);
	}

	@Override public void followTarget(float cameraZoom) {
		this.target.transform.copy(this.transform);
		float scale = Math.min(this.target.transform.scale.x, this.target.transform.scale.y) * cameraZoom * SCALE;
		this.transform.scale = new Vector2f(scale, scale);
	}

	@Override public void use() {

	}
}
