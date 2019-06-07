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

package eu.the5zig.mod.server.hypixel.api;

public class HypixelAPIResponseException extends HypixelAPIException {

	private int errorCode;
	private String errorMessage;

	public HypixelAPIResponseException(int errorCode, String errorMessage) {
		super("Could not fetch data from API: " + errorMessage + " (error code " + errorCode + ")");
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public HypixelAPIResponseException(String message) {
		super("Could not fetch data from API: " + message);
		this.errorCode = -1;
		this.errorMessage = message;
	}

	public HypixelAPIResponseException(Throwable cause) {
		this(cause.toString());
	}

	public int getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
