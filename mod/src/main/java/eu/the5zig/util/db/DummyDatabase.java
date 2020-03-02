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

package eu.the5zig.util.db;

import eu.the5zig.util.db.exceptions.NoConnectionException;

import java.sql.Connection;

public class DummyDatabase extends Database {

	public DummyDatabase() throws NoConnectionException {
		super(null);
	}

	@Override
	protected synchronized Connection openConnection() {
		connected = true;
		return null;
	}

	@Override
	public <T> SQLQuery<T> get(Class<T> entity) {
		return new DummySQLQuery<T>(this, entity);
	}

	@Override
	public int doUpdate(String query, Object... fields) {
		return 0;
	}

	@Override
	protected int doUpdateWithGeneratedKeys(String query, Object... fields) {
		return 0;
	}

	@Override
	public synchronized Connection getConnection() throws NoConnectionException {
		return null;
	}

	@Override
	public synchronized boolean hasConnection() {
		return true;
	}

	@Override
	public synchronized void closeConnection() {
	}
}
