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

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.entity.Group;
import eu.the5zig.mod.chat.network.packets.PacketDeleteGroupChat;
import eu.the5zig.mod.chat.network.packets.PacketGroupChatStatus;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.util.Callback;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class GuiGroupChatSettings extends GuiOptions {

	private Group group;
	private int ADD_PLAYER, EDIT_NAME, DELETE;

	public GuiGroupChatSettings(Gui lastScreen, Group group) {
		super(lastScreen);
		this.group = group;
	}

	@Override
	public void initGui() {
		super.initGui();
		ADD_PLAYER = addOptionButton(I18n.translate("group.settings.add_player"), new Callback<IButton>() {
			@Override
			public void call(IButton callback) {
				The5zigMod.getVars().displayScreen(new GuiCenteredTextfield(GuiGroupChatSettings.this, new CenteredTextfieldCallback() {
					@Override
					public void onDone(String text) {
						The5zigMod.getNetworkManager().sendPacket(new PacketGroupChatStatus(group.getId(), PacketGroupChatStatus.GroupAction.ADD_PLAYER, text));
					}

					@Override
					public String title() {
						return I18n.translate("group.settings.confirm.add_player");
					}
				}, 1, 16));
			}
		});
		EDIT_NAME = addOptionButton(I18n.translate("group.settings.edit_name"), new Callback<IButton>() {
			@Override
			public void call(IButton callback) {
				The5zigMod.getVars().displayScreen(new GuiCenteredTextfield(GuiGroupChatSettings.this, new CenteredTextfieldCallback() {
					@Override
					public void onDone(String text) {
						The5zigMod.getNetworkManager().sendPacket(new PacketGroupChatStatus(group.getId(), PacketGroupChatStatus.GroupAction.CHANGE_NAME, text));
					}

					@Override
					public String title() {
						return I18n.translate("group.settings.confirm.change_name");
					}
				}, 2, 50));
			}
		});
		DELETE = addOptionButton(I18n.translate("group.settings.delete"), new Callback<IButton>() {
			@Override
			public void call(IButton callback) {
				The5zigMod.getVars().displayScreen(new GuiYesNo(GuiGroupChatSettings.this, new YesNoCallback() {
					@Override
					public void onDone(boolean yes) {
						if (yes) {
							The5zigMod.getNetworkManager().sendPacket(new PacketDeleteGroupChat(group.getId()));
							The5zigMod.getVars().displayScreen(lastScreen.lastScreen);
						}
					}

					@Override
					public String title() {
						return I18n.translate("group.settings.confirm.delete");
					}
				}));
			}
		});
	}

	@Override
	protected void tick() {
		boolean isOwner = group.getOwner().getUniqueId().equals(The5zigMod.getDataManager().getUniqueId());
		boolean isAdmin = group.isAdmin(The5zigMod.getDataManager().getUniqueId());

		getButtonById(ADD_PLAYER).setEnabled(isAdmin || isOwner);
		getButtonById(EDIT_NAME).setEnabled(isAdmin || isOwner);
		getButtonById(DELETE).setEnabled(isOwner);
	}

	@Override
	public String getTitleKey() {
		return "group.settings.title";
	}
}
