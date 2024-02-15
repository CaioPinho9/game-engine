package org.caiopinho.math;

import org.joml.Vector2f;

public class MathHelper {
	public static void rotate(Vector2f vector, float angleDegree, Vector2f origin) {
		float x = vector.x - origin.x;
		float y = vector.y - origin.y;

		float cos = (float) Math.cos(Math.toRadians(angleDegree));
		float sin = (float) Math.sin(Math.toRadians(angleDegree));

		float xPrime = (x * cos) - (y * sin);
		float yPrime = (x * sin) + (y * cos);

		xPrime += origin.x;
		yPrime += origin.y;

		vector.x = xPrime;
		vector.y = yPrime;
	}
}
