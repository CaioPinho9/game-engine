package org.caiopinho.assets;

import static org.lwjgl.opengl.GL11C.GL_LINEAR;
import static org.lwjgl.opengl.GL11C.GL_NEAREST;
import static org.lwjgl.opengl.GL11C.GL_REPEAT;
import static org.lwjgl.opengl.GL11C.GL_RGB;
import static org.lwjgl.opengl.GL11C.GL_RGBA;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11C.glBindTexture;
import static org.lwjgl.opengl.GL11C.glGenTextures;
import static org.lwjgl.opengl.GL11C.glTexImage2D;
import static org.lwjgl.opengl.GL11C.glTexParameteri;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import lombok.Getter;

import org.lwjgl.BufferUtils;

@Getter
public class Texture {
	private transient int id;
	private int width;
	private int height;
	private String filepath;

	public Texture() {
	}

	public Texture(int width, int height) {
		this.filepath = "Generated";

		this.width = width;
		this.height = height;

		// Generate texture on GPU
		this.id = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, this.id);

		// Set texture parameters
		// When stretching the image, pixelate
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		// When shrinking an image, pixelate
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		// Create empty texture
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, this.width, this.height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);

		glGenerateMipmap(GL_TEXTURE_2D);
	}

	public void init(String filePath) {
		this.filepath = filePath;

		// Generate texture on GPU
		this.id = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, this.id);

		// Set texture parameters
		// Repeat image in both directions
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

		// When stretching the image, pixelate
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		// When shrinking an image, pixelate
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		// Load image
		IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
		IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);
		IntBuffer channels = BufferUtils.createIntBuffer(1);

		stbi_set_flip_vertically_on_load(true);

		ByteBuffer image = stbi_load(filePath, widthBuffer, heightBuffer, channels, 0);

		assert image != null : "Failed to load texture: '" + filePath + "'";

		this.width = widthBuffer.get(0);
		this.height = heightBuffer.get(0);

		if (channels.get(0) == 3) {
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, this.width, this.height, 0, GL_RGB, GL_UNSIGNED_BYTE, image);
		} else if (channels.get(0) == 4) {
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
		} else {
			assert false : "Unknown number of channels '" + channels.get(0) + "' in file '" + filePath + "'";
		}

		stbi_image_free(image);
	}

	public void bind() {
		glBindTexture(GL_TEXTURE_2D, this.id);
	}

	public void unbind() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (!(object instanceof Texture texture)) {
			return false;
		}
		return this.id == texture.id && this.width == texture.width && this.height == texture.height && this.filepath.equals(texture.filepath);
	}
}
