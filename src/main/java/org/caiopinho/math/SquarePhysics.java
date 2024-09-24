package org.caiopinho.math;

import org.caiopinho.core.Transform;
import org.joml.Vector2f;

// TODO: Temporary class
public class SquarePhysics {
	public static boolean isPointInsideRectangle(Vector2f point, Transform rectangle) {
		Vector2f rotatedPoint = new Vector2f(point);

		if (rectangle.rotation != 0f) {
			Vector2f fixedPosition = new Vector2f(rectangle.position).add(new Vector2f(new Vector2f(rectangle.scale).mul(0.5f)));
			MathHelper.rotate(rotatedPoint, -rectangle.rotation, fixedPosition);
		}

		return (
				rotatedPoint.x >= rectangle.position.x
						&& rotatedPoint.x <= rectangle.position.x + rectangle.scale.x
						&& rotatedPoint.y >= rectangle.position.y
						&& rotatedPoint.y <= rectangle.position.y + rectangle.scale.y
		);
	}
}
