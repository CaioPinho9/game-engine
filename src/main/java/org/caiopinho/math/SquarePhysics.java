package org.caiopinho.math;

import org.caiopinho.core.Transform;
import org.joml.Vector2f;

// TODO: Temporary class
public class SquarePhysics {
	public static boolean isPointInsideRectangle(Vector2f point, Transform rectangle) {
		return (
				point.x >= rectangle.position.x
						&& point.x <= rectangle.position.x + rectangle.scale.x
						&& point.y >= rectangle.position.y
						&& point.y <= rectangle.position.y + rectangle.scale.y
		);
	}
}
