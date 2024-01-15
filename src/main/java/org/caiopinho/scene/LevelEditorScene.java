package org.caiopinho.scene;

import static org.lwjgl.opengl.GL11.GL_FALSE;
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
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import lombok.NoArgsConstructor;

import org.lwjgl.BufferUtils;

@NoArgsConstructor
public class LevelEditorScene extends Scene {

	private String vertexShaderSrc = "#version 330 core\n"
			+ "#define vertex\n"
			+ "layout (location = 0) in vec3 aPos;\n"
			+ "layout (location = 1) in vec4 aColor;\n"
			+ "\n"
			+ "out vec4 fColor;\n"
			+ "\n"
			+ "void main()\n"
			+ "{\n"
			+ "    fColor = aColor;\n"
			+ "    gl_Position = vec4(aPos, 1.0);\n"
			+ "}";

	private String fragmentShaderSrc = "#version 330 core\n"
			+ "#define fragment\n"
			+ "in vec4 fColor;\n"
			+ "out vec4 color;\n"
			+ "\n"
			+ "void main()\n"
			+ "{\n"
			+ "    color = fColor;\n"
			+ "}";

	private int vertexId, fragmentId, shaderProgram;

	private float[] vertexArray = {
			//positions         // colors
			0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, //bottom right
			-0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, //top left
			0.5f, 0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, //top right
			-0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f  //bottom left
	};

	// Must be in counter-clockwise order
	private int[] elementArray = {
			2, 1, 0, //top right triangle
			0, 1, 3, //bottom left triangle
	};

	private int vaoId, vboId, eboId;

	@Override
	public void start() {
		// Compile vertex shader
		vertexId = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertexId, vertexShaderSrc);
		glCompileShader(vertexId);

		if (glGetShaderi(vertexId, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println(glGetShaderInfoLog(vertexId));
			throw new AssertionError("Failed to compile vertex shader!");
		}

		// Compile fragment shader
		fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragmentId, fragmentShaderSrc);
		glCompileShader(fragmentId);

		if (glGetShaderi(fragmentId, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println("Failed to compile fragment shader!");
			System.err.println(glGetShaderInfoLog(fragmentId));
			throw new AssertionError("Failed to compile fragment shader!");
		}

		// Link shaders
		shaderProgram = glCreateProgram();
		glAttachShader(shaderProgram, vertexId);
		glAttachShader(shaderProgram, fragmentId);
		glLinkProgram(shaderProgram);

		if (glGetProgrami(shaderProgram, GL_LINK_STATUS) == GL_FALSE) {
			System.err.println("Failed to link shader program!");
			System.err.println(glGetProgramInfoLog(shaderProgram));
			throw new AssertionError("Failed to link shader!");
		}

		vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);

		// Create float buffer of vertices
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
		vertexBuffer.put(vertexArray).flip();

		// Create VBO upload the vertex buffer
		vboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

		// Create the indices and upload
		IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
		elementBuffer.put(elementArray).flip();

		eboId = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
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

	@Override
	public void update(float deltaTime) {
		//Bind shader program
		glUseProgram(shaderProgram);
		//Bind the VAO
		glBindVertexArray(vaoId);

		//Enable the vertex attribute pointers
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);

		glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

		//Unbind everything
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);

		glBindVertexArray(0);

		glUseProgram(0);

	}
}
