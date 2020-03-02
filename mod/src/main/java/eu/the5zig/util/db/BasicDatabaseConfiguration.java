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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class BasicDatabaseConfiguration implements IDatabaseConfiguration {

	private final String driver;
	private final String host;
	private final int port;
	private final String user;
	private final String pass;
	private final String database;

	public BasicDatabaseConfiguration(String host, int port, String user, String pass, String database) {
		this("com.mysql.jdbc.Driver", host, port, user, pass, database);
	}

	public BasicDatabaseConfiguration(String driver, String host, int port, String user, String pass, String database) {
		this.driver = driver;
		this.host = host;
		this.port = port;
		this.user = user;
		this.pass = pass;
		this.database = database;
	}

	@Override
	public String getDriver() {
		return driver;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getUser() {
		return user;
	}

	public String getPass() {
		return pass;
	}

	public String getDatabase() {
		return database;
	}

	public String getURL() {
		return String.format("jdbc:mysql://%s:%s/%s", getHost(), getPort(), getDatabase());
	}

	@Override
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(getURL(), getUser(), getPass());
	}

	@Override
	public int getThreadCount() {
		return 4;
	}

	@Override
	public String toString() {
		return host + ":" + port;
	}
}
