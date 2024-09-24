package org.caiopinho.renderer.batch;

import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15C.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15C.glBufferData;
import static org.lwjgl.opengl.GL15C.glBufferSubData;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.util.ArrayList;
import java.util.List;

import org.caiopinho.assets.AssetPool;
import org.caiopinho.assets.Texture;
import org.caiopinho.component.SpriteRenderer;
import org.caiopinho.core.Transform;
import org.caiopinho.math.MathHelper;
import org.caiopinho.renderer.OpenGLHelper;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class RenderBatch extends Batch<SpriteRenderer> {
	// Vertex

	// Pos 				Color							Texture Coordinates	Texture id
	// float, float		float, float, float, float		float, float	float
	private static final int POSITION_SIZE = 2;
	private static final int COLOR_SIZE = 4;
	private static final int TEXTURE_COORDINATES_SIZE = 2;
	private static final int TEXTURE_ID_SIZE = 1;

	private static final int POSITION_OFFSET = 0;
	private static final int COLOR_OFFSET = POSITION_OFFSET + POSITION_SIZE * Float.BYTES;
	private static final int TEXTURE_COORDINATES_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
	private static final int TEXTURE_ID_OFFSET = TEXTURE_COORDINATES_OFFSET + TEXTURE_COORDINATES_SIZE * Float.BYTES;
	private static final int VERTEX_SIZE = POSITION_SIZE + COLOR_SIZE + TEXTURE_COORDINATES_SIZE + TEXTURE_ID_SIZE;
	private static final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;
	private static final int VERTICES_PER_SPRITE = 4;
	private static final int INDICES_PER_SPRITE = 6;
	private static final String SHADER_PATH = "assets/shaders/default.glsl";

	private final List<Texture> textures = new ArrayList<>();
	private final int[] textureSlots = { 0, 1, 2, 3, 4, 5, 6, 7 };

	public RenderBatch(int maxBatchSize, int zIndex) {
		super(maxBatchSize, zIndex, AssetPool.getShader(SHADER_PATH), new SpriteRenderer[maxBatchSize]);

		// 4 vertices quads
		this.vertices = new float[maxBatchSize * VERTICES_PER_SPRITE * VERTEX_SIZE];
	}

	public void start() {
		// Create Vertex Array Object
		this.vaoId = OpenGLHelper.createVAO();

		// Allocate the space for vertices
		this.vboId = OpenGLHelper.createVBO((long) this.vertices.length * Float.BYTES);

		OpenGLHelper.createEBO();

		// Create the indices and upload
		int[] indices = this.generateIndices();
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

		// Add the vertex attribute pointers
		OpenGLHelper.addVertexAttribPointer(0, POSITION_SIZE, VERTEX_SIZE_BYTES, POSITION_OFFSET);
		OpenGLHelper.addVertexAttribPointer(1, COLOR_SIZE, VERTEX_SIZE_BYTES, COLOR_OFFSET);
		OpenGLHelper.addVertexAttribPointer(2, TEXTURE_COORDINATES_SIZE, VERTEX_SIZE_BYTES, TEXTURE_COORDINATES_OFFSET);
		OpenGLHelper.addVertexAttribPointer(3, TEXTURE_ID_SIZE, VERTEX_SIZE_BYTES, TEXTURE_ID_OFFSET);
	}

	@Override
	public void render() {
		boolean rebufferData = false;
		for (int i = 0; i < this.elementCount; i++) {
			SpriteRenderer spriteRenderer = this.elements[i];
			if (spriteRenderer.isDirty()) {
				this.loadVertexProperties(i);
				spriteRenderer.setClean();
				rebufferData = true;
			}
		}

		if (rebufferData) {
			// Rebuffer all data only when have dirty data
			glBindBuffer(GL_ARRAY_BUFFER, this.vboId);
			glBufferSubData(GL_ARRAY_BUFFER, 0, this.vertices);
		}

		this.shader.use();
		OpenGLHelper.setShaderCameraUniforms(this.shader);
		this.shader.uploadIntArray("uTextures", this.textureSlots);

		for (int i = 0; i < this.textures.size(); i++) {
			glActiveTexture(GL_TEXTURE0 + i + 1);
			this.textures.get(i).bind();
		}

		// Bind the VAO
		glBindVertexArray(this.vaoId);

		// Enable the vertex attribute pointers
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);

		glDrawElements(GL_TRIANGLES, this.elementCount * INDICES_PER_SPRITE, GL_UNSIGNED_INT, 0);

		// Unbind everything
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);

		glBindVertexArray(0);

		for (Texture texture : this.textures) {
			texture.unbind();
		}

		this.shader.detach();
	}

	public int[] generateIndices() {
		// 6 indices per quad (3 per triangle)
		int[] elements = new int[this.maxBatchSize * INDICES_PER_SPRITE];
		for (int i = 0; i < this.maxBatchSize; i++) {
			this.loadElementIndices(elements, i);
		}
		return elements;
	}

	public void loadElementIndices(int[] elements, int index) {
		int offset = index * INDICES_PER_SPRITE;
		int vertex = index * VERTICES_PER_SPRITE;

		// 3, 2, 0, 0, 2, 1			7, 6, 4, 4, 6, 5
		// Triangle 1
		elements[offset] = vertex + 3;
		elements[offset + 1] = vertex + 2;
		elements[offset + 2] = vertex;

		// Triangle 2
		elements[offset + 3] = vertex;
		elements[offset + 4] = vertex + 2;
		elements[offset + 5] = vertex + 1;
	}

	@Override
	public void addElement(SpriteRenderer spriteRenderer) {
		// Get index and add renderObject
		int index = this.elementCount;
		this.elements[index] = spriteRenderer;
		this.elementCount++;

		Texture texture = spriteRenderer.getTexture();
		if (texture != null) {
			if (!this.textures.contains(texture)) {
				this.textures.add(texture);
			}
		}

		// Add properties to local vertices array
		this.loadVertexProperties(index);

		if (this.elementCount >= this.maxBatchSize) {
			this.hasSpace = false;
		}
	}

	private void loadVertexProperties(int index) {
		SpriteRenderer spriteRenderer = this.elements[index];

		// Find offset within array (4 vertices per sprite)
		int offset = index * VERTICES_PER_SPRITE * VERTEX_SIZE;
		int textureId = 0;
		// [texture, texture, texture, texture, ...]
		if (spriteRenderer.getTexture() != null) {
			textureId = this.textures.indexOf(spriteRenderer.getTexture()) + 1;
		}

		Vector4f color = spriteRenderer.getColor();
		Vector2f[] texCoords = spriteRenderer.getTexCoords();
		Transform transform = spriteRenderer.getTransform();

		// Vertex 0
		Vector2f vertex0 = new Vector2f(transform.position.x + (.5f * transform.scale.x), transform.position.y + (.5f * transform.scale.y));
		MathHelper.rotate(vertex0, transform.rotation, transform.position);

		this.vertices[offset] = vertex0.x;
		this.vertices[offset + 1] = vertex0.y;
		this.vertices[offset + 2] = color.x;
		this.vertices[offset + 3] = color.y;
		this.vertices[offset + 4] = color.z;
		this.vertices[offset + 5] = color.w;
		this.vertices[offset + 6] = texCoords[0].x;
		this.vertices[offset + 7] = texCoords[0].y;
		this.vertices[offset + 8] = textureId;
		offset += VERTEX_SIZE;

		// Vertex 1
		Vector2f vertex1 = new Vector2f(transform.position.x + (.5f * transform.scale.x), transform.position.y + (-.5f * transform.scale.y));
		MathHelper.rotate(vertex1, transform.rotation, transform.position);

		this.vertices[offset] = vertex1.x;
		this.vertices[offset + 1] = vertex1.y;
		this.vertices[offset + 2] = color.x;
		this.vertices[offset + 3] = color.y;
		this.vertices[offset + 4] = color.z;
		this.vertices[offset + 5] = color.w;
		this.vertices[offset + 6] = texCoords[1].x;
		this.vertices[offset + 7] = texCoords[1].y;
		this.vertices[offset + 8] = textureId;
		offset += VERTEX_SIZE;

		// Vertex 2
		Vector2f vertex2 = new Vector2f(transform.position.x + (-.5f * transform.scale.x), transform.position.y + (-.5f * transform.scale.y));
		MathHelper.rotate(vertex2, transform.rotation, transform.position);

		this.vertices[offset] = vertex2.x;
		this.vertices[offset + 1] = vertex2.y;
		this.vertices[offset + 2] = color.x;
		this.vertices[offset + 3] = color.y;
		this.vertices[offset + 4] = color.z;
		this.vertices[offset + 5] = color.w;
		this.vertices[offset + 6] = texCoords[2].x;
		this.vertices[offset + 7] = texCoords[2].y;
		this.vertices[offset + 8] = textureId;
		offset += VERTEX_SIZE;

		// Vertex 3
		Vector2f vertex3 = new Vector2f(transform.position.x + (-.5f * transform.scale.x), transform.position.y + (.5f * transform.scale.y));
		MathHelper.rotate(vertex3, transform.rotation, transform.position);

		this.vertices[offset] = vertex3.x;
		this.vertices[offset + 1] = vertex3.y;
		this.vertices[offset + 2] = color.x;
		this.vertices[offset + 3] = color.y;
		this.vertices[offset + 4] = color.z;
		this.vertices[offset + 5] = color.w;
		this.vertices[offset + 6] = texCoords[3].x;
		this.vertices[offset + 7] = texCoords[3].y;
		this.vertices[offset + 8] = textureId;
	}

	public boolean hasTextureSpace() {
		return this.textures.size() < this.textureSlots.length;
	}

	public boolean hasTexture(Texture texture) {
		return this.textures.contains(texture);
	}

}
