package org.caiopinho.renderer;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL15C.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15C.glBufferData;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import org.caiopinho.component.SpriteRenderer;
import org.caiopinho.core.AssetPool;
import org.joml.Vector4f;

public class RenderBatch {
	// Vertex

	// Pos 				Color
	// float, float		float, float, float, float
	private final int POSITION_SIZE = 2;
	private final int COLOR_SIZE = 4;

	private final int POSITION_OFFSET = 0;
	private final int COLOR_OFFSET = this.POSITION_OFFSET + this.POSITION_SIZE * Float.BYTES;
	private final int VERTEX_SIZE = this.POSITION_SIZE + this.COLOR_SIZE;
	private final int VERTEX_SIZE_BYTES = this.VERTEX_SIZE * Float.BYTES;

	private final SpriteRenderer[] sprites;
	private int spriteCount;
	private boolean hasSpace;
	private final float[] vertices;

	private int vaoId, vboId;
	private final int maxBatchSize;
	private final Shader shader;

	public RenderBatch(int maxBatchSize) {
		this.shader = AssetPool.getShader("assets/shaders/default.glsl");
		this.sprites = new SpriteRenderer[maxBatchSize];
		this.maxBatchSize = maxBatchSize;

		//  4 vertices quads
		this.vertices = new float[maxBatchSize * 4 * this.VERTEX_SIZE];

		this.spriteCount = 0;
		this.hasSpace = true;
	}

	public boolean hasSpace() {
		return this.hasSpace;
	}

	public void start() {
		//  Create Vertex Array Object
		this.vaoId = glGenVertexArrays();
		glBindVertexArray(this.vaoId);

		//  Allocate the space for vertices
		this.vboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, this.vboId);
		glBufferData(GL_ARRAY_BUFFER, (long) this.vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

		//  Create the indices and upload
		int eboId = glGenBuffers();
		int[] indices = this.generateIndices();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

		//  Add the vertex attribute pointers
		glVertexAttribPointer(0, this.POSITION_SIZE, GL_FLOAT, false, this.VERTEX_SIZE_BYTES, 0);
		glEnableVertexAttribArray(0);

		glVertexAttribPointer(1, this.COLOR_SIZE, GL_FLOAT, false, this.VERTEX_SIZE_BYTES, this.POSITION_SIZE * Float.BYTES);
		glEnableVertexAttribArray(1);
	}

	public int[] generateIndices() {
		//  6 indices per quad (3 per triangle)
		int[] elements = new int[this.maxBatchSize * 6];
		for (int i = 0; i < this.maxBatchSize; i++) {
			this.loadElementIndices(elements, i);
		}
		return elements;
	}

	public void loadElementIndices(int[] elements, int index) {
		int offset = index * 6;
		int vertex = index * 4;

		//  3, 2, 0, 0, 2, 1			7, 6, 4, 4, 6, 5
		//  Triangle 1
		elements[offset] = vertex;
		elements[offset + 1] = vertex + 1;
		elements[offset + 2] = vertex + 2;

		//  Triangle 2
		elements[offset + 3] = vertex + 2;
		elements[offset + 4] = vertex + 3;
		elements[offset + 5] = vertex;
	}

	public void render() {
		//  Rebuffer all data every frame
		glBindBuffer(GL_ARRAY_BUFFER, this.vboId);
		glBufferSubData(GL_ARRAY_BUFFER, 0, this.vertices);

		//  Use shader
		this.shader.use();
		Camera camera = Window.getScene().getCamera();
		this.shader.uploadMatrix4f("uProjection", camera.getProjectionMatrix());
		this.shader.uploadMatrix4f("uView", camera.getViewMatrix());
		this.shader.uploadFloat("uTime", (float) glfwGetTime());

		// Bind the VAO
		glBindVertexArray(this.vaoId);

		// Enable the vertex attribute pointers
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);

		glDrawElements(GL_TRIANGLES, this.spriteCount * 6, GL_UNSIGNED_INT, 0);

		// Unbind everything
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);

		glBindVertexArray(0);

		this.shader.detach();
	}

	private void loadVertexProperties(int index) {
		SpriteRenderer spriteRenderer = this.sprites[index];

		// Find offset within array (4 vertices per sprite)
		int offset = index * 4 * this.VERTEX_SIZE;

		Vector4f color = spriteRenderer.getColor();

		float xAdd = 1;
		float yAdd = 1;
		for (int i = 0; i < 4; i++) {
			if (i == 1) {
				yAdd = 0;
			} else if (i == 2) {
				xAdd = 0;
			} else if (i == 3) {
				yAdd = 1;
			}

			// Load position
			this.vertices[offset] = spriteRenderer.gameObject.transform.position.x + (xAdd * spriteRenderer.gameObject.transform.scale.x);
			this.vertices[offset + 1] = spriteRenderer.gameObject.transform.position.y + (yAdd * spriteRenderer.gameObject.transform.scale.y);

			// Load colors
			this.vertices[offset + 2] = color.x;
			this.vertices[offset + 3] = color.y;
			this.vertices[offset + 4] = color.z;
			this.vertices[offset + 5] = color.w;

			offset += this.VERTEX_SIZE;
		}
	}

	public void addSprite(SpriteRenderer spriteRenderer) {
		// Get index and add renderObject
		int index = this.spriteCount;
		this.sprites[index] = spriteRenderer;
		this.spriteCount++;

		// Add properties to local vertices array
		this.loadVertexProperties(index);

		if (this.spriteCount >= this.maxBatchSize) {
			this.hasSpace = false;
		}

	}
}
