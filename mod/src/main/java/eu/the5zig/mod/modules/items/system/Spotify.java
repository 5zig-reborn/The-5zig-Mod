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

package eu.the5zig.mod.modules.items.system;

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.Gui;
import eu.the5zig.mod.manager.spotify.SpotifyManager;
import eu.the5zig.mod.manager.spotify.SpotifyNewStatus;
import eu.the5zig.mod.manager.spotify.SpotifyNewTrack;
import eu.the5zig.mod.modules.AbstractModuleItem;
import eu.the5zig.mod.render.Base64Renderer;
import eu.the5zig.mod.render.RenderLocation;
import eu.the5zig.mod.util.GLUtil;
import eu.the5zig.mod.util.ScrollingText;
import eu.the5zig.util.Utils;

import java.util.Locale;

public class Spotify extends AbstractModuleItem {

	private static final Base64Renderer base64Renderer = new Base64Renderer(The5zigMod.TRACK_LOCATION, 128, 128);

	static {
		base64Renderer.setInterpolateLinear(The5zigMod.isMinecraftVersionAvailable("1.8"));
	}

	private ScrollingText scrollingTrack;
	private ScrollingText scrollingArtist;

	@Override
	public void registerSettings() {
		getProperties().addSetting("showSpotifyAlways", false);
	}

	@Override
	public void render(int x, int y, RenderLocation renderLocation, boolean dummy) {
		Gui.drawRect(x, y, x + getWidth(dummy), y + getHeight(dummy) - 2, SpotifyManager.SPOTIFY_COLOR_BACKGROUND);

		SpotifyManager spotifyManager = The5zigMod.getDataManager().getSpotifyManager();
		SpotifyNewStatus status = spotifyManager != null && spotifyManager.isConnected() ? spotifyManager.getStatus() : null;
		SpotifyNewTrack track = status != null && status.getTrack() != null ? status.getTrack() : null;

		int trackLeft = x;
		int trackTop = y;
		int trackRight = x + getWidth(dummy) - 4;
		int albumSize = getHeight(dummy) - 2;
		// Album Preview
		if (track != null) {
			if (track.getImage() != null && !track.getImage().equals(base64Renderer.getBase64String())) {
				base64Renderer.setBase64String(track.getImage(), "spotify/track_" + track.getId());
			} else if (track.getImage() == null && base64Renderer.getBase64String() != null) {
				base64Renderer.reset();
			}
		} else if (base64Renderer.getBase64String() != null) {
			base64Renderer.reset();
		}
		base64Renderer.renderImage(trackLeft, trackTop, albumSize, albumSize);

		// Track Info
		int infoLeft = trackLeft + albumSize + 4;
		int infoTop = trackTop + 3;
		int maxInfoWidth = trackRight - infoLeft;
		String trackName = track != null ? status.getType() == SpotifyNewTrack.Type.ad ? I18n.translate("modules.item.spotify.ad") :
				track.getName() : spotifyManager != null && spotifyManager.getDisconnectError() != null ? I18n.translate(
				"modules.item.spotify.error." + spotifyManager.getDisconnectError().toString().toLowerCase(Locale.ROOT)) : null;
		String artistName = track != null && track.getArtists() != null ? track.getArtistsString() : null;

		if (trackName != null && (scrollingTrack == null || !trackName.equals(scrollingTrack.getText()))) {
			scrollingTrack = new ScrollingText(trackName, maxInfoWidth, 8, SpotifyManager.SPOTIFY_COLOR_BACKGROUND, 0xaaaaaa);
		} else if (scrollingTrack != null && trackName == null) {
			scrollingTrack = null;
		}
		if (artistName != null && (scrollingArtist == null || !artistName.equals(scrollingArtist.getText()))) {
			scrollingArtist = new ScrollingText(artistName, maxInfoWidth, 6, SpotifyManager.SPOTIFY_COLOR_BACKGROUND, 0xaaaaaa);
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

		// position
		int positionLineTop = y + getHeight(dummy) - 8;
		int positionLineBottom = positionLineTop + 2;
		int positionLineLeft = infoLeft + 11;
		int positionLineRight = trackRight - 11;
		int positionWidth = positionLineRight - positionLineLeft;
		Gui.drawRect(positionLineLeft, positionLineTop, positionLineRight, positionLineBottom, SpotifyManager.SPOTIFY_COLOR_SECOND);
		double playingPosition = status != null ? status.getProgress() * 1000 : 0;
		long millisSinceUpdate = status != null && status.isPlaying() ? (int) (System.currentTimeMillis() - status.getTimestamp()) : 0;
		double trackPosition = playingPosition + millisSinceUpdate;
		long trackLength = track != null ? track.getDuration() * 1000 : 1000;
		double trackPercentage = trackPosition / trackLength;
		Gui.drawRect(positionLineLeft, positionLineTop, positionLineLeft + (trackPercentage * positionWidth), positionLineBottom, SpotifyManager.SPOTIFY_COLOR_ACCENT);
		drawScaledString(Utils.convertToClock((long) trackPosition), infoLeft, positionLineTop, 0xaaaaaa, 0.35f);
		drawScaledString(Utils.convertToClock(trackLength), positionLineRight + 2, positionLineTop, 0xaaaaaa, 0.35f);
	}

	private void drawScaledString(String string, int x, int y, int color, float scale) {
		GLUtil.pushMatrix();
		GLUtil.translate(x, y, 1);
		GLUtil.scale(scale, scale, scale);
		The5zigMod.getVars().drawString(string, 0, 0, color);
		GLUtil.popMatrix();
	}

	@Override
	public boolean shouldRender(boolean dummy) {
		if (dummy) {
			return true;
		}
		if (((Boolean) getProperties().getSetting("showSpotifyAlways").get())) {
			return true;
		}
		SpotifyManager manager = The5zigMod.getDataManager().getSpotifyManager();
		return manager != null && manager.getStatus() != null && manager.getStatus().getTrack() != null;
	}

	@Override
	public int getWidth(boolean dummy) {
		return 120;
	}

	@Override
	public int getHeight(boolean dummy) {
		return 28;
	}
}
