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

import java.io.IOException;
import java.io.OutputStream;
import java.util.jar.JarOutputStream;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class CountingJarOutputStream extends JarOutputStream {

	private long count;

	public CountingJarOutputStream(OutputStream out) throws IOException {
		super(out);
	}

	public long getCount() {
		return count;
	}

	public void write(int b) throws IOException {
		super.write(b);
		count++;
	}

	public void write(byte b[]) throws IOException {
		super.write(b);
		count += b.length;
	}

	public void write(byte b[], int off, int len) throws IOException {
		super.write(b, off, len);
		count += len;
	}
}
