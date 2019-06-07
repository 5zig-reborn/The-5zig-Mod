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

package eu.the5zig.mod.chat.entity;

public class ImageMessage extends FileMessage {

	public ImageMessage(Conversation conversation, int id, String username, String message, long time, MessageType type) {
		super(conversation, id, username, message, time, type);
	}

	public ImageMessage(Conversation conversation, int id, String username, ImageData imageData, long time, MessageType type) {
		super(conversation, id, username, imageData, time, type);
	}

	@Override
	protected Class<? extends FileData> getDataClass() {
		return ImageData.class;
	}

	public static class ImageData extends FileData {

		private int width;
		private int height;
		private int realWidth, realHeight;

		public ImageData() {
			super();
			width = 100;
			height = 50;
		}

		public ImageData(Status status) {
			super(status);
			width = 100;
			height = 50;
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		public int getRealWidth() {
			return realWidth;
		}

		public void setRealWidth(int realWidth) {
			this.realWidth = realWidth;
		}

		public int getRealHeight() {
			return realHeight;
		}

		public void setRealHeight(int realHeight) {
			this.realHeight = realHeight;
		}
	}
}
