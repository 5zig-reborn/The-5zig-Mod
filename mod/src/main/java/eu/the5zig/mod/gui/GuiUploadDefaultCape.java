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
import eu.the5zig.mod.chat.network.packets.PacketCapeSettings;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.util.GLUtil;
import eu.the5zig.mod.util.IResourceLocation;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class GuiUploadDefaultCape extends Gui {

	private static final IResourceLocation capesTexture = The5zigMod.getVars().createResourceLocation("the5zigmod", "textures/capes.png");
	private int selectedCape = -1;

	public GuiUploadDefaultCape(Gui lastScreen) {
		super(lastScreen);
	}

	@Override
	public void initGui() {
		addCancelButton();
	}

	@Override
	protected void actionPerformed(IButton button) {

	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		if (button != 0 || selectedCape < 0 || selectedCape >= PacketCapeSettings.Cape.values().length)
			return;
		The5zigMod.getNetworkManager().sendPacket(new PacketCapeSettings(PacketCapeSettings.Action.UPLOAD_DEFAULT, PacketCapeSettings.Cape.values()[selectedCape]));
		The5zigMod.getVars().displayScreen(lastScreen);
		The5zigMod.getOverlayMessage().displayMessage(I18n.translate("cape.upload.uploading"));
	}

	@Override
	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawCenteredString(I18n.translate("cape.upload.default.help") + (selectedCape >= 0 && selectedCape < PacketCapeSettings.Cape.values().length ? " >" + StringUtils.capitalize(
				PacketCapeSettings.Cape.values()[selectedCape].toString().toLowerCase(Locale.ROOT).replace("_", " "))  : ""), getWidth() / 2, getHeight() / 6 - 10);

		The5zigMod.getVars().bindTexture(capesTexture);
		int capeWidth = 22 * 2;
		int capeHeight = 17 * 2;
		int xOffset = (getWidth() - (capeWidth + 8) * 8) / 2 + 4;
		int yOffset = getHeight() / 6 + 4;
		selectedCape = -1;
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 8; x++) {
				GLUtil.color(.7f, .7f, .7f, 1.0f);
				if (mouseX > xOffset && mouseX < xOffset + capeWidth && mouseY > yOffset && mouseY < yOffset + capeHeight) {
					selectedCape = x + y * 8;
					GLUtil.enableBlend();
					GLUtil.color(1, 1, 1, 1);
					Gui.drawModalRectWithCustomSizedTexture(xOffset, yOffset, capeWidth * x, capeHeight * y, capeWidth, capeHeight, capeWidth * 8, capeHeight * 4);
					GLUtil.disableBlend();
				} else {
					Gui.drawModalRectWithCustomSizedTexture(xOffset, yOffset, capeWidth * x, capeHeight * y, capeWidth, capeHeight, capeWidth * 8, capeHeight * 4);
				}
				xOffset += capeWidth + 8;
			}
			xOffset = (getWidth() - (capeWidth + 8) * 8) / 2 + 4;
			yOffset += capeHeight + 6;
		}
		GLUtil.color(1, 1, 1, 1);
	}

	@Override
	public String getTitleKey() {
		return "cape.upload.default.title";
	}
}
