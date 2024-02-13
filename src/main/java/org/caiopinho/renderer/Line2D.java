package org.caiopinho.renderer;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.joml.Vector2f;
import org.joml.Vector4f;

@AllArgsConstructor
@Getter
public class Line2D {
	private Vector2f from;
	private Vector2f to;
	private Vector4f color;
	private int lifetime;

	public int beginFrame() {
		return --this.lifetime;
	}
}
