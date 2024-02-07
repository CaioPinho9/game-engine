package org.caiopinho.component;

import lombok.Getter;
import lombok.Setter;

import org.caiopinho.core.Component;
import org.caiopinho.renderer.Texture;
import org.joml.Vector2f;
import org.joml.Vector4f;

@Getter
@Setter
public class SpriteRenderer extends Component {
	private Vector4f color;
	private Sprite sprite;

	public SpriteRenderer(Vector4f color) {
		this.color = color;
		this.sprite = null;
	}

	public SpriteRenderer(Sprite sprite) {
		this.color = new Vector4f(1, 1, 1, 1);
		this.sprite = sprite;
	}

	public SpriteRenderer(Vector4f color, Sprite sprite) {
		this.color = color;
		this.sprite = sprite;
	}

	@Override public void update(float deltaTime) {

	}

	public Texture getTexture() {
		return this.sprite.getTexture();
	}

	public Vector2f[] getTexCoords() {
		return this.sprite.getTexCoords();
	}
}
