package org.caiopinho.editor.gizmos;

import org.caiopinho.assets.AssetPool;
import org.caiopinho.component.SpriteRenderer;
import org.caiopinho.core.MouseListener;
import org.caiopinho.math.MathHelper;
import org.joml.Vector2f;

public class RotationGizmo extends Gizmo {
	private float lastAngle = 0;
	private final float FIXED_INCREMENT = 15.0f;

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
		float scale = cameraZoom * SCALE;
		this.transform.scale = new Vector2f(scale, scale);
	}

	@Override
	public void use() {
		float currentAngle = MathHelper.angleBetweenTwoPoints(this.target.transform.position, MouseListener.getOrtho());
		float angleDifference = currentAngle - this.lastAngle;

		if (this.fixedMode) {
			// Snap the rotation to the nearest fixed increment
			angleDifference = Math.round(angleDifference / this.FIXED_INCREMENT) * this.FIXED_INCREMENT;
			this.target.transform.rotation = Math.round(this.target.transform.rotation / this.FIXED_INCREMENT) * this.FIXED_INCREMENT;
		}

		// Update the target's rotation incrementally
		this.target.transform.rotation += angleDifference;

		// Wrap the rotation to keep it between 0 and 360 degrees
		this.target.transform.rotation %= 360;
		if (this.target.transform.rotation < 0) {
			this.target.transform.rotation += 360;
		}

		// Update the last angle
		this.lastAngle = this.target.transform.rotation;
	}
}
