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

/**
 * A class representing a resource (eg. track, artist or album information)
 */
public class SpotifyResource {

	/**
	 * The name of the resource.
	 */
	private String name;
	/**
	 * The Spotify-URI of the resource.
	 */
	private String uri;
	/**
	 * The location of the resource.
	 */
	private Location location;

	public SpotifyResource() {
	}

	public SpotifyResource(String name, String uri, Location location) {
		this.name = name;
		this.uri = uri;
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		if (uri == null || !uri.startsWith("spotify:")) {
			return null;
		}
		String[] split = uri.split(":");
		if (split.length != 3) {
			return null;
		}
		return split[2];
	}

	public String getUri() {
		return uri;
	}

	public Location getLocation() {
		return location;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		SpotifyResource that = (SpotifyResource) o;

		if (name != null ? !name.equals(that.name) : that.name != null)
			return false;
		if (uri != null ? !uri.equals(that.uri) : that.uri != null)
			return false;
		return location != null ? location.equals(that.location) : that.location == null;

	}

	@Override
	public int hashCode() {
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (uri != null ? uri.hashCode() : 0);
		result = 31 * result + (location != null ? location.hashCode() : 0);
		return result;
	}

	public class Location {
		private String og;

		public String getOg() {
			return og;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			Location location = (Location) o;

			return og != null ? og.equals(location.og) : location.og == null;

		}

		@Override
		public int hashCode() {
			return og != null ? og.hashCode() : 0;
		}
	}

}
