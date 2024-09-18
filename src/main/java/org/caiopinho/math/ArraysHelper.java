package org.caiopinho.math;

import java.util.Objects;

import org.caiopinho.component.SpriteRenderer;

public class ArraysHelper {
	// Checks if the array contains the specified element.
	public static <T> boolean contains(T[] array, T element) {
		for (T item : array) {
			if (Objects.equals(item, element)) {
				return true;
			}
		}
		return false;
	}

	// Method to remove an element from an array by its index

	public static <T> void removeByIndex(T[] array, int index) {
		// Check if the index is out of bounds for the array
		if (index < 0 || index >= array.length) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + array.length);
		}

		// Use System.arraycopy to shift elements left by one from the index+1 to the end
		if (index < array.length - 1) {
			System.arraycopy(array, index + 1, array, index, array.length - index - 1);
		}

		// Nullify the last element to prevent memory leaks if it's an object array
		array[array.length - 1] = null;
	}

	// Checks if the array is empty.
	public static <T> boolean isEmpty(T[] array) {
		return array == null || array.length == 0;
	}

	public static <T> void removeByValue(T[] array, T value) {
		for (int i = 0; i < array.length; i++) {
			if (Objects.equals(array[i], value)) {
				removeByIndex(array, i);
				return;
			}
		}
	}

	public static <T> int indexOf(SpriteRenderer[] elements, T value) {
		for (int i = 0; i < elements.length; i++) {
			if (Objects.equals(elements[i], value)) {
				return i;
			}
		}
		return -1;
	}
}
