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

	public static boolean isInsideRange(float value, float min, float max) {
		return value >= min && value <= max;
	}

	public static float clamp(float value, float min, float max) {
		return Math.max(min, Math.min(max, value));
	}

	public static int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}

	public static float[] clamp(float[] vector, float min, float max) {
		float[] newVector = new float[vector.length];

		for (int i = 0; i < vector.length; i++) {
			newVector[i] = clamp(vector[i], min, max);
		}

		return newVector;
	}

	public static float lerp(float start, float end, float factor) {
		return start + factor * (end - start);
	}
}
