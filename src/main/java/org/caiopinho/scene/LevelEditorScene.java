package org.caiopinho.scene;

import lombok.Getter;
import lombok.NoArgsConstructor;

import org.caiopinho.assets.AssetPool;
import org.caiopinho.assets.Sprite;
import org.caiopinho.assets.Spritesheet;
import org.caiopinho.component.RigidBody;
import org.caiopinho.component.SpriteRenderer;
import org.caiopinho.core.GameObject;
import org.caiopinho.core.Prefabs;
import org.caiopinho.core.Transform;
import org.caiopinho.editor.TranslateGizmos;
import org.caiopinho.editor.components.CameraControls;
import org.caiopinho.editor.components.DebugView;
import org.caiopinho.editor.components.GridTools;
import org.caiopinho.editor.components.MouseControls;
import org.caiopinho.math.MathHelper;
import org.caiopinho.renderer.Camera;
import org.caiopinho.renderer.debug.DebugDraw;
import org.joml.Vector2f;
import org.joml.Vector4f;

import imgui.ImGui;
import imgui.ImVec2;

@NoArgsConstructor public class LevelEditorScene extends Scene {
	private Spritesheet sprites;
	private GameObject levelEditor;
	@Getter private TranslateGizmos translateGizmos;

	@Override public void init() {
		this.camera = new Camera(new Vector2f());

		// Add sprite without gameobject to renderer
		SpriteRenderer spriteRenderer0 = new SpriteRenderer();
		spriteRenderer0.setColor(new Vector4f(1, 0, 0, .8f));
		spriteRenderer0.setTransform(new Transform(new Vector2f(0, 210), new Vector2f(100, 100)));
		spriteRenderer0.setSprite(this.sprites.getSprite(0));
		this.renderer.add(spriteRenderer0);

		this.translateGizmos = new TranslateGizmos(this.camera);
		this.translateGizmos.setSelectable(false);
		this.translateGizmos.setSerializable(false);
		this.addGameObjectToScene(this.translateGizmos);
		this.addGameObjectToScene(this.translateGizmos.getGizmoVertical());
		this.addGameObjectToScene(this.translateGizmos.getGizmoHorizontal());

		GridTools gridTools = new GridTools();
		this.levelEditor = new GameObject("LevelEditor", new Transform(), 0);
		this.levelEditor.setSelectable(false);
		this.levelEditor.setSerializable(false);
		this.levelEditor.addComponent(new MouseControls(this.translateGizmos, gridTools, this));
		this.levelEditor.addComponent(gridTools);
		this.levelEditor.addComponent(new DebugView(this));
		this.levelEditor.addComponent(new CameraControls(this.camera));
		this.addGameObjectToScene(this.levelEditor);

		if (this.wasLoaded) {
			System.out.println("Scene was loaded");
			return;
		}

		GameObject gameObject1 = new GameObject("Object1", new Transform(new Vector2f(100, 100), new Vector2f(100, 100)), 1);
		SpriteRenderer spriteRenderer1 = new SpriteRenderer();
		spriteRenderer1.setColor(new Vector4f(1, 0, 0, .8f));
		gameObject1.addComponent(spriteRenderer1);
		gameObject1.addComponent(new RigidBody());

		GameObject gameObject2 = new GameObject("Object2", new Transform(new Vector2f(180, 100), new Vector2f(100, 100)), 0);
		SpriteRenderer spriteRenderer2 = new SpriteRenderer();
		spriteRenderer2.setColor(new Vector4f(0, 1, 0, .8f));
		gameObject2.addComponent(spriteRenderer2);

		GameObject gameObject3 = new GameObject("Object3", new Transform(new Vector2f(0, 0), new Vector2f(100, 100)), 0);
		SpriteRenderer spriteRenderer3 = new SpriteRenderer();
		Sprite sprite3 = new Sprite();
		sprite3.setTexture(AssetPool.getTexture("assets/textures/logo.png"));
		spriteRenderer3.setSprite(sprite3);
		gameObject3.addComponent(spriteRenderer3);

		GameObject gameObject4 = new GameObject("Object4", new Transform(new Vector2f(300, 0), new Vector2f(900, 168.75f)), 0);
		SpriteRenderer spriteRenderer4 = new SpriteRenderer();
		Sprite sprite4 = new Sprite();
		sprite4.setTexture(AssetPool.getTexture("assets/textures/ubuntu dices.png"));
		spriteRenderer4.setSprite(sprite4);
		gameObject4.addComponent(spriteRenderer4);

		this.addGameObjectToScene(gameObject1);
		this.addGameObjectToScene(gameObject2);
		this.addGameObjectToScene(gameObject3);
		this.addGameObjectToScene(gameObject4);

		for (int i = 0; i < 26; i++) {
			GameObject gameObject = new GameObject("Object " + (i + 5), new Transform(new Vector2f(i * 25 + 400, 0), new Vector2f(100, 100)), 0);
			SpriteRenderer spriteRenderer5 = new SpriteRenderer();
			spriteRenderer5.setSprite(AssetPool.getSpritesheet("character").getSprite(i));
			gameObject.addComponent(spriteRenderer5);
			this.addGameObjectToScene(gameObject);
		}

	}

