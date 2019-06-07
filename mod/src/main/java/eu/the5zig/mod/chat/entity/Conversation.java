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

package eu.the5zig.mod.chat.entity;

import com.google.common.collect.Lists;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.gui.elements.Row;

import java.util.List;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public abstract class Conversation implements Row, Comparable<Conversation> {

	/**
	 * How much the {@code maxMessages} field should be increased.
	 */
	public static final int MAX_MESSAGES_PLUS = 50;
	/**
	 * How many messages should be initially loaded.
	 */
	public static final int MAX_MESSAGES_START = 50;
	/**
	 * The Id of the Conversation, used for MySQL statements.
	 */
	private int id;
	/**
	 * A List with all messages of the Conversation.
	 */
	private final List<Message> messages = Lists.newArrayList();
	/**
	 * The Time in Millis, when the conversation has been last used.
	 * Used to sort all conversations.
	 */
	private long lastUsed;
	private boolean read;
	/**
	 * How many messages should be displayed.
	 * Default Value is 100, can be increased by {@code MAX_MESSAGES_PLUS}.
	 */
	private int maxMessages = MAX_MESSAGES_START;

	private boolean hasUnloadedMessages = false;
	/**
	 * The current MessageStatus of the conversation.
	 */
	private Message.MessageStatus status;

	private Behaviour behaviour;

	/**
	 * The currently typing message.
	 */
	private String currentMessage = "";
	/**
	 * A List with all last sent messages.
	 */
	private List<String> lastMessages = Lists.newArrayList();
	private int lastMessageIndex;

	/**
	 * Default constructor of a conversation
	 *
	 * @param id        The id of the conversation
	 * @param lastUsed  The time the conversation has been last used
	 * @param read      If the conversation has been read
	 * @param behaviour The Notification Behaviour of this conversation.
	 */
	public Conversation(int id, long lastUsed, boolean read, Message.MessageStatus status, Behaviour behaviour) {
		this.id = id;
		this.lastUsed = lastUsed;
		this.read = read;
		this.status = status;
		this.behaviour = behaviour;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void addMessage(Message message) {
		synchronized (messages) {
			messages.add(message);
		}
	}

	public long getLastUsed() {
		return lastUsed;
	}

	public void setLastUsed(long lastUsed) {
		this.lastUsed = lastUsed;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		synchronized (this.messages) {
			this.messages.addAll(messages);
		}
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public Message.MessageStatus getStatus() {
		return status;
	}

	public void setStatus(Message.MessageStatus status) {
		this.status = status;
	}

	public int getMaxMessages() {
		return maxMessages;
	}

	public void setMaxMessages(int maxMessages) {
		this.maxMessages = maxMessages;
	}

	public boolean hasUnloadedMessages() {
		return hasUnloadedMessages;
	}

	public void setHasUnloadedMessages(boolean hasUnloadedMessages) {
		this.hasUnloadedMessages = hasUnloadedMessages;
	}

	public Behaviour getBehaviour() {
		return behaviour;
	}

	public void setBehaviour(Behaviour behaviour) {
		this.behaviour = behaviour;
	}

	public String getCurrentMessage() {
		return currentMessage;
	}

	public void setCurrentMessage(String currentMessage) {
		this.currentMessage = currentMessage;
	}

	public void addLastSentMessage(String message) {
		lastMessages.add(message);
		lastMessageIndex = lastMessages.size();
	}

	public String getPreviousSentMessage() {
		if (lastMessages.isEmpty())
			return "";
		lastMessageIndex--;
		if (lastMessageIndex < 0)
			return lastMessages.get(lastMessageIndex = 0);
		return lastMessages.get(lastMessageIndex);
	}

	public String getNextSentMessage() {
		if (lastMessages.isEmpty())
			return "";
		lastMessageIndex++;
		if (lastMessageIndex > lastMessages.size() - 1) {
			lastMessageIndex = lastMessages.size();
			return "";
		}
		return lastMessages.get(lastMessageIndex);
	}


	/**
	 * Compares the lastUsedTime of this conversation with another one
	 *
	 * @param conversation The other conversation
	 * @return The difference between both lastUsedTimes
	 */
	@Override
	public int compareTo(Conversation conversation) {
		return Long.valueOf(conversation.getLastUsed()).compareTo(getLastUsed());
	}

	public enum Behaviour {

		DEFAULT("chat.conversation_settings.behaviour.default"), SHOW("chat.conversation_settings.behaviour.show"), HIDE("chat.conversation_settings.behaviour.hide");

		private String type;

		Behaviour(String type) {
			this.type = type;
		}

		public Behaviour getNext() {
			return values()[(ordinal() + 1) % values().length];
		}

		public String getName() {
			return I18n.translate(type);
		}
	}

}
