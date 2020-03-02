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

package eu.the5zig.util.io;

import eu.the5zig.util.Callback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class NotifiableInputStreamCopier implements Runnable {

	private final InputStream[] src;
	private final File dest;
	private final Callback<Float> callback;

	private NotifiableInputStreamCopier(Callback<Float> callback, InputStream[] src, File dest) {
		this.callback = callback;
		this.src = src;
		this.dest = dest;
		run();
	}

	@Override
	public void run() {
		long fileSize = 0;
		for (InputStream is : src) {
			try {
				fileSize += is.available();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		for (InputStream is : src) {
			FileOutputStream output = null;
			try {
				output = new FileOutputStream(dest);
				byte[] buf = new byte[8192];
				int bytesRead;
				while ((bytesRead = is.read(buf)) > 0) {
					output.write(buf, 0, bytesRead);
					if (callback != null)
						callback.call((float) dest.length() / (float) fileSize);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (output != null) {
					try {
						output.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static NotifiableInputStreamCopier copy(Callback<Float> callback, InputStream[] src, File dest) {
		return new NotifiableInputStreamCopier(callback, src, dest);
	}


}
