package org.caiopinho.core;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.caiopinho.renderer.Shader;
import org.caiopinho.renderer.Texture;

public class AssetPool {
	private static final Map<String, Shader> shaders = new HashMap<>();
	private static final Map<String, Texture> textures = new HashMap<>();

	public static Shader getShader(String shaderPath) {
		File file = new File(shaderPath);
		if (shaders.containsKey(file.getAbsolutePath())) {
			return shaders.get(file.getAbsolutePath());
		} else {
			Shader shader = new Shader(shaderPath);
			shader.compile();
			shaders.put(file.getAbsolutePath(), shader);
			return shader;
		}
	}

	public static Texture getTexture(String texturePath) {
		File file = new File(texturePath);
		if (textures.containsKey(file.getAbsolutePath())) {
			return textures.get(file.getAbsolutePath());
		} else {
			Texture texture = new Texture(texturePath);
			textures.put(file.getAbsolutePath(), texture);
			return texture;
		}
	}

}