	float angle = 0;
	float percent = 0;

	@Override public void update(float deltaTime) {
		this.angle += 6 * deltaTime;
		this.percent += .05f * deltaTime;
		this.percent %= 1;

		DebugDraw.addLine2D(new Vector2f(100, 500), new Vector2f(200, 200), new Vector4f(1, 0, 0, 1));
		DebugDraw.addBox2D(new Transform(new Vector2f(400, 400), new Vector2f(100, 200), this.angle), new Vector4f(0, 1, 0, 1), 5);
		DebugDraw.addCircle2D(new Transform(new Vector2f(600, 400), new Vector2f(50, 50), this.angle / 2), new Vector4f(0, 0, 1, 1), 1000, 1, this.percent);
		this.levelEditor.getComponent(MouseControls.class).update(deltaTime);

		Vector2f center = new Vector2f(800, 300);
		Vector2f end1 = new Vector2f(center).add(new Vector2f(0, 50));
		MathHelper.rotate(end1, -this.angle, center);
		DebugDraw.addLine2D(center, end1, new Vector4f(1, 0, 0, .5f), 1, 1, -1);

		Vector2f end2 = new Vector2f(center).add(new Vector2f(0, 45));
		MathHelper.rotate(end2, -this.angle / 60, center);
		DebugDraw.addLine2D(center, end2, new Vector4f(.75f, 0, 0, 1), 1, 2, 0);

		Vector2f end3 = new Vector2f(center).add(new Vector2f(0, 40));
		MathHelper.rotate(end3, -this.angle / 360, center);
		DebugDraw.addLine2D(center, end3, new Vector4f(0, 0, 0, 1), 1, 1.5f, 1);
		DebugDraw.addCircle2D(new Transform(center, new Vector2f(60, 60)), new Vector4f(0, .5f, 0, 1), 30, 1, 1, 1);

		super.update(deltaTime);
	}

	@Override protected void loadResources() {
		AssetPool.getShader("assets/shaders/default.glsl");
		AssetPool.getTexture("assets/textures/logo.png");
		AssetPool.getTexture("assets/textures/ubuntu dices.png");
		this.sprites = new Spritesheet(AssetPool.getTexture("assets/textures/spritesheet.png"), 16, 16, 26, 0);
		AssetPool.addSpritesheet("character", this.sprites);
		AssetPool.addSpritesheet("assets/images/gizmos.png", new Spritesheet(AssetPool.getTexture("assets/textures/gizmos.png"), 24, 48, 2, 0));
	}

	@Override public void imgui() {
		ImGui.begin("Level Editor");
		this.levelEditor.imgui();
		ImGui.end();

		ImGui.begin("Test window");

		ImVec2 windowPos = new ImVec2();
		ImGui.getWindowPos(windowPos);
		ImVec2 windowSize = new ImVec2();
		ImGui.getWindowSize(windowSize);
		ImVec2 itemSpacing = new ImVec2();
		ImGui.getStyle().getItemSpacing(itemSpacing);

		float windowX2 = windowPos.x + windowSize.x;
		for (int i = 0; i < this.sprites.size(); i++) {
			Sprite sprite = this.sprites.getSprite(i);
			float spriteWidth = sprite.getWidth() * 3;
			float spriteHeight = sprite.getHeight() * 3;
			int id = sprite.getTextureId();
			Vector2f[] texCoords = sprite.getTexCoords();

			ImGui.pushID(i);
			if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
				GameObject object = Prefabs.createSpriteObject(sprite, spriteWidth, spriteHeight);
				this.addGameObjectToScene(object);
				this.levelEditor.getComponent(MouseControls.class).setHoldingGameObject(object);
			}
			ImGui.popID();

			ImVec2 lastButtonPos = new ImVec2();
			ImGui.getItemRectMax(lastButtonPos);
			float lastButtonX2 = lastButtonPos.x;
			float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
			if (i + 1 < this.sprites.size() && nextButtonX2 < windowX2) {
				ImGui.sameLine();
			}

		}

		ImGui.text("Hello, world!");
		ImGui.end();
	}
}
