package org.caiopinho.assets;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;

public class Spritesheet {
	private final List<Sprite> sprites = new ArrayList<>();

	public Spritesheet(Texture texture, int spriteWidth, int spriteHeight, int spriteCount, int spacing) {
		int currentX = 0;
		int currentY = texture.getHeight() - spriteHeight;

		for (int i = 0; i < spriteCount; i++) {
			float leftX = currentX / (float) texture.getWidth();
			float rightX = (currentX + spriteWidth) / (float) texture.getWidth();
			float topY = (currentY + spriteHeight) / (float) texture.getHeight();
			float bottomY = currentY / (float) texture.getHeight();

			Vector2f[] texCoords = new Vector2f[] {
					new Vector2f(rightX, topY),
					new Vector2f(rightX, bottomY),
					new Vector2f(leftX, bottomY),
					new Vector2f(leftX, topY)
			};
			Sprite sprite = new Sprite(texture, texCoords);
			this.sprites.add(sprite);

			currentX += spriteWidth + spacing;
			if (currentX >= texture.getWidth()) {
				currentX = 0;
				currentY -= spriteHeight + spacing;
			}
		}
	}

	public Sprite getSprite(int index) {
		return this.sprites.get(index);
	}
}
