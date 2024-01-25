package org.caiopinho.component;

import lombok.Getter;

import org.caiopinho.core.Component;
import org.joml.Vector4f;

@Getter
public class SpriteRenderer extends Component {
	private final Vector4f color;

	public SpriteRenderer(Vector4f color) {
		this.color = color;
	}

	@Override public void update(float deltaTime) {
		
	}
}
