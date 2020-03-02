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

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.Gui;
import eu.the5zig.mod.gui.elements.CheckBox;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.elements.ITextfield;
import eu.the5zig.mod.gui.elements.RadioCheckBox;
import eu.the5zig.mod.util.SliderCallback;
import eu.the5zig.teamspeak.TeamSpeak;
import eu.the5zig.teamspeak.api.Channel;
import eu.the5zig.teamspeak.api.ChannelCodec;
import eu.the5zig.teamspeak.api.ChannelLifespan;
import eu.the5zig.teamspeak.api.ServerTab;
import eu.the5zig.util.Utils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Locale;

public abstract class GuiTeamSpeakCreateChannel extends Gui {

	protected Channel parentChannel;

	protected String channelName;
	protected String channelPassword;
	protected String channelTopic;
	protected String channelDescription;
	protected RadioCheckBox channelLifespan;
	protected int channelLifespanIndex;
	protected CheckBox defaultChannel;
	protected boolean defaultChannelBool;
	protected ChannelCodec currentCodec = ChannelCodec.OPUS_VOICE;
	protected int currentCodecIndex = 4;
	protected int codecQuality = 6;
	protected int neededTalkPower;
	protected CheckBox orderBottom;
	protected boolean orderBottomBool = true;
	protected Channel orderChannel;
	protected int maxClients = -1;

	public GuiTeamSpeakCreateChannel(Gui lastScreen, Channel parentChannel) {
		super(lastScreen);
		this.parentChannel = parentChannel;
		List<? extends Channel> channels;
		if (parentChannel != null) {
			channels = parentChannel.getChildren();
		} else {
			ServerTab selectedTab = TeamSpeak.getClient().getSelectedTab();
			if (selectedTab == null) {
				return;
			}
			channels = selectedTab.getChannels();
		}
		orderChannel = channels.isEmpty() ? parentChannel : channels.get(channels.size() - 1);
	}

