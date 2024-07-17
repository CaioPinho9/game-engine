package org.caiopinho.editor.gizmos;

import org.caiopinho.assets.AssetPool;
import org.caiopinho.component.SpriteRenderer;
import org.caiopinho.core.MouseListener;
import org.joml.Vector2f;

public class RotationGizmo extends Gizmo {

	public RotationGizmo(String name) {
		super(name, GizmoMode.ROTATE);

		SpriteRenderer spriteRenderer = new SpriteRenderer();
		spriteRenderer.setColor(0, .3f, 1, 1);
		spriteRenderer.setTexture(AssetPool.getTexture("assets/textures/gizmo_rotation.png"));
		this.addComponent(spriteRenderer);
	}

	@Override
	public void followTarget(float cameraZoom) {
		this.target.transform.copy(this.transform);
		float scale = Math.min(this.target.transform.scale.x, this.target.transform.scale.y) * cameraZoom * SCALE;
		this.transform.scale = new Vector2f(scale, scale);
	}

	@Override
	public void use() {
		if (this.fixedMode) {
		} else {
			float tan = (float) ((MouseListener.getOrthoY() - this.target.transform.position.y) / (MouseListener.getOrthoX() - this.target.transform.position.x));

			float rotation = (float) Math.toDegrees(Math.atan(tan));

			if (rotation < 0) {
				rotation += 180;
			}

			this.target.transform.rotation = rotation;
			System.out.println(this.target.transform.rotation);
		}
	}
}
