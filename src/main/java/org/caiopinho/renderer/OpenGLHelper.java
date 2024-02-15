package org.caiopinho.renderer;

import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL15C.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.glBufferData;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import org.caiopinho.assets.Shader;

public class OpenGLHelper {

	public static void setShaderCameraUniforms(Shader shader) {
		Camera camera = Window.getScene().getCamera();
		shader.uploadMatrix4f("uProjection", camera.getProjectionMatrix());
		shader.uploadMatrix4f("uView", camera.getViewMatrix());
	}

	public static int createVAO() {
		//  Create Vertex Array Object
		int vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);
		return vaoId;
	}

	public static int createVBO(long bufferSize) {
		//  Allocate the space for vertices
		int vboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, bufferSize, GL_DYNAMIC_DRAW);
		return vboId;
	}

	public static void createEBO() {
		int eboId = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
	}

	public static void addVertexAttribPointer(int index, int size, int bytes, int offset) {
		glVertexAttribPointer(index, size, GL_FLOAT, false, bytes, offset);
		glEnableVertexAttribArray(index);
	}

}
