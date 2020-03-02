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

package eu.the5zig.mod.manager.itunes.com;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.DispatchEvents;
import com.jacob.com.Variant;

/**
 * Defines the top-level iTunes application object.
 * <p>
 * This interface defines the top-level iTunes application object. All other iTunes interfaces are accessed through this object.
 */
public class IiTunes {

	private ActiveXComponent iTunes;
	private DispatchEvents dispatchEvents;

	/**
	 * Initiate iTunes Controller.
	 */
	public IiTunes() {
		iTunes = new ActiveXComponent("iTunes.Application");
	}

	/**
	 * Sets an event handler to the iTunes controller.
	 *
	 * @param eventHandler The class that will handle the iTunes events.
	 */
	public void addEventHandler(IiTunesEventHandler eventHandler) {
		IiTunesEvents iTunesEvents = new IiTunesEvents(eventHandler);
		dispatchEvents = new DispatchEvents(iTunes, iTunesEvents);
	}

	/**
	 * Play the currently targeted track.
	 */
	public void play() {
		iTunes.invoke("Play");
	}

	/**
	 * Pause playback.
	 */
	public void pause() {
		iTunes.invoke("Pause");
	}

	/**
	 * Toggle the playing/paused state of the current track.
	 */
	public void playPause() {
		iTunes.invoke("PlayPause");
	}

	/**
	 * Disable fast forward/rewind and resume playback, if playing.
	 */
	public void resume() {
		iTunes.invoke("Resume");
	}

	/**
	 * Stop playback.
	 */
	public void stop() {
		iTunes.invoke("Stop");
	}

	/**
	 * Reposition to the beginning of the current track or go to the previous
	 * track if already at start of current track.
	 */
	public void backTrack() {
		iTunes.invoke("BackTrack");
	}

	/**
	 * Skip forward in a playing track.
	 */
	public void fastForward() {
		iTunes.invoke("FastForward");
	}

	/**
	 * Advance to the next track in the current playlist.
	 */
	public void nextTrack() {
		iTunes.invoke("NextTrack");
	}

	/**
	 * Skip backwards in a playing track.
	 */
	public void rewind() {
		iTunes.invoke("Rewind");
	}

	/**
	 * Return to the previous track in the current playlist.
	 */
	public void previousTrack() {
		iTunes.invoke("PreviousTrack");
	}

	/**
	 * Returns the currently targetd track.
	 *
	 * @return An ITTrack object corresponding to the currently targeted track.
	 * Will be set to NULL if there is no currently targeted track.
	 */
	public IITTrack getCurrentTrack() {
		Variant variant = iTunes.getProperty("CurrentTrack");
		if (variant.isNull()) {
			return null;
		}
		Dispatch item = variant.toDispatch();
		IITTrack track = new IITTrack(item);
		if (track.getKind() == IITTrackKind.ITTrackKindFile) {
			return new IITFileOrCDTrack(item);
		} else if (track.getKind() == IITTrackKind.ITTrackKindCD) {
			return new IITFileOrCDTrack(item);
		} else if (track.getKind() == IITTrackKind.ITTrackKindURL) {
			return new ITURLTrack(item);
		} else {
			return track;
		}
	}

	/**
	 * Returns the current player state.
	 *
	 * @return Returns the current player state.
	 */
	public IITPlayerState getPlayerState() {
		return IITPlayerState.values()[Dispatch.get(iTunes, "PlayerState").getInt()];
	}

	/**
	 * Sets the player's position within the currently playing track in
	 * seconds.
	 * If playerPos specifies a position before the beginning of the track,
	 * the position will be set to the beginning. If playerPos specifies a
	 * position after the end of the track, the position will be set to the
	 * end.
	 *
	 * @param playerPos The player's position within the currently playing
	 *                  track in seconds.
	 */
	public void setPlayerPosition(int playerPos) {
		iTunes.setProperty("playerPosition", playerPos);
	}

	/**
	 * Returns the player's position within the currently playing track in
	 * seconds.
	 *
	 * @return The player's position within the currently playing track in
	 * seconds.
	 */
	public int getPlayerPosition() {
		return iTunes.getPropertyAsInt("playerPosition");
	}

	/**
	 * Exits the iTunes application.
	 */
	public void quit() {
		iTunes.invoke("Quit");
	}

	public void release() {
		dispatchEvents.safeRelease();
		iTunes.safeRelease();
	}

}
