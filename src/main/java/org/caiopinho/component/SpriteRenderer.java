package org.caiopinho.component;

import lombok.Getter;

import org.caiopinho.assets.Sprite;
import org.caiopinho.assets.Texture;
import org.caiopinho.core.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;

@Getter
public class SpriteRenderer extends Component {
	private Vector4f color;
	private Sprite sprite;
	private boolean isDirty = false;

	private Transform lastTransform;

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

	@Override public void start() {
		this.lastTransform = this.gameObject.transform.copy();
	}

	@Override public void update(float deltaTime) {
		if (this.lastTransform.equals(this.gameObject.transform)) {
			this.gameObject.transform.copy(this.lastTransform);
			this.isDirty = true;
		}
	}

	public Texture getTexture() {
		return this.sprite.getTexture();
	}

	public Vector2f[] getTexCoords() {
		return this.sprite.getTexCoords();
	}

	public void setColor(Vector4f color) {
		if (!this.color.equals(color)) {
			this.isDirty = true;
			this.color = color;
		}
	}

	public void setSprite(Sprite sprite) {
		//TODO: Dirty sprite logic
		this.sprite = sprite;
		this.isDirty = true;
	}

	public void setClean() {
		this.isDirty = false;
	}
}
