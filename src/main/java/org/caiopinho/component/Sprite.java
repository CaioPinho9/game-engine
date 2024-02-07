package org.caiopinho.component;

import lombok.Getter;

import org.caiopinho.renderer.Texture;
import org.joml.Vector2f;

@Getter
public class Sprite {
	private final Texture texture;
	private final Vector2f[] texCoords;

	public Sprite(Texture texture) {
		this.texture = texture;
		this.texCoords = new Vector2f[] {
				new Vector2f(1, 1),
				new Vector2f(1, 0),
				new Vector2f(0, 0),
				new Vector2f(0, 1)
		};
	}

	public Sprite(Texture texture, Vector2f[] texCoords) {
		this.texture = texture;
		this.texCoords = texCoords;
	}

}
