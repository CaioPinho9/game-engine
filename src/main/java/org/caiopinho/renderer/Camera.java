package org.caiopinho.renderer;

import lombok.Getter;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

@Getter
public class Camera {
	private Matrix4f projectionMatrix, viewMatrix, inverseProjection, inverseView;
	public Vector2f position;
	// TODO: Fix projection
	private Vector2f projectionSize = new Vector2f(32 * 38, 32 * 21);

	public Camera(Vector2f position) {
		this.position = position;
		this.projectionMatrix = new Matrix4f();
		this.viewMatrix = new Matrix4f();
		this.inverseProjection = new Matrix4f();
		this.inverseView = new Matrix4f();
		this.adjustProjection();
	}

	public void adjustProjection() {
		this.projectionMatrix.identity();
		this.projectionMatrix.ortho(0, this.projectionSize.x, 0, this.projectionSize.y, -1, 100);
		this.projectionMatrix.invert(this.inverseProjection);
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
		this.viewMatrix.invert(this.inverseView);
		return this.viewMatrix;
	}

	public void moveCameraTo(float x, float y, float deltaTime) {
		Vector2f newPosition = new Vector2f(x, y);
		Vector2f delta = new Vector2f(newPosition.x - this.position.x, newPosition.y - this.position.y);
		this.position.add(delta.x * deltaTime, delta.y * deltaTime);
	}

	public void addDeltaMoveCamera(float x, float y, float deltaTime) {
		Vector2f delta = new Vector2f(x, y);
		this.position.add(delta.x * deltaTime, delta.y * deltaTime);
	}
}
