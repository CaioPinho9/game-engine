package org.caiopinho.renderer.batch;

import lombok.Getter;

import org.caiopinho.assets.Shader;

public abstract class Batch<T> implements Comparable<Batch<?>> {
	protected final T[] elements;

	protected float[] vertices;
	protected final int maxBatchSize;
	@Getter protected int elementCount;

	protected boolean hasSpace;
	protected int vaoId, vboId;
	protected final Shader shader;
	@Getter protected final int zIndex;

	public boolean hasSpace() {
		return this.hasSpace;
	}

	public Batch(int maxBatchSize, int zIndex, Shader shader, T[] elements) {
		this.zIndex = zIndex;
		this.shader = shader;
		this.elements = elements;
		this.maxBatchSize = maxBatchSize;

		this.elementCount = 0;
		this.hasSpace = true;
	}

	public abstract void render();

	public abstract void addElement(T element);

	@Override
	public int compareTo(Batch o) {
		return Integer.compare(this.getZIndex(), o.getZIndex());
	}
}
