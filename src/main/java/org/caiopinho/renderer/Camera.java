package org.caiopinho.renderer;

import lombok.Getter;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {
	@Getter private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;
	public Vector2f position;

	public Camera(Vector2f position) {
		this.position = position;
		this.projectionMatrix = new Matrix4f();
		this.viewMatrix = new Matrix4f();
		this.adjustProjection();
	}

	public void adjustProjection() {
		this.projectionMatrix.identity();
		this.projectionMatrix.ortho(0, 32 * 40, 0, 32 * 21, -1, 100);
	}

	public Matrix4f getViewMatrix() {
		Vector3f cameraFront = new Vector3f(0, 0, -1);
		Vector3f cameraUp = new Vector3f(0, 1, 0);
		this.viewMatrix.identity();
		this.viewMatrix = this.viewMatrix.lookAt(
				new Vector3f(this.position.x, this.position.y, 20),
				cameraFront.add(this.position.x, this.position.y, 0),
				cameraUp
		);
		return this.viewMatrix;
	}

}
