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

import eu.the5zig.mod.gui.elements.Row;
import eu.the5zig.mod.render.Base64Renderer;
import eu.the5zig.teamspeak.api.Client;

import java.awt.image.BufferedImage;

public class TeamSpeakClientAvatarRow implements Row {

	private Base64Renderer renderer;
	private int width;
	private int height;

	public TeamSpeakClientAvatarRow(int width, Client client) {
		BufferedImage avatar = client.getAvatar();
		if (avatar != null) {
			this.width = avatar.getWidth();
			this.height = avatar.getHeight();
			int maxWidth = Math.min(100, width - 10);
			int maxHeight = 100;
			while (this.width > maxWidth || this.height > maxHeight) {
				this.width /= 1.2;
				this.height /= 1.2;
			}
			this.renderer = Base64Renderer.getRenderer(avatar, "ts/avatar_" + client.getUniqueId());
		}
	}

	@Override
	public void draw(int x, int y) {
		if (renderer != null) {
			renderer.renderImage(x + 5, y + 2, width, height);
		}
	}

	@Override
	public int getLineHeight() {
		return height == 0 ? 0 : height + 4;
	}
}
