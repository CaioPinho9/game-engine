package org.caiopinho.component;

import lombok.Getter;

import org.caiopinho.assets.Sprite;
import org.caiopinho.assets.Texture;
import org.caiopinho.core.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;

import imgui.ImGui;
import imgui.enums.ImGuiColorEditFlags;

@Getter
public class SpriteRenderer extends Component {
	private Vector4f color;
	private Sprite sprite;

	private boolean isDirty = true;
	private Transform lastTransform;

	public SpriteRenderer(Vector4f color) {
		this.color = color;
		this.sprite = null;
	}

	public SpriteRenderer(Sprite sprite) {
		this.color = new Vector4f(1, 1, 1, 1);
		this.sprite = sprite;
	}

	public SpriteRenderer(Vector4f color, Sprite sprite) {
		this.color = color;
		this.sprite = sprite;
	}

	@Override public void start() {
		this.lastTransform = this.gameObject.transform.copy();
	}

	@Override public void update(float deltaTime) {
		if (!this.lastTransform.equals(this.gameObject.transform)) {
			this.gameObject.transform.copy(this.lastTransform);
			this.isDirty = true;
		}
	}

	public Texture getTexture() {
		if (this.sprite == null) {
			return null;
		}
		return this.sprite.getTexture();
	}

	public Vector2f[] getTexCoords() {
		if (this.sprite == null) {
			return new Vector2f[] {
					new Vector2f(0, 0),
					new Vector2f(0, 0),
					new Vector2f(0, 0),
					new Vector2f(0, 0)
			};
		}
		return this.sprite.getTexCoords();
	}

	public void setColor(Vector4f color) {
		if (!this.color.equals(color)) {
			this.isDirty = true;
			this.color = color;
		}
	}

	public void setColor(float x, float y, float z, float w) {
		Vector4f color = new Vector4f(x, y, z, w);
		if (!this.color.equals(color)) {
			this.isDirty = true;
			this.color = color;
		}
	}

	public void setSprite(Sprite sprite) {
		//TODO: Dirty sprite logic
		this.sprite = sprite;
		this.isDirty = true;
	}

	public void setClean() {
		this.isDirty = false;
	}

	@Override public void imgui() {
		ImGui.begin("Sprite Renderer"); // Begin ImGui window
		ImGui.text("Color Picker");
		// Temporary storage for color editing
		float[] newColor = { this.color.x, this.color.y, this.color.z, this.color.w };

		// Color picker widget
		int flags = ImGuiColorEditFlags.AlphaBar | ImGuiColorEditFlags.AlphaPreviewHalf | ImGuiColorEditFlags.DisplayRGB | ImGuiColorEditFlags.DisplayHex;
		if (ImGui.colorPicker4("Color", newColor, flags)) {
			// Update the color vector if the color picker value changes
			this.setColor(newColor[0], newColor[1], newColor[2], newColor[3]);
		}

		ImGui.end(); // End ImGui window
	}
}
