/*
 * Original: Copyright (c) 2015-2019 5zig [MIT]
 * Current: Copyright (c) 2019 5zig Reborn [GPLv3+]
 *
 * This file is part of The 5zig Mod
 * The 5zig Mod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The 5zig Mod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with The 5zig Mod.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.the5zig.mod.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import eu.the5zig.util.Callback;
import eu.the5zig.mod.gui.elements.ITextfield;
import org.lwjgl.input.Keyboard;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public abstract class SearchEntry<T> {

	private final ITextfield textfield;
	private final List<T> entries;
	private Comparator<T> comparator;
	private Callback<T> enterCallback;
	private final List<T> entriesRemoved = Lists.newArrayList();
	private final HashMap<Object, Integer> entryIndexMap = Maps.newHashMap();
	private boolean visible = false;
	private long lastInteractTime;
	private boolean alwaysVisible = false;

	public SearchEntry(ITextfield textfield, List<T> list) {
		this(textfield, list, null);
	}

	public SearchEntry(ITextfield textfield, List<T> list, Comparator<T> comparator) {
		this(textfield, list, comparator, null);
	}

	public SearchEntry(ITextfield textfield, List<T> list, Comparator<T> comparator, Callback<T> enterCallback) {
		this.textfield = textfield;
		this.entries = list;
		this.comparator = comparator;
		this.enterCallback = enterCallback;
		if (comparator == null) {
			synchronized (entries) {
				for (T o : entries) {
					entryIndexMap.put(o, entries.indexOf(o));
				}
			}
		}
	}

	public void setComparator(Comparator<T> comparator) {
		this.comparator = comparator;
	}

	public void setEnterCallback(Callback<T> enterCallback) {
		this.enterCallback = enterCallback;
	}

	public boolean isAlwaysVisible() {
		return alwaysVisible;
	}

	public void setAlwaysVisible(boolean alwaysVisible) {
		this.alwaysVisible = alwaysVisible;
		if (alwaysVisible)
			visible = true;
	}

	@SuppressWarnings("unchecked")
	public void draw() {
		if (!visible)
			return;


		textfield.callDraw();
	}

	public void reset() {
		synchronized (entries) {
			entries.addAll(entriesRemoved);
		}
		textfield.callSetText("");
		sort();
		entriesRemoved.clear();
	}

	private void sort() {
		synchronized (entries) {
			if (comparator != null)
				Collections.sort(entries, comparator);
			else if (entries.size() == entryIndexMap.size()) {
				Collections.sort(entries, new Comparator<T>() {
					@Override
					public int compare(Object o1, Object o2) {
						Integer integer1 = entryIndexMap.get(o1);
						Integer integer2 = entryIndexMap.get(o2);
						if (integer1 == null && integer2 == null)
							return 0;
						if (integer1 == null)
							return 1;
						if (integer2 == null)
							return -1;
						return integer1.compareTo(integer2);
					}
				});
			}
		}
	}

	public boolean keyTyped(int key, int scanCode, int modifiers) {
		synchronized (entries) {
			if (key == Keyboard.KEY_RETURN && enterCallback != null && !entries.isEmpty() && !textfield.callGetText().isEmpty()) {
				enterCallback.call(entries.get(0));
			}
		}
		boolean result = getTextfield().keyPressed(key, scanCode, modifiers);

		update();

		return result;
	}

	public boolean keyTyped(char character, int code) {
		synchronized (entries) {
			if (code == Keyboard.KEY_RETURN && enterCallback != null && !entries.isEmpty() && !textfield.callGetText().isEmpty()) {
				enterCallback.call(entries.get(0));
			}
		}
		boolean result = getTextfield().callKeyTyped(character, code);

		update();

		return result;
	}

	private void update() {
		String text = textfield.callGetText();
		List<T> addList = Lists.newArrayList();
		List<T> removeList = Lists.newArrayList();
		synchronized (entries) {
			for (T o : entries) {
				if (!entriesRemoved.contains(o) && !filter(text, o)) {
					removeList.add(o);
				}
			}
		}
		for (T o : entriesRemoved) {
			synchronized (entries) {
				if (!entries.contains(o) && filter(text, o)) {
					addList.add(o);
				}
			}
		}
		entriesRemoved.addAll(removeList);
		entriesRemoved.removeAll(addList);
		synchronized (entries) {
			entries.removeAll(removeList);
			for (T o : addList) {
				int addIndex = getAddIndex();
				if (addIndex < 0) {
					entries.add(o);
				} else {
					while (addIndex > entries.size() - 1)
						addIndex--;
					entries.add(addIndex < 0 ? 0 : addIndex, o);
				}
			}
		}
		if (!addList.isEmpty()) {
			sort();
		}
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
		if (visible) {
			textfield.callSetFocused(true);
		} else {
			textfield.callSetFocused(false);
		}
	}

	public boolean isVisible() {
		return visible;
	}

	public void setLastInteractTime(long lastInteractTime) {
		this.lastInteractTime = lastInteractTime;
	}

	public long getLastInteractTime() {
		return lastInteractTime;
	}

	public ITextfield getTextfield() {
		return textfield;
	}

	public abstract boolean filter(String text, T o);

	protected int getAddIndex() {
		return -1;
	}
}
