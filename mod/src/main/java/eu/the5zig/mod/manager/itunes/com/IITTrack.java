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
 * Represents a track.
 * <p>
 * A track represents a song in a single playlist. A song may be in more than
 * one playlist, in which case it would be represented by multiple tracks.
 * <p>
 * You can retrieve the currently targeted (playing) track using
 * <code>iTunes.getCurrentTrack()</code>.
 * <p>
 * Typically, an ITrack is accessed through an ITTrackCollection.
 * <p>
 * You can retrieve all the tracks defined for a playlist using
 * <code>ITPlaylist.getTracks()</code>.
 * <p>
 * You can retrieve the currently selected track or tracks using
 * <code>iTunes.getSelectedTracks()</code>.
 */
public class IITTrack extends IITObject {

	public IITTrack(Dispatch d) {
		super(d);
	}

	/**
	 * Delete this object.
	 */
	public void delete() {
		Dispatch.call(object, "Delete");
	}

	/**
	 * Start playing this object.
	 */
	public void play() {
		Dispatch.call(object, "Play");
	}

	/**
	 * Set the name of the album containing the object.;
	 *
	 * @param album The new name of the album containing the object.
	 */
	public void setAlbum(String album) {
		Dispatch.put(object, "Album", album);
	}

	/**
	 * Returns the name of the album containing the object.
	 *
	 * @return Returns the name of the album containing the object.
	 */
	public String getAlbum() {
		return Dispatch.get(object, "Album").getString();
	}

	/**
	 * Set the name of the artist/source of the object.
	 *
	 * @param artist The new artist/source of the object.
	 */
	public void setArtist(String artist) {
		Dispatch.put(object, "Artist", artist);
	}

	/**
	 * Returns the name of the artist/source of the object.
	 *
	 * @return Returns the name of the artist/source of the object.
	 */
	public String getArtist() {
		return Dispatch.get(object, "Artist").getString();
	}

	/**
	 * Returns the length of the object (in seconds).
	 *
	 * @return Returns the length of the object (in seconds).
	 */
	public int getDuration() {
		return Dispatch.get(object, "Duration").getInt();
	}

	/**
	 * Returns the length of the object (in MM:SS format).
	 *
	 * @return Returns the length of the object (in MM:SS format).
	 */
	public String getTime() {
		return Dispatch.get(object, "Time").getString();
	}

	/**
	 * Set the start time of the object (in seconds).
	 *
	 * @param start The new start time of the object (in seconds).
	 */
	public void setStart(int start) {
		Dispatch.put(object, "Start", start);
	}

	/**
	 * Returns the start time of the object (in seconds).
	 *
	 * @return Returns the start time of the object (in seconds).
	 */
	public int getStart() {
		return Dispatch.get(object, "Start").getInt();
	}

	/**
	 * Set the stop time of the object (in seconds).
	 *
	 * @param finish The new stop time of the object (in seconds).
	 */
	public void setFinish(int finish) {
		Dispatch.put(object, "Finish", finish);
	}

	/**
	 * Returns the stop time of the object (in seconds).
	 *
	 * @return Returns the stop time of the object (in seconds).
	 */
	public int getFinish() {
		return Dispatch.get(object, "Finish").getInt();
	}

	/**
	 * @return the kind of the track.
	 */
	public IITTrackKind getKind() {
		return IITTrackKind.values()[Dispatch.get(object, "Kind").getInt()];
	}

	/**
	 * Returns the text description of the object (e.g. "AAC audio file").
	 *
	 * @return Returns the text description of the object (e.g. "AAC audio file").
	 */
	public String getKindAsString() {
		return Dispatch.get(object, "KindAsString").getString();
	}

	/**
	 * Set the number of times the object has been played. This property cannot
	 * be set if the object is not playable (e.g. a PDF file).
	 *
	 * @param playedCount The new number of times the object has been played.
	 */
	public void setPlayedCount(int playedCount) {
		Dispatch.put(object, "PlayedCount", playedCount);
	}

	/**
	 * Returns the number of times the object has been played.
	 *
	 * @return Returns the number of times the object has been played.
	 */
	public int getPlayedCount() {
		return Dispatch.get(object, "PlayedCount").getInt();
	}

	/**
	 * Returns the play order index of the object in the owner playlist
	 * (1-based).
	 * You can pass this index to IITTrackCollection::ItemByPlayOrder() for the
	 * collection returned by ITPlaylist::Tracks() to retrieve an ITTrack
	 * object corresponding to this object.
	 *
	 * @return Returns the play order index of the object in the owner playlist.
	 */
	public int getPlayOrderIndex() {
		return Dispatch.get(object, "PlayOrderIndex").getInt();
	}

	/**
	 * @return a collection containing the artwork for the track.
	 */
	public IITArtworkCollection getArtwork() {
		Dispatch art = Dispatch.get(object, "Artwork").toDispatch();
		return new IITArtworkCollection(art);

	}

}
