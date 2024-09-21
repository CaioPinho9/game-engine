package org.caiopinho.editor.gizmos;

import org.caiopinho.assets.AssetPool;
import org.caiopinho.component.SpriteRenderer;
import org.caiopinho.core.MouseListener;
import org.caiopinho.core.Transform;
import org.caiopinho.editor.components.GridTools;
import org.joml.Vector2f;

public class ScaleGizmo extends Gizmo {
	private final GridTools gridTools;

	private final float SENSITIVITY = 20f;
	private float deltaAccumulator = 0;

	public ScaleGizmo(String name, boolean isVertical, GridTools gridTools) {
		super(name, new Transform(new Vector2f(), new Vector2f(1, 1), isVertical ? 180 : 90, Z_INDEX), isVertical, GizmoMode.SCALE);
		this.gridTools = gridTools;

		SpriteRenderer spriteRenderer = new SpriteRenderer();
		spriteRenderer.setColor(isVertical ? 1 : 0, isVertical ? 0 : 1, 0, 1);
		spriteRenderer.setTexture(AssetPool.getTexture("assets/textures/gizmo_scale.png"));
		this.addComponent(spriteRenderer);
	}

	@Override
	public void use() {
		if (this.fixedMode) {
			this.handleFixedMode();
		} else {
			this.handleDynamicMode();
		}
	}

	private void handleFixedMode() {
		this.deltaAccumulator += this.isVertical ? MouseListener.getDeltaY() : MouseListener.getDeltaX();
		float aspectRatio = 1;

		if (this.aspectRatioLockMode) {
			aspectRatio = this.target.transform.scale.x / this.target.transform.scale.y;
		}

		if (Math.abs(this.deltaAccumulator) >= this.SENSITIVITY) {
			if (this.isVertical) {
				float targetScaleY = this.calculateGridCoordinate(this.target.transform.scale.y);

				if (this.deltaAccumulator > 0) {
					targetScaleY += this.gridTools.getGridSize();
				} else {
					targetScaleY -= this.gridTools.getGridSize();
				}

				this.target.transform.scale.y = targetScaleY;

				if (this.aspectRatioLockMode) {
					this.target.transform.scale.x = targetScaleY * aspectRatio;
				}

			} else {
				float targetScaleX = this.calculateGridCoordinate(this.target.transform.scale.x);

				if (this.deltaAccumulator < 0) {
					targetScaleX += this.gridTools.getGridSize();
				} else {
					targetScaleX -= this.gridTools.getGridSize();
				}

				this.target.transform.scale.x = targetScaleX;

				if (this.aspectRatioLockMode) {
					this.target.transform.scale.y = targetScaleX / aspectRatio;
				}
			}
			this.deltaAccumulator = 0;
		}
	}

	private void handleDynamicMode() {
		float valueY = 0;
		float valueX = 0;
		float aspectRatio = 1;

		if (this.aspectRatioLockMode) {
			aspectRatio = this.target.transform.scale.x / this.target.transform.scale.y;
		}

		if (this.isVertical) {
			valueY = (float) (MouseListener.getDeltaY() * Math.max(Math.log(Math.abs(this.target.transform.scale.y)), .1f));
			if (this.aspectRatioLockMode) {
				valueX = valueY * aspectRatio;
			}
		} else {
			valueX = (float) (MouseListener.getDeltaX() * -Math.max(Math.log(Math.abs(this.target.transform.scale.x)), .1f));
			if (this.aspectRatioLockMode) {
				valueY = valueX / aspectRatio;
			}
		}

		this.target.transform.scale.add(valueX, valueY);
	}

	private float calculateGridCoordinate(float coordinate) {
		return (int) (coordinate / this.gridTools.getGridSize()) * this.gridTools.getGridSize();
	}
}
