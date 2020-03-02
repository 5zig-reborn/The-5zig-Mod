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

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import eu.the5zig.util.db.exceptions.NoConnectionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import eu.the5zig.util.Callback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class Database {

	private static final Logger LOGGER = LogManager.getLogger("5zig");

	protected final IDatabaseConfiguration databaseConfiguration;
	protected Connection connection;
	protected boolean connected = false;

	final ExecutorService EXECUTOR;

	public Database(IDatabaseConfiguration databaseConfiguration) throws NoConnectionException {
		this.databaseConfiguration = databaseConfiguration;
		this.connection = openConnection();
		EXECUTOR = Executors.newFixedThreadPool(databaseConfiguration == null ? 1 : databaseConfiguration.getThreadCount(),
				new ThreadFactoryBuilder().setNameFormat("Database Queue #%d").build());
	}

	public IDatabaseConfiguration getDatabaseConfiguration() {
		return databaseConfiguration;
	}

	/**
	 * Connects to a Database.
	 */
	protected synchronized Connection openConnection() throws NoConnectionException {
		try {
			try {
				if (connection != null) {
					closeConnection();
				}
			} catch (Exception ignored) {
			}
			getLogger().debug("Connecting to " + databaseConfiguration);
			Class.forName(databaseConfiguration.getDriver());
			Connection conn = databaseConfiguration.getConnection();
			getLogger().debug("Connected to Database!");
			connected = true;
			return this.connection = conn;
		} catch (Exception e) {
			throw new NoConnectionException("Could not connect to " + databaseConfiguration, e);
		}
	}

	public <T> SQLQuery<T> get(Class<T> entity) {
		return new SQLQuery<T>(this, entity);
	}

	/**
	 * Executes a MySQL Update with a PreparedStatement and given fields.
	 *
	 * @param query  The Prepared Statement Query. Use {@code ?} for fields.
	 * @param fields The Fields that should be inserted into the Statement.
	 */
	public void update(final String query, final Object... fields) {
		update(null, query, fields);
	}

	/**
	 * Executes a MySQL Update with a PreparedStatement and given fields.
	 *
	 * @param query  The Prepared Statement Query. Use {@code ?} for fields.
	 * @param fields The Fields that should be inserted into the Statement.
	 */
	public void update(final Callback<Integer> callback, final String query, final Object... fields) {
		EXECUTOR.execute(new Runnable() {
			@Override
			public void run() {
				int result = doUpdate(query, fields);
				if (callback != null) {
					callback.call(result);
				}
			}
		});
	}

	/**
	 * Executes a MySQL Update with a PreparedStatement and given fields.
	 *
	 * @param query  The Prepared Statement Query. Use {@code ?} for fields.
	 * @param fields The Fields that should be inserted into the Statement.
	 */
	public int doUpdate(String query, Object... fields) {
		Connection connection;
		try {
			connection = getConnection();
		} catch (NoConnectionException e) {
			getLogger().debug(e);
			return 0;
		}

		PreparedStatement st = null;
		try {
			st = connection.prepareStatement(query);
			for (int i = 0; i < fields.length; i++) {
				st.setObject(i + 1, fields[i]);
			}
			return st.executeUpdate();
		} catch (SQLException e) {
			getLogger().warn("Could not Execute MySQL Update " + query, e);
		} finally {
			closeResources(st);
		}
		return 0;
	}

	public void updateWithGeneratedKeys(String query, Object... fields) {
		updateWithGeneratedKeys(null, query, fields);
	}

	public void updateWithGeneratedKeys(final Callback<Integer> callback, final String query, final Object... fields) {
		EXECUTOR.execute(new Runnable() {
			@Override
			public void run() {
				int result = doUpdateWithGeneratedKeys(query, fields);
				if (callback != null) {
					callback.call(result);
				}
			}
		});
	}

	protected int doUpdateWithGeneratedKeys(String query, Object... fields) {
		Connection connection;
		try {
			connection = getConnection();
		} catch (NoConnectionException e) {
			getLogger().debug(e);
			return 1;
		}

		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
			for (int i = 0; i < fields.length; i++) {
				st.setObject(i + 1, fields[i]);
			}
			int affectedRows = st.executeUpdate();
			if (affectedRows == 0)
				throw new SQLException("No rows Affected after Insert Query!");
			rs = st.getGeneratedKeys();
			if (!rs.next())
				throw new SQLException("Inserted Id could not be fetched!");
			return rs.getInt(1);
		} catch (SQLException e) {
			getLogger().warn("Could not Execute MySQL Insert " + query, e);
			return 0;
		} finally {
			closeResources(rs, st);
		}
	}

	public void closeResources(ResultSet rs, PreparedStatement st) {
		closeResources(rs);
		closeResources(st);
	}

	public void closeResources(PreparedStatement st) {
		if (st == null)
			return;
		try {
			st.close();
		} catch (SQLException ignored) {
		}
	}

	public void closeResources(ResultSet rs) {
		if (rs == null)
			return;
		try {
			rs.close();
		} catch (SQLException ignored) {
		}
	}

	public synchronized Connection getConnection() throws NoConnectionException {
		if (!connected)
			throw new NoConnectionException("No SQL Connection available!");

		if (!hasConnection())
			return openConnection();

		return this.connection;
	}

	public synchronized boolean hasConnection() {
		try {
			return connected && this.connection != null && this.connection.isValid(10) && !this.connection.isClosed();
		} catch (Exception e) {
			return false;
		}
	}

	public synchronized void closeConnection() {
		if (!this.connected)
			return;
		try {
			this.connection.close();
		} catch (SQLException e) {
			getLogger().error("Could not close SQL Connection!", e);
		} finally {
			this.connection = null;
		}
	}

	public Logger getLogger() {
		return LOGGER;
	}

}
