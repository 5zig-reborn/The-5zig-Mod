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

package eu.the5zig.util.io;

import eu.the5zig.util.Callback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class NotifiableFileCopier {

	private final File[] src;
	private final File dest;
	private final Callback<Float> callback;

	private NotifiableFileCopier(Callback<Float> callback, File[] src, File dest) throws IOException {
		this.callback = callback;
		this.src = src;
		this.dest = dest;
		run();
	}

	public static NotifiableFileCopier copy(Callback<Float> callback, File[] src, File dest) throws IOException {
		return new NotifiableFileCopier(callback, src, dest);
	}

	public static NotifiableFileCopier copy(File[] src, File dest) throws IOException {
		return copy(null, src, dest);
	}

	public void run() throws IOException {
		long fileSize = 0;
		for (File file : src) {
			fileSize += file.length();
		}

		for (File file : src) {
			FileInputStream input = null;
			FileOutputStream output = null;
			try {
				input = new FileInputStream(file);
				output = new FileOutputStream(dest);
				byte[] buf = new byte[8192];
				int bytesRead;
				while ((bytesRead = input.read(buf)) > 0) {
					output.write(buf, 0, bytesRead);
					if (callback != null)
						callback.call((float) dest.length() / (float) fileSize);
				}
			} finally {
				if (input != null)
					input.close();
				if (output != null)
					output.close();
			}
		}
	}

}
