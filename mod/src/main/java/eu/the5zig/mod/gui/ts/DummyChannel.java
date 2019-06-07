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

package eu.the5zig.mod.gui.ts;

import eu.the5zig.teamspeak.api.*;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;

public class DummyChannel implements Channel {

	private String name;

	public DummyChannel(String name) {
		this.name = name;
	}

	@Override
	public ServerTab getServerTab() {
		return null;
	}

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getFormattedName() {
		return name;
	}

	@Override
	public ChannelType getType() {
		return ChannelType.NORMAL;
	}

	@Override
	public BufferedImage getIcon() {
		return null;
	}

	@Override
	public int getIconId() {
		return 0;
	}

	@Override
	public Channel getParent() {
		return null;
	}

	@Override
	public Channel getAbove() {
		return null;
	}

	@Override
	public List<? extends Channel> getChildren() {
		return null;
	}

	@Override
	public List<? extends Client> getClients() {
		return Collections.emptyList();
	}

	@Override
	public Client getClient(int id) {
		return null;
	}

	@Override
	public String getTopic() {
		return null;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public boolean hasSubscribed() {
		return true;
	}

	@Override
	public boolean isDefault() {
		return false;
	}

	@Override
	public boolean requiresPassword() {
		return false;
	}

	@Override
	public boolean isPermanent() {
		return false;
	}

	@Override
	public boolean isSemiPermanent() {
		return false;
	}

	@Override
	public ChannelCodec getCodec() {
		return null;
	}

	@Override
	public int getCodecQuality() {
		return 0;
	}

	@Override
	public int getNeededTalkPower() {
		return 0;
	}

	@Override
	public int getMaxClients() {
		return 1;
	}

	@Override
	public int getMaxFamilyClients() {
		return 0;
	}

	@Override
	public void moveBelow(Channel channel) {

	}

	@Override
	public void moveInside(Channel channel) {

	}

	@Override
	public void moveInside(Channel channel, Channel above) {

	}
}
