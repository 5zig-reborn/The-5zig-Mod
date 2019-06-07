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
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.event.EventHandler;
import eu.the5zig.mod.event.TickEvent;
import eu.the5zig.mod.util.Keyboard;

import java.util.Collections;
import java.util.List;

public class SearchManager {

	private List<SearchEntry> entries = Lists.newArrayList();

	public SearchManager() {
		The5zigMod.getListener().registerListener(this);
	}

	public void onGuiClose() {
		Keyboard.enableRepeatEvents(false);
	}

	public void addSearch(SearchEntry searchEntry, SearchEntry... searchEntries) {
		for (SearchEntry entry : entries) {
			entry.reset();
		}
		entries.clear();
		Keyboard.enableRepeatEvents(true);
		entries.add(searchEntry);
		Collections.addAll(entries, searchEntries);
	}

	public void draw() {
		for (SearchEntry entry : entries) {
			entry.draw();
		}
	}

	public void keyTyped(char character, int code) {
		boolean atLeastOneFocused = false;
		for (SearchEntry entry : entries) {
			if (entry.getTextfield().callIsFocused()) {
				atLeastOneFocused = true;
				break;
			}
		}
		for (SearchEntry entry : entries) {
			if ((!entry.isVisible() || !entry.getTextfield().callIsFocused()) && !atLeastOneFocused && !entry.isAlwaysVisible()) {
				entry.setVisible(true);
				// only set visible if key has been successfully typed
				if (!entry.keyTyped(character, code))
					entry.setVisible(false);
				else
					entry.setLastInteractTime(System.currentTimeMillis());
			} else if (entry.isVisible()) {
				if (entry.keyTyped(character, code))
					entry.setLastInteractTime(System.currentTimeMillis());
			}
		}
	}

	public void keyTyped(int key, int scanCode, int modifiers) {
		boolean atLeastOneFocused = false;
		for (SearchEntry entry : entries) {
			if (entry.getTextfield().callIsFocused()) {
				atLeastOneFocused = true;
				break;
			}
		}
		for (SearchEntry entry : entries) {
			if ((!entry.isVisible() || !entry.getTextfield().callIsFocused()) && !atLeastOneFocused && !entry.isAlwaysVisible()) {
				entry.setVisible(true);
				// only set visible if key has been successfully typed
				if (!entry.keyTyped(key, scanCode, modifiers))
					entry.setVisible(false);
				else
					entry.setLastInteractTime(System.currentTimeMillis());
			} else if (entry.isVisible()) {
				if (entry.keyTyped(key, scanCode, modifiers))
					entry.setLastInteractTime(System.currentTimeMillis());
			}
		}
	}

	public void mouseClicked(int mouseX, int mouseY, int button) {
		for (SearchEntry entry : entries) {
			entry.getTextfield().callMouseClicked(mouseX, mouseY, button);
		}
	}

	@EventHandler
	public void onTick(TickEvent event) {
		for (SearchEntry entry : entries) {
			if (!entry.getTextfield().callIsFocused() && entry.getTextfield().callGetText().isEmpty() && System.currentTimeMillis() - entry.getLastInteractTime() > 1000 * 5 &&
					!entry.isAlwaysVisible()) {
				entry.setVisible(false);
			}
		}
	}
}
