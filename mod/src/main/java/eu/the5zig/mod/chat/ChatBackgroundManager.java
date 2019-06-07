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

package eu.the5zig.mod.chat;

import eu.the5zig.mod.The5zigMod;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ChatBackgroundManager {

	private Object chatBackgroundLocation;
	private int imageWidth, imageHeight;

	public ChatBackgroundManager() {
		reloadBackgroundImage();
	}

	public void reloadBackgroundImage() {
		String location = (String) The5zigMod.getConfig().get("chatBackgroundLocation").get();
		if (location == null) {
			resetBackgroundImage();
			return;
		}
		File file = new File(location);
		if (!file.exists()) {
			resetBackgroundImage();
			return;
		}
		try {
			BufferedImage bufferedImage = ImageIO.read(file);
			imageWidth = bufferedImage.getWidth();
			imageHeight = bufferedImage.getHeight();
			chatBackgroundLocation = The5zigMod.getVars().loadDynamicImage("chat_background", bufferedImage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void resetBackgroundImage() {
		chatBackgroundLocation = null;
		imageWidth = 0;
		imageHeight = 0;
	}

	public Object getChatBackground() {
		return chatBackgroundLocation;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}
}
