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

import com.jacob.com.Variant;

public class IiTunesEvents {

	private final IiTunesEventHandler eventHandler;

	public IiTunesEvents(IiTunesEventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}

	public void OnDatabaseChangedEvent(Variant[] args) {
	}

	/**
	 * The ITEventPlayerPlay event is fired when a track begins playing.
	 * <p>
	 * When iTunes switches to playing another track, you will received an ITEventPlayerStop event followed by an ITEventPlayerPlay event, unless it is playing joined CD tracks
	 * (see _IiTunesEvents::OnPlayerPlayingTrackChangedEvent).
	 *
	 * @param args An IITTrack object (a VARIANT of type VT_DISPATCH) corresponding to the track that has started playing.
	 */
	public void OnPlayerPlayEvent(Variant[] args) {
		IITTrack track = new IITTrack(args[0].getDispatch());
		eventHandler.onPlayerPlay(track);
	}

	/**
	 * The ITEventPlayerPlayingTrackChanged event is fired when information about the currently playing track has changed.
	 * <p>
	 * This event is fired when the user changes information about the currently playing track (e.g. the name of the track).
	 * <p>
	 * This event is also fired when iTunes plays the next joined CD track in a CD playlist, since joined CD tracks are treated as a single track.
	 *
	 * @param args An IITTrack object (a VARIANT of type VT_DISPATCH) corresponding to the track that is now playing.
	 */
	public void OnPlayerPlayingTrackChangedEvent(Variant[] args) {
		IITTrack track = new IITTrack(args[0].getDispatch());
		eventHandler.onPlayerPlayingTrackChanged(track);
	}

	/**
	 * The ITEventPlayerStop event is fired when a track stops playing.
	 * <p>
	 * When iTunes switches to playing another track, you will received an ITEventPlayerStop event followed by an ITEventPlayerPlay event, unless it is playing joined CD tracks
	 * (see _IiTunesEvents::OnPlayerPlayingTrackChangedEvent).
	 *
	 * @param args An IITTrack object (a VARIANT of type VT_DISPATCH) corresponding to the track that has stopped playing.
	 */
	public void OnPlayerStopEvent(Variant[] args) {
		IITTrack track = new IITTrack(args[0].getDispatch());
		eventHandler.onPlayerStop(track);
	}

	public void OnAboutToPromptUserToQuitEvent(Variant[] args) {
	}

	public void OnSoundVolumeChangedEvent(Variant[] args) {
	}

	public void OnCOMCallsDisabledEvent(Variant[] args) {
	}

	public void OnCOMCallsEnabledEvent(Variant[] args) {
	}

	/**
	 * The ITEventQuitting event is fired when iTunes is about to quit.
	 * <p>
	 * If the user attempts to quit iTunes while a client still has outstanding iTunes COM objects instantiated, iTunes will display a warning dialog. The user can still choose to quit
	 * iTunes anyway, in which case this event will be fired. After this event is fired, any existing iTunes COM objects will no longer be valid.
	 * <p>
	 * This event is only used to notify clients that iTunes is quitting, clients cannot prevent this from happening.
	 *
	 * @param args empty.
	 */
	public void OnQuittingEvent(Variant[] args) {
		eventHandler.onQuit();
	}


}
