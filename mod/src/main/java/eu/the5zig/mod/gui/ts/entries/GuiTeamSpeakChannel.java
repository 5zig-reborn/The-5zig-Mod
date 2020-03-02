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

package eu.the5zig.mod.gui.ts.entries;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.CenteredTextfieldCallback;
import eu.the5zig.mod.gui.GuiCenteredTextfield;
import eu.the5zig.mod.gui.elements.Row;
import eu.the5zig.mod.gui.ts.MovableEntry;
import eu.the5zig.teamspeak.TeamSpeak;
import eu.the5zig.teamspeak.api.Channel;
import eu.the5zig.teamspeak.api.OwnClient;
import eu.the5zig.teamspeak.util.Callback;

import java.util.List;
import java.util.Locale;

public abstract class GuiTeamSpeakChannel extends GuiTeamSpeakEntry implements MovableEntry {

	protected final Channel channel;

	public GuiTeamSpeakChannel(Channel channel) {
		this.channel = channel;
	}

	@Override
	public int getXOffset() {
		return getParentChannels(channel) * 10 + 10;
	}

	@Override
	public void onClick(boolean doubleClick) {
		if (doubleClick) {
			tryJoin(null);
		}
	}

	private void tryJoin(String password) {
		OwnClient self = TeamSpeak.getClient().getSelectedTab().getSelf();
		if (self != null) {
			self.joinChannel(channel, password, new Callback<Integer>() {
				@Override
				public void onDone(Integer response) {
					if (response == 781) {
						The5zigMod.getVars().displayScreen(new GuiCenteredTextfield(The5zigMod.getVars().getCurrentScreen(), new CenteredTextfieldCallback() {
							@Override
							public void onDone(String text) {
								if (text != null) {
									tryJoin(text);
								}
							}

							@Override
							public String title() {
								return I18n.translate("teamspeak.enter_channel_password");
							}
						}));
					}
				}
			});
		}
	}

	@Override
	public List<? extends Row> getDescription(int width) {
		ImmutableList.Builder<Row> builder = ImmutableList.builder();
		builder.add(row(width, "channel.name", channel.getName()));
		if (!Strings.isNullOrEmpty(channel.getTopic())) {
			builder.add(row(width, "channel.topic", channel.getTopic()));
		}
		builder.add(row(width, "channel.codec", I18n.translate("teamspeak.entry.channel.codec." + channel.getCodec().toString().toLowerCase(Locale.ROOT))));
		builder.add(row(width, "channel.codec_quality", channel.getCodecQuality()));
		List<String> channelTypes = Lists.newArrayList();
		if (channel.isPermanent()) {
			channelTypes.add(I18n.translate("teamspeak.entry.channel.type.permanent"));
		}
		if (channel.isSemiPermanent()) {
			channelTypes.add(I18n.translate("teamspeak.entry.channel.type.semi_permanent"));
		}
		if (channel.isDefault()) {
			channelTypes.add(I18n.translate("teamspeak.entry.channel.type.default"));
		}
		if (channel.requiresPassword()) {
			channelTypes.add(I18n.translate("teamspeak.entry.channel.type.password"));
		}
		if (!channelTypes.isEmpty()) {
			builder.add(row(width, "channel.type", Joiner.on(", ").join(channelTypes)));
		}
		if (channel.getMaxClients() != 0) {
			builder.add(row(width, "channel.clients", channel.getClients().size(),
					channel.getMaxClients() == -1 ? I18n.translate("teamspeak.entry.channel.clients.unlimited") : channel.getMaxClients()));
		}
		if (channel.getNeededTalkPower() > 0) {
			builder.add(row(width, "channel.talk_power", channel.getNeededTalkPower()));
		}
		builder.add(row(width, "channel.subscription_status", I18n.translate("teamspeak.entry.channel.subscription_status." + (channel.hasSubscribed() ? "subscribed" : "unsubscribed"))));

		return builder.build();
	}

	@Override
	public boolean canBeMovedTo(Channel to, DragLocation location) {
		return location == DragLocation.ABOVE ? !channel.equals(to.getAbove()) : !channel.equals(to);
	}

	@Override
	public void moveEntryTo(Channel to, DragLocation location) {
		switch (location) {
			case ABOVE:
				Channel above = to.getAbove();
				if (above == null) {
					channel.moveInside(to.getParent());
				} else {
					channel.moveBelow(above);
				}
				break;
			case INSIDE:
				channel.moveInside(to);
				break;
			case BELOW:
				channel.moveBelow(to);
				break;
			default:
				break;
		}
	}

	public abstract boolean canBeCollapsed();

	public Channel getChannel() {
		return channel;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof GuiTeamSpeakChannel && ((GuiTeamSpeakChannel) obj).channel.equals(channel);
	}
}
