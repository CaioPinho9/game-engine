package org.caiopinho.renderer;

import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
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

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import org.caiopinho.assets.AssetPool;
import org.caiopinho.assets.Shader;
import org.caiopinho.assets.Texture;
import org.caiopinho.component.SpriteRenderer;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class RenderBatch implements Comparable<RenderBatch> {
	// Vertex

	// Pos 				Color							Texture Coordinates	Texture id
	// float, float		float, float, float, float		float, float	float
	private final int POSITION_SIZE = 2;
	private final int COLOR_SIZE = 4;
	private final int TEXTURE_COORDINATES_SIZE = 2;
	private final int TEXTURE_ID_SIZE = 1;

	private final int POSITION_OFFSET = 0;
	private final int COLOR_OFFSET = this.POSITION_OFFSET + this.POSITION_SIZE * Float.BYTES;
	private final int TEXTURE_COORDINATES_OFFSET = this.COLOR_OFFSET + this.COLOR_SIZE * Float.BYTES;
	private final int TEXTURE_ID_OFFSET = this.TEXTURE_COORDINATES_OFFSET + this.TEXTURE_COORDINATES_SIZE * Float.BYTES;
	private final int VERTEX_SIZE = this.POSITION_SIZE + this.COLOR_SIZE + this.TEXTURE_COORDINATES_SIZE + this.TEXTURE_ID_SIZE;
	private final int VERTEX_SIZE_BYTES = this.VERTEX_SIZE * Float.BYTES;

	private final SpriteRenderer[] sprites;
	private final List<Texture> textures;
	private final int[] textureSlots = { 0, 1, 2, 3, 4, 5, 6, 7 };
	private final float[] vertices;

	private final int maxBatchSize;

	private int spriteCount;
	private boolean hasSpace;
	private int vaoId, vboId;
	private final Shader shader;
	@Getter private final int zIndex;

	public RenderBatch(int maxBatchSize, int zIndex) {
		this.zIndex = zIndex;
		this.shader = AssetPool.getShader("assets/shaders/default.glsl");
		this.sprites = new SpriteRenderer[maxBatchSize];
		this.textures = new ArrayList<>();
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
		glVertexAttribPointer(0, this.POSITION_SIZE, GL_FLOAT, false, this.VERTEX_SIZE_BYTES, this.POSITION_OFFSET);
		glEnableVertexAttribArray(0);

		glVertexAttribPointer(1, this.COLOR_SIZE, GL_FLOAT, false, this.VERTEX_SIZE_BYTES, this.COLOR_OFFSET);
		glEnableVertexAttribArray(1);

		glVertexAttribPointer(2, this.TEXTURE_COORDINATES_SIZE, GL_FLOAT, false, this.VERTEX_SIZE_BYTES, this.TEXTURE_COORDINATES_OFFSET);
		glEnableVertexAttribArray(2);

		glVertexAttribPointer(3, this.TEXTURE_ID_SIZE, GL_FLOAT, false, this.VERTEX_SIZE_BYTES, this.TEXTURE_ID_OFFSET);
		glEnableVertexAttribArray(3);
	}

	public void render() {
		boolean rebufferData = false;
		for (int i = 0; i < this.spriteCount; i++) {
			SpriteRenderer spriteRenderer = this.sprites[i];
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

		Camera camera = Window.getScene().getCamera();
		//  Use shader
		this.shader.use();
		this.shader.uploadMatrix4f("uProjection", camera.getProjectionMatrix());
		this.shader.uploadMatrix4f("uView", camera.getViewMatrix());

		for (int i = 0; i < this.textures.size(); i++) {
			glActiveTexture(GL_TEXTURE0 + i + 1);
			this.textures.get(i).bind();
		}
		this.shader.uploadIntArray("uTextures", this.textureSlots);

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

		for (Texture texture : this.textures) {
			texture.unbind();
		}

		this.shader.detach();
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
		elements[offset] = vertex + 3;
		elements[offset + 1] = vertex + 2;
		elements[offset + 2] = vertex;

		// Triangle 2
		elements[offset + 3] = vertex;
		elements[offset + 4] = vertex + 2;
		elements[offset + 5] = vertex + 1;
	}

	public void addSprite(SpriteRenderer spriteRenderer) {
		// Get index and add renderObject
		int index = this.spriteCount;
		this.sprites[index] = spriteRenderer;
		this.spriteCount++;

		Texture texture = spriteRenderer.getTexture();
		if (texture != null) {
			if (!this.textures.contains(texture)) {
				this.textures.add(texture);
			}
		}

		// Add properties to local vertices array
		this.loadVertexProperties(index);

		if (this.spriteCount >= this.maxBatchSize) {
			this.hasSpace = false;
		}

	}

	private void loadVertexProperties(int index) {
		SpriteRenderer spriteRenderer = this.sprites[index];

		// Find offset within array (4 vertices per sprite)
		int offset = index * 4 * this.VERTEX_SIZE;
		int textureId = 0;
		// [texture, texture, texture, texture, ...]
		if (spriteRenderer.getTexture() != null) {
			textureId = this.textures.indexOf(spriteRenderer.getTexture()) + 1;
		}

		Vector4f color = spriteRenderer.getColor();
		Vector2f[] texCoords = spriteRenderer.getTexCoords();

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

			// Load texture coordinates
			this.vertices[offset + 6] = texCoords[i].x;
			this.vertices[offset + 7] = texCoords[i].y;

			// Load texture id
			this.vertices[offset + 8] = textureId;

			offset += this.VERTEX_SIZE;
		}
	}

	public boolean hasTextureSpace() {
		return this.textures.size() < 8;
	}

	public boolean hasTexture(Texture texture) {
		return this.textures.contains(texture);
	}

	@Override public int compareTo(RenderBatch o) {
		return Integer.compare(this.getZIndex(), o.getZIndex());
	}
}
