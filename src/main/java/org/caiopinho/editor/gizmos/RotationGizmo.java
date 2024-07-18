package org.caiopinho.editor.gizmos;

import org.caiopinho.assets.AssetPool;
import org.caiopinho.component.SpriteRenderer;
import org.caiopinho.core.MouseListener;
import org.joml.Vector2f;

public class RotationGizmo extends Gizmo {
	float lastAngle = 0;
	float fixedIncrement = 15.0f;

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
		float mouseX = MouseListener.getOrthoX();
		float mouseY = MouseListener.getOrthoY();

		// Get the target's position
		float targetX = this.target.transform.position.x;
		float targetY = this.target.transform.position.y;

		// Calculate the angle between the target and the mouse position
		float deltaX = mouseX - targetX;
		float deltaY = mouseY - targetY;

		// Calculate the current angle
		float currentAngle = (float) Math.toDegrees(Math.atan2(deltaY, deltaX));

		// Calculate the angle difference
		float angleDifference = currentAngle - lastAngle;

		if (fixedMode) {
			// Snap the rotation to the nearest fixed increment
			angleDifference = Math.round(angleDifference / fixedIncrement) * fixedIncrement;
			this.target.transform.rotation = Math.round(this.target.transform.rotation / fixedIncrement) * fixedIncrement;
		}

		// Update the target's rotation incrementally
		this.target.transform.rotation += angleDifference;

		// Wrap the rotation to keep it between 0 and 360 degrees
		this.target.transform.rotation %= 360;
		if (this.target.transform.rotation < 0) {
			this.target.transform.rotation += 360;
		}

		// Update the last angle
		lastAngle = this.target.transform.rotation;
	}
}
