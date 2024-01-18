package org.caiopinho.scene;

import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
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
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

@NoArgsConstructor public class LevelEditorScene extends Scene {
	private int shaderProgram;
	private Shader defaultShader;
	private float[] vertexArray = {
			//positions         // colors
			100.5f, 0.5f, 0, 1, 0, 0, 1, //bottom right
			0.5f, 100.5f, 0, 0, 1, 0, 1, //top left
			100.5f, 100.5f, 0, 0, 0, 1, 1, //top right
			0.5f, 0.5f, 0, 1, 1, 0, 1  //bottom left
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
		int floatSizeBytes = 4;
		int vertexSizeBytes = (positionsSize + colorSize) * floatSizeBytes;
		glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
		glEnableVertexAttribArray(0);

		glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * floatSizeBytes);

	}

	@Override public void update(float deltaTime) {
		this.defaultShader.use();
		this.defaultShader.uploadMatrix4f("uProjection", this.camera.getProjectionMatrix());
		this.defaultShader.uploadMatrix4f("uView", this.camera.getViewMatrix());

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
