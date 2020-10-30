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

package eu.the5zig.mod.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.elements.BasicRow;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.elements.IGuiList;
import eu.the5zig.mod.gui.list.GuiArrayList;
import eu.the5zig.mod.server.hypixel.api.*;
import eu.the5zig.mod.util.JsonUtil;
import eu.the5zig.mod.util.MojangAPIManager;
import eu.the5zig.util.ExceptionCallback;
import eu.the5zig.util.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

public class GuiHypixelGuild extends Gui {

	private IGuiList guiList;
	private GuiArrayList<BasicRow> stats = new GuiArrayList<>();
	private String text = "";

	private String status;
	private List<String> statusSplit;

	public GuiHypixelGuild(Gui lastScreen) {
		super(lastScreen);
	}

	@Override
	public void initGui() {
		addTextField(The5zigMod.getVars().createTextfield(I18n.translate("server.hypixel.guild.guild"), 1, getWidth() / 2 - 100, getHeight() / 6 - 6, 80, 20));
		getTextfieldById(1).callSetText(text);
		addButton(The5zigMod.getVars().createButton(100, getWidth() / 2 - 15, getHeight() / 6 - 6, 55, 20, I18n.translate("server.hypixel.guild.search.by_name")));
		addButton(The5zigMod.getVars().createButton(101, getWidth() / 2 + 45, getHeight() / 6 - 6, 55, 20, I18n.translate("server.hypixel.guild.search.by_player")));


		if (status == null && stats.isEmpty())
			updateStatus(I18n.translate("server.hypixel.guild.help"));
		else if (stats.isEmpty())
			updateStatus(status);

		guiList = The5zigMod.getVars().createGuiList(null, getWidth(), getHeight(), getHeight() / 6 + 18, getHeight() - 36, getWidth() / 2 - 100, getWidth() / 2 + 100, stats);
		guiList.setScrollX(getWidth() / 2 + 95);
		guiList.setDrawSelection(false);
		guiList.setLeftbound(true);

		addBottomDoneButton();
	}

	@Override
	protected void tick() {
		text = getTextfieldById(1).callGetText();
	}

	@Override
	protected void actionPerformed(IButton button) {
		if ((button.getId() == 100 || button.getId() == 101) && !getTextfieldById(1).callGetText().isEmpty()) {
			findGuild(getTextfieldById(1).callGetText(), button.getId() == 101);
		}
	}

