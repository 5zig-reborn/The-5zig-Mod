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

package eu.the5zig.mod.gui.ts.rows;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.Gui;
import eu.the5zig.mod.gui.elements.Row;
import eu.the5zig.mod.gui.ts.entries.GuiTeamSpeakClient;
import eu.the5zig.mod.render.Base64Renderer;
import eu.the5zig.mod.util.GLUtil;
import eu.the5zig.mod.util.Vector2i;
import eu.the5zig.teamspeak.api.Group;

import java.awt.image.BufferedImage;

public class GroupRow implements Row {

	public final Group group;
	private final String serverUniqueId;
	public boolean member;

	public GroupRow(Group group, String serverUniqueId) {
		this.group = group;
		this.serverUniqueId = serverUniqueId;
	}

	@Override
	public void draw(int x, int y) {
		The5zigMod.getVars().bindTexture(The5zigMod.TEAMSPEAK_ICONS);
		GLUtil.enableBlend();
		if (member) {
			Gui.drawModalRectWithCustomSizedTexture(x + 180, y + 2, 9 * 128 / 12, 0 * 128 / 12, 128 / 12, 128 / 12, 2048 / 12, 2048 / 12);
		}
		if (GuiTeamSpeakClient.DEFAULT_GROUP_ICONS.containsKey(group.getIconId())) {
			Vector2i uv = GuiTeamSpeakClient.DEFAULT_GROUP_ICONS.get(group.getIconId());
			Gui.drawModalRectWithCustomSizedTexture(x + 2, y, uv.getX() * 128 / 12, uv.getY() * 128 / 12, 128 / 12, 128 / 12, 2048 / 12, 2048 / 12);
		} else {
			BufferedImage icon = group.getIcon();
			if (icon != null) {
				Base64Renderer renderer = Base64Renderer.getRenderer(icon, "ts/" + serverUniqueId + "/icon_" + group.getIconId());
				renderer.renderImage(x + 8, y - 2, 10, 10);
			}
		}
		The5zigMod.getVars().drawString(The5zigMod.getVars().shortenToWidth(group.getName(), 120) + (group.isPersistent() ? " [perm]" : " [temp]"), x + 16, y + 2);
	}

	@Override
	public int getLineHeight() {
		return 18;
	}
}
