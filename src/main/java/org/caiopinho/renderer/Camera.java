package org.caiopinho.renderer;

import lombok.Getter;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

@Getter
public class Camera {
	private static final float MAX_ZOOM = 10;
	private static final float MIN_ZOOM = .01f;
	private Matrix4f projectionMatrix, viewMatrix, inverseProjection, inverseView;
	public Vector2f position;
	private float zoom;
	// TODO: Fix projection
	private transient Vector2f projectionSize = new Vector2f(32 * 38, 32 * 21);

	public Camera(Vector2f position) {
		this.position = position;
		this.projectionMatrix = new Matrix4f();
		this.viewMatrix = new Matrix4f();
		this.inverseProjection = new Matrix4f();
		this.inverseView = new Matrix4f();
		this.zoom = 1;
		this.adjustProjection();
	}

	public void adjustProjection() {
		this.projectionMatrix.identity();
		this.projectionMatrix.ortho(0, this.projectionSize.x * this.zoom, 0, this.projectionSize.y * this.zoom, -1, 100);
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

	public void addZoom(float zoom) {
		float newZoom = this.zoom + zoom;
		if (MAX_ZOOM <= newZoom || newZoom <= MIN_ZOOM) {
			return;
		}

		this.zoom = newZoom;
		this.adjustProjection();
	}

	public void setZoom(float zoom) {
		if (zoom > MAX_ZOOM) {
			zoom = MAX_ZOOM;
		}
		if (zoom < MIN_ZOOM) {
			zoom = MIN_ZOOM;
		}

		this.zoom = zoom;
		this.adjustProjection();
	}

	public Vector2f normalizeScreenCoordinates(float x, float y) {
		// Convert the pixel coordinates to range from (0, 1)
		float normalizedX = (x - this.position.x) / (this.projectionSize.x * this.zoom);
		float normalizedY = (y - this.position.y) / (this.projectionSize.y * this.zoom);

		// Adjust the range from (0, 1) to (-1, 1)
		normalizedX = normalizedX * 2 - 1;
		normalizedY = normalizedY * 2 - 1;

		return new Vector2f(normalizedX, normalizedY);
	}

	public Vector2f calculateViewCenter() {
		return new Vector2f(this.position.x + (this.projectionSize.x * this.zoom) / 2,
				this.position.y + (this.projectionSize.y * this.zoom) / 2);
	}
}
