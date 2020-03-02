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

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class FileUtils {

	private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static File createDir(File dir) throws IOException {
		if (dir.exists())
			return dir;

		if (!dir.mkdirs()) {
			throw new IOException("Could not create Directory: " + dir.getPath());
		}
		return dir;
	}

	public static File createFile(File file) throws IOException {
		if (file.exists()) {
			return file;
		}
		if (!file.createNewFile()) {
			throw new IOException("Could not create File: " + file.getPath());
		}
		return file;
	}

	public static boolean deleteDirectory(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (String aChildren : children) {
				boolean success = deleteDirectory(new File(dir, aChildren));
				if (!success) {
					return false;
				}
			}
		}
		// The directory is now empty or this is a file so delete it
		return dir.delete();
	}

	public static String readFile(File file) throws IOException {
		FileInputStream inputStream = null;
		BufferedReader reader = null;
		try {
			inputStream = new FileInputStream(file);
			reader = new BufferedReader(new InputStreamReader(inputStream));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append('\n').append(line);
			}
			sb.delete(0, 1);
			return sb.toString();
		} catch (IOException e) {
			throw new IOException(e);
		} finally {
			if (reader != null) {
				reader.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

	public static void writeFile(File file, String toWrite) throws IOException {
		FileOutputStream outputStream = null;
		BufferedWriter writer = null;
		try {
			outputStream = new FileOutputStream(file);
			writer = new BufferedWriter(new OutputStreamWriter(outputStream));
			writer.write(toWrite);
		} catch (IOException e) {
			throw new IOException(e);
		} finally {
			if (writer != null) {
				writer.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}

	public static URL getClassLoaderURL() throws URISyntaxException, IOException {
		URLClassLoader classLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
		for (URL url : classLoader.getURLs()) {
			File f = new File(url.toURI().getPath());
			ZipFile zipFile = new ZipFile(f);
			ZipEntry entry = zipFile.getEntry("eu/the5zig/util/io/FileUtils.class");
			zipFile.close();
			if (entry != null) {
				return url;
			}
		}
		throw new RuntimeException("Current Class Loader Context not found!");
	}

	public static String hexDigest(String data) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-1");
			digest.reset();
			digest.update(data.getBytes("UTF-8"));
		} catch (Exception e) {
			throw new RuntimeException("Could not generate hex digest", e);
		}
		byte[] hash = digest.digest();
		boolean negative = (hash[0] & 0x80) == 0x80;
		if (negative)
			hash = twosCompliment(hash);
		String digests = getHexString(hash);
		if (digests.startsWith("0")) {
			digests = digests.replaceFirst("0", digests);
		}
		if (negative) {
			digests = "-" + digests;
		}
		digests = digests.toLowerCase(Locale.ROOT);
		return digests;
	}

	public static String getHexString(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	private static byte[] twosCompliment(byte[] p) {
		int i;
		boolean carry = true;
		for (i = p.length - 1; i >= 0; i--) {
			p[i] = (byte) ~p[i];
			if (carry) {
				carry = p[i] == 0xFF;
				p[i]++;
			}
		}
		return p;
	}

	public static void downloadToFile(String path, File dest) throws IOException {
		URL url = new URL(path);
		ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		FileOutputStream fos = new FileOutputStream(dest);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
	}

	public static String md5(File file) throws NoSuchAlgorithmException, IOException {
		return md5(file, true);
	}

	public static String md5(File file, boolean close) throws NoSuchAlgorithmException, IOException {
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			return md5(is);
		} finally {
			if (is != null && close) {
				try {
					is.close();
				} catch (IOException ignored) {
				}
			}
		}
	}

	public static String md5(InputStream is) throws NoSuchAlgorithmException, IOException {
		return md5(is, true);
	}

	public static String md5(InputStream is, boolean close) throws NoSuchAlgorithmException, IOException {
		return hash("MD5", is, close);
	}

	public static String hash(String algorithm, InputStream is, boolean close) throws NoSuchAlgorithmException, IOException {
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			byte[] buffer = new byte[1024 * 8];
			int bytes;
			while ((bytes = is.read(buffer)) > 0) {
				md.update(buffer, 0, bytes);
			}
			byte[] digest = md.digest();
			StringBuilder hexString = new StringBuilder();
			for (byte aDigest : digest) {
				hexString.append(Integer.toHexString(0xFF & aDigest));
			}
			return hexString.toString();
		} finally {
			if (close) {
				if (is != null) {
					try {
						is.close();
					} catch (IOException ignored) {
					}
				}
			}
		}
	}
}
