package org.caiopinho.renderer.debug;

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

import lombok.Getter;

import org.caiopinho.assets.AssetPool;
import org.caiopinho.assets.Shader;
import org.caiopinho.renderer.OpenGLHelper;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class DebugBatch implements Comparable<DebugBatch> {
	private final int POSITION_SIZE = 3;
	private final int COLOR_SIZE = 4;
	private final int POSITION_OFFSET = 0;
	private final int COLOR_OFFSET = this.POSITION_OFFSET + this.POSITION_SIZE * Float.BYTES;
	private final int VERTEX_SIZE = this.POSITION_SIZE + this.COLOR_SIZE;
	private final int VERTEX_SIZE_BYTES = this.VERTEX_SIZE * Float.BYTES;
	// 7 floats per vertice, 2 vertices per line
	private final float[] vertexArray;
	private final Shader shader = AssetPool.getShader("assets/shaders/debugLine2D.glsl");
	private int vaoId, vboId;

	private final int maxBatchSize;
	private final List<Line2D> lines;
	@Getter private int lineCount;
	@Getter private boolean hasSpace = true;
	@Getter private final int zIndex;
	@Getter private final float lineWidth;

	public DebugBatch(int maxBatchSize, float lineWidth, int zIndex) {
		this.vertexArray = new float[maxBatchSize * this.VERTEX_SIZE * 2];
		this.lines = new ArrayList<>();
		this.maxBatchSize = maxBatchSize;
		this.zIndex = zIndex;
		this.lineWidth = lineWidth;
	}

	public void addLine(Line2D line) {
		if (line != null) {
			if (!this.lines.contains(line)) {
				this.lines.add(line);
				this.lineCount++;
			}
		}

		if (this.lineCount >= this.maxBatchSize) {
			this.hasSpace = false;
		}
	}

	public void start() {
		// Create Vertex Array Object
		this.vaoId = OpenGLHelper.createVAO();

		// Allocate the space for vertices
		this.vboId = OpenGLHelper.createVBO((long) this.vertexArray.length * Float.BYTES);

		// Add the vertex attribute pointers
		OpenGLHelper.addVertexAttribPointer(0, this.POSITION_SIZE, this.VERTEX_SIZE_BYTES, this.POSITION_OFFSET);
		OpenGLHelper.addVertexAttribPointer(1, this.COLOR_SIZE, this.VERTEX_SIZE_BYTES, this.COLOR_OFFSET);
	}

	public void beginFrame() {
		for (int i = 0; i < this.lineCount; i++) {
			if (this.lines.get(i).beginFrame() < 0) {
				this.lines.remove(i--);
				this.lineCount--;
			}
		}
	}

	public void render() {
		if (this.lines.isEmpty()) {
			return;
		}

		int index = 0;
		for (Line2D line : this.lines) {
			for (int i = 0; i < 2; i++) {
				Vector2f position = i == 0 ? line.getFrom() : line.getTo();
				Vector4f color = line.getColor();

				// Load position
				this.vertexArray[index] = position.x;
				this.vertexArray[index + 1] = position.y;
				this.vertexArray[index + 2] = -10.0f;

				// Load color
				this.vertexArray[index + 3] = color.x;
				this.vertexArray[index + 4] = color.y;
				this.vertexArray[index + 5] = color.z;
				this.vertexArray[index + 6] = color.w;
				index += this.VERTEX_SIZE;
			}
		}

		glBindBuffer(GL_ARRAY_BUFFER, this.vboId);
		glBufferData(GL_ARRAY_BUFFER, this.vertexArray, GL_DYNAMIC_DRAW);

		this.shader.use();
		OpenGLHelper.setShaderCameraUniforms(this.shader);

		glBindVertexArray(this.vaoId);

		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);

		glLineWidth(this.lineWidth);

		glDrawArrays(GL_LINES, 0, this.lines.size() * 2);

		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);

		this.shader.detach();
	}

	@Override public int compareTo(DebugBatch o) {
		int zIndexComparison = Integer.compare(this.getZIndex(), o.getZIndex());
		if (zIndexComparison == 0) {
			return Float.compare(this.getLineWidth(), o.getLineWidth());
		} else {
			return zIndexComparison;
		}
	}

	public boolean hasSpace() {
		return this.hasSpace;
	}
}
