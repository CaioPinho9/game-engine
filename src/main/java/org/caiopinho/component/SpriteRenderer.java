package org.caiopinho.component;

import lombok.Getter;

import org.caiopinho.core.Component;
import org.caiopinho.renderer.Texture;
import org.joml.Vector2f;
import org.joml.Vector4f;

@Getter
public class SpriteRenderer extends Component {
	private Vector4f color;
	private Texture texture;
	private Vector2f[] texCoordinates;

	public SpriteRenderer(Vector4f color) {
		this.color = color;
	}

	public SpriteRenderer(Texture texture) {
		this.color = new Vector4f(1, 1, 1, 1);
		this.texture = texture;
	}

	public SpriteRenderer(Vector4f color, Texture texture) {
		this.color = color;
		this.texture = texture;
	}

	@Override public void update(float deltaTime) {

	}

	public Vector2f[] getTextureCoordinates() {
		return new Vector2f[] {
				new Vector2f(1, 1),
				new Vector2f(1, 0),
				new Vector2f(0, 0),
				new Vector2f(0, 1)
		};
	}
}
