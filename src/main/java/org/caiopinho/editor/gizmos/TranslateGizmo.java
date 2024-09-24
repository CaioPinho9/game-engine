package org.caiopinho.editor.gizmos;

import org.caiopinho.assets.AssetPool;
import org.caiopinho.component.SpriteRenderer;
import org.caiopinho.core.MouseListener;
import org.caiopinho.core.Transform;
import org.caiopinho.editor.components.GridTools;
import org.joml.Vector2f;

public class TranslateGizmo extends Gizmo {
	private final GridTools gridTools;

	public TranslateGizmo(String name, boolean isVertical, GridTools gridTools) {
		super(name, new Transform(new Vector2f(), new Vector2f(1, 1), isVertical ? 180 : 90, Z_INDEX), isVertical, GizmoMode.TRANSLATE);
		this.gridTools = gridTools;

		SpriteRenderer spriteRenderer = new SpriteRenderer();
		spriteRenderer.setColor(isVertical ? 1 : 0, isVertical ? 0 : 1, 0, 1);
		spriteRenderer.setTexture(AssetPool.getTexture("assets/textures/gizmo_translation.png"));
		this.addComponent(spriteRenderer);
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
	
	@Override
	public void setDragging(boolean dragging) {
		super.setDragging(dragging);
		if (this.isVertical()) {
			this.setGizmoOffset(MouseListener.getOrthoY() - this.transform.position.y + this.transform.scale.y / 2);
		} else {
			this.setGizmoOffset(MouseListener.getOrthoX() - this.transform.position.x + this.transform.scale.x / 2);
		}
	}

	private float calculateGridCoordinate(float coordinate) {
		return (int) ((coordinate - this.gizmoOffset) / this.gridTools.getGridSize()) * this.gridTools.getGridSize();
	}
}
