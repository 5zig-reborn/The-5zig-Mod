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

package eu.the5zig.mod.manager.itunes;

public class ITunesStatus {

	private boolean running;
	private boolean playing;
	private ITunesTrack track;
	private double playingPosition;

	private transient long serverTime;

	public ITunesStatus() {
	}

	public ITunesStatus(boolean playing, ITunesTrack track, double playingPosition) {
		this.running = true;
		this.playing = playing;
		this.track = track;
		this.playingPosition = playingPosition;
		this.serverTime = System.currentTimeMillis();
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isPlaying() {
		return playing;
	}

	public ITunesTrack getTrack() {
		return track;
	}

	public double getPlayingPosition() {
		return playingPosition;
	}

	public void setPlayingPosition(double playingPosition) {
		this.playingPosition = playingPosition;
	}

	public long getServerTime() {
		return serverTime;
	}

	public void setServerTime(long serverTime) {
		this.serverTime = serverTime;
	}

	@Override
	public String toString() {
		return "ITunesStatus{" +
				"running=" + running +
				", playing=" + playing +
				", track=" + track +
				", playingPosition=" + playingPosition +
				'}';
	}
}
