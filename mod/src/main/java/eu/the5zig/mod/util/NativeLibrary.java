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

package eu.the5zig.mod.util;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.util.Utils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class NativeLibrary {

	private NativeLibrary() {
	}

	public static String load(String name, NativeOS os) throws IOException, UnsatisfiedLinkError {
		switch (os) {
			case WINDOWS:
			case WINDOWS_32:
			case WINDOWS_64:
				return loadWindows(name, os);
			case UNIX:
				return loadUnix(name);
			default:
				if (Utils.getPlatform() == Utils.Platform.WINDOWS) {
					return loadWindows(name, NativeOS.WINDOWS);
				} else {
					return loadUnix(name);
				}
		}
	}

	private static String loadWindows(String name, NativeOS os) throws IOException {
		if (Utils.getPlatform() != Utils.Platform.WINDOWS) {
			throw new IllegalArgumentException();
		}
		String arch = System.getProperty("sun.arch.data.model");
		if (os != NativeOS.WINDOWS && (os != NativeOS.WINDOWS_32 || !"32".equals(arch)) && (os != NativeOS.WINDOWS_64 || !"64".equals(arch))) {
			throw new IllegalArgumentException();
		}
		return loadLibrary(name.replace("${arch}", arch) + ".dll");
	}

	private static String loadUnix(String name) throws IOException {
		if (Utils.getPlatform() == Utils.Platform.WINDOWS) {
			throw new IllegalArgumentException();
		}
		return loadLibrary(name + ".so");
	}

	private static String loadLibrary(String fileName) throws IOException {
		InputStream resourceAsStream = null;
		byte[] resourceSha1;
		try {
			resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("core/native/" + fileName);
			resourceSha1 = DigestUtils.sha1(resourceAsStream);
		} finally {
			IOUtils.closeQuietly(resourceAsStream);
		}
		File dir = new File(The5zigMod.getModDirectory(), "native");
		if (!dir.exists() && !dir.mkdirs()) {
			throw new IOException("Could not create native directory!");
		}
		File file = new File(dir, fileName);
		InputStream inputStream = null;
		try {
			if (!file.exists() || !Arrays.equals(resourceSha1, DigestUtils.sha1(inputStream = new FileInputStream(file)))) {
				IOUtils.closeQuietly(inputStream);
				resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("core/native/" + fileName);
				FileUtils.copyInputStreamToFile(resourceAsStream, file);
			}
		} finally {
			IOUtils.closeQuietly(inputStream);
			IOUtils.closeQuietly(resourceAsStream);
		}
		String absolutePath = file.getAbsolutePath();
		System.load(absolutePath);

		return absolutePath;
	}

	public enum NativeOS {
		WINDOWS, WINDOWS_32, WINDOWS_64, UNIX, ANY
	}

}
