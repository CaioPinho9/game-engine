package org.caiopinho.renderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.caiopinho.assets.Texture;
import org.caiopinho.component.SpriteRenderer;
import org.caiopinho.core.GameObject;
import org.caiopinho.renderer.batch.Batch;
import org.caiopinho.renderer.batch.DebugBatch;
import org.caiopinho.renderer.batch.RenderBatch;
import org.caiopinho.renderer.debug.Line2D;

public class Renderer {
	private final int MAX_BATCH_SIZE = 1000;
	private final List<Batch<?>> batches;

	public Renderer() {
		this.batches = new ArrayList<>();
	}

	public void add(GameObject gameObject) {
		SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);
		if (spriteRenderer != null) {
			this.add(spriteRenderer);
		}
	}

	public void add(SpriteRenderer spriteRenderer) {
		for (Batch<?> batch : this.batches) {
			if (!(batch instanceof RenderBatch renderBatch)) {
				continue;
			}

			if (renderBatch.hasSpace() && renderBatch.getZIndex() == spriteRenderer.getZIndex()) {
				Texture texture = spriteRenderer.getTexture();
				if (texture == null || (renderBatch.hasTexture(texture) || renderBatch.hasTextureSpace())) {
					renderBatch.addElement(spriteRenderer);
					return;
				}
			}
		}

		RenderBatch newBatch = new RenderBatch(this.MAX_BATCH_SIZE, spriteRenderer.getZIndex());
		newBatch.start();
		this.batches.add(newBatch);
		newBatch.addElement(spriteRenderer);
		Collections.sort(this.batches);
	}

	public void add(Line2D line) {
		for (Batch<?> batch : this.batches) {
			if (!(batch instanceof DebugBatch debugBatch)) {
				continue;
			}

			if (debugBatch.hasSpace() && debugBatch.getZIndex() == line.getZIndex() && debugBatch.getLineWidth() == line.getWidth()) {
				debugBatch.addElement(line);
				return;
			}
		}

		DebugBatch newBatch = new DebugBatch(this.MAX_BATCH_SIZE, line.getWidth(), line.getZIndex());
		newBatch.start();
		this.batches.add(newBatch);
		newBatch.addElement(line);
		Collections.sort(this.batches);
	}

	public void render() {
		for (int i = 0; i < this.batches.size(); i++) {
			Batch<?> batch = this.batches.get(i);

			if (batch.getElementCount() == 0) {
				this.batches.remove(i--);
				continue;
			}

			batch.render();
		}
	}

	public void clear() {
		this.batches.clear();
	}
}
