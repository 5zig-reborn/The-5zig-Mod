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

package eu.the5zig.mod.manager.einslive;

public class ITunesSearchResultEntry {

	private String wrapperType;
	private String kind;
	private int artistId;
	private int collectionId;
	private int trackId;
	private String artistName;
	private String collectionName;
	private String trackName;
	private String collectionCensoredName;
	private String trackCensoredName;
	private String artistViewUrl;
	private String collectionViewUrl;
	private String trackViewUrl;
	private String previewUrl;
	private String artworkUrl30;
	private String artworkUrl60;
	private String artworkUrl100;
	private float collectionPrice;
	private float trackPrice;
	private String releaseDate;
	private String collectionExplicitness;
	private String trackExplicitness;
	private int discCount;
	private int discNumber;
	private int trackCount;
	private int trackNumber;
	private long trackTimeMillis;
	private String country;
	private String currency;
	private String primaryGenreName;
	private boolean isStreamable;

	private transient String image;

	public ITunesSearchResultEntry(String trackName) {
		this.trackName = trackName;
	}

	public String getWrapperType() {
		return wrapperType;
	}

	public String getKind() {
		return kind;
	}

	public int getArtistId() {
		return artistId;
	}

	public int getCollectionId() {
		return collectionId;
	}

	public int getTrackId() {
		return trackId;
	}

	public String getArtistName() {
		return artistName;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public String getTrackName() {
		return trackName;
	}

	public String getCollectionCensoredName() {
		return collectionCensoredName;
	}

	public String getTrackCensoredName() {
		return trackCensoredName;
	}

	public String getArtistViewUrl() {
		return artistViewUrl;
	}

	public String getCollectionViewUrl() {
		return collectionViewUrl;
	}

	public String getTrackViewUrl() {
		return trackViewUrl;
	}

	public String getPreviewUrl() {
		return previewUrl;
	}

	public String getArtworkUrl30() {
		return artworkUrl30;
	}

	public String getArtworkUrl60() {
		return artworkUrl60;
	}

	public String getArtworkUrl100() {
		return artworkUrl100;
	}

	public float getCollectionPrice() {
		return collectionPrice;
	}

	public float getTrackPrice() {
		return trackPrice;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public String getCollectionExplicitness() {
		return collectionExplicitness;
	}

	public String getTrackExplicitness() {
		return trackExplicitness;
	}

	public int getDiscCount() {
		return discCount;
	}

	public int getDiscNumber() {
		return discNumber;
	}

	public int getTrackCount() {
		return trackCount;
	}

	public int getTrackNumber() {
		return trackNumber;
	}

	public long getTrackTimeMillis() {
		return trackTimeMillis;
	}

	public String getCountry() {
		return country;
	}

	public String getCurrency() {
		return currency;
	}

	public String getPrimaryGenreName() {
		return primaryGenreName;
	}

	public boolean isStreamable() {
		return isStreamable;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
}
