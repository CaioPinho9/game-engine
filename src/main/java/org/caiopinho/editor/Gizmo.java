package org.caiopinho.editor;

import org.caiopinho.core.GameObject;
import org.caiopinho.core.Transform;

public class Gizmo extends GameObject {
	public boolean isDragging = false;

	public Gizmo(String name, Transform transform, int zIndex) {
		super(name, transform, zIndex);
	}
}
