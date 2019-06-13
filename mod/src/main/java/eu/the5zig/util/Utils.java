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

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.Version;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

	/**
	 * A Pattern to Match URLs.
	 */
	private static final Pattern INCREMENTAL_PATTERN = Pattern.compile("(?:(https?://[^ ][^ ]*?)(?=[\\.\\?!,;:]?(?:[ \\n]|$)))", Pattern.CASE_INSENSITIVE);

	/**
	 * Appends a new Line to a File.
	 *
	 * @param text The Line that should be appended.
	 * @param file The File.
	 */
	public static void appendToFile(String text, File file) {
		try {
			if (!file.exists() && !file.createNewFile())
				throw new IOException("Could not create File at path: " + file.getPath());
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getPath()), "UTF-8"));
			out.append(text).append('\n');
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String downloadFile(String path) {
		return downloadFile(path, 5000);
	}

	public static String downloadFile(String path, int timeout) {
		HttpURLConnection connection;
		BufferedReader br = null;
		InputStreamReader isr = null;
		try {
			String line;
			StringBuilder buffer = new StringBuilder();
			URL url = new URL(path);
			connection = (HttpURLConnection) (url).openConnection();
			connection.addRequestProperty("User-Agent", "5zig/" + Version.VERSION);
			connection.setConnectTimeout(timeout);
			connection.setReadTimeout(timeout);
			connection.connect();

			int code = connection.getResponseCode();

			if (code == 200) {
				isr = new InputStreamReader(connection.getInputStream(), "UTF-8");
			} else {
				isr = new InputStreamReader(connection.getErrorStream());
			}

			br = new BufferedReader(isr);
			while ((line = br.readLine()) != null) {
				buffer.append(line);
			}
			if (code == 200) {
				return buffer.toString();
			} else {
				System.err.println("Could not download string! Error code " + code + "!");
				return buffer.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Could not download string!");
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (isr != null) {
				try {
					isr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * Compares two version strings.
	 * <p/>
	 * Use this instead of String.compareTo() for a non-lexicographical
	 * comparison that works for version strings. e.g. "1.10".compareTo("1.6").
	 *
	 * @param str1 a string of ordinal numbers separated by decimal points.
	 * @param str2 a string of ordinal numbers separated by decimal points.
	 * @return The result is a negative integer if str1 is _numerically_ less than str2.
	 * The result is a positive integer if str1 is _numerically_ greater than str2.
	 * The result is zero if the strings are _numerically_ equal.
	 * @note It does not work if "1.10" is supposed to be equal to "1.10.0".
	 */
	public static Integer versionCompare(String str1, String str2) {
		String[] vals1 = str1.split("\\.");
		String[] vals2 = str2.split("\\.");
		int i = 0;
		// set index to first non-equal ordinal or length of shortest version string
		while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
			i++;
		}
		// compare first non-equal ordinal number
		if (i < vals1.length && i < vals2.length) {
			int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
			return Integer.signum(diff);
		}
		// the strings are equal or one string is a substring of the other
		// e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
		else {
			return Integer.signum(vals1.length - vals2.length);
		}
	}

	public static void setUI(String className) {
		try {
			UIManager.setLookAndFeel(className);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static File getRunningJar() {
		try {
			return new File(Utils.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
		} catch (Exception e) {
			return null;
		}
	}

	public static String convertStreamToString(InputStream is) throws IOException {
		return new BufferedReader(new InputStreamReader(is)).readLine();
	}

	public static String getShortenedDouble(double value, int decimals) {
		if (decimals == 0)
			return String.valueOf((int) Math.round(value));
		double l = 1;
		for (int i = 0; i < decimals; i++) {
			l *= 1e1;
		}
		return String.valueOf((long) (value * l) / l);
	}

	public static String getShortenedDouble(double value) {
		return getShortenedDouble(value, 2);
	}

	public static String getShortenedFloat(float value, int decimals) {
		if (decimals == 0)
			return String.valueOf(Math.round(value));
		float l = 1;
		for (int i = 0; i < decimals; i++) {
			l *= 1e1;
		}
		return String.valueOf(Math.round(value * l) / l);
	}

	public static String getShortenedFloat(float value) {
		return getShortenedFloat(value, 2);
	}

	/**
	 * Converts a UUID-String without dashes to a UUID
	 *
	 * @param uuid The string to be converted
	 * @return The created UUID.
	 */
	public static UUID getUUID(String uuid) {
		return UUID.fromString(uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" + uuid.substring(16, 20) + "-" + uuid.substring(20, 32));
	}

	/**
	 * Returns a UUID-String without dashes
	 *
	 * @param uuid The UUID that should be replaced.
	 * @return a UUID-String without dashes.
	 */
	public static String getUUIDWithoutDashes(UUID uuid) {
		return uuid.toString().replace("-", "");
	}

	public static String upperToDash(String input) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (Character.isUpperCase(c)) {
				if (i == 0 || input.charAt(i - 1) == '_') {
					result.append(Character.toLowerCase(c));
				} else {
					result.append("_").append(Character.toLowerCase(c));
				}
			} else {
				result.append(c);
			}
		}
		return result.toString();
	}

	/**
	 * Returns the closest value of a List to given Integer.
	 *
	 * @param of The Integer that should be compared with the Lists values.
	 * @param in The List.
	 * @return The closest value to given Integer of the List.
	 */
	public static int closest(int of, List<Integer> in) {
		int min = Integer.MAX_VALUE;
		int closest = of;

		for (int v : in) {
			final int diff = Math.abs(v - of);

			if (diff < min) {
				min = diff;
				closest = v;
			}
		}

		return closest;
	}

	public static float clamp(float f1, float f2, float f3) {
		return f1 < f2 ? f2 : (f1 > f3 ? f3 : f1);
	}

	/**
	 * Formats a String by time.
	 *
	 * @param millis Time in milliseconds.
	 * @return either 'Today' or 'Yesterday' and time in HH:MM-Format or the Date and time in local Format.
	 */
	public static String convertToDate(long millis) {
		Date dateTime = new Date(millis);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateTime);
		Calendar today = Calendar.getInstance();
		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DATE, -1);
		DateFormat timeFormatter = DateFormat.getTimeInstance(DateFormat.SHORT);

		if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
			return "Today " + timeFormatter.format(dateTime);
		} else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
			return "Yesterday " + timeFormatter.format(dateTime);
		} else {
			timeFormatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
			return timeFormatter.format(dateTime);
		}
	}

	public static String convertToDateAndTime(long millis) {
		Date dateTime = new Date(millis);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateTime);
		DateFormat timeFormatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
		return timeFormatter.format(dateTime);
	}

	/**
	 * Formats a String by date.
	 *
	 * @param millis Time in milliseconds.
	 * @return either 'Today' or 'Yesterday' or the Date and time in local Format.
	 */
	public static String convertToDateWithoutTime(long millis) {
		Date dateTime = new Date(millis);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateTime);
		Calendar today = Calendar.getInstance();
		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DATE, -1);

		if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
			return "Today";
		} else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
			return "Yesterday";
		} else {
			DateFormat timeFormatter = DateFormat.getDateInstance(DateFormat.SHORT);
			return timeFormatter.format(dateTime);
		}
	}

	/**
	 * Checks if both times are still the same day.
	 *
	 * @param millis1 The first time in milliseconds.
	 * @param millis2 The second time in milliseconds.
	 * @return if both times are still the same day.
	 */
	public static boolean isSameDay(long millis1, long millis2) {
		Date dateTime1 = new Date(millis1);
		Date dateTime2 = new Date(millis2);
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(dateTime1);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(dateTime2);
		return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) && calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR);
	}

	/**
	 * Creates a String in HH:MM:SS-Format for showing time.
	 *
	 * @param millis Time in milliseconds.
	 * @return a String in HH:MM:SS-Format.
	 */
	public static String convertToTime(long millis) {
		return convertToTime(millis, true);
	}

	/**
	 * Creates a String in HH:MM:SS-Format for showing time.
	 *
	 * @param millis   Time in milliseconds.
	 * @param withInfo true, if should return String with information.
	 * @return a String in HH:MM:SS-Format.
	 */
	public static String convertToTime(long millis, boolean withInfo) {
		return String.format("%02d:%02d:%02d" + (withInfo ? " (HH:MM:SS)" : ""), TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
	}

	public static String convertToClock(long millis) {
		return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis),
				TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
	}

	/**
	 * Creates a String in HH:MM-Format for showing time.
	 *
	 * @param millis Time in milliseconds.
	 * @return a String in HH:MM-Format.
	 */
	public static String convertToTimeWithMinutes(long millis) {
		Date dateTime = new Date(millis);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateTime);
		DateFormat timeFormatter = DateFormat.getTimeInstance(DateFormat.SHORT);
		return timeFormatter.format(dateTime);
	}

	/**
	 * Creates a String in HH:MM:SS-Format for showing time.
	 *
	 * @param millis Time in milliseconds.
	 * @return a String in HH:MM:SS-Format.
	 */
	public static String convertToTimeWithSeconds(long millis) {
		Date dateTime = new Date(millis);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateTime);
		DateFormat timeFormatter = DateFormat.getTimeInstance(DateFormat.MEDIUM);
		return timeFormatter.format(dateTime);
	}

	/**
	 * Creates a String in DD:HH:MM:SS-Format for showing time.
	 *
	 * @param millis Time in milliseconds.
	 * @return a String in DD:HH:MM:SS-Format.
	 */
	public static String convertToTimeWithDays(long millis) {
		return String.format("%d:%02d:%02d:%02d (DD:HH:MM:SS)", TimeUnit.MILLISECONDS.toDays(millis),
				TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis)),
				TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
	}

	public static long parseTimeFormatToMillis(String time) {
		return parseTimeFormatToMillis(time, "HH:mm:ss");
	}

	/**
	 * Parses a Time-String to the duration in milliseconds.
	 *
	 * @param time   The Time-String.
	 * @param format The format of the Time.
	 * @return The duration in milliseconds.
	 */
	public static long parseTimeFormatToMillis(String time, String format) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			Date date = sdf.parse(time);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int hour = calendar.get(Calendar.HOUR);
			int minute = calendar.get(Calendar.MINUTE);
			int second = calendar.get(Calendar.SECOND);
			return 1000 * second + 1000 * 60 * minute + 1000 * 60 * 60 * hour;
		} catch (ParseException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public static void openURL(String path) {
		try {
			Desktop.getDesktop().browse(new URI(path));
		} catch (Throwable e) {
			System.err.println("Couldn't open url: " + path);
			e.printStackTrace();
		}
	}

	public static void openURL(URI uri) {
		try {
			Desktop.getDesktop().browse(uri);
		} catch (Throwable e) {
			System.err.println("Couldn't open url: " + uri.toString());
			e.printStackTrace();
		}
	}

	/**
	 * Opens an URL if one has been found in given input String.
	 *
	 * @param input The input String that should be matched to the URL-Regex.
	 */
	public static void openURLIfFound(String input) {
		List<String> matchedURLs = matchURL(input);
		if (matchedURLs.isEmpty())
			return;
		for (String matchedURL : matchedURLs) {
			openURL(matchedURL);
		}
	}

	/**
	 * Returns a List with all URLs that could been matched from given input String.
	 *
	 * @param input The input String.
	 * @return A List with all URLs that could been matched from given input String.
	 */
	public static List<String> matchURL(String input) {
		List<String> result = new ArrayList<String>();
		Matcher matcher = INCREMENTAL_PATTERN.matcher(input);
		while (matcher.find()) {
			result.add(matcher.group());
		}
		return result;
	}

	/**
	 * Tries to match the message with the match string. Use * in match string will match any characters. Use ? will match a single character.
	 *
	 * @param message The message that should be checked.
	 * @param match   The pattern.
	 * @return {@code true}, if the Pattern matches the message.
	 */
	public static boolean matches(String message, String match) {
		message = message.toLowerCase(Locale.ROOT);
		Pattern pattern = compileMatchPattern(match);
		Matcher matcher = pattern.matcher(message);

		return matcher.matches();
	}

	/**
	 * Tries to match the message with the match string. Use * in match string will match any characters. Use ? will match a single character.
	 *
	 * @param message The message that should be checked.
	 * @param find    The pattern.
	 * @return {@code true}, if the Pattern matches the message.
	 */
	public static boolean contains(String message, String find) {
		message = message.toLowerCase(Locale.ROOT);
		Pattern pattern = compileMatchPattern(find);
		Matcher matcher = pattern.matcher(message);

		return matcher.find();
	}

	public static Pattern compileMatchPattern(String match) {
		// replace duplicate stars
		// match = match.replaceAll("(\\*)\\1+", "*");

		// replace any pair of backslashes by [*]
		match = match.replaceAll("(?<!\\\\)(\\\\\\\\)+(?!\\\\)", "*");
		// minimize unescaped redundant wildcards
		match = match.replaceAll("(?<!\\\\)[?]*[*][*?]+", "*");
		// escape unescaped regexps special chars, but [\], [?] and [*]
		match = match.replaceAll("(?<!\\\\)([|\\[\\]{}(),.^$+-])", "\\\\$1");
		// replace unescaped [?] by [.?]
		match = match.replaceAll("(?<!\\\\)[?]", "(.?)");
		// replace unescaped [*] by [.*]
		match = match.replaceAll("(?<!\\\\)[*]", "(.*)");
		return Pattern.compile(match, Pattern.CASE_INSENSITIVE);
	}

	public static String escapeStringForRegex(String string) {
		// escape unescaped regexps special chars
		string = string.replaceAll("(?<!\\\\)([|\\[\\]{}(),.^$+-]\\?*)", "\\\\$1");

		return string;
	}

	/**
	 * Substrings a String from the position of one of its content-strings.
	 *
	 * @param input The input String that should be substringed.
	 * @param from  The content from which the String should be substringed at.
	 * @return The substringed String.
	 */
	public static String substringFrom(String input, String from) {
		if (!input.contains(from))
			return input;
		int substring = input.indexOf(from);
		return input.substring(substring);
	}

	public static String bytesToReadable(long bytes) {
		final int unit = 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		char pre = "KMGTPE".charAt(exp - 1);
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	/**
	 * Checks if a Char-Sequence is an Integer.
	 *
	 * @param cs The Char-Sequence that should be tested.
	 * @return if the Char-Sequence is an Integer.
	 */
	public static boolean isInt(CharSequence cs) {
		if (cs == null || cs.length() == 0) {
			return false;
		} else {
			int sz = cs.length();

			for (int i = 0; i < sz; ++i) {
				char currentChar = cs.charAt(i);
				if (currentChar == '-' && i == 0 && sz > 1)
					continue;
				if (!Character.isDigit(currentChar)) {
					return false;
				}
			}

			return true;
		}
	}

	public static String loadJson(File fileJson) throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(fileJson));
			String str;
			StringBuilder sb = new StringBuilder();
			while ((str = reader.readLine()) != null) {
				sb.append(str).append("\n");
			}
			return sb.toString();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	public static int getARBGInt(int a, int r, int g, int b) {
		return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF));
	}

	public static void copyToClipboard(String string) {
		StringSelection selection = new StringSelection(string);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(selection, selection);
	}

	public static String getOSName() {
		return System.getProperty("os.name");
	}

	public static String getJava() {
		return System.getProperty("java.version");
	}

	public static String getJavaExecutable() {
		String javaHome = System.getProperty("java.home");
		if (javaHome == null) {
			return "java";
		}
		String path = javaHome;
		switch (getPlatform()) {
			case WINDOWS:
			case MAC:
			case LINUX:
			case SOLARIS:
				path += File.separator + "bin" + File.separator + "java";
				break;
			default:
				return "java";
		}
		return path;
	}

	public static String lineSeparator() {
		return System.getProperty("line.separator");
	}

	public static String lineSeparator(int count) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < count; i++) {
			result.append(lineSeparator());
		}
		return result.toString();
	}

	/**
	 * Gets the current Platform of the Client.
	 *
	 * @return the current Platform of the Client.
	 */
	public static Platform getPlatform() {
		String osName = getOSName().toLowerCase(Locale.ROOT);
		if (osName.contains("win"))
			return Platform.WINDOWS;
		if (osName.contains("mac"))
			return Platform.MAC;
		if (osName.contains("linux") || osName.contains("sunos") || osName.contains("unix"))
			return Platform.LINUX;
		if (osName.contains("solaris"))
			return Platform.SOLARIS;
		return Platform.UNKNOWN;
	}

	/**
	 * Merges two Arrays.
	 *
	 * @param first  The first Array.
	 * @param second The second Array.
	 * @param <T>    The type of both Arrays.
	 * @return a merged Array.
	 */
	public static <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	public static <T> T[] asArray(T... array) {
		return array;
	}

	public static int parseInt(String input) {
		if (input == null || input.isEmpty()) {
			return 0;
		}
		try {
			return Integer.parseInt(input);
		} catch (NumberFormatException ignored) {
			return 0;
		}
	}

	public enum Platform {
		WINDOWS, MAC, LINUX, SOLARIS, UNKNOWN
	}

}