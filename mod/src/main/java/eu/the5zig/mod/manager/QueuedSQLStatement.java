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

import eu.the5zig.util.Callback;
import eu.the5zig.util.db.Database;

public class QueuedSQLStatement {

	private Callback<Integer> callback;
	private boolean sync;
	private String query;
	private boolean returnGeneratedKeys;
	private Object[] parameters;

	private Callback<Database> databaseCallback;

	public QueuedSQLStatement(Callback<Integer> callback, boolean sync, boolean returnGeneratedKeys, String query, Object... parameters) {
		this.callback = callback;
		this.sync = sync;
		this.query = query;
		this.returnGeneratedKeys = returnGeneratedKeys;
		this.parameters = parameters;
	}

	public QueuedSQLStatement(Callback<Database> databaseCallback) {
		this.databaseCallback = databaseCallback;
	}

	public String getQuery() {
		return query;
	}

	public boolean isReturnGeneratedKeys() {
		return returnGeneratedKeys;
	}

	public boolean isSync() {
		return sync;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public Callback<Integer> getCallback() {
		return callback;
	}

	public Callback<Database> getDatabaseCallback() {
		return databaseCallback;
	}
}
