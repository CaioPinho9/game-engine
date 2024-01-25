package org.caiopinho.renderer;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniformMatrix3fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

public class Shader {

	private int shaderProgramId;
	private String vertexSource;
	private String fragmentSource;
	private final String filePath;
	private boolean beingUsed = false;

	public Shader(String filePath) {
		this.filePath = filePath;
		try {
			String source = new String(Files.readAllBytes(Paths.get(filePath)));
			String[] splitString = source.split("(#define)( )+([a-zA-Z]+)");

			int index = source.indexOf("#define") + 8;
			int eol = source.indexOf("\n", index);
			String firstPattern = source.substring(index, eol).trim();

			index = source.indexOf("#define", eol) + 8;
			eol = source.indexOf("\n", index);
			String secondPattern = source.substring(index, eol).trim();

			if (firstPattern.equals("vertex")) {
				this.vertexSource = splitString[1];
			} else if (firstPattern.equals("fragment")) {
				this.fragmentSource = splitString[1];
			} else {
				throw new IOException("Unexpected token '" + firstPattern + "' in file '" + filePath + "'");
			}

			if (secondPattern.equals("vertex")) {
				this.vertexSource = splitString[2];
			} else if (secondPattern.equals("fragment")) {
				this.fragmentSource = splitString[2];
			} else {
				throw new IOException("Unexpected token '" + firstPattern + "' in file '" + filePath + "'");
			}

			assert this.vertexSource != null : "Vertex shader not found in file '" + filePath + "'";
			assert this.fragmentSource != null : "Fragment shader not found in file '" + filePath + "'";
		} catch (IOException e) {
			e.printStackTrace();
			assert false : "Error: Could not open file for shader: '" + filePath + "'";
		}
	}

	public void compile() {
		int vertexId, fragmentId;

		// Compile vertex shader
		vertexId = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertexId, this.vertexSource);
		glCompileShader(vertexId);

		if (glGetShaderi(vertexId, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println(glGetShaderInfoLog(vertexId));
			assert false : "Failed to compile vertex shader in file '" + this.filePath + "'";
		}

		// Compile fragment shader
		fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragmentId, this.fragmentSource);
		glCompileShader(fragmentId);

		if (glGetShaderi(fragmentId, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println(glGetShaderInfoLog(fragmentId));
			assert false : "Failed to compile fragment shader in file '" + this.filePath + "'";
		}

		// Link shaders
		this.shaderProgramId = glCreateProgram();
		glAttachShader(this.shaderProgramId, vertexId);
		glAttachShader(this.shaderProgramId, fragmentId);
		glLinkProgram(this.shaderProgramId);

		if (glGetProgrami(this.shaderProgramId, GL_LINK_STATUS) == GL_FALSE) {
			System.err.println(glGetProgramInfoLog(this.shaderProgramId));
			assert false : "Failed to link shader program in file '" + this.filePath + "'";
		}
	}

	public void use() {
		// Bind shader program
		if (!this.beingUsed) {
			glUseProgram(this.shaderProgramId);
			this.beingUsed = true;
		}
	}

	public void detach() {
		glUseProgram(0);
		this.beingUsed = false;
	}

	public void uploadMatrix4f(String variableName, Matrix4f matrix4) {
		int variableLocation = glGetUniformLocation(this.shaderProgramId, variableName);
		FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
		matrix4.get(matrixBuffer);
		glUniformMatrix4fv(variableLocation, false, matrixBuffer);
	}

	public void uploadMatrix3f(String variableName, Matrix3f matrix3) {
		int variableLocation = glGetUniformLocation(this.shaderProgramId, variableName);
		FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
		matrix3.get(matrixBuffer);
		glUniformMatrix3fv(variableLocation, false, matrixBuffer);
	}

	public void uploadVector4f(String variableName, Vector4f vector4f) {
		int variableLocation = glGetUniformLocation(this.shaderProgramId, variableName);
		this.use();
		glUniform4f(variableLocation, vector4f.x, vector4f.y, vector4f.z, vector4f.w);
	}

	public void uploadVector3f(String variableName, Vector3f vector3f) {
		int variableLocation = glGetUniformLocation(this.shaderProgramId, variableName);
		this.use();
		glUniform3f(variableLocation, vector3f.x, vector3f.y, vector3f.z);
	}

	public void uploadVector2f(String variableName, Vector2f vector2f) {
		int variableLocation = glGetUniformLocation(this.shaderProgramId, variableName);
		this.use();
		glUniform2f(variableLocation, vector2f.x, vector2f.y);
	}

	public void uploadFloat(String variableName, float value) {
		int variableLocation = glGetUniformLocation(this.shaderProgramId, variableName);
		this.use();
		glUniform1f(variableLocation, value);
	}

	public void uploadInt(String variableName, int value) {
		int variableLocation = glGetUniformLocation(this.shaderProgramId, variableName);
		this.use();
		glUniform1i(variableLocation, value);
	}

	public void uploadTexture(String variableName, int slot) {
		int variableLocation = glGetUniformLocation(this.shaderProgramId, variableName);
		this.use();
		glUniform1i(variableLocation, slot);
	}

}
