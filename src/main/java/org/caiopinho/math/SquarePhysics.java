package org.caiopinho.math;

import org.caiopinho.core.Transform;
import org.caiopinho.renderer.debug.DebugDraw;
import org.joml.Vector2f;

// TODO: Temporary class
public class SquarePhysics {
	public static boolean isPointInsideRectangle(Vector2f point, Transform rectangle) {
		Vector2f rotatedPoint = new Vector2f(point);

		if (rectangle.rotation != 0)
			MathHelper.rotate(rotatedPoint, rectangle.rotation, rectangle.position);
		System.out.println("rotatedPoint: " + rotatedPoint);

		DebugDraw.addCircle2D(new Transform(rotatedPoint, new Vector2f(1f)), 100000);
		Transform fixed = new Transform(rectangle);
		fixed.position.add(new Vector2f(new Vector2f(fixed.scale).mul(0.5f)));
		DebugDraw.addBox2D(fixed, 100);

		return (
				rotatedPoint.x >= rectangle.position.x
						&& rotatedPoint.x <= rectangle.position.x + rectangle.scale.x
						&& rotatedPoint.y >= rectangle.position.y
						&& rotatedPoint.y <= rectangle.position.y + rectangle.scale.y
		);
	}
}
