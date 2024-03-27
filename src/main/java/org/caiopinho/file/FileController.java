package org.caiopinho.file;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.caiopinho.component.Component;
import org.caiopinho.core.GameObject;
import org.caiopinho.serializer.ComponentSerializer;
import org.caiopinho.serializer.GameObjectSerializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FileController<T> {
	private static final Gson GSON = new GsonBuilder()
			.setPrettyPrinting()
			.registerTypeAdapter(GameObject.class, new GameObjectSerializer())
			.registerTypeAdapter(Component.class, new ComponentSerializer())
			.create();

	public void writeGSON(String directory, String fileName, List<T> content) {
		try {
			ensureDirectoryExists(directory);
			FileWriter writer = new FileWriter(directory + fileName);
			writer.write(GSON.toJson(content));
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public <T> T[] readGSON(String path, Class<T[]> clazz) {
		String text = "";
		try {
			text = new String(Files.readAllBytes(Paths.get(path)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		T[] objects = null;
		if (!text.isEmpty()) {
			objects = GSON.fromJson(text, clazz);
		}
		return objects;
	}

	public static void ensureDirectoryExists(String directoryName) {
		Path path = Paths.get(directoryName);
		if (!Files.exists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				throw new RuntimeException(String.format("Could not create %s directory", directoryName), e);
			}
		}
	}
}
