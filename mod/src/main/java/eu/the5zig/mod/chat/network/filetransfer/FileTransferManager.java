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

package eu.the5zig.mod.chat.network.filetransfer;

import com.google.common.collect.Maps;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.entity.ConversationChat;
import eu.the5zig.mod.chat.entity.ConversationGroupChat;
import eu.the5zig.mod.chat.entity.FileMessage;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class FileTransferManager {

	private HashMap<Integer, FileUploadTask> uploadTasks = Maps.newHashMap();
	private HashMap<Integer, FileDownloadTask> downloadTasks = Maps.newHashMap();

	public void initFileUpload(FileMessage message) throws IOException {
		uploadTasks.put(message.getFileData().getFileId(), new FileUploadTask(message.getFileData().getFileId(), message.getFileData().getHash(), message));
	}

	public void startUpload(int fileId) {
		if (!uploadTasks.containsKey(fileId))
			return;
		try {
			uploadTasks.get(fileId).initSend();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void abortUpload(int fileId) {
		if (!uploadTasks.containsKey(fileId))
			return;
		uploadTasks.get(fileId).abortUpload();
	}

	public void initFileDownload(int fileId, int parts, int chunkSize, FileMessage message) throws IOException {
		downloadTasks.put(fileId, new FileDownloadTask(parts, chunkSize, message));
	}

	public boolean handleChunkDownload(Integer fileId, int partId, byte[] data, FileMessage message) throws IOException, FileTransferException, NoSuchAlgorithmException {
		if (!downloadTasks.containsKey(fileId))
			return false;
		FileDownloadTask task = downloadTasks.get(fileId);
		task.handle(partId, data);
		if (task.hasFinished()) {
			downloadTasks.remove(fileId);
			File partFile = new File(The5zigMod.getModDirectory(), "media/" + The5zigMod.getDataManager().getUniqueId().toString() + "/" + getFileName(message) + "/" + fileId + ".part");
			File mediaFile = new File(The5zigMod.getModDirectory(), "media/" + The5zigMod.getDataManager().getUniqueId().toString() + "/" + getFileName(message) + "/" + sha1(partFile));
			if (!mediaFile.exists())
				org.apache.commons.io.FileUtils.moveFile(partFile, mediaFile);
			else
				org.apache.commons.io.FileUtils.deleteQuietly(partFile);
			return true;
		}
		return false;
	}

	public void abortDownload(final Integer fileId) {
		if (!downloadTasks.containsKey(fileId))
			return;
		final FileDownloadTask remove = downloadTasks.remove(fileId);
		The5zigMod.getAsyncExecutor().execute(new Runnable() {
			@Override
			public void run() {
				File partFile = new File(The5zigMod.getModDirectory(),
						"media/" + The5zigMod.getDataManager().getUniqueId().toString() + "/" + getFileName(remove.getMessage()) + "/" + fileId + "" + ".part");
				org.apache.commons.io.FileUtils.deleteQuietly(partFile);
			}
		});
	}

	public String getFileName(FileMessage message) {
		if (message.getConversation() instanceof ConversationChat) {
			return ((ConversationChat) message.getConversation()).getFriendUUID().toString();
		} else if (message.getConversation() instanceof ConversationGroupChat) {
			return String.valueOf(((ConversationGroupChat) message.getConversation()).getGroupId());
		}
		return "";
	}

	public void cleanUp(File dir) {
		File[] files = dir.listFiles();
		if (files == null)
			return;
		for (File file : files) {
			if (file.isDirectory())
				cleanUp(file);
			else if (file.getName().endsWith(".part"))
				org.apache.commons.io.FileUtils.deleteQuietly(file);
		}
	}

	public static String sha1(File file) {
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			return DigestUtils.sha1Hex(is);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}
}
