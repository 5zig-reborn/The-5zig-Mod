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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class NotifiableJarCopier {

	private final File[] jarFiles;
	private final File destinationJar;
	private final Callback<Float> callback;
	private long length;

	private NotifiableJarCopier(File[] jarFiles, File destinationJar, Callback<Float> callback) throws IOException {
		this.jarFiles = jarFiles;
		this.destinationJar = destinationJar;
		this.callback = callback;
		for (File jarFile : jarFiles) {
			JarFile file = new JarFile(jarFile);
			Enumeration entries = file.entries();
			while (entries.hasMoreElements()) {
				length += ((JarEntry) entries.nextElement()).getSize();
			}
		}
		run();
	}

	public static NotifiableJarCopier copy(File[] jarFiles, File destinationJar, Callback<Float> callback) throws IOException {
		return new NotifiableJarCopier(jarFiles, destinationJar, callback);
	}

	public static NotifiableJarCopier copy(File[] jarFiles, File destinationJar) throws IOException {
		return copy(jarFiles, destinationJar, null);
	}

	public void run() throws IOException {
		CountingJarOutputStream tempJar = null;
		try {
			tempJar = new CountingJarOutputStream(new FileOutputStream(destinationJar));
			ArrayList<String> entryList = new ArrayList<String>();

			int j = jarFiles.length;

			for (int i = 0; i < j; i++) {
				File jarFile = jarFiles[i];
				JarFile jar = null;
				try {
					jar = new JarFile(jarFile);

					for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements(); ) {
						JarEntry entry = new JarEntry(entries.nextElement().getName());
						if (!entryList.contains(entry.getName())) {
							entryList.add(entry.getName());
							if ((i == 0) || (!entry.getName().startsWith("META-INF/"))) {

								InputStream entryStream = jar.getInputStream(entry);

								tempJar.putNextEntry(entry);
								int bytesRead;
								byte[] buffer = new byte[1024 * 8];
								while ((bytesRead = entryStream.read(buffer)) > 0) {
									tempJar.write(buffer, 0, bytesRead);
									if (callback != null)
										callback.call((float) tempJar.getCount() / (float) length);
								}
								entryStream.close();
								tempJar.flush();
								tempJar.closeEntry();
							}
						}
					}
				} finally {
					if (jar != null)
						jar.close();
				}
			}
		} finally {
			if (tempJar != null)
				tempJar.close();
		}
	}
}
