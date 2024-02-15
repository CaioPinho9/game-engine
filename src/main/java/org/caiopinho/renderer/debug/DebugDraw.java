package org.caiopinho.renderer.debug;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.caiopinho.core.Transform;
import org.caiopinho.math.MathHelper;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class DebugDraw {
	private static final int MAX_LINES = 10000;
	private static final Vector4f DEFAULT_COLOR = new Vector4f(0, 0, 0, 1);
	private static final int DEFAULT_LIFETIME = 1;
	private static final float DEFAULT_CIRCLE_RATIO = 1;
	private static final int DEFAULT_LINE_SEGMENTS = 50;
	private static final float DEFAULT_WIDTH = 2;
	private static final int DEFAULT_Z_INDEX = 0;

	private static final List<DebugBatch> batches = new ArrayList<>();
	private static int lineCount = 0;

	public static void render() {
		lineCount = 0;
		for (DebugBatch batch : batches) {
			batch.beginFrame();
			batch.render();
			lineCount += batch.getLineCount();
		}
	}

	public static void addLine2D(Vector2f from, Vector2f to, Vector4f color, int lifetime, float width, int zIndex) {
		if (lineCount >= MAX_LINES) {
			return;
		}

		Line2D line = new Line2D(from, to, color, lifetime, width, zIndex);

		for (DebugBatch batch : batches) {
			if (batch.hasSpace() && batch.getZIndex() == zIndex && batch.getLineWidth() == width) {
				batch.addLine(line);
				lineCount++;
				return;
			}
		}

		DebugBatch newBatch = new DebugBatch(MAX_LINES, width, zIndex);
		newBatch.start();
		batches.add(newBatch);
		newBatch.addLine(line);
		Collections.sort(batches);
		lineCount++;
	}

	public static void addLine2D(Vector2f from, Vector2f to, int lifetime) {
		addLine2D(from, to, DEFAULT_COLOR, lifetime, DEFAULT_WIDTH, DEFAULT_Z_INDEX);
	}

	public static void addLine2D(Vector2f from, Vector2f to, Vector4f color, int lifetime, float width) {
		addLine2D(from, to, color, lifetime, width, DEFAULT_Z_INDEX);
	}

	public static void addLine2D(Vector2f from, Vector2f to, Vector4f color, float width) {
		addLine2D(from, to, color, DEFAULT_LIFETIME, width, DEFAULT_Z_INDEX);
	}

	public static void addLine2D(Vector2f from, Vector2f to, Vector4f color, int lifetime) {
		addLine2D(from, to, color, lifetime, DEFAULT_WIDTH, DEFAULT_Z_INDEX);
	}

	public static void addLine2D(Vector2f from, Vector2f to, Vector4f color) {
		addLine2D(from, to, color, DEFAULT_LIFETIME, DEFAULT_WIDTH, DEFAULT_Z_INDEX);
	}

	public static void addLine2D(Vector2f from, Vector2f to) {
		addLine2D(from, to, DEFAULT_COLOR, DEFAULT_LIFETIME, DEFAULT_WIDTH, DEFAULT_Z_INDEX);
	}

	public static void addBox2D(Transform transform, Vector4f color, int lifetime, float width) {
		Vector2f halfSize = new Vector2f(transform.scale).mul(0.5f);
		Vector2f topLeft = new Vector2f(transform.position).sub(halfSize);
		Vector2f bottomRight = new Vector2f(transform.position).add(halfSize);

		Vector2f bottomLeft = new Vector2f(topLeft.x, bottomRight.y);
		Vector2f topRight = new Vector2f(bottomRight.x, topLeft.y);

		Vector2f[] vertices = { topLeft, topRight, bottomRight, bottomLeft };

		if (transform.rotation != 0) {
			Vector2f center = new Vector2f(transform.position);
			for (Vector2f vertex : vertices) {
				MathHelper.rotate(vertex, transform.rotation, center);
			}
		}

		for (int i = 0; i < vertices.length; i++) {
			addLine2D(vertices[i], vertices[(i + 1) % vertices.length], color, lifetime, width, transform.zIndex);
		}
	}

	public static void addBox2D(Transform transform, Vector4f color, int lifetime) {
		addBox2D(transform, color, lifetime, DEFAULT_WIDTH);
	}

	public static void addBox2D(Transform transform, Vector4f color) {
		addBox2D(transform, color, DEFAULT_LIFETIME, DEFAULT_WIDTH);
	}

	public static void addBox2D(Transform transform, int lifetime) {
		addBox2D(transform, DEFAULT_COLOR, lifetime, DEFAULT_WIDTH);
	}

	public static void addBox2D(Transform transform) {
		addBox2D(transform, DEFAULT_COLOR, DEFAULT_LIFETIME, DEFAULT_WIDTH);
	}

	public static void addCircle2D(Transform transform, Vector4f color, int lineSegments, int lifetime, float circleRatio, float width) {
		boolean isCircle = transform.scale.x == transform.scale.y;
		float radius = transform.scale.x;

		int verticesCount = (int) (lineSegments * circleRatio) + 1;
		Vector2f[] vertices = new Vector2f[verticesCount];
		float increment = (float) 360 / lineSegments;
		float currentAngle = 0;

		for (int i = 0; i < verticesCount; i++) {
			Vector2f tmp;
			if (isCircle) {
				tmp = new Vector2f(radius, 0);
				MathHelper.rotate(tmp, currentAngle, new Vector2f());
			} else {
				float radian = (float) Math.toRadians(currentAngle);
				float x = (float) (transform.scale.x * Math.cos(radian));
				float y = (float) (transform.scale.y * Math.sin(radian));
				tmp = new Vector2f(x, y);
			}
			vertices[i] = new Vector2f(tmp).add(transform.position);
			currentAngle += increment;

			if (i > 0 && circleRatio == 1) {
				addLine2D(vertices[i - 1], vertices[i], color, lifetime, width, transform.zIndex);
			}
		}

		if (circleRatio == 1) {
			addLine2D(vertices[vertices.length - 1], vertices[0], color, lifetime, width, transform.zIndex);
		} else {
			drawPartialCircle(transform, color, lifetime, vertices, width);
		}
	}

	private static void drawPartialCircle(Transform transform, Vector4f color, int lifetime, Vector2f[] vertices, float width) {
		Vector2f center = new Vector2f(transform.position);
		for (int i = 0; i < vertices.length; i++) {
			MathHelper.rotate(vertices[i], transform.rotation, center);
			if (i > 0) {
				addLine2D(vertices[i - 1], vertices[i], color, lifetime, width, transform.zIndex);
			}
		}

		addLine2D(vertices[vertices.length - 1], transform.position, color, lifetime, width, transform.zIndex);
		addLine2D(transform.position, vertices[0], color, lifetime, width, transform.zIndex);
	}

	public static void addCircle2D(Transform transform, Vector4f color, int lineSegments, int lifetime) {
		addCircle2D(transform, color, lineSegments, lifetime, DEFAULT_CIRCLE_RATIO, DEFAULT_WIDTH);
	}

	public static void addCircle2D(Transform transform, Vector4f color, int lineSegments) {
		addCircle2D(transform, color, lineSegments, DEFAULT_LIFETIME, DEFAULT_CIRCLE_RATIO, DEFAULT_WIDTH);
	}

	public static void addCircle2D(Transform transform, int lineSegments, int lifetime, float circleRatio) {
		addCircle2D(transform, DEFAULT_COLOR, lineSegments, lifetime, circleRatio, DEFAULT_WIDTH);
	}

	public static void addCircle2D(Transform transform, int lineSegments, float circleRatio) {
		addCircle2D(transform, DEFAULT_COLOR, lineSegments, DEFAULT_LIFETIME, circleRatio, DEFAULT_WIDTH);
	}

	public static void addCircle2D(Transform transform, Vector4f color, int lineSegments, int lifetime, float circleRatio) {
		addCircle2D(transform, color, lineSegments, lifetime, circleRatio, DEFAULT_WIDTH);
	}

	public static void addCircle2D(Transform transform, int lineSegments, int lifetime) {
		addCircle2D(transform, DEFAULT_COLOR, lineSegments, lifetime, DEFAULT_CIRCLE_RATIO, DEFAULT_WIDTH);
	}

	public static void addCircle2D(Transform transform, int lifetime) {
		addCircle2D(transform, DEFAULT_COLOR, DEFAULT_LINE_SEGMENTS, lifetime, DEFAULT_CIRCLE_RATIO, DEFAULT_WIDTH);
	}

	public static void addCircle2D(Transform transform, Vector4f color) {
		addCircle2D(transform, color, DEFAULT_LINE_SEGMENTS, DEFAULT_LIFETIME, DEFAULT_CIRCLE_RATIO, DEFAULT_WIDTH);
	}

	public static void addCircle2D(Transform transform) {
		addCircle2D(transform, DEFAULT_COLOR, DEFAULT_LINE_SEGMENTS, DEFAULT_LIFETIME, DEFAULT_CIRCLE_RATIO, DEFAULT_WIDTH);
	}
}
