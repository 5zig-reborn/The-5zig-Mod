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

package eu.the5zig.mod.gui.ts;

import com.google.common.collect.Lists;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.Gui;
import eu.the5zig.mod.gui.elements.BasicRow;
import eu.the5zig.mod.gui.elements.Clickable;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.elements.IGuiList;
import eu.the5zig.teamspeak.TeamSpeak;
import eu.the5zig.teamspeak.api.Channel;
import eu.the5zig.teamspeak.api.ServerTab;

import java.util.List;

public abstract class GuiTeamSpeakSelectChannelOrder extends Gui {

	private final Channel parentChannel;

	private IGuiList<ChannelRow> guiList;

	public GuiTeamSpeakSelectChannelOrder(Gui lastScreen, Channel parentChannel) {
		super(lastScreen);
		this.parentChannel = parentChannel;
	}

	@Override
	public void initGui() {
		addBottomDoneButton();

		guiList = The5zigMod.getVars().createGuiList(new Clickable<ChannelRow>() {
			@Override
			public void onSelect(int id, ChannelRow row, boolean doubleClick) {
				if (doubleClick) {
					actionPerformed0(getButtonById(200));
				}
			}
		}, getWidth(), getHeight(), 48, getHeight() - 48, 0, getWidth(), Lists.<ChannelRow>newArrayList());
		guiList.setRowWidth(200);
		addGuiList(guiList);
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 200) {
			onSelect(guiList.getSelectedRow().channel);
		}
	}

	protected abstract void onSelect(Channel channel);

	@Override
	protected void tick() {
		List<? extends Channel> channels;
		ServerTab selectedTab = TeamSpeak.getClient().getSelectedTab();
		if (selectedTab == null) {
			The5zigMod.getVars().displayScreen(lastScreen);
			return;
		}
		if (parentChannel != null) {
			channels = parentChannel.getChildren();
		} else {
			channels = selectedTab.getChannels();
		}
		guiList.getRows().clear();
		String topName = parentChannel == null ? selectedTab.getServerInfo().getName() : parentChannel.getName();
		guiList.getRows().add(new ChannelRow(topName, parentChannel));
		for (Channel channel : channels) {
			guiList.getRows().add(new ChannelRow(channel.getName(), channel));
		}
	}

	@Override
	public String getTitleKey() {
		return "teamspeak.create_channel.select_order.title";
	}

	private class ChannelRow extends BasicRow {

		private Channel channel;

		public ChannelRow(String name, Channel channel) {
			super(name, 200);
			this.channel = channel;
		}
	}
}
