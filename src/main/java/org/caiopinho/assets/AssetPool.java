package org.caiopinho.assets;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {
	private static final Map<String, Shader> shaders = new HashMap<>();
	private static final Map<String, Texture> textures = new HashMap<>();
	private static final Map<String, Spritesheet> spritesheets = new HashMap<>();

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

	public static void addSpriteSheet(String resourceName, Spritesheet spritesheet) {
		File file = new File(resourceName);
		if (!spritesheets.containsKey(file.getAbsolutePath())) {
			spritesheets.put(file.getAbsolutePath(), spritesheet);
		}
	}

	public static Spritesheet getSpritesheet(String resourceName) {
		File file = new File(resourceName);
		Spritesheet spritesheet = spritesheets.getOrDefault(file.getAbsolutePath(), null);

		assert spritesheet != null : "Error: Spritesheet not found: " + resourceName;

		return spritesheet;
	}
}
