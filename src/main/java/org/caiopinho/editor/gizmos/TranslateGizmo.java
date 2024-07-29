package org.caiopinho.editor.gizmos;

import lombok.Getter;

import org.caiopinho.assets.AssetPool;
import org.caiopinho.component.SpriteRenderer;
import org.caiopinho.core.MouseListener;
import org.caiopinho.core.Transform;
import org.caiopinho.editor.components.GridTools;
import org.joml.Vector2f;

public class TranslateGizmo extends Gizmo {
	@Getter
	private final boolean isVertical;
	private final GridTools gridTools;

	public TranslateGizmo(String name, boolean isVertical, GridTools gridTools) {
		super(name, new Transform(new Vector2f(), new Vector2f(1, 1), isVertical ? 180 : 90), GizmoMode.TRANSLATE);
		this.isVertical = isVertical;
		this.gridTools = gridTools;

		SpriteRenderer spriteRenderer = new SpriteRenderer();
		spriteRenderer.setColor(isVertical ? 1 : 0, isVertical ? 0 : 1, 0, 1);
		spriteRenderer.setTexture(AssetPool.getTexture("assets/textures/gizmo_translation.png"));
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
		if (this.fixedMode) {
			if (this.isVertical) {
				this.target.transform.position.y = this.calculateGridCoordinate(MouseListener.getOrthoY());
			} else {
				this.target.transform.position.x = this.calculateGridCoordinate(MouseListener.getOrthoX());
			}
		} else {
			if (this.isVertical) {
				this.target.transform.position.y = MouseListener.getOrthoY() - this.gizmoOffset;
			} else {
				this.target.transform.position.x = MouseListener.getOrthoX() - this.gizmoOffset;
			}
		}
	}

	private float calculateGridCoordinate(float coordinate) {
		return (int) ((coordinate - this.gizmoOffset) / this.gridTools.getGridSize()) * this.gridTools.getGridSize();
	}
}
