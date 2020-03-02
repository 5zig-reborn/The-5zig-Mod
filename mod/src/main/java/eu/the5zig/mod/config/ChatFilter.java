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

package eu.the5zig.mod.config;

import com.google.common.collect.Lists;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.Gui;
import eu.the5zig.mod.gui.elements.Row;
import eu.the5zig.util.Utils;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class ChatFilter {

	private List<ChatFilterMessage> chatMessages = Lists.newArrayList();

	public ChatFilter() {
	}

	public List<ChatFilterMessage> getChatMessages() {
		return chatMessages;
	}

	public class ChatFilterMessage implements Row {

		private transient Pattern pattern;
		private transient String[] exceptArray;
		private transient List<Pattern> serverPatterns;

		private String name;
		private String message;
		private String except;
		private List<String> servers = Lists.newArrayList();
		private Action action = Action.IGNORE;
		private boolean useRegex = false;
		private String autoText;
		private String autoTextCancel;
		private int autoTextDelay;

		public ChatFilterMessage() {
		}

		public ChatFilterMessage(String message, Action action, String... servers) {
			this(null, message, null, action, false, servers);
		}

		public ChatFilterMessage(String name, String message, String except, Action action, boolean useRegex, String... servers) {
			this.name = name;
			this.message = message;
			this.except = except;
			this.action = action;
			this.useRegex = useRegex;
			Collections.addAll(this.servers, servers);
			updatePatterns();
		}

		public Pattern getPattern() {
			return pattern;
		}

		public String[] getExceptArray() {
			return exceptArray;
		}

		public List<Pattern> getServerPatterns() {
			return serverPatterns;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;

			try {
				if (useRegex()) {
					pattern = Pattern.compile(message, Pattern.CASE_INSENSITIVE);
				} else {
					pattern = Utils.compileMatchPattern(message);
				}
			} catch (Exception e) {
				The5zigMod.logger.error("Could not compile pattern: " + message + "!", e);
			}
		}

		public String getExcept() {
			return except;
		}

		public void setExcept(String except) {
			this.except = except;
			if (except == null || except.isEmpty()) {
				exceptArray = null;
			} else {
				exceptArray = except.replace(", ", ",").split(",");
			}
		}

		public void clearServers() {
			servers.clear();
		}

		public void addServer(String server) {
			servers.add(server);
			try {
				serverPatterns.add(Utils.compileMatchPattern(server));
			} catch (Exception e) {
				The5zigMod.logger.error("Could not compile pattern: " + server + "!", e);
			}
		}

		public String[] getServers() {
			return servers.toArray(new String[servers.size()]);
		}

		public Action getAction() {
			return action;
		}

		public void setAction(Action action) {
			this.action = action;
		}

		public boolean useRegex() {
			return useRegex;
		}

		public void setUseRegex(boolean useRegex) {
			this.useRegex = useRegex;
		}

		public void updatePatterns() {
			serverPatterns = Lists.newArrayList();
			List<String> servers = Lists.newArrayList(this.servers);
			clearServers();
			for (String server : servers) {
				addServer(server);
			}
			setMessage(message);
			setExcept(except);
		}

		public String getAutoText() {
			return autoText;
		}

		public void setAutoText(String autoText) {
			this.autoText = autoText;
		}

		public String getAutoTextCancel() {
			return autoTextCancel;
		}

		public void setAutoTextCancel(String autoTextCancel) {
			this.autoTextCancel = autoTextCancel;
		}

		public int getAutoTextDelay() {
			return autoTextDelay;
		}

		public void setAutoTextDelay(int autoTextDelay) {
			this.autoTextDelay = autoTextDelay;
		}

		@Override
		public ChatFilterMessage clone() {
			ChatFilterMessage clone = new ChatFilterMessage();
			clone.pattern = pattern;
			clone.exceptArray = exceptArray;
			clone.serverPatterns = serverPatterns;
			clone.name = name;
			clone.message = message;
			clone.except = except;
			clone.servers = servers;
			clone.action = action;
			clone.useRegex = useRegex;
			clone.autoText = autoText;
			clone.autoTextDelay = autoTextDelay;
			return clone;
		}

		@Override
		public int getLineHeight() {
			return 16;
		}

		@Override
		public void draw(int x, int y) {
			Gui gui = The5zigMod.getVars().getCurrentScreen();

			int chatMessageX = x + 2;
			int maxChatMessageX = gui.getWidth() / 2 + 50;
			int serverX = maxChatMessageX + 20;

			The5zigMod.getVars().drawString(The5zigMod.getVars().shortenToWidth(name != null ? name : message, Math.max(1, maxChatMessageX - chatMessageX)), chatMessageX, y + 2);
			The5zigMod.getVars().drawString("|", maxChatMessageX + 10, y + 2);
			The5zigMod.getVars().drawString(action.getName(), serverX, y + 2);
		}
	}

	public enum Action {

		IGNORE("chat_filter.edit.action.ignore"), SECOND_CHAT("chat_filter.edit.action.2nd_chat"), COPY_SECOND_CHAT("chat_filter.edit.action.copy_2nd_chat"),
		NOTIFY("chat_filter.edit.action.notify"), AUTO_TEXT("chat_filter.edit.action.auto_text"), PLAY_SOUND("chat_filter.edit.action.play_sound");

		private String key;

		Action(String key) {
			this.key = key;
		}

		public String getName() {
			return I18n.translate(key);
		}

		public Action getNext() {
			return values()[(ordinal() + 1) % values().length];
		}
	}

}
