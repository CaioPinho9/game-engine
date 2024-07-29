package org.caiopinho.editor.gizmos;

import lombok.Getter;

import org.caiopinho.assets.AssetPool;
import org.caiopinho.component.SpriteRenderer;
import org.caiopinho.core.MouseListener;
import org.caiopinho.core.Transform;
import org.caiopinho.editor.components.GridTools;
import org.joml.Vector2f;

public class ScaleGizmo extends Gizmo {
	@Getter
	private final boolean isVertical;
	private final GridTools gridTools;

	private final float SENSITIVITY = 20f;
	private float deltaAccumulator = 0;

	public ScaleGizmo(String name, boolean isVertical, GridTools gridTools) {
		super(name, new Transform(new Vector2f(), new Vector2f(1, 1), isVertical ? 180 : 90), GizmoMode.SCALE);
		this.isVertical = isVertical;
		this.gridTools = gridTools;

		SpriteRenderer spriteRenderer = new SpriteRenderer();
		spriteRenderer.setColor(isVertical ? 1 : 0, isVertical ? 0 : 1, 0, 1);
		spriteRenderer.setTexture(AssetPool.getTexture("assets/textures/gizmo_scale.png"));
		this.addComponent(spriteRenderer);
	}

	@Override
	public void followTarget(float cameraZoom) {
		this.target.transform.copy(this.transform);
		float scale = cameraZoom * SCALE;
		this.transform.scale = new Vector2f(scale * ASPECT_RATIO, scale);
		this.transform.position.add(this.isVertical ? 0 : this.transform.scale.y / 2, this.isVertical ? this.transform.scale.y / 2 : 0);
	}

	@Override
	public void use() {
		if (fixedMode) {
			handleFixedMode();
		} else {
			handleDynamicMode();
		}

		clampScale();
	}

	private void handleFixedMode() {
		deltaAccumulator += isVertical ? MouseListener.getDeltaY() : MouseListener.getDeltaX();
		float aspectRatio = 1;

		if (aspectRatioLockMode) {
			aspectRatio = target.transform.scale.x / target.transform.scale.y;
		}

		if (Math.abs(deltaAccumulator) >= SENSITIVITY) {
			if (isVertical) {
				float targetScaleY = calculateGridCoordinate(this.target.transform.scale.y);

				if (deltaAccumulator > 0) {
					targetScaleY += gridTools.getGridSize();
				} else {
					targetScaleY -= gridTools.getGridSize();
				}

				target.transform.scale.y = targetScaleY;

				if (aspectRatioLockMode) {
					target.transform.scale.x = targetScaleY * aspectRatio;
				}

			} else {
				float targetScaleX = calculateGridCoordinate(this.target.transform.scale.x);

				if (deltaAccumulator < 0) {
					targetScaleX += gridTools.getGridSize();
				} else {
					targetScaleX -= gridTools.getGridSize();
				}

				target.transform.scale.x = targetScaleX;

				if (aspectRatioLockMode) {
					target.transform.scale.y = targetScaleX / aspectRatio;
				}
			}
			deltaAccumulator = 0;
		}
	}

	private void handleDynamicMode() {
		float valueY = 0;
		float valueX = 0;
		float aspectRatio = 1;

		if (aspectRatioLockMode) {
			aspectRatio = target.transform.scale.x / target.transform.scale.y;
		}

		if (isVertical) {
			valueY = (float) (MouseListener.getDeltaY() * Math.log(target.transform.scale.y));
			if (aspectRatioLockMode) {
				valueX = valueY * aspectRatio;
			}
		} else {
			valueX = (float) (MouseListener.getDeltaX() * -Math.log(target.transform.scale.x));
			if (aspectRatioLockMode) {
				valueY = valueX / aspectRatio;
			}
		}

		target.transform.scale.add(valueX, valueY);
	}

	private void clampScale() {
		target.transform.scale.x = Math.max(target.transform.scale.x, MIN_SCALE_OBJECT);
		target.transform.scale.y = Math.max(target.transform.scale.y, MIN_SCALE_OBJECT);
	}

	private float calculateGridCoordinate(float coordinate) {
		return (int) (coordinate / this.gridTools.getGridSize()) * this.gridTools.getGridSize();
	}
}
