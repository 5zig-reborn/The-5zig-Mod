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

package eu.the5zig.util;

import java.util.*;

/**
 * Created by 5zig.
 * All rights reserved (C) 2015
 */
public class LinkedProperties extends Properties {


	private static final long serialVersionUID = 1L;

	private Map<Object, Object> linkMap = new LinkedHashMap<Object, Object>();

	@Override
	public synchronized Object put(Object key, Object value) {
		return linkMap.put(key, value);
	}

	@Override
	public synchronized boolean contains(Object value) {
		return linkMap.containsValue(value);
	}

	@Override
	public boolean containsValue(Object value) {
		return linkMap.containsValue(value);
	}

	@Override
	public synchronized Enumeration<Object> elements() {
		throw new UnsupportedOperationException("Enumerations are so old-school, don't use them, " + "use keySet() or entrySet() instead");
	}

	@Override
	public Set<Map.Entry<Object, Object>> entrySet() {
		return linkMap.entrySet();
	}

	@Override
	public synchronized void clear() {
		linkMap.clear();
	}

	@Override
	public synchronized boolean containsKey(Object key) {
		return linkMap.containsKey(key);
	}

}