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

import eu.the5zig.teamspeak.api.*;

import java.util.List;

public class DummyServerTab implements ServerTab {

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public void setSelected() {
	}

	@Override
	public ServerInfo getServerInfo() {
		return null;
	}

	@Override
	public List<? extends Channel> getChannels() {
		return null;
	}

	@Override
	public void createChannel(String name, String password, String topic, String description, ChannelLifespan lifespan, boolean defaultChannel, Channel parentChannel, Channel orderChannel,
			boolean bottomPosition, int neededTalkPower, ChannelCodec codec, int codecQuality, int maxClients) {
	}

	@Override
	public void updateChannelProperties(Channel channel, String name, String password, String topic, String description, ChannelLifespan lifespan, boolean defaultChannel,
			Channel parentChannel, Channel orderChannel, boolean bottomPosition, int neededTalkPower, ChannelCodec codec, int codecQuality, int maxClients) {
	}

	@Override
	public void deleteChannel(Channel channel, boolean force) {
	}

	@Override
	public OwnClient getSelf() {
		return null;
	}

	@Override
	public List<? extends Group> getServerGroups() {
		return null;
	}

	@Override
	public Group getServerGroup(int id) {
		return null;
	}

	@Override
	public Group getDefaultServerGroup() {
		return null;
	}

	@Override
	public List<? extends Group> getChannelGroups() {
		return null;
	}

	@Override
	public Group getChannelGroup(int id) {
		return null;
	}

	@Override
	public Group getDefaultChannelGroup() {
		return null;
	}

	@Override
	public Chat getServerChat() {
		return null;
	}

	@Override
	public Chat getChannelChat() {
		return null;
	}

	@Override
	public Chat getPokeChat() {
		return null;
	}

	@Override
	public void resetPokeChat() {
	}

	@Override
	public List<? extends PrivateChat> getPrivateChats() {
		return null;
	}

	@Override
	public PrivateChat getPrivateChat(Client to) {
		return null;
	}

	@Override
	public void removePrivateChat(PrivateChat privateChat) {
	}
}
