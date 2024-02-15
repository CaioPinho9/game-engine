package org.caiopinho.renderer;

import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11C.GL_LINES;
import static org.lwjgl.opengl.GL11C.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15C.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15C.glBufferData;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.util.ArrayList;
import java.util.List;

import org.caiopinho.assets.AssetPool;
import org.caiopinho.assets.Shader;
import org.caiopinho.core.Transform;
import org.caiopinho.math.MathHelper;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class DebugDraw {
	private static final int POSITION_SIZE = 3;
	private static final int COLOR_SIZE = 4;
	private static final int POSITION_OFFSET = 0;
	private static final int COLOR_OFFSET = POSITION_OFFSET + POSITION_SIZE * Float.BYTES;
	private static final int VERTEX_SIZE = POSITION_SIZE + COLOR_SIZE;
	private static final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;
	private static final int MAX_LINES = 10000;
	private static final Vector4f DEFAULT_COLOR = new Vector4f(0, 0, 0, 1);
	private static final int DEFAULT_LIFETIME = 1;
	private static final float DEFAULT_CIRCLE_RATIO = 1;
	private static final int DEFAULT_LINE_SEGMENTS = 50;
	private static final List<Line2D> lines = new ArrayList<>();
	// 7 floats per vertice, 2 vertices per line
	private static final float[] vertexArray = new float[MAX_LINES * VERTEX_SIZE * 2];
	private static final Shader shader = AssetPool.getShader("assets/shaders/debugLine2D.glsl");
	private static int vaoId, vboId;
	private static boolean started = false;

	public static void start() {
		//  Create Vertex Array Object
		vaoId = OpenGLHelper.createVAO();

		//  Allocate the space for vertices
		vboId = OpenGLHelper.createVBO((long) vertexArray.length * Float.BYTES);

		//  Add the vertex attribute pointers
		OpenGLHelper.addVertexAttribPointer(0, POSITION_SIZE, VERTEX_SIZE_BYTES, POSITION_OFFSET);
		OpenGLHelper.addVertexAttribPointer(1, COLOR_SIZE, VERTEX_SIZE_BYTES, COLOR_OFFSET);
	}

	public static void beginFrame() {
		if (!started) {
			start();
			started = true;
		}

		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).beginFrame() < 0) {
				lines.remove(i--);
			}
		}
	}

	public static void render() {
		if (lines.isEmpty()) {
			return;
		}

		int index = 0;
		for (Line2D line : lines) {
			for (int i = 0; i < 2; i++) {
				Vector2f position = i == 0 ? line.getFrom() : line.getTo();
				Vector4f color = line.getColor();

				// Load position
				vertexArray[index] = position.x;
				vertexArray[index + 1] = position.y;
				vertexArray[index + 2] = -10.0f;

				// Load color
				vertexArray[index + 3] = color.x;
				vertexArray[index + 4] = color.y;
				vertexArray[index + 5] = color.z;
				vertexArray[index + 6] = color.w;
				index += VERTEX_SIZE;
			}
		}

		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, vertexArray, GL_DYNAMIC_DRAW);

		shader.use();
		OpenGLHelper.setShaderCameraUniforms(shader);

		glBindVertexArray(vaoId);

		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);

		glDrawArrays(GL_LINES, 0, lines.size() * 2);

		glLineWidth(2);

		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);

		shader.detach();
	}

	public static void addLine2D(Vector2f from, Vector2f to, Vector4f color, int lifetime) {
		if (lines.size() < MAX_LINES) {
			lines.add(new Line2D(from, to, color, lifetime));
		}
	}

	public static void addLine2D(Vector2f from, Vector2f to, int lifetime) {
		addLine2D(from, to, DEFAULT_COLOR, lifetime);
	}

	public static void addLine2D(Vector2f from, Vector2f to, Vector4f color) {
		addLine2D(from, to, color, DEFAULT_LIFETIME);
	}

	public static void addLine2D(Vector2f from, Vector2f to) {
		addLine2D(from, to, DEFAULT_COLOR, DEFAULT_LIFETIME);
	}

	public static void addBox2D(Transform transform, Vector4f color, int lifetime) {
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
			addLine2D(vertices[i], vertices[(i + 1) % vertices.length], color, lifetime);
		}
	}

	public static void addBox2D(Transform transform, Vector4f color) {
		addBox2D(transform, color, DEFAULT_LIFETIME);
	}

	public static void addBox2D(Transform transform, int lifetime) {
		addBox2D(transform, DEFAULT_COLOR, lifetime);
	}

	public static void addBox2D(Transform transform) {
		addBox2D(transform, DEFAULT_COLOR, DEFAULT_LIFETIME);
	}

	public static void addCircle2D(Transform transform, Vector4f color, int lineSegments, int lifetime, float circleRatio) {
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
			// Create the vertex and add the transform's position to it
			vertices[i] = new Vector2f(tmp).add(transform.position);
			currentAngle += increment;

			if (i > 0 && circleRatio == 1) {
				addLine2D(vertices[i - 1], vertices[i], color, lifetime);
			}
		}

		if (circleRatio == 1) {
			addLine2D(vertices[vertices.length - 1], vertices[0], color, lifetime);
		} else {
			Vector2f center = new Vector2f(transform.position);
			for (int i = 0; i < vertices.length; i++) {
				MathHelper.rotate(vertices[i], transform.rotation, center);
				if (i > 0) {
					addLine2D(vertices[i - 1], vertices[i], color, lifetime);
				}
			}

			addLine2D(vertices[vertices.length - 1], transform.position, color, lifetime);
			addLine2D(transform.position, vertices[0], color, lifetime);
		}
	}

	public static void addCircle2D(Transform transform, Vector4f color, int lineSegments, int lifetime) {
		addCircle2D(transform, color, lineSegments, lifetime, DEFAULT_CIRCLE_RATIO);
	}

	public static void addCircle2D(Transform transform, Vector4f color, int lineSegments) {
		addCircle2D(transform, color, lineSegments, DEFAULT_LIFETIME, DEFAULT_CIRCLE_RATIO);
	}

	public static void addCircle2D(Transform transform, int lineSegments, int lifetime, float circleRatio) {
		addCircle2D(transform, DEFAULT_COLOR, lineSegments, lifetime, circleRatio);
	}

	public static void addCircle2D(Transform transform, int lineSegments, float circleRatio) {
		addCircle2D(transform, DEFAULT_COLOR, lineSegments, DEFAULT_LIFETIME, circleRatio);
	}

	public static void addCircle2D(Transform transform, int lineSegments, int lifetime) {
		addCircle2D(transform, DEFAULT_COLOR, lineSegments, lifetime, DEFAULT_CIRCLE_RATIO);
	}

	public static void addCircle2D(Transform transform, int lifetime) {
		addCircle2D(transform, DEFAULT_COLOR, DEFAULT_LINE_SEGMENTS, lifetime, DEFAULT_CIRCLE_RATIO);
	}

	public static void addCircle2D(Transform transform, Vector4f color) {
		addCircle2D(transform, color, DEFAULT_LINE_SEGMENTS, DEFAULT_LIFETIME, DEFAULT_CIRCLE_RATIO);
	}

	public static void addCircle2D(Transform transform) {
		addCircle2D(transform, DEFAULT_COLOR, DEFAULT_LINE_SEGMENTS, DEFAULT_LIFETIME, DEFAULT_CIRCLE_RATIO);
	}
}
