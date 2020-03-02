/*
 * Copyright (c) 2019-2020 5zig Reborn
 * Copyright (c) 2015-2019 5zig
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

package eu.the5zig.mod.api;

import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class ServerAPIBackend {

	private String displayName = "Unknown Server";
	private final LinkedHashMap<String, String> stats = Maps.newLinkedHashMap();
	private HashMap<Integer, String> imageCache = Maps.newHashMap();
	private String base64;
	private String largeText;
	private long countdownTime;
	private String countdownName;
	private String lobby;

	public ServerAPIBackend() {
	}

	public void updateStat(String name, String score) {
		stats.put(name, score);
	}

	public void resetStat(String stat) {
		stats.remove(stat);
	}

	public void reset() {
		stats.clear();
		imageCache.clear();
		displayName = "Unknown Server";
		largeText = null;
		base64 = null;
		resetCountdown();
		lobby = null;
	}

	public Map<String, String> getStats() {
		return stats;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setImage(String base64, int id) {
		this.base64 = base64;
		imageCache.put(id, base64);
	}

	public void setImage(int id) {
		this.base64 = imageCache.get(id);
	}

	public void resetImage() {
		this.base64 = null;
	}

	public String getBase64() {
		return base64;
	}

	public void setLargeText(String largeText) {
		this.largeText = largeText;
	}

	public String getLargeText() {
		return largeText;
	}

	public void startCountdown(String name, long time) {
		countdownName = name;
		countdownTime = System.currentTimeMillis() + time;
	}

	public void resetCountdown() {
		countdownTime = -1;
		countdownName = null;
	}

	public long getCountdownTime() {
		if (countdownTime - System.currentTimeMillis() < 0)
			resetCountdown();
		return countdownTime;
	}

	public String getCountdownName() {
		return countdownName;
	}

	public String getLobby() {
		return lobby;
	}

	public void setLobby(String lobby) {
		this.lobby = lobby;
	}
}
