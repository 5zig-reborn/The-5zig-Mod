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

package eu.the5zig.mod.manager;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.config.ChatFilter;
import eu.the5zig.mod.event.ChatEvent;
import eu.the5zig.mod.event.EventHandler;
import eu.the5zig.mod.event.TickEvent;
import eu.the5zig.mod.server.GameServer;
import eu.the5zig.mod.server.Server;
import eu.the5zig.mod.util.Display;
import eu.the5zig.util.Utils;
import eu.the5zig.util.minecraft.ChatColor;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class ChatFilterManager {

	private static final Pattern UNESCAPED_QUOTATION_MARK_PATTERN = Pattern.compile("(?<!\\\\)(?:\\\\{2})*\"");

	private final List<DelayedAutoText> delayedAutoTexts = Lists.newArrayList();
	public boolean enabled = true;

	@EventHandler
	public void onTick(TickEvent event) {
		for (Iterator<DelayedAutoText> it = delayedAutoTexts.iterator(); it.hasNext(); ) {
			DelayedAutoText delayedAutoText = it.next();
			delayedAutoText.currentTick++;
			if (delayedAutoText.currentTick >= delayedAutoText.ticksDelayed) {
				The5zigMod.getListener().onSendChatMessage(delayedAutoText.autoText);
				it.remove();
			}
		}

		if (The5zigMod.getKeybindingManager().toggleChatFilter.callIsPressed()) {
			enabled = !enabled;
			if (enabled) {
				The5zigMod.getVars().messagePlayer(ChatColor.GREEN + I18n.translate("chat_filter.message.enabled"));
			} else {
				The5zigMod.getVars().messagePlayer(ChatColor.RED + I18n.translate("chat_filter.message.disabled",
						The5zigMod.getVars().getKeyDisplayStringShort(The5zigMod.getKeybindingManager().toggleChatFilter.callGetKeyCode())));
				delayedAutoTexts.clear();
			}
		}
	}

	@EventHandler
	public void onServerChat(ChatEvent event) {
		String message = event.getMessage();
		Object chatComponent = event.getChatComponent();
		message = ChatColor.stripColor(message);
		Server currentServer = The5zigMod.getDataManager().getServer();

		for (Iterator<DelayedAutoText> it = delayedAutoTexts.iterator(); it.hasNext(); ) {
			DelayedAutoText delayedAutoText = it.next();
			if (delayedAutoText.cancelPattern == null) {
				continue;
			}
			if (delayedAutoText.cancelPattern.matcher(message).matches()) {
				it.remove();
			}
		}

		if (!enabled) {
			return;
		}

		boolean alreadyPrintedIn2ndChat = false;
		for (ChatFilter.ChatFilterMessage chatMessage : The5zigMod.getChatFilterConfig().getConfigInstance().getChatMessages()) {
			List<Pattern> servers = chatMessage.getServerPatterns();
			if (servers.isEmpty()) {
				if (tryIgnoreMessage(chatMessage, message, chatComponent, alreadyPrintedIn2ndChat)) {
					if (chatMessage.getAction() == ChatFilter.Action.SECOND_CHAT || chatMessage.getAction() == ChatFilter.Action.COPY_SECOND_CHAT) {
						alreadyPrintedIn2ndChat = true;
					}
					event.setCancelled(true);
				}
			} else if (currentServer != null) {
				for (Pattern server : servers) {
					if (!server.matcher(currentServer.getHost()).matches()) {
						continue;
					}
					if (tryIgnoreMessage(chatMessage, message, chatComponent, alreadyPrintedIn2ndChat)) {
						if (chatMessage.getAction() == ChatFilter.Action.SECOND_CHAT || chatMessage.getAction() == ChatFilter.Action.COPY_SECOND_CHAT) {
							alreadyPrintedIn2ndChat = true;
						}
						event.setCancelled(true);
					}
				}
			}
		}
	}

	private boolean tryIgnoreMessage(ChatFilter.ChatFilterMessage chatMessage, String message, Object chatComponent, boolean alreadyPrintedIn2ndChat) {
		Matcher matcher = chatMessage.getPattern().matcher(message);
		if (!matcher.matches()) {
			return false;
		}
		if (chatMessage.getExceptArray() != null) {
			for (String s : chatMessage.getExceptArray()) {
				if (message.contains(s)) {
					return false;
				}
			}
		}

		if (chatMessage.getAction() == ChatFilter.Action.IGNORE) {
			The5zigMod.logger.debug("Ignored Chat Message {}!", message);
			return true;
		} else if (chatMessage.getAction() == ChatFilter.Action.SECOND_CHAT || chatMessage.getAction() == ChatFilter.Action.COPY_SECOND_CHAT) {
			if (!The5zigMod.getConfig().getBool("2ndChatVisible"))
				return false;
			if (!alreadyPrintedIn2ndChat) {
				The5zigMod.getVars().get2ndChat().printChatMessage(chatComponent);
			}
			return chatMessage.getAction() != ChatFilter.Action.COPY_SECOND_CHAT;
		} else if (chatMessage.getAction() == ChatFilter.Action.NOTIFY) {
			if (!Display.isActive()) {
				The5zigMod.getTrayManager().displayMessage("The 5zig Mod - " + I18n.translate("ingame_chat.new_message"), message);
			}
			return false;
		} else if (chatMessage.getAction() == ChatFilter.Action.AUTO_TEXT && !Strings.isNullOrEmpty(chatMessage.getAutoText())
				&& !The5zigMod.getDataManager().getAfkManager().isAfk()) {
			String autoText = chatMessage.getAutoText();
			for (int i = 1; i <= matcher.groupCount(); i++) {
				String group = matcher.group(i);
				autoText = autoText.replace("{" + i + "}", group == null ? "" : group);
			}
			autoText = replacePlaceholders(autoText);
			if (chatMessage.getAutoTextDelay() > 0) {
				String autoTextCancelMessage = chatMessage.getAutoTextCancel();
				if (autoTextCancelMessage != null) {
					for (int i = 1; i <= matcher.groupCount(); i++) {
						String group = matcher.group(i);
						autoTextCancelMessage = autoTextCancelMessage.replace("{" + i + "}", group == null ? "" : group);
					}
					autoTextCancelMessage = replacePlaceholders(autoTextCancelMessage);
				}
				Pattern cancelPattern;
				if (autoTextCancelMessage != null) {
					if (chatMessage.useRegex()) {
						cancelPattern = Pattern.compile(autoTextCancelMessage, Pattern.CASE_INSENSITIVE);
					} else {
						cancelPattern = Utils.compileMatchPattern(autoTextCancelMessage);
					}
				} else {
					cancelPattern = null;
				}
				delayedAutoTexts.add(new DelayedAutoText(chatMessage.getAutoTextDelay(), cancelPattern, autoText));
			} else {
				The5zigMod.getListener().onSendChatMessage(autoText);
			}
			return false;
		} else if (chatMessage.getAction() == ChatFilter.Action.PLAY_SOUND) {
			The5zigMod.getVars().playSound("the5zigmod", "chat_filter.sound", 1);
			return false;
		}
		return false;
	}

	public static String replacePlaceholders(String autoText) {
		if(autoText.contains("${username}")) {
			autoText = autoText.replace("${username}", The5zigMod.getDataManager().getGameProfile().getName());
		}
		if(autoText.contains("${uuid}")) {
			autoText = autoText.replace("${uuid}", The5zigMod.getDataManager().getGameProfile().getId().toString());
		}
		if(autoText.contains("${uuid-stripped}")) {
			autoText = autoText.replace("${uuid-stripped}", The5zigMod.getDataManager().getGameProfile().getId().toString().replace("-", ""));
		}
		if (autoText.contains("${time-min}")) {
			autoText = autoText.replace("${time-min}", Utils.convertToTimeWithMinutes(System.currentTimeMillis()));
		}
		if (autoText.contains("${time-sec}")) {
			autoText = autoText.replace("${time-sec}", Utils.convertToTimeWithSeconds(System.currentTimeMillis()));
		}
		Server server = The5zigMod.getDataManager().getServer();
		if (server != null) {
			if (autoText.contains("${server-ip}")) {
				autoText = autoText.replace("${server-ip}", server.getHost());
			}
			if (autoText.contains("${server-port}")) {
				autoText = autoText.replace("${server-port}", String.valueOf(server.getPort()));
			}
			if (server instanceof GameServer) {
				GameServer gameServer = (GameServer) server;
				if (gameServer.getLobby() != null && autoText.contains("${server-lobby}")) {
					autoText = autoText.replace("${server-lobby}", gameServer.getLobby());
				}
				if (gameServer.getGameMode() != null && autoText.contains("${server-gamemode}")) {
					autoText = autoText.replace("${server-gamemode}", gameServer.getGameMode().getName());
				}
			}
		}
		if (autoText.contains("${coords}")) {
			autoText = autoText.replace("${coords}", shorten(The5zigMod.getVars().getPlayerPosX()) + ", " + shorten(The5zigMod.getVars().getPlayerPosY()) + ", "
					+ shorten(The5zigMod.getVars().getPlayerPosZ()));
		}
		if (autoText.contains("${coordX}")) {
			autoText = autoText.replace("${coordX}", shorten(The5zigMod.getVars().getPlayerPosX()));
		}
		if (autoText.contains("${coordY}")) {
			autoText = autoText.replace("${coordY}", shorten(The5zigMod.getVars().getPlayerPosY()));
		}
		if (autoText.contains("${coordZ}")) {
			autoText = autoText.replace("${coordZ}", shorten(The5zigMod.getVars().getPlayerPosZ()));
		}
		if (autoText.contains("${clipboard}")) {
			try {
				autoText = autoText.replace("${clipboard}", (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor));
			} catch (Exception e) {
				The5zigMod.logger.warn("Failed to paste clipboard", e);
			}
		}

		int randomIndexStart;
		int randomIndexEnd = 0;
		while ((randomIndexStart = autoText.indexOf("${random[", randomIndexEnd)) != -1) {
			int index = randomIndexStart;
			while ((index = autoText.indexOf("]}", index)) != -1) {
				index += 2;
				String substring = autoText.substring(randomIndexStart, index);
				String replaced = UNESCAPED_QUOTATION_MARK_PATTERN.matcher(substring).replaceAll("");
				if ((substring.length() - replaced.length()) % 2 == 0) {
					String json = substring.substring("${random".length(), substring.length() - "}".length());

					try {
						JsonElement element = new JsonParser().parse(json);
						if (element.isJsonArray()) {
							List<String> randomWords = Lists.newArrayList();
							JsonArray array = element.getAsJsonArray();
							for (JsonElement arrayElement : array) {
								randomWords.add(arrayElement.getAsString());
							}
							autoText = autoText.replace(substring, randomWords.get(The5zigMod.random.nextInt(randomWords.size())));
						}
					} catch (Exception e) {
						The5zigMod.logger.warn("Could not parse json of random selector: \"" + json + "\"", e);
					}

					randomIndexEnd = index;
					break;
				}
			}
			randomIndexEnd++;
		}

		return autoText;
	}

	private static String shorten(double d) {
		return Utils.getShortenedDouble(d, The5zigMod.getConfig().getInt("numberPrecision"));
	}

	private class DelayedAutoText {

		private final int ticksDelayed;
		private int currentTick;
		private final Pattern cancelPattern;
		private final String autoText;

		public DelayedAutoText(int ticksDelayed, Pattern cancelPattern, String autoText) {
			this.ticksDelayed = ticksDelayed;
			this.cancelPattern = cancelPattern;
			this.autoText = autoText;
		}

	}

}
