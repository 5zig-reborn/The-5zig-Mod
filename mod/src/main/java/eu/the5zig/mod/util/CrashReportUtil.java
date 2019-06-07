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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static eu.the5zig.mod.util.ReflectionUtil.invoke;
import static eu.the5zig.mod.util.ReflectionUtil.newInstance;

public class CrashReportUtil {

	private static final Method createCrashReport;
	private static final Method createCrashCategory;
	private static final Constructor<?> reportedException;

	static {
		try {
			Class<?> crashReportClass = Class.forName("b");
			createCrashReport = crashReportClass.getMethod("a", Throwable.class, String.class);
			createCrashCategory = crashReportClass.getMethod("a", String.class);
			reportedException = Class.forName("e").getConstructor(crashReportClass);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private CrashReportUtil() {
	}

	public static void makeCrashReport(Throwable throwable, String reason) {
		Object crashReport = invoke(createCrashReport, throwable, reason);
		invoke(crashReport, createCrashCategory, "The 5zig Mod");
		throw (RuntimeException) newInstance(reportedException, crashReport);
	}

}
