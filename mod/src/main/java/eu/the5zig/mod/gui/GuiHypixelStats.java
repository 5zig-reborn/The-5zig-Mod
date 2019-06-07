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

package eu.the5zig.mod.gui;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.elements.*;
import eu.the5zig.mod.server.hypixel.HypixelGameType;
import eu.the5zig.mod.server.hypixel.api.*;
import eu.the5zig.mod.util.Keyboard;
import eu.the5zig.mod.util.NetworkPlayerInfo;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.*;

public class GuiHypixelStats extends Gui {

	private static HashMap<HypixelGameType, HypixelStatCategory> categories = Maps.newHashMap();

	private IGuiList<BasicRow> guiListGameTypes;

	private IGuiList guiListStat;
	private List<BasicRow> stats = Lists.newArrayList();
	private int selected;
	private HypixelAPIResponse hypixelStats;

	private String status;
	private List<String> statusSplit;

	private String player;

	private int tabIndex = 0;
	private String tabWord;

	public GuiHypixelStats(Gui lastScreen) {
		this(lastScreen, The5zigMod.getDataManager().getUsername());
	}

	public GuiHypixelStats(Gui lastScreen, String player) {
		super(lastScreen);

		this.player = player;

		categories.put(HypixelGameType.GENERAL, new HypixelStatGeneral());
		categories.put(HypixelGameType.QUAKECRAFT, new HypixelStatQuake());
		categories.put(HypixelGameType.WALLS, new HypixelStatWalls());
		categories.put(HypixelGameType.PAINTBALL, new HypixelStatPaintball());
		categories.put(HypixelGameType.SURVIVAL_GAMES, new HypixelStatSurvivalGames());
		categories.put(HypixelGameType.TNTGAMES, new HypixelStatTNTGames());
		categories.put(HypixelGameType.VAMPIREZ, new HypixelStatVampireZ());
		categories.put(HypixelGameType.WALLS3, new HypixelStatWalls3());
		categories.put(HypixelGameType.ARCADE, new HypixelStatArcade());
		categories.put(HypixelGameType.ARENA, new HypixelStatArena());
		categories.put(HypixelGameType.MCGO, new HypixelStatMCGO());
		categories.put(HypixelGameType.UHC, new HypixelStatUHC());
		categories.put(HypixelGameType.BATTLEGROUND, new HypixelStatBattleground());
		categories.put(HypixelGameType.TURBO_KART_RACERS, new HypixelStatTurboKartRacers());
		categories.put(HypixelGameType.SKYWARS, new HypixelStatSkyWars());
	}

