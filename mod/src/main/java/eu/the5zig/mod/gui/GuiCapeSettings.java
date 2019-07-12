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
import eu.the5zig.mod.chat.entity.Rank;
import eu.the5zig.mod.chat.network.packets.PacketCapeSettings;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.util.FileSelectorCallback;
import eu.the5zig.mod.util.GLUtil;
import eu.the5zig.util.minecraft.ChatColor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class GuiCapeSettings extends Gui {

	private static final int[] ALLOWED_WIDTHS = {64, 128, 256, 512, 1024};

	private static long throttle;
	private String status;

	private Object capeLocation;

	public GuiCapeSettings(Gui lastScreen) {
		super(lastScreen);
	}

	@Override
	protected void tick() {
		enableCapeButtons(true);
		if (!The5zigMod.getNetworkManager().isConnected()) {
			status = ChatColor.RED + I18n.translate("cape.not_connected");
			enableCapeButtons(false);
		} else if (The5zigMod.getDataManager().getProfile().getRank().get(0) == Rank.USER) {
			status = ChatColor.RED + I18n.translate("cape.not_donator");
			enableCapeButtons(false);
		} else if (The5zigMod.getDataManager().getProfile().getRank().get(0) == Rank.PATRON) {
			status = ChatColor.GOLD + I18n.translate("cape.not_custom");
			getButtonById(2).setEnabled(false);
		} else {
			status = null;
			if (!The5zigMod.getDataManager().getProfile().isCapeEnabled()) {
				getButtonById(2).setEnabled(false);
				getButtonById(3).setEnabled(false);
			} else {
				getButtonById(2).setEnabled(true);
				getButtonById(3).setEnabled(true);
			}
		}
		capeLocation = The5zigMod.getVars().getResourceManager().getOwnCapeLocation();
	}

	private void enableCapeButtons(boolean enable) {
		if (System.currentTimeMillis() - throttle > 0) {
			throttle = 0;
		} else {
			enable = false;
		}
		for (int i = 1; i < 4; i++) {
			getButtonById(i).setEnabled(enable);
		}
	}

	@Override
	public void initGui() {
		addDoneButton();
		addButton(The5zigMod.getVars().createButton(1, getWidth() / 2 - 155, getHeight() / 6 - 6, 150, 20,
				The5zigMod.getDataManager().getProfile().isCapeEnabled() ? I18n.translate("cape.disable") : I18n.translate("cape.enable")));
		addButton(The5zigMod.getVars().createButton(2, getWidth() / 2 + 5, getHeight() / 6 - 6, 150, 20, I18n.translate("cape.upload")));
		addButton(The5zigMod.getVars().createButton(3, getWidth() / 2 - 155, getHeight() / 6 + 24 - 6, 150, 20, I18n.translate("cape.default")));
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 1) {
			The5zigMod.getDataManager().getProfile().setCapeEnabled(!The5zigMod.getDataManager().getProfile().isCapeEnabled());

			button.setLabel(The5zigMod.getDataManager().getProfile().isCapeEnabled() ? I18n.translate("cape.disable") : I18n.translate("cape.enable"));
			button.setTicksDisabled(20 * 5);
			throttle = System.currentTimeMillis() + 1000 * 5;
		}
		if (button.getId() == 2) {
			The5zigMod.getVars().displayScreen(new GuiFileSelector(this, new FileSelectorCallback() {
				@Override
				public void onDone(File file) {
					try {
						if (file.length() > 1000000) {
							The5zigMod.getOverlayMessage().displayMessageAndSplit(ChatColor.RED + I18n.translate("cape.upload.max_size", "1mb"));
							return;
						}
						BufferedImage image = ImageIO.read(file);
						int width = image.getWidth();
						int height = image.getHeight();

						if (width == 0 || height % (width / 2) != 0) {
							The5zigMod.getOverlayMessage().displayMessageAndSplit(ChatColor.RED + I18n.translate("cape.upload.wrong_dimension"));
							return;
						}

						if (height == 0 || height / (width / 2) > 32) {
							The5zigMod.getOverlayMessage().displayMessageAndSplit(ChatColor.RED + I18n.translate("cape.upload.wrong_dimension"));
							return;
						}

						boolean widthAllowed = false;
						for (int allowedWidth : ALLOWED_WIDTHS) {
							if (width == allowedWidth) {
								widthAllowed = true;
								break;
							}
						}
						if (!widthAllowed) {
							The5zigMod.getOverlayMessage().displayMessageAndSplit(ChatColor.RED + I18n.translate("cape.upload.wrong_dimension"));
							return;
						}

						ByteArrayOutputStream out = new ByteArrayOutputStream();
						ImageIO.write(image, "PNG", out);
						The5zigMod.getNetworkManager().sendPacket(new PacketCapeSettings(out.toByteArray()));

						The5zigMod.getOverlayMessage().displayMessage(I18n.translate("cape.upload.uploading"));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				@Override
				public String getTitle() {
					return "The 5zig Mod - " + I18n.translate("cape.upload.title");
				}
			}, "png"));
		}
		if (button.getId() == 3) {
			The5zigMod.getVars().displayScreen(new GuiUploadDefaultCape(this));
		}
	}

	@Override
	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (status != null) {
			drawCenteredString(status, getWidth() / 2, getHeight() / 6 + 43);
		}

		if (capeLocation == null)
			return;

		The5zigMod.getVars().bindTexture(capeLocation);
		int width = 256 + 128;
		int height = 128 + 64;
		int texWidth = 22 * width / 64;
		int texHeight = 17 * height / 32;
		GLUtil.color(1, 1, 1, 1);
		Gui.drawModalRectWithCustomSizedTexture(getWidth() / 2 - texWidth / 2, getHeight() / 6 + 110 - texHeight / 2, 0, 0, texWidth, texHeight, width, height);
	}

	@Override
	public String getTitleKey() {
		return "cape.title";
	}
}
