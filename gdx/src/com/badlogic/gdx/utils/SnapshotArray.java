
package com.badlogic.gdx.utils;

import java.util.Comparator;

/** Guarantees that array entries provided by {@link #begin()} between indexes 0 and {@link #size} at the time begin was called
 * will not be modified until {@link #end()} is called. If modification does occur, the backing array is copied prior to the
 * modification, ensuring that the backing array returned by {@link #begin()} is unaffected. To avoid allocation, an attempt is
 * made to reuse the extra array created as a result of this copy on subsequent copies.
 * <p>
 * It is suggested iteration be done in this way:
 * 
 * <pre>
 * SnapshotArray array = new SnapshotArray();
 * // ...
 * Object[] items = array.begin();
 * for (int i = 0, n = array.size; i &lt; n; i++) {
 * 	Object item = items[i];
 * 	// ...
 * }
 * array.end();
 * </pre>
 * @author Nathan Sweet */
public class SnapshotArray<T> extends Array<T> {
	private T[] snapshot, recycled;

	public SnapshotArray () {
		super();
	}

	public SnapshotArray (Array array) {
		super(array);
	}

	public SnapshotArray (boolean ordered, int capacity, Class<T> arrayType) {
		super(ordered, capacity, arrayType);
	}

	public SnapshotArray (boolean ordered, int capacity) {
		super(ordered, capacity);
	}

	public SnapshotArray (boolean ordered, T[] array) {
		super(ordered, array);
	}

	public SnapshotArray (Class<T> arrayType) {
		super(arrayType);
	}

	public SnapshotArray (int capacity) {
		super(capacity);
	}

	public SnapshotArray (T[] array) {
		super(array);
	}

	/** Returns the backing array, which is guaranteed to not be modified before {@link #end()}. */
	public T[] begin () {
		snapshot = items;
		return items;
	}

	/** Releases the guarantee that the array returned by {@link #begin()} won't be modified. */
	public void end () {
		if (snapshot == null) return;
		if (snapshot != items) {
			// The backing array was copied, keep around the old array.
			recycled = snapshot;
			for (int i = 0, n = recycled.length; i < n; i++)
				recycled[i] = null;
		}
		snapshot = null;
	}

	private void modified () {
		if (snapshot == null || snapshot != items) return;
		// Snapshot is in use, copy backing array to recycled array or create new backing array.
		if (recycled != null && recycled.length >= size) {
			System.arraycopy(items, 0, recycled, 0, size);
			items = recycled;
			recycled = null;
		} else
			resize(items.length);
	}

	public void set (int index, T value) {
		modified();
		super.set(index, value);
	}

	public void insert (int index, T value) {
		modified();
		super.insert(index, value);
	}

	public void swap (int first, int second) {
		modified();
		super.swap(first, second);
	}

	public boolean removeValue (T value, boolean identity) {
		modified();
		return super.removeValue(value, identity);
	}

	public T removeIndex (int index) {
		modified();
		return super.removeIndex(index);
	}

	public T pop () {
		modified();
		return super.pop();
	}

	public void clear () {
		modified();
		super.clear();
	}

	public void sort () {
		modified();
		super.sort();
	}

	public void sort (Comparator<T> comparator) {
		modified();
		super.sort(comparator);
	}

	public void reverse () {
		modified();
		super.reverse();
	}

	public void shuffle () {
		modified();
		super.shuffle();
	}

	public void truncate (int newSize) {
		modified();
		super.truncate(newSize);
	}
}