	@Override
	public void initGui() {
		addTextField(The5zigMod.getVars().createTextfield(I18n.translate("server.hypixel.stats.player"), 1, getWidth() / 2 - 30, getHeight() / 6 - 4, 105, 16, 16));
		addButton(The5zigMod.getVars().createButton(100, getWidth() / 2 + 80, getHeight() / 6 - 6, 70, 20, I18n.translate("server.hypixel.stats.search")));
		getTextfieldById(1).callSetText(player);

		List<BasicRow> rows = Lists.newArrayList();
		for (HypixelGameType gameType : HypixelGameType.values()) {
			rows.add(new BasicRow(gameType.getName(), 100) {
				@Override
				public int getLineHeight() {
					return 18;
				}
			});
		}
		guiListGameTypes = The5zigMod.getVars().createGuiList(new Clickable<BasicRow>() {
			@Override
			public void onSelect(int id, BasicRow row, boolean doubleClick) {
				selected = id;
				guiListStat.scrollTo(0);
				updateSelected(hypixelStats == null ? null : hypixelStats);
			}
		}, getWidth(), getHeight(), getHeight() / 6 + 18, getHeight() - 48, getWidth() / 2 - 155, getWidth() / 2 - 45, rows);
		guiListGameTypes.setLeftbound(true);
		guiListGameTypes.setScrollX(getWidth() / 2 - 50);
		guiListGameTypes.setRowWidth(105);

		guiListStat = The5zigMod.getVars().createGuiList(null, getWidth(), getHeight(), getHeight() / 6 + 18, getHeight() - 48, getWidth() / 2 - 30, getWidth() / 2 + 155, stats);
		guiListStat.setLeftbound(true);
		guiListStat.setDrawSelection(false);
		guiListStat.setScrollX(getWidth() / 2 + 150);

		guiListGameTypes.setSelectedId(selected);
		if (status == null && stats.isEmpty()) {
			refresh(player);
		} else if (status != null) {
			updateStatus(status);
		}


		addBottomDoneButton();
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 100) {
			if (getTextfieldById(1).callGetText().isEmpty())
				return;
			refresh(player = getTextfieldById(1).callGetText());
		}
	}

	@Override
	protected void tick() {
		ITextfield textfield = getTextfieldById(1);
		if (textfield.callGetText().contains(" ")) {
			textfield.callSetText(textfield.callGetText().replace(" ", ""));
		}
	}

	@Override
	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (status == null) {
			guiListGameTypes.callDrawScreen(mouseX, mouseY, partialTicks);
			guiListStat.callDrawScreen(mouseX, mouseY, partialTicks);
			The5zigMod.getVars().drawString(ChatColor.UNDERLINE + I18n.translate("server.hypixel.stats.categories"), getWidth() / 2 - 155, getHeight() / 6 + 6);
		} else {
			int y = getHeight() / 6 + 30;
			for (String s : statusSplit) {
				drawCenteredString(s, getWidth() / 2, y);
				y += 12;
			}
		}
	}

	@Override
	protected void handleMouseInput() {
		if (status == null) {
			guiListGameTypes.callHandleMouseInput();
			guiListStat.callHandleMouseInput();
		}
	}

	@Override
	protected void onKeyType(char character, int key) {
		ITextfield textfield = getTextfieldById(1);
		if (textfield.callIsFocused() && !The5zigMod.getVars().isPlayerNull() && key == Keyboard.KEY_TAB) {
			if (tabWord == null) {
				tabWord = textfield.callGetText().toLowerCase();
			}
			List<NetworkPlayerInfo> tabPlayers = The5zigMod.getVars().getServerPlayers();
			Collections.sort(tabPlayers, new Comparator<NetworkPlayerInfo>() {
				@Override
				public int compare(NetworkPlayerInfo t0, NetworkPlayerInfo t1) {
					return ComparisonChain.start().compare(t0.getGameProfile().getName(), t1.getGameProfile().getName()).result();
				}
			});
			for (Iterator<NetworkPlayerInfo> it = tabPlayers.iterator(); it.hasNext(); ) {
				NetworkPlayerInfo next = it.next();
				if (next.getGameProfile().getName() == null || !next.getGameProfile().getName().toLowerCase().startsWith(tabWord)) {
					it.remove();
				}
			}
			if (!tabPlayers.isEmpty()) {
				tabIndex = tabIndex % tabPlayers.size();
				NetworkPlayerInfo playerInfo = tabPlayers.get(tabIndex);
				textfield.callSetText(playerInfo.getGameProfile().getName());
				tabIndex = (tabIndex + 1) % tabPlayers.size();
			}
		} else {
			tabIndex = 0;
			tabWord = null;
		}
	}

	@Override
	public String getTitleKey() {
		return "server.hypixel.stats.title";
	}

	private void refresh(String player) {
		stats.clear();
		if (status != null)
			updateStatus(I18n.translate("server.hypixel.loading"));
		this.player = player;
		try {
			The5zigMod.getHypixelAPIManager().get("player?name=" + player, new HypixelAPICallback() {
				@Override
				public void call(HypixelAPIResponse response) {
					hypixelStats = response;
					updateStatus(null);
					updateSelected(response);
				}

				@Override
				public void call(HypixelAPIResponseException e) {
					updateStatus(e.getErrorMessage());
				}
			});
		} catch (HypixelAPITooManyRequestsException e) {
			updateStatus(I18n.translate("server.hypixel.too_many_requests"));
		} catch (HypixelAPIMissingKeyException e) {
			updateStatus(I18n.translate("server.hypixel.no_key"));
		} catch (HypixelAPIException e) {
			updateStatus(e.getMessage());
		}
	}

	private void updateStatus(String status) {
		this.status = status;
		if (status == null) {
			statusSplit = null;
		} else {
			this.statusSplit = The5zigMod.getVars().splitStringToWidth(status, getWidth() - 50);
		}
	}

	private void updateSelected(HypixelAPIResponse response) {
		if (response == null)
			return;
		if (response.data() == null) {
			updateStatus(I18n.translate("server.hypixel.stats.player_not_found"));
			return;
		}

		BasicRow selectedRow = guiListGameTypes.getSelectedRow();
		final HypixelGameType gameType = HypixelGameType.fromName(selectedRow.getString());
		if (gameType == null || !categories.containsKey(gameType))
			return;
		if (response.data().get("player") == null || response.data().get("player").isJsonNull()) {
			updateStatus(I18n.translate("server.hypixel.stats.player_not_found"));
			return;
		}
		JsonObject root = response.data().get("player").getAsJsonObject();
		if (root.get("stats") == null || root.get("stats").isJsonNull()) {
			updateStatus(I18n.translate("server.hypixel.stats.stats_not_found"));
			return;
		}
		JsonElement element = gameType == HypixelGameType.GENERAL ? root : root.get("stats").getAsJsonObject().get(gameType.getDatabaseName());
		if (element == null)
			element = new JsonObject();

		final JsonElement finalElement = element;
		The5zigMod.getScheduler().postToMainThread(new Runnable() {
			@Override
			public void run() {
				stats.clear();
				stats.add(new BasicRow(ChatColor.UNDERLINE + I18n.translate("server.hypixel.stats.info", gameType.getName()), 175));
				for (String s : categories.get(gameType).getStats(finalElement.getAsJsonObject())) {
					stats.add(new BasicRow(s, 175));
				}
			}
		});
	}
}
