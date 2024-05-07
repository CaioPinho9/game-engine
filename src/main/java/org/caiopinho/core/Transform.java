package org.caiopinho.core;

import org.joml.Vector2f;

public class Transform {
	public Vector2f position;
	public int zIndex;
	public Vector2f scale;
	public float rotation;

	public Transform() {
		this.init(new Vector2f(), new Vector2f(), 0, 0);
	}

	public Transform(Vector2f position) {
		this.init(position, new Vector2f(), 0, 0);
	}

	public Transform(Vector2f position, Vector2f scale) {
		this.init(position, scale, 0, 0);
	}

	public Transform(Vector2f position, Vector2f scale, float rotation) {
		this.init(position, scale, rotation, 0);
	}

	public Transform(Vector2f position, Vector2f scale, float rotation, int zIndex) {
		this.init(position, scale, rotation, zIndex);
	}

	public void init(Vector2f position, Vector2f scale, float rotation, int zIndex) {
		this.position = position;
		this.zIndex = zIndex;
		this.scale = scale;
		this.rotation = rotation;
	}

	public Transform copy() {
		return new Transform(new Vector2f(this.position), new Vector2f(this.scale));
	}

	public void copy(Transform transform) {
		transform.position.set(this.position);
		transform.scale.set(this.scale);
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (!(object instanceof Transform transform)) {
			return false;
		}
		return this.position.equals(transform.position) && this.scale.equals(transform.scale);
	}
}
