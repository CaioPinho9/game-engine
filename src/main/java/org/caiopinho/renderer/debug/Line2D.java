package org.caiopinho.renderer.debug;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.joml.Vector2f;
import org.joml.Vector4f;

@Getter
@AllArgsConstructor
public class Line2D {
	private Vector2f from;
	private Vector2f to;
	private Vector4f color;
	private int lifetime;
	private float width;
	private int zIndex;

	public int beginFrame() {
		return --this.lifetime;
	}

	public boolean equals(Line2D other) {
		if (other == null) {
			return false;
		}

		return this.from.equals(other.from) && this.to.equals(other.to) && this.color.equals(other.color) && this.width == other.width;
	}
}
