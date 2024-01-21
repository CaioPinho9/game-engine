package org.caiopinho.scene;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL15C.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15C.glBufferData;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import lombok.NoArgsConstructor;

import org.caiopinho.renderer.Camera;
import org.caiopinho.renderer.Shader;
import org.caiopinho.renderer.Texture;
import org.caiopinho.renderer.Window;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

@NoArgsConstructor public class LevelEditorScene extends Scene {
	private int shaderProgram;
	private Shader defaultShader;
	private Texture texture;
	private final float halfWidth = Window.get().width / 4f;
	private final float halfHeight = Window.get().height / 6f;
	private final float size = 300f;
	private float[] vertexArray = {
			//positions 3         // colors 4         //texture coords 2
			this.halfWidth + this.size, this.halfHeight, 0, 1, 0, 0, 1, 1, 1,//bottom right
			this.halfWidth, this.halfHeight + this.size, 0, 0, 1, 0, 1, 0, 0,//top left
			this.halfWidth + this.size, this.halfHeight + this.size, 0, 0, 0, 1, 1, 1, 0, //top right
			this.halfWidth, this.halfHeight, 0, 1, 1, 0, 1, 0, 1//bottom left
	};

	// Must be in counter-clockwise order
	private int[] elementArray = { 2, 1, 0, //top right triangle
			0, 1, 3, //bottom left triangle
	};

	private int vaoId, vboId, eboId;

	@Override public void start() {
		this.camera = new Camera(new Vector2f(0, 0));
		this.defaultShader = new Shader("assets/shaders/default.glsl");
		this.defaultShader.compile();
		this.texture = new Texture("assets/textures/logo.png");

		this.vaoId = glGenVertexArrays();
		glBindVertexArray(this.vaoId);

		// Create float buffer of vertices
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(this.vertexArray.length);
		vertexBuffer.put(this.vertexArray).flip();

		// Create VBO upload the vertex buffer
		this.vboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, this.vboId);
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

		// Create the indices and upload
		IntBuffer elementBuffer = BufferUtils.createIntBuffer(this.elementArray.length);
		elementBuffer.put(this.elementArray).flip();

		this.eboId = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.eboId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

		// Add the vertex attribute pointers
		int positionsSize = 3;
		int colorSize = 4;
		int uvSize = 2;
		int vertexSizeBytes = (positionsSize + colorSize + uvSize) * Float.BYTES;
		glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
		glEnableVertexAttribArray(0);

		glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * Float.BYTES);
		glEnableVertexAttribArray(1);

		glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeBytes, (positionsSize + colorSize) * Float.BYTES);
		glEnableVertexAttribArray(2);
	}

	@Override public void update(float deltaTime) {
		this.defaultShader.use();

		//Upload texture
		this.defaultShader.uploadTexture("uTexture", 0);
		glActiveTexture(GL_TEXTURE0);
		this.texture.bind();

		this.defaultShader.uploadMatrix4f("uProjection", this.camera.getProjectionMatrix());
		this.defaultShader.uploadMatrix4f("uView", this.camera.getViewMatrix());
		this.defaultShader.uploadFloat("uTime", (float) glfwGetTime());

		//Bind the VAO
		glBindVertexArray(this.vaoId);

		//Enable the vertex attribute pointers
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);

		glDrawElements(GL_TRIANGLES, this.elementArray.length, GL_UNSIGNED_INT, 0);

		//Unbind everything
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);

		glBindVertexArray(0);

		this.defaultShader.detach();

	}
}
