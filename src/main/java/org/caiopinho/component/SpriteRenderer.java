package org.caiopinho.component;

import org.caiopinho.core.Component;

public class SpriteRenderer extends Component {

	@Override public void start() {
		System.out.println("Hello from SpriteRenderer starting!");
	}

	@Override public void update(float deltaTime) {
		System.out.println("Hello from SpriteRenderer updating!");
	}
}
