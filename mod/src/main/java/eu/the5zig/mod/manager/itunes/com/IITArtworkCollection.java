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

package eu.the5zig.mod.manager.itunes.com;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 * Represents a collection of Artwork objects.
 * <p>
 * Note that collection indices are always 1-based.
 * <p>
 * You can retrieve all the Artworks defined for a source using
 * <code>ITSource.getArtwork()</code>.
 */
public class IITArtworkCollection {

	protected Dispatch object;

	public IITArtworkCollection(Dispatch d) {
		object = d;
	}

	/**
	 * Returns the number of playlists in the collection.
	 *
	 * @return Returns the number of playlists in the collection.
	 */
	public int getCount() {
		return Dispatch.get(object, "Count").getInt();
	}

	/**
	 * Returns an ITArtwork object corresponding to the given index (1-based).
	 *
	 * @param index Index of the playlist to retrieve, must be less than or
	 *              equal to <code>ITArtworkCollection.getCount()</code>.
	 * @return Returns an ITArtwork object corresponding to the given index.
	 * Will be set to NULL if no playlist could be retrieved.
	 */
	public IITArtwork getItem(int index) {
		Variant variant = Dispatch.call(object, "Item", index);
		if (variant.isNull()) {
			return null;
		}
		Dispatch item = variant.toDispatch();
		return new IITArtwork(item);
	}

	/**
	 * Returns an ITArtwork object with the specified persistent ID. See the
	 * documentation on ITObject for more information on persistent IDs.
	 *
	 * @param highID The high 32 bits of the 64-bit persistent ID.
	 * @param lowID  The low 32 bits of the 64-bit persistent ID.
	 * @return Returns an ITArtwork object with the specified persistent ID.
	 * Will be set to NULL if no playlist could be retrieved.
	 */
	public IITArtwork getItemByPersistentID(int highID, int lowID) {
		Variant variant = Dispatch.call(object, "ItemByPersistentID", highID, lowID);
		if (variant.isNull()) {
			return null;
		}
		Dispatch item = variant.toDispatch();
		return new IITArtwork(item);
	}
}