	@Override
	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (status == null) {
			guiList.callDrawScreen(mouseX, mouseY, partialTicks);
		} else {
			int y = getHeight() / 6 + 50;
			for (String s : statusSplit) {
				drawCenteredString(s, getWidth() / 2, y);
				y += 12;
			}
		}
	}

	@Override
	protected void handleMouseInput() {
		if (status == null) {
			guiList.callHandleMouseInput();
		}
	}

	private void findGuild(String name, boolean byPlayerName) {
		updateStatus(I18n.translate("server.hypixel.loading"));
		if (byPlayerName) {
			The5zigMod.getMojangAPIManager().resolveUUID(name, new ExceptionCallback<String>() {
				@Override
				public void call(String uuid, Throwable throwable) {
					findGuild("byUuid=" + uuid);
				}
			});
		} else {
			try {
				findGuild("byName=" + URLEncoder.encode(name, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				updateStatus(e.getMessage());
			}
		}
	}

	private void findGuild(String param) {
		makeRequest("findGuild?" + param, new HypixelAPICallback() {
			@Override
			public void call(HypixelAPIResponse findGuildResponse) {
				handleGuildResponse(findGuildResponse);
			}

			@Override
			public void call(HypixelAPIResponseException e) {
				updateStatus(e.getErrorMessage());
			}
		});
	}

	private void handleGuildResponse(HypixelAPIResponse findGuildResponse) {
		if (findGuildResponse.data().get("guild") == null || findGuildResponse.data().get("guild").isJsonNull()) {
			updateStatus(I18n.translate("server.hypixel.guild.not_found"));
			return;
		}
		makeRequest("guild?id=" + findGuildResponse.data().get("guild").getAsString(), new HypixelAPICallback() {
			@Override
			public void call(HypixelAPIResponse response) {
				updateStatus(null);
				stats.clear();

				JsonObject guild = response.data().get("guild").getAsJsonObject();
				LinkedHashMap<String, Object> l = Maps.newLinkedHashMap();
				l.put("name", JsonUtil.getString(guild, "name"));
				l.put("tag", JsonUtil.getString(guild, "tag"));
				l.put("created", Utils.convertToDate(JsonUtil.getLong(guild, "created"))
						.replace("Today", I18n.translate("profile.today").replace("Yesterday", I18n.translate("profile.yesterday"))));
				l.put("coins", JsonUtil.getInt(guild, "coins"));
				l.put("coins_ever", JsonUtil.getInt(guild, "coinsEver"));
				l.put("members", "(" + guild.get("members").getAsJsonArray().size() + "/75)");

				for (Map.Entry<String, Object> entry : l.entrySet()) {
					stats.add(new BasicRow(I18n.translate("server.hypixel.guild.info." + entry.getKey()) + ": " + entry.getValue(), 190));
				}

				List<String> namesToResolve = Lists.newArrayList();
				final HashMap<String, MemberRow> membersToResolve = Maps.newHashMap();
				List<MemberRow> members = Lists.newArrayList();
				for (JsonElement element : guild.get("members").getAsJsonArray()) {
					JsonObject member = element.getAsJsonObject();
					if (member.isJsonNull() || member.get("uuid") == null || member.get("uuid").isJsonNull())
						continue;

					String rank = "NONE";
					if (member.get("rank") != null && !member.get("rank").isJsonNull())
						rank = member.get("rank").getAsString();

					if (member.get("name") != null && !member.get("name").isJsonNull()) {
						members.add(new MemberRow(member.get("name").getAsString(), rank, 190));
					} else {
						String uuid = member.get("uuid").getAsString();
						MemberRow row = new MemberRow(uuid, rank, 190);
						members.add(row);
						membersToResolve.put(uuid, row);
						namesToResolve.add(uuid);
					}
				}
				Collections.sort(members, new Comparator<MemberRow>() {
					@Override
					public int compare(MemberRow o1, MemberRow o2) {
						if (o1.rank.equals("GUILDMASTER") && !o2.rank.equals("GUILDMASTER"))
							return -1;
						if (!o1.rank.equals("GUILDMASTER") && o2.rank.equals("GUILDMASTER"))
							return 1;
						if (o1.rank.equals("GUILDMASTER") && o2.rank.equals("GUILDMASTER"))
							return 0;

						if (o1.rank.equals("OFFICER") && !o2.rank.equals("OFFICER"))
							return -1;
						if (!o1.rank.equals("OFFICER") && o2.rank.equals("OFFICER"))
							return 1;
						if (o1.rank.equals("OFFICER") && o2.rank.equals("OFFICER"))
							return 0;

						if (o1.rank.equals("MEMBER") && !o2.rank.equals("MEMBER"))
							return -1;
						if (!o1.rank.equals("MEMBER") && o2.rank.equals("MEMBER"))
							return 1;
						if (o1.rank.equals("MEMBER") && o2.rank.equals("MEMBER"))
							return 0;

						return 0;
					}
				});
				stats.addAll(members);

				for (final String uuid : namesToResolve) {
					try {
						The5zigMod.getMojangAPIManager().resolveNameHistory(uuid, new ExceptionCallback<List<MojangAPIManager.NameHistory>>() {
							@Override
							public void call(List<MojangAPIManager.NameHistory> callback, Throwable throwable) {
								if (throwable != null) {
									updateStatus(throwable.getMessage());
									return;
								}
								membersToResolve.get(uuid).setName(callback.get(0).name);
							}
						});
					} catch (Exception e) {
						The5zigMod.logger.warn("Could not resolve UUID " + uuid + "!", e);
					}
				}
			}
		});
	}

	private void makeRequest(String endpoint, HypixelAPICallback callback) {
		try {
			The5zigMod.getHypixelAPIManager().get(endpoint, callback);
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

	@Override
	public String getTitleKey() {
		return "server.hypixel.guild.title";
	}

	public class MemberRow extends BasicRow {

		private String rank;

		public MemberRow(String string, String rank, int maxWidth) {
			super(string, maxWidth);
			this.rank = rank;
			setName(getName());
		}

		public String getName() {
			return string;
		}

		public void setName(String name) {
			this.string = "  - " + name + " (" + rank + ")";
		}
	}
}
