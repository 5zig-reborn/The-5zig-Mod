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

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class FileDatabaseConfiguration implements IDatabaseConfiguration {

	private final String driver;
	private File file;
	private final String properties;

	public FileDatabaseConfiguration(File file, String... properties) {
		this.driver = "org.hsqldb.jdbc.JDBCDriver";
		this.file = file;
		StringBuilder stringBuilder = new StringBuilder();
		for (String property : properties) {
			stringBuilder.append(";").append(property);
		}
		this.properties = stringBuilder.toString();
	}

	@Override
	public String getDriver() {
		return driver;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public String getProperties() {
		return properties;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:hsqldb:file:" + getFile().getAbsolutePath() + getProperties(), "SA", "");
	}

	@Override
	public int getThreadCount() {
		return 1;
	}

	@Override
	public String toString() {
		return file.toString();
	}
}
