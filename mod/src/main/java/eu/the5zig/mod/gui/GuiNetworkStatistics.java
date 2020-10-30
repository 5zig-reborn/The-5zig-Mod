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
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.NetworkStats;
import eu.the5zig.mod.chat.network.packets.PacketServerStats;
import eu.the5zig.mod.gui.elements.BasicRow;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.elements.IGuiList;
import eu.the5zig.mod.gui.elements.Row;
import eu.the5zig.mod.gui.list.GuiArrayList;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.List;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class GuiNetworkStatistics extends Gui {

	private IGuiList statistics;
	private GuiArrayList<Row> rows = new GuiArrayList<>();
	private int ticks = 20 * 15;

	public GuiNetworkStatistics(Gui lastScreen) {
		super(lastScreen);
	}

	@Override
	public void initGui() {
		addButton(The5zigMod.getVars().createButton(200, getWidth() / 2 - 100, getHeight() - 35, The5zigMod.getVars().translate("gui.done")));

		rows.clear();
		List<String> lines = Lists.newArrayList();
		lines.add(ChatColor.UNDERLINE + I18n.translate("stats.current") + ":");
		final NetworkStats networkStats = The5zigMod.getDataManager().getNetworkStats();
		lines.add(I18n.translate("stats.packets_sent") + ": " + networkStats.getCurrentPacketsSent());
		lines.add(I18n.translate("stats.packets_received") + ": " + networkStats.getCurrentPacketsReceived());
		lines.add(I18n.translate("stats.bytes_sent") + ": " + networkStats.getCurrentBytesSent());
		lines.add(I18n.translate("stats.bytes_received") + ": " + networkStats.getCurrentBytesReceived());
		lines.add("");
		lines.add(ChatColor.UNDERLINE + I18n.translate("stats.total") + ":");
		lines.add(I18n.translate("stats.packets_sent") + ": " + networkStats.getTotalPacketsSent());
		lines.add(I18n.translate("stats.packets_received") + ": " + networkStats.getTotalPacketsReceived());
		lines.add(I18n.translate("stats.bytes_sent") + ": " + networkStats.getTotalBytesSent());
		lines.add(I18n.translate("stats.bytes_received") + ": " + networkStats.getTotalBytesReceived());
		lines.add("");
		lines.add(ChatColor.UNDERLINE + I18n.translate("stats.total_packets") + ": " + networkStats.getPacketsTotal());
		lines.add(ChatColor.UNDERLINE + I18n.translate("stats.total_bytes") + ": " + networkStats.getBytesTotal());
		lines.add(ChatColor.ITALIC + I18n.translate("stats.since") + ": " + networkStats.since());
		lines.add("");
		lines.add(ChatColor.UNDERLINE + I18n.translate("stats.server") + ":");
		for (final String line : lines) {
			rows.add(new BasicRow(line, 200, 14));
		}
		rows.add(new StatisticRow("stats.ping", 0, 0, networkStats.getPing()));
		rows.add(new StatisticRow("stats.up_time", 0, 0, networkStats.getServerUpTime()));
		rows.add(new StatisticRow("stats.lag", 0, 0, networkStats.getLag()));
		rows.add(new StatisticRow("stats.connected_players", 0, 0, networkStats.getConnectedClients(), networkStats.getMaxClients()));
		statistics = The5zigMod.getVars().createGuiList(null, getWidth(), getHeight(), 50, getHeight() - 50, 0, getWidth(), rows);
		statistics.setDrawSelection(false);
		statistics.setRowWidth(220);
		statistics.setScrollX(getWidth() / 2 + 120);
		addGuiList(statistics);
	}

	@Override
	protected void tick() {
		ticks++;
		if (ticks > 20 * 15) {
			The5zigMod.getNetworkManager().sendPacket(new PacketServerStats());
			ticks = 0;
		}
	}

	@Override
	public String getTitleKey() {
		return "stats.title";
	}

	@Override
	protected void actionPerformed(IButton button) {
	}
}
