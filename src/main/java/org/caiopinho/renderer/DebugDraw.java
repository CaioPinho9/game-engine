package org.caiopinho.renderer;

import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11C.GL_LINES;
import static org.lwjgl.opengl.GL11C.glDrawArrays;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.caiopinho.assets.AssetPool;
import org.caiopinho.assets.Shader;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class DebugDraw {
	private static final int POSITION_SIZE = 3;
	private static final int COLOR_SIZE = 4;
	private static final int POSITION_OFFSET = 0;
	private static final int COLOR_OFFSET = POSITION_OFFSET + POSITION_SIZE * Float.BYTES;
	private static final int VERTEX_SIZE = POSITION_SIZE + COLOR_SIZE;
	private static final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;
	private static final int MAX_LINES = 1000;
	private static List<Line2D> lines = new ArrayList<>();
	// 7 floats per vertice, 2 vertices per line
	private static float[] vertexArray = new float[MAX_LINES * 7 * 2];
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

		OpenGLHelper.bufferData(vboId, Arrays.copyOfRange(vertexArray, 0, lines.size() * 2 * 7));

		shader.use();
		OpenGLHelper.setShaderCameraUniforms(shader);

		glBindVertexArray(vaoId);

		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);

		glDrawArrays(GL_LINES, 0, lines.size() * 2 * 7);

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
		addLine2D(from, to, new Vector4f(0, 0, 0, 1), lifetime);
	}

	public static void addLine2D(Vector2f from, Vector2f to, Vector4f color) {
		addLine2D(from, to, color, 1);
	}

	public static void addLine2D(Vector2f from, Vector2f to) {
		addLine2D(from, to, new Vector4f(0, 0, 0, 1), 1);
	}
}
