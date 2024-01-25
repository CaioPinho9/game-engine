package org.caiopinho.scene;

import lombok.NoArgsConstructor;

import org.caiopinho.component.SpriteRenderer;
import org.caiopinho.core.GameObject;
import org.caiopinho.core.Transform;
import org.caiopinho.renderer.Camera;
import org.joml.Vector2f;
import org.joml.Vector4f;

@NoArgsConstructor public class LevelEditorScene extends Scene {

	@Override public void init() {
		this.camera = new Camera(new Vector2f());

		int xOffset = 10;
		int yOffset = 10;

		float totalWidth = (float) 600 - xOffset * 2;
		float totalHeight = (float) 300 - xOffset * 2;
		float sizeX = totalWidth / 100;
		float sizeY = totalHeight / 100;

		for (int x = 0; x < 100; x++) {
			for (int y = 0; y < 100; y++) {
				float xPos = (float) x * sizeX + xOffset;
				float yPos = (float) y * sizeY + yOffset;
				GameObject gameObject = new GameObject("Test" + x + y, new Transform(new Vector2f(xPos, yPos), new Vector2f(sizeX, sizeY)));
				gameObject.addComponent(new SpriteRenderer(new Vector4f(xPos / totalWidth, yPos / totalHeight, 1f, 1)));
				this.addGameObjectToScene(gameObject);
			}
		}
	}

	@Override public void update(float deltaTime) {
		System.out.print("FPS: " + 1 / deltaTime + "\r");
		for (GameObject gameObject : this.gameObjects) {
			gameObject.update(deltaTime);
		}
		this.renderer.render();
	}
}
