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

package eu.the5zig.mod.modules.items.system;

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.config.items.IntItem;
import eu.the5zig.mod.gui.Gui;
import eu.the5zig.mod.manager.iloveradio.ILoveRadioChannel;
import eu.the5zig.mod.manager.iloveradio.ILoveRadioManager;
import eu.the5zig.mod.manager.iloveradio.ILoveRadioTrack;
import eu.the5zig.mod.modules.AbstractModuleItem;
import eu.the5zig.mod.modules.ModuleItemPropertiesImpl;
import eu.the5zig.mod.render.Base64Renderer;
import eu.the5zig.mod.render.RenderLocation;
import eu.the5zig.mod.util.GLUtil;
import eu.the5zig.mod.util.ScrollingText;

import java.util.Locale;

public class ILoveRadio extends AbstractModuleItem {

	private static final Base64Renderer base64Renderer = new Base64Renderer(The5zigMod.TRACK_LOCATION, 60, 60);

	private ScrollingText scrollingTrack;
	private ScrollingText scrollingArtist;

	@Override
	public void registerSettings() {
		((ModuleItemPropertiesImpl) getProperties()).addSetting(new IntItem("channel", ((ModuleItemPropertiesImpl) getProperties()).getItemCategory(), 0) {
			@Override
			public String translateValue() {
				ILoveRadioChannel channelById = The5zigMod.getDataManager().getiLoveRadioManager().getChannelById(((Integer) getProperties().getSetting("channel").get()) + 1);
				return channelById == null ? I18n.translate("modules.item.iloveradio.unknown") : channelById.getName();
			}

			@Override
			public void next() {
				int channelSize = The5zigMod.getDataManager().getiLoveRadioManager().getChannelSize();
				if (channelSize > 0) {
					set((get() + 1) % channelSize);
				}
			}
		});
	}

	@Override
	public void render(int x, int y, RenderLocation renderLocation, boolean dummy) {
		Gui.drawRect(x, y, x + getWidth(dummy), y + getHeight(dummy) - 2, ILoveRadioManager.ILOVERADIO_COLOR_BACKGROUND);
		ILoveRadioManager manager = The5zigMod.getDataManager().getiLoveRadioManager();
		Integer channelId = (Integer) getProperties().getSetting("channel").get();
		channelId = channelId == null ? 1 : channelId + 1;

		ILoveRadioChannel channelById = manager.getChannelById(channelId);
		ILoveRadioTrack track = channelById != null ? channelById.getCurrentTrack() : null;
		int trackLeft = x;
		int trackTop = y;
		int trackRight = x + getWidth(dummy) - 4;
		int albumSize = getHeight(dummy) - 2;

		if (track != null) {
			if (track.getImage() != null && !track.getImage().equals(base64Renderer.getBase64String())) {
				base64Renderer.setBase64String(track.getImage(),
						"iloveradio/track_" + track.getTitle().toLowerCase(Locale.ROOT).replace(" ", "") + "_" + track.getArtist().toLowerCase(Locale.ROOT).replace(" ", ""));
			} else if (track.getImage() == null && base64Renderer.getBase64String() != null) {
				base64Renderer.reset();
			}
		} else if (base64Renderer.getBase64String() != null) {
			base64Renderer.reset();
		}
		base64Renderer.renderImage(trackLeft, trackTop, albumSize, albumSize);

		int infoLeft = trackLeft + albumSize + 4;
		int infoTop = trackTop + 3;
		int maxInfoWidth = trackRight - infoLeft;
		String trackName = track != null ? track.getTitle() : I18n.translate("modules.item.iloveradio.unknown");
		String artistName = track != null ? track.getArtist() : "";

		if (trackName != null && (scrollingTrack == null || !trackName.equals(scrollingTrack.getText()))) {
			scrollingTrack = new ScrollingText(trackName, maxInfoWidth, 8, ILoveRadioManager.ILOVERADIO_COLOR_BACKGROUND, 0xffffff);
		} else if (scrollingTrack != null && trackName == null) {
			scrollingTrack = null;
		}
		if (artistName != null && (scrollingArtist == null || !artistName.equals(scrollingArtist.getText()))) {
			scrollingArtist = new ScrollingText(artistName, maxInfoWidth, 6, ILoveRadioManager.ILOVERADIO_COLOR_BACKGROUND, 0xffffff);
		} else if (scrollingArtist != null && artistName == null) {
			scrollingArtist = null;
		}
		if (scrollingTrack != null) {
			scrollingTrack.setChild(scrollingArtist);
			scrollingTrack.setScale(getRenderSettings().getScale());
			scrollingTrack.render(infoLeft, infoTop);
		}
		if (scrollingArtist != null) {
			scrollingArtist.setParent(scrollingTrack);
			scrollingArtist.setScale(getRenderSettings().getScale());
			scrollingArtist.render(infoLeft, infoTop + 9);
		}
	}

	private void drawScaledString(String string, int x, int y, int color, float scale) {
		GLUtil.pushMatrix();
		GLUtil.translate(x, y, 1);
		GLUtil.scale(scale, scale, scale);
		The5zigMod.getVars().drawString(string, 0, 0, color);
		GLUtil.popMatrix();
	}

	@Override
	public int getWidth(boolean dummy) {
		return 100;
	}

	@Override
	public int getHeight(boolean dummy) {
		return 22;
	}
}
