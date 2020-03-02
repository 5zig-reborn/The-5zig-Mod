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

package eu.the5zig.mod.chat.network.filetransfer;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.entity.FileMessage;
import eu.the5zig.util.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;

public class FileDownloadTask {

	private final File file;
	private final int parts;
	private final int chunkSize;
	private final FileMessage message;
	private final long totalLength;

	private final Object LOCK = new Object();

	private int partCount;
	private long lengthCount;

	private OutputStream out;

	public FileDownloadTask(int parts, int chunkSize, FileMessage message) throws IOException {
		this.parts = parts;
		this.chunkSize = chunkSize;
		this.totalLength = message.getFileData().getLength();
		this.message = message;
		File dir = FileUtils.createDir(new File(The5zigMod.getModDirectory(),
				"media/" + The5zigMod.getDataManager().getUniqueId().toString() + "/" + The5zigMod.getDataManager().getFileTransferManager().getFileName(message)));
		this.file = new File(dir, message.getFileData().getFileId() + ".part");
		file.deleteOnExit();

		try {
			out = new FileOutputStream(file);
		} catch (IOException e) {
			close();
			throw e;
		}
	}

	public void handle(int partId, byte[] data) throws IOException, FileTransferException, NoSuchAlgorithmException {
		synchronized (LOCK) {
			if (partCount++ != partId)
				throw new FileTransferException("Illegal part received (out of order?!)");
			if (data.length > chunkSize)
				throw new FileTransferException("Illegal chunk length!");

			out.write(data, 0, data.length);

			lengthCount += data.length;
			if (partCount == parts && lengthCount != totalLength)
				throw new FileTransferException();

			if (!hasFinished()) {
				message.setPercentage((float) partId / (float) parts);
			} else {
				close();
				message.setPercentage(1);
				message.getFileData().setHash(FileTransferManager.sha1(file));
				message.saveData();
			}
		}
	}

	public boolean hasFinished() {
		synchronized (LOCK) {
			return partCount == parts && lengthCount == totalLength;
		}
	}

	public FileMessage getMessage() {
		return message;
	}

	private void close() {
		IOUtils.closeQuietly(out);
	}
}
