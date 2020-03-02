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

import com.jacob.com.Dispatch;

/**
 * Defines a source, playlist or track.
 * <p>
 * An ITObject uniquely identifies a source, playlist, or track in iTunes using
 * four separate IDs. These are runtime IDs, they are only valid while the
 * current instance of iTunes is running.
 * <p>
 * As of iTunes 7.7, you can also identify an ITObject using a 64-bit persistent
 * ID, which is valid across multiple invocations of iTunes.
 * <p>
 * The main use of the ITObject interface is to allow clients to track iTunes
 * database changes using
 * <code>iTunesEventsInterface.onDatabaseChangedEvent()</code>.
 * <p>
 * You can retrieve an ITObject with a specified runtime ID using
 * <code>iTunes.getITObjectByID()</code>.
 * <p>
 * An ITObject will always have a valid, non-zero source ID.
 * <p>
 * An ITObject corresponding to a playlist or track will always have a valid
 * playlist ID. The playlist ID will be zero for a source.
 * <p>
 * An ITObject corresponding to a track will always have a valid track and
 * track database ID. These IDs will be zero for a source or playlist.
 * <p>
 * A track ID is unique within the track's playlist. A track database ID is
 * unique across all playlists. For example, if the same music file is in two
 * different playlists, each of the tracks could have different track IDs, but
 * they will have the same track database ID.
 * <p>
 * An ITObject also has a 64-bit persistent ID which can be used to identify
 * the ITObject across multiple invocations of iTunes.
 */
public class IITObject {

	protected Dispatch object;

	public IITObject(Dispatch d) {
		object = d;
	}

	/**
	 * Returns the JACOB Dispatch object for this object.
	 *
	 * @return Returns the JACOB Dispatch object for this object.
	 */
	public Dispatch fetchDispatch() {
		return object;
	}

	/**
	 * Set the name of the object.
	 *
	 * @param name The new name of the object.
	 */
	public void setName(String name) {
		Dispatch.put(object, "Name", name);
	}

	/**
	 * Returns the name of the object.
	 *
	 * @return Returns the name of the object.
	 */
	public String getName() {
		return Dispatch.get(object, "Name").getString();
	}

	/**
	 * Returns the index of the object in internal application order.
	 *
	 * @return The index of the object in internal application order.
	 */
	public int getIndex() {
		return Dispatch.get(object, "Index").getInt();
	}

	/**
	 * Returns the ID that identifies the source.
	 *
	 * @return Returns the ID that identifies the source.
	 */
	public int getSourceID() {
		return Dispatch.get(object, "SourceID").getInt();
	}

	/**
	 * Returns the ID that identifies the playlist.
	 *
	 * @return Returns the ID that identifies the playlist.
	 */
	public int getPlaylistID() {
		return Dispatch.get(object, "PlaylistID").getInt();
	}

	/**
	 * Returns the ID that identifies the track within the playlist.
	 *
	 * @return Returns the ID that identifies the track within the playlist.
	 */
	public int getTrackID() {
		return Dispatch.get(object, "TrackID").getInt();
	}

	/**
	 * Returns the ID that identifies the track, independent of its playlist.
	 *
	 * @return Returns the ID that identifies the track, independent of its playlist.
	 */
	public int getTrackDatabaseID() {
		return Dispatch.get(object, "TrackDatabaseID").getInt();
	}

}
