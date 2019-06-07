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

public class ITunesTrack {

	private String id;
	private String name;
	private String artist;
	private double length;
	private String artwork;

	private transient String image;

	public ITunesTrack() {
	}

	public ITunesTrack(String id, String name, String artist, double length) {
		this.id = id;
		this.name = name;
		this.artist = artist;
		this.length = length;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getArtist() {
		return artist;
	}

	public double getLength() {
		return length;
	}

	public String getArtwork() {
		return artwork;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public boolean hasTrackInformation() {
		return id != null && name != null && artist != null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		ITunesTrack that = (ITunesTrack) o;

		return id != null ? id.equals(that.id) : that.id == null;

	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "ITunesTrack{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", artist='" + artist + '\'' +
				", length=" + length +
				'}';
	}
}
