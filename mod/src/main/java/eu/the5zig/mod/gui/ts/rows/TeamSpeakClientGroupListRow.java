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

package eu.the5zig.mod.gui.ts.rows;

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.Gui;
import eu.the5zig.mod.gui.elements.Row;
import eu.the5zig.mod.gui.ts.entries.GuiTeamSpeakClient;
import eu.the5zig.mod.render.Base64Renderer;
import eu.the5zig.mod.util.Vector2i;
import eu.the5zig.teamspeak.api.Group;

import java.awt.image.BufferedImage;
import java.util.List;

public class TeamSpeakClientGroupListRow implements Row {

	private final int width;
	private final List<? extends Group> serverGroups;
	private final Group channelGroup;
	private final String serverUniqueId;

	public TeamSpeakClientGroupListRow(int width, List<? extends Group> serverGroups, Group channelGroup, String serverUniqueId) {
		this.width = width;
		this.serverGroups = serverGroups;
		this.channelGroup = channelGroup;
		this.serverUniqueId = serverUniqueId;
	}

	@Override
	public void draw(int x, int y) {
		x += 2;
		y += 2;
		The5zigMod.getVars().bindTexture(The5zigMod.TEAMSPEAK_ICONS);
		if (!serverGroups.isEmpty()) {
			Gui.drawModalRectWithCustomSizedTexture(x, y - 2, 12 * 128 / 12, 6 * 128 / 12, 128 / 12, 128 / 12, 2048 / 12, 2048 / 12);
		}
		if (channelGroup != null) {
			Gui.drawModalRectWithCustomSizedTexture(x, y + (serverGroups.isEmpty() ? 0 : 12 + serverGroups.size() * 10) - 2, 9 * 128 / 12, 6 * 128 / 12, 128 / 12, 128 / 12, 2048 / 12,
					2048 / 12);
		}
		if (!serverGroups.isEmpty()) {
			The5zigMod.getVars().drawString(I18n.translate("teamspeak.entry.client.server_groups"), x + 12, y);
			y += 10;
			for (Group group : serverGroups) {
				if (GuiTeamSpeakClient.DEFAULT_GROUP_ICONS.containsKey(group.getIconId())) {
					Vector2i uv = GuiTeamSpeakClient.DEFAULT_GROUP_ICONS.get(group.getIconId());
					The5zigMod.getVars().bindTexture(The5zigMod.TEAMSPEAK_ICONS);
					Gui.drawModalRectWithCustomSizedTexture(x + 8, y - 2, uv.getX() * 128 / 12, uv.getY() * 128 / 12, 128 / 12, 128 / 12, 2048 / 12, 2048 / 12);
				} else {
					BufferedImage icon = group.getIcon();
					if (icon != null) {
						Base64Renderer renderer = Base64Renderer.getRenderer(icon, "ts/" + serverUniqueId.replace("=", "") + "/icon_" + group.getIconId());
						renderer.renderImage(x + 8, y - 2, 10, 10);
					}
				}
				The5zigMod.getVars().drawString(The5zigMod.getVars().shortenToWidth(group.getName(), width - 20), x + 20, y);
				y += 10;
			}
			y += 2;
		}
		if (channelGroup != null) {
			The5zigMod.getVars().drawString(I18n.translate("teamspeak.entry.client.channel_group"), x + 12, y);
			y += 10;
			if (GuiTeamSpeakClient.DEFAULT_GROUP_ICONS.containsKey(channelGroup.getIconId())) {
				Vector2i uv = GuiTeamSpeakClient.DEFAULT_GROUP_ICONS.get(channelGroup.getIconId());
				The5zigMod.getVars().bindTexture(The5zigMod.TEAMSPEAK_ICONS);
				Gui.drawModalRectWithCustomSizedTexture(x + 8, y - 2, uv.getX() * 128 / 12, uv.getY() * 128 / 12, 128 / 12, 128 / 12, 2048 / 12, 2048 / 12);
			} else {
				BufferedImage icon = channelGroup.getIcon();
				if (icon != null) {
					Base64Renderer renderer = Base64Renderer.getRenderer(icon, "ts/" + serverUniqueId.replace("=", "") + "/icon_" + channelGroup.getIconId());
					renderer.renderImage(x + 8, y - 2, 10, 10);
				}
			}
			The5zigMod.getVars().drawString(The5zigMod.getVars().shortenToWidth(channelGroup.getName(), width - 20), x + 20, y);
		}
	}

	@Override
	public int getLineHeight() {
		int height = 0;

		if (!serverGroups.isEmpty()) {
			height += 12 + 10 * serverGroups.size();
		}
		if (channelGroup != null) {
			height += 22;
		}

		return height;
	}
}
