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

/**
 * Represents a artwork.
 * <p>
 * Defines a single piece of artwork.
 * <p>
 * Artwork is always associated with an individual track.
 * To add a piece of artwork to a track, use IITTrack::AddArtworkFromFile().
 * The IITTrack::Artwork property
 * <p>
 * To get a collection of artwork associated with a track call
 * <code>ITTrack.getArtwork()</code>.
 */
public class IITArtwork extends IITObject {

	public IITArtwork(Dispatch d) {
		super(d);
	}

	/**
	 * Delete this object.
	 */
	public void delete() {
		Dispatch.call(object, "Delete");
	}

	/**
	 * Returns the kind of the object.
	 *
	 * @return Returns the kind of the object.
	 */
	public ITArtworkFormat getFormat() {
		return ITArtworkFormat.values()[Dispatch.get(object, "Format").getInt()];
	}

	/**
	 * @return true if the artwork was downloaded by iTunes.
	 */
	public boolean getIsDownloadedArtwork() {
		return Dispatch.get(object, "IsDownloadedArtwork").getBoolean();
	}

	/**
	 * @return the description for the artwork. Artwork descriptions are only supported in files that use ID3 tags (i.e. MP3 files).
	 */
	public String getDescription() {
		return Dispatch.get(object, "Description").getString();
	}

	/**
	 * Save artwork data to an image file.
	 * <p>
	 * The format of the saved data is specified by the artwork's format (JPEG, PNG, or BMP). The directory that contains the file must already exist, it will not be created. If the file
	 * already exists, its contents will be replaced.
	 *
	 * @param filePath Full path to the artwork image file.
	 */
	public void SaveArtworkToFile(String filePath) {
		Dispatch.call(object, "SaveArtworkToFile", filePath);
	}

}
