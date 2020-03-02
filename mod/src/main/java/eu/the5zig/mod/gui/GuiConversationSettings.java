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

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.entity.Conversation;
import eu.the5zig.mod.chat.entity.ConversationGroupChat;
import eu.the5zig.mod.chat.entity.Group;
import eu.the5zig.mod.chat.network.packets.PacketLeaveGroupChat;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.util.Callback;
import eu.the5zig.util.minecraft.ChatColor;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class GuiConversationSettings extends GuiOptions {

	private Conversation conversation;
	private int BEHAVIOUR, DELETE;

	public GuiConversationSettings(Gui lastScreen, Conversation conversation) {
		super(lastScreen);
		this.conversation = conversation;
	}

	@Override
	public void initGui() {
		super.initGui();

		BEHAVIOUR = addOptionButton(I18n.translate("chat.conversation_settings.behaviour", conversation.getBehaviour().getName()), new Callback<IButton>() {
			@Override
			public void call(IButton button) {
				Conversation.Behaviour next = conversation.getBehaviour().getNext();
				The5zigMod.getConversationManager().setBehaviour(conversation, next);
				button.setLabel(I18n.translate("chat.conversation_settings.behaviour", next.getName()));
			}
		});
		DELETE = addOptionButton(I18n.translate("chat.conversation_settings.delete"), new Callback<IButton>() {
			@Override
			public void call(IButton button) {
				The5zigMod.getVars().displayScreen(lastScreen);
				if (conversation instanceof ConversationGroupChat) {
					if (!The5zigMod.getNetworkManager().isConnected())
						return;
					ConversationGroupChat c = (ConversationGroupChat) conversation;
					Group group = The5zigMod.getGroupChatManager().getGroup(c.getGroupId());
					if (group != null) {
						if (!group.getOwner().getUniqueId().equals(The5zigMod.getDataManager().getUniqueId())) {
							The5zigMod.getNetworkManager().sendPacket(new PacketLeaveGroupChat(c.getGroupId()));
						} else {
							The5zigMod.getOverlayMessage().displayMessageAndSplit(ChatColor.RED + I18n.translate("chat.conversation_settings.transfer_ownership"));
						}
					} else {
						The5zigMod.getConversationManager().deleteConversation(c);
					}
				} else {
					The5zigMod.getConversationManager().deleteConversation(conversation);
				}
			}
		});
	}

	@Override
	protected void tick() {
		getButtonById(BEHAVIOUR).setEnabled(
				!(conversation instanceof ConversationGroupChat) || The5zigMod.getGroupChatManager().getGroup(((ConversationGroupChat) conversation).getGroupId()) != null);
		getButtonById(DELETE).setLabel(
				conversation instanceof ConversationGroupChat && The5zigMod.getGroupChatManager().getGroup(((ConversationGroupChat) conversation).getGroupId()) != null ?
						I18n.translate("chat.conversation_settings.leave") : I18n.translate("chat.conversation_settings.delete"));
	}

	@Override
	public String getTitleKey() {
		return "chat.conversation_settings.title";
	}
}
