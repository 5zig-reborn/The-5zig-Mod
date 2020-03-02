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

package eu.the5zig.mod.chat.sql;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.ConversationManager;
import eu.the5zig.mod.chat.entity.Conversation;
import eu.the5zig.util.db.Database;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class DatabaseMigration {

	private final int CURRENT_VERSION = 3;

	private Database database;

	public DatabaseMigration(Database database) {
		this.database = database;
	}

	public void start() {
		init();

		int version = getVersion();
		if (version == CURRENT_VERSION)
			return;
		if (version > CURRENT_VERSION) {
			updateVersion();
			return;
		}
		The5zigMod.logger.info("Old Database Found! Migrating...");
		migrate(version);
	}

	private void init() {
		database.update("CREATE TABLE IF NOT EXISTS version (version INT)");
	}

	private int getVersion() {
		VersionEntity versionEntity = database.get(VersionEntity.class).query("SELECT * FROM version").unique();
		if (versionEntity == null) {
			database.update("INSERT INTO version (version) VALUES (?)", CURRENT_VERSION);
			return CURRENT_VERSION;
		}
		return versionEntity.getVersion();
	}

	private void updateVersion() {
		database.update("UPDATE version SET version=?", CURRENT_VERSION);
	}

	private void migrate(int dbVersion) {
		if (dbVersion == CURRENT_VERSION) {
			updateVersion();
			return;
		}
		if (dbVersion == 0) {
			database.update("ALTER TABLE " + ConversationManager.TABLE_CHAT + " ADD behaviour INT");
			database.update("UPDATE " + ConversationManager.TABLE_CHAT + " SET behaviour=?", Conversation.Behaviour.DEFAULT.ordinal());
			database.update("ALTER TABLE " + ConversationManager.TABLE_GROUP_CHAT + " ADD behaviour INT");
			database.update("UPDATE " + ConversationManager.TABLE_GROUP_CHAT + " SET behaviour=?", Conversation.Behaviour.DEFAULT.ordinal());
			database.update("ALTER TABLE " + ConversationManager.TABLE_ANNOUNCEMENTS + " ADD behaviour INT");
			database.update("UPDATE " + ConversationManager.TABLE_ANNOUNCEMENTS + " SET behaviour=?", Conversation.Behaviour.DEFAULT.ordinal());
		} else if (dbVersion == 1) {
			database.update("ALTER TABLE " + ConversationManager.TABLE_CHAT_MESSAGES + " ALTER COLUMN message VARCHAR(512)");
			database.update("ALTER TABLE " + ConversationManager.TABLE_GROUP_CHAT_MESSAGES + " ALTER COLUMN message VARCHAR(512)");
			database.update("ALTER TABLE " + ConversationManager.TABLE_ANNOUNCEMENTS_MESSAGES + " ALTER COLUMN message VARCHAR(512)");
		} else if (dbVersion == 2) {
		}

		The5zigMod.logger.info("Migrating Database from version {} to version {}...", dbVersion, dbVersion + 1);
		migrate(++dbVersion);
	}
}