	@Override
	public void initGui() {
		ServerTab selectedTab = TeamSpeak.getClient().getSelectedTab();
		if (selectedTab == null) {
			The5zigMod.getVars().displayScreen(lastScreen);
			return;
		}


		addButton(The5zigMod.getVars().createButton(100, getWidth() / 2 + 5, getHeight() / 6 + 168, 150, 20, The5zigMod.getVars().translate("gui.done")));
		addButton(The5zigMod.getVars().createButton(200, getWidth() / 2 - 155, getHeight() / 6 + 168, 150, 20, The5zigMod.getVars().translate("gui.cancel")));

		ITextfield textfieldName = The5zigMod.getVars().createTextfield(100, getWidth() / 2 - 40, getHeight() / 6 - 12, 200, 12, 100);
		textfieldName.callSetText(channelName == null ? "" : channelName);
		addTextField(textfieldName);
		ITextfield textfieldPassword = The5zigMod.getVars().createTextfield(101, getWidth() / 2 - 40, getHeight() / 6 - 12 + 16, 200, 12, 100);
		textfieldPassword.callSetText(channelPassword == null ? "" : channelPassword);
		addTextField(textfieldPassword);
		ITextfield textfieldTopic = The5zigMod.getVars().createTextfield(102, getWidth() / 2 - 40, getHeight() / 6 - 12 + 16 * 2, 200, 12, 100);
		textfieldTopic.callSetText(channelTopic == null ? "" : channelTopic);
		addTextField(textfieldTopic);
		ITextfield textfieldDescription = The5zigMod.getVars().createTextfield(103, getWidth() / 2 - 40, getHeight() / 6 - 12 + 16 * 3, 200, 12, 100);
		textfieldDescription.callSetText(channelDescription == null ? "" : channelDescription);
		addTextField(textfieldDescription);

		addRadioCheckBox(channelLifespan = new RadioCheckBox(getWidth() / 2 - 160, getHeight() / 6 - 12 + 16 * 4 + 18, 160, 12, I18n.translate("teamspeak.create_channel.lifespan.temporary"),
				I18n.translate("teamspeak.create_channel.lifespan.semi_permanent"), I18n.translate("teamspeak.create_channel.lifespan.permanent")));
		channelLifespan.setSelectedIndex(channelLifespanIndex);
		addCheckBox(defaultChannel = new CheckBox(getWidth() / 2 - 160, getHeight() / 6 - 12 + 16 * 4 + 12 * 3 + 26, 160, I18n.translate("teamspeak.create_channel.default")));
		defaultChannel.setSelected(defaultChannelBool);

		addButton(The5zigMod.getVars().createButton(104, getWidth() / 2 - 160, getHeight() / 6 - 12 + 16 * 4 + 12 * 3 + 26 + 26, 75, 20,
				I18n.translate("teamspeak.entry.channel.codec." + currentCodec.toString().toLowerCase(Locale.ROOT))));
		addButton(The5zigMod.getVars().createSlider(105, getWidth() / 2 - 80, getHeight() / 6 - 12 + 16 * 4 + 12 * 3 + 26 + 26, 75, 20, new SliderCallback() {
			@Override
			public String translate() {
				return I18n.translate("teamspeak.create_channel.codec_quality");
			}

			@Override
			public float get() {
				return codecQuality;
			}

			@Override
			public void set(float value) {
				codecQuality = (int) value;
			}

			@Override
			public float getMinValue() {
				return 0;
			}

			@Override
			public float getMaxValue() {
				return 10;
			}

			@Override
			public int getSteps() {
				return 1;
			}

			@Override
			public String getCustomValue(float value) {
				return null;
			}

			@Override
			public String getSuffix() {
				return "";
			}

			@Override
			public void action() {
			}
		}));

		addCheckBox(orderBottom = new CheckBox(getWidth() / 2 + 5, getHeight() / 6 - 12 + 16 * 4 + 4, 160, I18n.translate("teamspeak.create_channel.order_bottom")));
		orderBottom.setSelected(orderBottomBool);
		addButton(The5zigMod.getVars().createButton(106, getWidth() / 2 + 5, getHeight() / 6 - 12 + 16 * 4 + 16, 157, 20, The5zigMod.getVars()
				.shortenToWidth(I18n.translate("teamspeak.create_channel.order", orderChannel == null ? selectedTab.getServerInfo().getName() : orderChannel.getName()), 155)));
		addTextField(The5zigMod.getVars().createTextfield(107, getWidth() / 2 + 110, getHeight() / 6 - 12 + 16 * 4 + 50, 50, 14, 15));
		getTextfieldById(107).callSetText(String.valueOf(neededTalkPower));
		addTextField(The5zigMod.getVars().createTextfield(108, getWidth() / 2 + 110, getHeight() / 6 - 12 + 16 * 4 + 80, 50, 14, 15));
		getTextfieldById(108).callSetText(String.valueOf(maxClients));
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 100) {
			if (StringUtils.isEmpty(channelName)) {
				return;
			}
			ChannelLifespan lifespan = defaultChannelBool ? ChannelLifespan.PERMANENT : ChannelLifespan.values()[channelLifespanIndex];
			int neededTalkPower = Utils.parseInt(getTextfieldById(107).callGetText());
			int maxClients = Utils.parseInt(getTextfieldById(108).callGetText());
			ChannelResult result = new ChannelResult(channelName, channelPassword, channelTopic, channelDescription, lifespan, defaultChannelBool, parentChannel, orderChannel,
					orderBottom.isSelected(), neededTalkPower, currentCodec, codecQuality, maxClients);
			onDone(result);

			The5zigMod.getVars().displayScreen(lastScreen);
		} else if (button.getId() == 104) {
			currentCodec = ChannelCodec.values()[(currentCodec.ordinal() + 1) % ChannelCodec.values().length];
			button.setLabel(I18n.translate("teamspeak.entry.channel.codec." + currentCodec.toString().toLowerCase(Locale.ROOT)));
		} else if (button.getId() == 106) {
			The5zigMod.getVars().displayScreen(new GuiTeamSpeakSelectChannelOrder(this, parentChannel) {
				@Override
				protected void onSelect(Channel channel) {
					orderChannel = channel;
				}
			});
		}
	}

	@Override
	protected void tick() {
		if (defaultChannel.isSelected()) {
			channelLifespan.setEnabled(false);
			channelLifespan.setSelectedIndex(2);
		} else {
			channelLifespan.setEnabled(true);
		}
		getButtonById(100).setEnabled(!getTextfieldById(100).callGetText().isEmpty());
		getButtonById(106).setEnabled(!orderBottom.isSelected());

		channelName = getTextfieldById(100).callGetText();
		channelPassword = getTextfieldById(101).callGetText();
		channelTopic = getTextfieldById(102).callGetText();
		channelDescription = getTextfieldById(103).callGetText();
		channelLifespanIndex = channelLifespan.getSelectedIndex();
		defaultChannelBool = defaultChannel.isSelected();
		neededTalkPower = Utils.parseInt(getTextfieldById(107).callGetText());
		maxClients = Utils.parseInt(getTextfieldById(108).callGetText());
		orderBottomBool = orderBottom.isSelected();
	}

	@Override
	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
		The5zigMod.getVars().drawString(I18n.translate("teamspeak.create_channel.name"), getWidth() / 2 - 160, getHeight() / 6 - 12 + 4);
		The5zigMod.getVars().drawString(I18n.translate("teamspeak.create_channel.password"), getWidth() / 2 - 160, getHeight() / 6 - 12 + 16 + 4);
		The5zigMod.getVars().drawString(I18n.translate("teamspeak.create_channel.topic"), getWidth() / 2 - 160, getHeight() / 6 - 12 + 16 * 2 + 4);
		The5zigMod.getVars().drawString(I18n.translate("teamspeak.create_channel.description"), getWidth() / 2 - 160, getHeight() / 6 - 12 + 16 * 3 + 4);
		The5zigMod.getVars().drawString(I18n.translate("teamspeak.create_channel.lifespan"), getWidth() / 2 - 160, getHeight() / 6 - 12 + 16 * 4 + 6);
		The5zigMod.getVars().drawString(I18n.translate("teamspeak.create_channel.codec"), getWidth() / 2 - 160, getHeight() / 6 - 12 + 16 * 4 + 12 * 3 + 26 + 18);
		The5zigMod.getVars().drawString(I18n.translate("teamspeak.create_channel.needed_talk_power"), getWidth() / 2 + 5, getHeight() / 6 - 12 + 16 * 4 + 54);
		The5zigMod.getVars().drawString(I18n.translate("teamspeak.create_channel.max_clients"), getWidth() / 2 + 5, getHeight() / 6 - 12 + 16 * 4 + 84);
	}

	@Override
	public String getTitleKey() {
		return "teamspeak.create_channel.title";
	}

	protected abstract void onDone(ChannelResult result);

	protected class ChannelResult {

		public final String name;
		public final String password;
		public final String topic;
		public final String description;
		public final ChannelLifespan lifespan;
		public final boolean defaultChannel;
		public final Channel parentChannel;
		public final Channel orderChannel;
		public final boolean bottomPosition;
		public final int neededTalkPower;
		public final ChannelCodec codec;
		public final int codecQuality;
		public final int maxClients;

		public ChannelResult(String name, String password, String topic, String description, ChannelLifespan lifespan, boolean defaultChannel, Channel parentChannel, Channel orderChannel,
				boolean bottomPosition, int neededTalkPower, ChannelCodec codec, int codecQuality, int maxClients) {
			this.name = name;
			this.password = password;
			this.topic = topic;
			this.description = description;
			this.lifespan = lifespan;
			this.defaultChannel = defaultChannel;
			this.parentChannel = parentChannel;
			this.orderChannel = orderChannel;
			this.bottomPosition = bottomPosition;
			this.neededTalkPower = neededTalkPower;
			this.codec = codec;
			this.codecQuality = codecQuality;
			this.maxClients = maxClients;
		}
	}

}
