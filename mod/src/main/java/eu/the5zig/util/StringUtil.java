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

package eu.the5zig.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class StringUtil {

	private StringUtil() {
	}

	/**
	 * Checks for different words of two Strings.
	 *
	 * @param s1 The first String.
	 * @param s2 The second String.
	 * @return A List with all words that are different in String 1.
	 */
	public static List<String> differentWords(String s1, String s2) {
		List<String> result = new ArrayList<String>();
		if (s1 == null && s2 == null) {
			return result;
		}
		if (s1 == null) {
			return Arrays.asList(s2.split(" "));
		}
		if (s2 == null) {
			return Arrays.asList(s1.split(" "));
		}

		List<String> words1 = Arrays.asList(s1.split(" "));
		List<String> words2 = Arrays.asList(s2.split(" "));
		result.addAll(words1);
		result.removeAll(words2);

		return result;
	}
}
