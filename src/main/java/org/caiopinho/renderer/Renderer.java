package org.caiopinho.renderer;

import java.util.ArrayList;
import java.util.List;

import org.caiopinho.component.SpriteRenderer;
import org.caiopinho.core.GameObject;

public class Renderer {
	private final int MAX_BATCH_SIZE = 1000;
	private List<RenderBatch> batches;

	public Renderer() {
		this.batches = new ArrayList<>();
	}

	public void add(GameObject gameObject) {
		SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);
		if (spriteRenderer != null) {
			this.addToRenderBatch(spriteRenderer);
		}
	}

	private void addToRenderBatch(SpriteRenderer spriteRenderer) {
		for (RenderBatch batch : this.batches) {
			if (batch.hasSpace()) {
				Texture texture = spriteRenderer.getTexture();
				if (texture == null || (batch.hasTexture(texture) || batch.hasTextureSpace())) {
					batch.addSprite(spriteRenderer);
					return;
				}
			}
		}

		RenderBatch newBatch = new RenderBatch(this.MAX_BATCH_SIZE);
		newBatch.start();
		this.batches.add(newBatch);
		newBatch.addSprite(spriteRenderer);
	}

	public void render() {
		for (RenderBatch batch : this.batches) {
			batch.render();
		}
	}
}
