package org.caiopinho.scene;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import org.caiopinho.component.Component;
import org.caiopinho.core.GameObject;
import org.caiopinho.renderer.Camera;
import org.caiopinho.renderer.Renderer;
import org.caiopinho.serializer.ComponentSerializer;
import org.caiopinho.serializer.GameObjectSerializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import imgui.ImGui;

public abstract class Scene {
	@Getter protected Camera camera;
	protected Renderer renderer = new Renderer();
	private boolean isRunning;
	protected boolean wasLoaded;
	protected List<GameObject> gameObjects = new ArrayList<>();

	protected GameObject activeGameObject = null;

	private final Gson gson = new GsonBuilder()
			.setPrettyPrinting()
			.registerTypeAdapter(GameObject.class, new GameObjectSerializer())
			.registerTypeAdapter(Component.class, new ComponentSerializer())
			.create();

	public void init() {
	}

	private void loadResources() {
	}

	public void start() {
		for (GameObject gameObject : this.gameObjects) {
			gameObject.start();
			this.renderer.add(gameObject);
		}
		this.isRunning = true;
	}

	public void update(float deltaTime) {
		System.out.print("FPS: " + 1 / deltaTime + "\r");
		for (GameObject gameObject : this.gameObjects) {
			gameObject.update(deltaTime);
		}
		this.renderer.render();
	}

	public void sceneImgui() {
		if (this.activeGameObject != null) {
			ImGui.begin("Inspector");
			this.activeGameObject.imgui();
			ImGui.end();
		}

		this.imgui();
	}

	public void imgui() {

	}

	public void addGameObjectToScene(GameObject gameObject) {
		this.gameObjects.add(gameObject);
		if (this.isRunning) {
			gameObject.start();
			this.renderer.add(gameObject);
		}
	}

	public void save() {
		try {
			FileWriter writer = new FileWriter("level.txt");
			writer.write(this.gson.toJson(this.gameObjects));
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void load() {
		String text = "";

		try {
			text = new String(Files.readAllBytes(Paths.get("level.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (!text.isEmpty()) {
			GameObject[] gameObjects = this.gson.fromJson(text, GameObject[].class);
			this.gameObjects.clear();
			this.renderer.clear();
			for (GameObject gameObject : gameObjects) {
				this.addGameObjectToScene(gameObject);
			}
			this.wasLoaded = true;
			this.activeGameObject = gameObjects[0];
		}
	}
}
