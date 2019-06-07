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

public class SpotifyTrack {

	/**
	 * Information about the track itself.
	 */
	@SerializedName(value = "track_resource")
	private SpotifyResource trackInformation;
	/**
	 * Information about the artist.
	 */
	@SerializedName(value = "artist_resource")
	private SpotifyResource artistInformation;
	/**
	 * Information about the album.
	 */
	@SerializedName(value = "album_resource")
	private SpotifyResource albumInformation;
	/**
	 * The length of this track in seconds.
	 */
	private int length;
	/**
	 * The type of the track. Can be "normal" or "ad"
	 */
	@SerializedName("track_type")
	private Type type;

	/**
	 * A base64 encoded preview image of the track or null, if the image hasn't been fetched yet.
	 */
	private String image;

	public SpotifyTrack() {
	}

	public SpotifyTrack(SpotifyResource trackInformation, SpotifyResource artistInformation, SpotifyResource albumInformation, int length, Type type) {
		this.trackInformation = trackInformation;
		this.artistInformation = artistInformation;
		this.albumInformation = albumInformation;
		this.length = length;
		this.type = type;
	}

	public SpotifyResource getTrackInformation() {
		return trackInformation;
	}

	public SpotifyResource getArtistInformation() {
		return artistInformation;
	}

	public SpotifyResource getAlbumInformation() {
		return albumInformation;
	}

	public int getLength() {
		return length;
	}

	public Type getType() {
		return type;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public boolean hasTrackInformation() {
		return getTrackInformation() != null && getAlbumInformation() != null && getArtistInformation() != null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		SpotifyTrack that = (SpotifyTrack) o;

		if (length != that.length)
			return false;
		if (trackInformation != null ? !trackInformation.equals(that.trackInformation) : that.trackInformation != null)
			return false;
		if (artistInformation != null ? !artistInformation.equals(that.artistInformation) : that.artistInformation != null)
			return false;
		if (albumInformation != null ? !albumInformation.equals(that.albumInformation) : that.albumInformation != null)
			return false;
		return type == that.type;

	}

	@Override
	public int hashCode() {
		int result = trackInformation != null ? trackInformation.hashCode() : 0;
		result = 31 * result + (artistInformation != null ? artistInformation.hashCode() : 0);
		result = 31 * result + (albumInformation != null ? albumInformation.hashCode() : 0);
		result = 31 * result + length;
		result = 31 * result + (type != null ? type.hashCode() : 0);
		return result;
	}

	public enum Type {
		normal, ad;
	}

}
