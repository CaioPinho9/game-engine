package org.caiopinho.component;

import org.joml.Vector3f;

public class RigidBody extends Component {
	private int colliderType = 0;
	private float mass = 1;
	private Vector3f velocity = new Vector3f(0, 0, 0);
	private transient boolean isGrounded = false;

	@Override
	public void update(float deltaTime) {
	}
}
