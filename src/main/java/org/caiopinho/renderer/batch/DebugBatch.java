package org.caiopinho.renderer.batch;

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

import lombok.Getter;

import org.caiopinho.assets.AssetPool;
import org.caiopinho.math.ArraysHelper;
import org.caiopinho.renderer.OpenGLHelper;
import org.caiopinho.renderer.debug.Line2D;
import org.joml.Vector2f;
import org.joml.Vector4f;

@Getter
public class DebugBatch extends Batch<Line2D> {
	private static final int POSITION_SIZE = 3;
	private static final int COLOR_SIZE = 4;
	private static final int POSITION_OFFSET = 0;
	private static final int COLOR_OFFSET = POSITION_OFFSET + POSITION_SIZE * Float.BYTES;
	private static final int VERTEX_SIZE = POSITION_SIZE + COLOR_SIZE;
	private static final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;
	private static final int VERTICES_PER_SPRITE = 2;
	private static final String SHADER_PATH = "assets/shaders/debugLine2D.glsl";
	// 7 floats per vertice, 2 vertices per line

	private final float lineWidth;

	public DebugBatch(int maxBatchSize, float lineWidth, int zIndex) {
		super(maxBatchSize, zIndex, AssetPool.getShader(SHADER_PATH), new Line2D[maxBatchSize]);
		this.vertices = new float[maxBatchSize * VERTICES_PER_SPRITE * VERTEX_SIZE];
		this.lineWidth = lineWidth;
	}

	@Override
	public void addElement(Line2D line) {
		if (line != null) {
			if (!ArraysHelper.contains(this.elements, line)) {
				this.elements[this.elementCount] = line;
				this.elementCount++;
			}
		}

		if (this.elementCount >= this.maxBatchSize) {
			this.hasSpace = false;
		}
	}

	public void start() {
		// Create Vertex Array Object
		this.vaoId = OpenGLHelper.createVAO();

		// Allocate the space for vertices
		this.vboId = OpenGLHelper.createVBO((long) this.vertices.length * Float.BYTES);

		// Add the vertex attribute pointers
		OpenGLHelper.addVertexAttribPointer(0, POSITION_SIZE, VERTEX_SIZE_BYTES, POSITION_OFFSET);
		OpenGLHelper.addVertexAttribPointer(1, COLOR_SIZE, VERTEX_SIZE_BYTES, COLOR_OFFSET);
	}

	public void beginFrame() {
		for (int i = 0; i < this.elementCount; i++) {
			if (this.elements[i].beginFrame() < 0) {
				ArraysHelper.removeByIndex(this.elements, i--);
				this.elementCount--;
				this.hasSpace = true;
			}
		}
	}

	@Override
	public void render() {
		if (this.elementCount == 0) {
			return;
		}

		this.beginFrame();

		for (int i = 0; i < this.elementCount; i++) {
			this.loadVertexProperties(i);
		}

		glBindBuffer(GL_ARRAY_BUFFER, this.vboId);
		glBufferData(GL_ARRAY_BUFFER, this.vertices, GL_DYNAMIC_DRAW);

		this.shader.use();
		OpenGLHelper.setShaderCameraUniforms(this.shader);

		glBindVertexArray(this.vaoId);

		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);

		glLineWidth(this.lineWidth);

		glDrawArrays(GL_LINES, 0, this.elementCount * VERTICES_PER_SPRITE);

		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);

		this.shader.detach();
	}

	private void loadVertexProperties(int index) {
		int offset = index * VERTICES_PER_SPRITE * VERTEX_SIZE;

		Line2D line = this.elements[index];
		for (int j = 0; j < 2; j++) {
			Vector2f position = j == 0 ? line.getFrom() : line.getTo();
			Vector4f color = line.getColor();

			// Load position
			this.vertices[offset] = position.x;
			this.vertices[offset + 1] = position.y;
			this.vertices[offset + 2] = -10.0f;

			// Load color
			this.vertices[offset + 3] = color.x;
			this.vertices[offset + 4] = color.y;
			this.vertices[offset + 5] = color.z;
			this.vertices[offset + 6] = color.w;
			offset += VERTEX_SIZE;
		}
	}

	public int compareTo(DebugBatch o) {
		int zIndexComparison = Integer.compare(this.getZIndex(), o.getZIndex());
		if (zIndexComparison == 0) {
			return Float.compare(this.getLineWidth(), o.getLineWidth());
		} else {
			return zIndexComparison;
		}
	}
}
