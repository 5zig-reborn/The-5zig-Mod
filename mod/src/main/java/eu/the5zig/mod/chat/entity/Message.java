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

package eu.the5zig.mod.chat.entity;

import eu.the5zig.util.minecraft.ChatColor;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class Message implements Comparable<Message> {

	private final Conversation conversation;
	private int id;
	private String username;
	private String message;
	private long time;
	private MessageType messageType;

	public Message(Conversation conversation, int id, String username, String message, long time, MessageType messageType) {
		this.conversation = conversation;
		this.id = id;
		this.username = username;
		this.message = message;
		this.time = time;
		this.messageType = messageType;
	}

	public Conversation getConversation() {
		return conversation;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public String getUsername() {
		return username;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public long getTime() {
		return time;
	}

	@Override
	public int compareTo(Message o) {
		return Long.valueOf(getTime()).compareTo(o.getTime());
	}

	@Override
	public String toString() {
		return username + ChatColor.RESET + ": " + message;
	}

	public enum MessageStatus {
		PENDING, SENT, DELIVERED, READ
	}

	public enum MessageType {
		LEFT, RIGHT, CENTERED, DATE, IMAGE, AUDIO
	}

}
