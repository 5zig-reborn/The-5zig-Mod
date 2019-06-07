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

package eu.the5zig.mod.manager.spotify;

import com.google.gson.annotations.SerializedName;

public class SpotifyStatus {

	/**
	 * The Protocol Version id.
	 */
	private int version;
	/**
	 * The Version String of the Spotify Client.
	 */
	private String clientVersion;

	/**
	 * Indicates whether Spotify is currently playing a song.
	 */
	private boolean playing;
	/**
	 * Indicates whether shuffle-mode is enabled.
	 */
	private boolean shuffle;
	/**
	 * Indicates whether the playlist will be repeated.
	 */
	private boolean repeat;

	/**
	 * Indicates whether the client is allowed to play a track.
	 */
	@SerializedName(value = "play_enabled")
	private boolean playEnabled;
	/**
	 * Indicates whether the client is allowed to replay the previous track.
	 */
	@SerializedName(value = "prev_enabled")
	private boolean prevEnabled;
	/**
	 * Indicates whether the client is allowed to skip the current track.
	 */
	@SerializedName(value = "next_enabled")
	private boolean nextEnabled;

	/**
	 * Contains all information about the currently selected track.
	 */
	private SpotifyTrack track;

	/**
	 * The position of the currently selected track in seconds from the beginning.
	 */
	@SerializedName(value = "playing_position")
	private float playingPosition;
	/**
	 * The current volume-amount, from 0 to 1.
	 */
	private float volume;

	/**
	 * The unix timestamp of the spotify http server.
	 */
	@SerializedName(value = "server_time")
	private long serverTime;
	/**
	 * Indicates whether the Spotify client is currently connected to the internet.
	 */
	private boolean online;
	/**
	 * Indicates whether the Spotify client is currently running.
	 */
	private boolean running;

	public int getVersion() {
		return version;
	}

	public String getClientVersion() {
		return clientVersion;
	}

	public boolean isPlaying() {
		return playing;
	}

	public boolean isShuffle() {
		return shuffle;
	}

	public boolean isRepeat() {
		return repeat;
	}

	public boolean isPlayEnabled() {
		return playEnabled;
	}

	public boolean isPrevEnabled() {
		return prevEnabled;
	}

	public boolean isNextEnabled() {
		return nextEnabled;
	}

	public SpotifyTrack getTrack() {
		return track;
	}

	public float getPlayingPosition() {
		return playingPosition;
	}

	public float getVolume() {
		return volume;
	}

	public long getServerTime() {
		return serverTime;
	}

	public void setServerTime(long serverTime) {
		this.serverTime = serverTime;
	}

	public boolean isOnline() {
		return online;
	}

	public boolean isRunning() {
		return running;
	}
}
