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

package eu.the5zig.mod.server;

import com.google.common.collect.Lists;
import eu.the5zig.util.Callback;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.regex.Pattern;

public class MultiLineIgnore {

	private final RegisteredServerInstance instance;

	private String startMessage;
	private String endMessage;

	private Pattern startPattern;
	private int numberOfMessages;

	private Pattern abort;
	private Callback<IMultiPatternResult> callback;

	private boolean startedListening = false;
	private final List<String> messages = Lists.newArrayList();

	public MultiLineIgnore(RegisteredServerInstance instance, String startMessage, String endMessage, Callback<IMultiPatternResult> callback) {
		this.instance = instance;
		this.startMessage = startMessage;
		this.endMessage = endMessage;
		this.callback = callback;
	}

	public MultiLineIgnore(RegisteredServerInstance instance, Pattern startPattern, int numberOfMessages, Pattern abort, Callback<IMultiPatternResult> callback) {
		this.instance = instance;
		this.startPattern = startPattern;
		this.numberOfMessages = numberOfMessages;
		this.abort = abort;
		this.callback = callback;
	}

	public String getStartMessage() {
		return startMessage;
	}

	public String getEndMessage() {
		return endMessage;
	}

	public Pattern getStartPattern() {
		return startPattern;
	}

	public int getNumberOfMessages() {
		return numberOfMessages;
	}

	public int getCurrentMessageCount() {
		return messages.size();
	}

	public Pattern getAbort() {
		return abort;
	}

	public Callback<IMultiPatternResult> getCallback() {
		return callback;
	}

	public boolean hasStartedListening() {
		return startedListening;
	}

	public void setStartedListening(boolean startedListening) {
		this.startedListening = startedListening;
	}

	public void add(String message) {
		Validate.validState(startedListening, "Hadn't started listening yet!");
		messages.add(message);
	}

	public void callCallback() {
		Validate.validState(startedListening, "Hadn't started listening yet!");
		callback.call(new MultiPatternResult(instance, messages));
	}
}
