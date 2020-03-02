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

import eu.the5zig.mod.gui.Gui;
import eu.the5zig.teamspeak.TeamSpeak;
import eu.the5zig.teamspeak.api.Channel;
import eu.the5zig.teamspeak.api.ChannelLifespan;
import eu.the5zig.teamspeak.api.ServerTab;

public class GuiTeamSpeakEditChannel extends GuiTeamSpeakCreateChannel {

	private final Channel channel;

	public GuiTeamSpeakEditChannel(Gui lastScreen, Channel channel) {
		super(lastScreen, channel.getParent());
		this.channel = channel;
		this.channelName = channel.getName();
		this.channelTopic = channel.getTopic();
		this.channelDescription = channel.getDescription();
		if (channel.isPermanent()) {
			channelLifespanIndex = ChannelLifespan.PERMANENT.ordinal();
		} else if (channel.isSemiPermanent()) {
			channelLifespanIndex = ChannelLifespan.SEMI_PERMANENT.ordinal();
		} else {
			channelLifespanIndex = ChannelLifespan.TEMPORARY.ordinal();
		}
		this.defaultChannelBool = channel.isDefault();
		this.currentCodecIndex = channel.getCodec().ordinal();
		this.codecQuality = channel.getCodecQuality();
		this.neededTalkPower = channel.getNeededTalkPower();
		this.maxClients = channel.getMaxClients();
		this.orderChannel = channel.getAbove();
	}

	@Override
	protected void onDone(ChannelResult result) {
		ServerTab selectedTab = TeamSpeak.getClient().getSelectedTab();
		if (selectedTab == null) {
			return;
		}
		selectedTab.updateChannelProperties(channel, result.name, result.password, result.topic, result.description, result.lifespan, result.defaultChannel, result.parentChannel,
				result.orderChannel, result.bottomPosition, result.neededTalkPower, result.codec, result.codecQuality, result.maxClients);
	}
}
