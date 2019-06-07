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

package eu.the5zig.mod.chat;

import com.google.common.collect.Lists;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.entity.*;
import eu.the5zig.mod.chat.gui.ChatLine;
import eu.the5zig.mod.chat.gui.ImageChatLine;
import eu.the5zig.mod.chat.network.filetransfer.FileTransferManager;
import eu.the5zig.mod.chat.network.filetransfer.FileUploadTask;
import eu.the5zig.mod.chat.network.packets.*;
import eu.the5zig.mod.chat.sql.*;
import eu.the5zig.mod.gui.GuiConversations;
import eu.the5zig.mod.util.Display;
import eu.the5zig.util.Callback;
import eu.the5zig.util.Utils;
import eu.the5zig.util.db.Database;
import eu.the5zig.util.io.FileUtils;
import eu.the5zig.util.minecraft.ChatColor;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class ConversationManager {

	public static final String TABLE_CHAT = "conversations_chat";
	public static final String TABLE_CHAT_MESSAGES = "conversation_chat_messages";
	public static final String TABLE_GROUP_CHAT = "conversations_groupchat";
	public static final String TABLE_GROUP_CHAT_MESSAGES = "conversation_groupchat_messages";
	public static final String TABLE_ANNOUNCEMENTS = "announcements";
	public static final String TABLE_ANNOUNCEMENTS_MESSAGES = "announcements_messages";
	private final List<Conversation> conversations = new ArrayList<Conversation>();

	public ConversationManager() {
	}

	public void queueStatement(String query, Object... parameters) {
		queueStatement(null, false, false, query, parameters);
	}

	public void queueStatement(Callback<Integer> callback, String query, Object... parameters) {
		queueStatement(callback, false, false, query, parameters);
	}

	public void queueStatement(Callback<Integer> callback, boolean returnGeneratedKeys, String query, Object... parameters) {
		queueStatement(callback, false, returnGeneratedKeys, query, parameters);
	}

	public void queueStatement(final Callback<Integer> callback, boolean sync, boolean returnGeneratedKeys, String query, Object... parameters) {
		if (sync) {
			Callback<Integer> syncCallback = callback == null ? null : new Callback<Integer>() {
				@Override
				public void call(final Integer result) {
					The5zigMod.getScheduler().postToMainThread(new Runnable() {
						@Override
						public void run() {
							callback.call(result);
						}
					});
				}
			};
			if (returnGeneratedKeys) {
				The5zigMod.getConversationDatabase().updateWithGeneratedKeys(syncCallback, query, parameters);
			} else {
				The5zigMod.getConversationDatabase().update(syncCallback, query, parameters);
			}
		} else {
			if (returnGeneratedKeys) {
				The5zigMod.getConversationDatabase().updateWithGeneratedKeys(callback, query, parameters);
			} else {
				The5zigMod.getConversationDatabase().update(callback, query, parameters);
			}
		}
	}

	public void init(final Database sql) {
		The5zigMod.getDataManager().getNetworkStats().init(sql);

		new DatabaseMigration(sql).start();

		The5zigMod.getScheduler().postToMainThread(new Runnable() {
			@Override
			public void run() {
				try {
					loadConversations(sql);
					The5zigMod.logger.info("Loaded {} Chats and Group Chats!", conversations.size());
				} catch (Throwable throwable) {
					The5zigMod.logger.error("Could not initialize MySQL!", throwable);
				}
				sortConversations();
				try {
					The5zigMod.getFriendManager().loadSuggestions(sql);
				} catch (Throwable throwable) {
					The5zigMod.logger.error("Could not initialize MySQL!", throwable);
				}
			}
		});

	}

	private void loadConversations(Database sql) {
		sql.update("CREATE TABLE IF NOT EXISTS " + TABLE_CHAT +
				" (id INT AUTO_INCREMENT PRIMARY KEY, uuid VARCHAR(36), friend VARCHAR(16), lastused BIGINT, read BOOLEAN, status INT, behaviour INT)");
		sql.update("CREATE TABLE IF NOT EXISTS " + TABLE_CHAT_MESSAGES + " (id INT AUTO_INCREMENT PRIMARY KEY, conversationid INT, player VARCHAR(20), message VARCHAR(512), time " +
				"BIGINT, type INT)");

		sql.update("CREATE TABLE IF NOT EXISTS " + TABLE_ANNOUNCEMENTS + " (id INT AUTO_INCREMENT PRIMARY KEY, lastused BIGINT, read BOOLEAN, behaviour INT)");
		sql.update("CREATE TABLE IF NOT EXISTS " + TABLE_ANNOUNCEMENTS_MESSAGES + " (id INT AUTO_INCREMENT PRIMARY KEY, message VARCHAR(512), type INT, time BIGINT)");

		sql.update("CREATE TABLE IF NOT EXISTS " + TABLE_GROUP_CHAT +
				" (id INT AUTO_INCREMENT PRIMARY KEY, groupId INT, name VARCHAR(50), lastused BIGINT, read BOOLEAN, status INT, behaviour INT)");
		sql.update("CREATE TABLE IF NOT EXISTS " + TABLE_GROUP_CHAT_MESSAGES +
				" (id INT AUTO_INCREMENT PRIMARY KEY, conversationid INT, player VARCHAR(20), message VARCHAR(512), time BIGINT, type INT)");

		loadChatConversations(sql);
		loadGroupChatConversations(sql);
		loadAnnouncements(sql);
	}

	private void loadChatConversations(Database sql) {
		List<ChatEntity> conversationEntities = sql.get(ChatEntity.class).query("SELECT * FROM " + TABLE_CHAT + " ORDER BY lastused DESC").getAll();
		for (ChatEntity entity : conversationEntities) {
			ConversationChat conversation = new ConversationChat(entity.getId(), entity.getFriend(), entity.getUuid(), entity.getLastUsed(), entity.isRead(),
					Message.MessageStatus.values()[entity.getStatus()], Conversation.Behaviour.values()[entity.getBehaviour()]);
			conversation.setMessages(loadMessages(sql, conversation, TABLE_CHAT_MESSAGES));
			conversations.add(conversation);
		}
	}

	private void loadGroupChatConversations(Database sql) {
		List<GroupChatEntity> conversationEntities = sql.get(GroupChatEntity.class).query("SELECT * FROM " + TABLE_GROUP_CHAT + " ORDER BY lastused DESC").getAll();
		for (GroupChatEntity entity : conversationEntities) {
			ConversationGroupChat conversation = new ConversationGroupChat(entity.getId(), entity.getGroupId(), entity.getName(), entity.getLastused(), entity.isRead(),
					Message.MessageStatus.values()[entity.getStatus()], Conversation.Behaviour.values()[entity.getBehaviour()]);
			conversation.setMessages(loadMessages(sql, conversation, TABLE_GROUP_CHAT_MESSAGES));
			conversations.add(conversation);
		}
	}

	private void loadAnnouncements(Database sql) {
		AnnouncementEntity entity = sql.get(AnnouncementEntity.class).query("SELECT * FROM " + TABLE_ANNOUNCEMENTS + " ORDER BY lastused DESC").unique();
		if (entity == null)
			return;
		ConversationAnnouncements conversation = new ConversationAnnouncements(entity.getId(), entity.getLastused(), entity.isRead(), Conversation.Behaviour.values()[entity.getBehaviour()]);
		List<Message> messages = Lists.newArrayList();
		List<MessageEntity> messagesEntities = sql.get(MessageEntity.class).query("SELECT * FROM " + TABLE_ANNOUNCEMENTS_MESSAGES + " ORDER BY time ASC").getAll();
		for (MessageEntity messageEntity : messagesEntities) {
			messages.add(new Message(conversation, messageEntity.getId(), "Announcement", messageEntity.getMessage(), messageEntity.getTime(),
					Message.MessageType.values()[messageEntity.getType()]));
		}
		conversation.setMessages(messages);
		conversations.add(conversation);
	}

	private List<Message> loadMessages(Database sql, Conversation conversation, String table) {
		return loadMessages(sql, conversation, table, 0);
	}

	private List<Message> loadMessages(Database sql, Conversation conversation, String table, int offset) {
		List<Message> messages = Lists.newArrayList();
		List<MessageEntity> messagesEntities = sql.get(MessageEntity.class).query(
				"SELECT * FROM (SELECT * FROM " + table + " WHERE conversationid=? ORDER BY time DESC LIMIT " + (Conversation.MAX_MESSAGES_START + 1) + " OFFSET " + offset + ") sub ORDER " +
						"BY time ASC", conversation.getId()).getAll();
		if (messagesEntities.size() > Conversation.MAX_MESSAGES_START) {
			messagesEntities.remove(0);
			conversation.setHasUnloadedMessages(true);
		} else {
			conversation.setHasUnloadedMessages(false);
		}
		for (MessageEntity messageEntity : messagesEntities) {
			if (messageEntity.getType() == Message.MessageType.IMAGE.ordinal()) {
				messages.add(new ImageMessage(conversation, messageEntity.getId(), messageEntity.getPlayer(), messageEntity.getMessage(), messageEntity.getTime(),
						Message.MessageType.values()[messageEntity.getType()]));
			} else if (messageEntity.getType() == Message.MessageType.AUDIO.ordinal()) {
				messages.add(new AudioMessage(conversation, messageEntity.getId(), messageEntity.getPlayer(), messageEntity.getMessage(), messageEntity.getTime(),
						Message.MessageType.values()[messageEntity.getType()]));
			} else {
				messages.add(new Message(conversation, messageEntity.getId(), messageEntity.getPlayer(), messageEntity.getMessage(), messageEntity.getTime(),
						Message.MessageType.values()[messageEntity.getType()]));
			}
		}
		return messages;
	}

	public void increaseMaxMessages(final Conversation conversation, final Runnable callback) {
		The5zigMod.getAsyncExecutor().execute(new Runnable() {
			@Override
			public void run() {
				List<Message> messages = loadMessages(The5zigMod.getConversationDatabase(), conversation, getMessagesTableNameByConversation(conversation), conversation.getMaxMessages());
				conversation.getMessages().addAll(0, messages);
				conversation.setMaxMessages(conversation.getMaxMessages() + Conversation.MAX_MESSAGES_PLUS);
				The5zigMod.getScheduler().postToMainThread(callback);
			}
		});
	}

	public List<Conversation> getConversations() {
		return conversations;
	}

	/**
	 * Updates all Conversation-Names that contains the Friend.
	 *
	 * @param friend The Friend
	 */
	public void updateConversationNames(final Friend friend) {
		for (Conversation conversation : conversations) {
			if (!(conversation instanceof ConversationChat))
				continue;
			ConversationChat conversationChat = (ConversationChat) conversation;
			if (!conversationChat.getFriendUUID().equals(friend.getUniqueId()) || conversationChat.getFriendName().equals(friend.getUsername()))
				continue;
			The5zigMod.logger.info("Friend {} changed its name to {}!", conversationChat.getFriendName(), friend.getUsername());
			conversationChat.setFriendName(friend.getUsername());

			queueStatement("UPDATE " + TABLE_CHAT + " SET friend=? WHERE uuid=?", friend.getUsername(), friend.getUniqueId().toString());
		}
	}

	public void setConversationName(final ConversationGroupChat conversation, final String name) {
		conversation.setName(name);

		queueStatement("UPDATE " + TABLE_GROUP_CHAT + " SET name=? WHERE id=?", name, conversation.getId());
	}

	/**
	 * Creates a new chat conversation with a friend
	 *
	 * @param friend The friend
	 * @return A new conversation
	 */
	public ConversationChat newConversation(Friend friend) {
		final ConversationChat conversation = new ConversationChat(-1, friend.getUsername(), friend.getUniqueId(), System.currentTimeMillis(), true, Message.MessageStatus.PENDING,
				Conversation.Behaviour.DEFAULT);
		conversations.add(conversation);
		queueStatement(new Callback<Integer>() {
			@Override
			public void call(Integer callback) {
				conversation.setId(callback);
			}
		}, true, "INSERT INTO " + TABLE_CHAT + " (uuid, friend, lastused) VALUES (?, ?, ?)", friend.getUniqueId().toString(), friend.getUsername(), System.currentTimeMillis());
		return conversation;
	}

	/**
	 * Creates a new group chat conversation
	 *
	 * @param group The group
	 * @return A new conversation
	 */
	public ConversationGroupChat newConversation(Group group) {
		final ConversationGroupChat conversation = new ConversationGroupChat(-1, group.getId(), group.getName(), System.currentTimeMillis(), true, Message.MessageStatus.PENDING,
				Conversation.Behaviour.DEFAULT);
		conversations.add(conversation);
		queueStatement(new Callback<Integer>() {
			@Override
			public void call(Integer callback) {
				conversation.setId(callback);
			}
		}, true, "INSERT INTO " + TABLE_GROUP_CHAT + " (groupId, name, lastused) VALUES (?, ?, ?)", group.getId(), group.getName(), System.currentTimeMillis());
		return conversation;
	}

	/**
	 * Creates a new conversation for announcements
	 *
	 * @return A new conversation
	 */
	public ConversationAnnouncements newConversation() {
		final ConversationAnnouncements conversation = new ConversationAnnouncements(-1, System.currentTimeMillis(), true, Conversation.Behaviour.DEFAULT);
		conversations.add(conversation);
		queueStatement(new Callback<Integer>() {
			@Override
			public void call(Integer callback) {
				conversation.setId(callback);
			}
		}, true, "INSERT INTO " + TABLE_ANNOUNCEMENTS + " (lastused) VALUES (?)", System.currentTimeMillis());
		return conversation;
	}

	/**
	 * Gets a chat conversation by a Friend or creates a new one if doesn't exist.
	 *
	 * @param friend The friend
	 * @return A conversation instance of this Friend.
	 */
	public ConversationChat getConversation(Friend friend) {
		ConversationChat conversation = null;
		for (Conversation conversation1 : conversations) {
			if (!(conversation1 instanceof ConversationChat))
				continue;

			ConversationChat conversationChat = (ConversationChat) conversation1;
			if (conversationChat.getFriendUUID().equals(friend.getUniqueId()))
				conversation = conversationChat;
		}
		if (conversation == null) {
			conversation = newConversation(friend);
		}
		return conversation;
	}

	public boolean conversationExists(Friend friend) {
		for (Conversation conversation : conversations) {
			if (!(conversation instanceof ConversationChat))
				continue;

			ConversationChat conversationChat = (ConversationChat) conversation;
			if (conversationChat.getFriendUUID().equals(friend.getUniqueId()))
				return true;
		}
		return false;
	}

	/**
	 * Gets a group chat conversation by a Group or creates a new one if doesn't exist.
	 *
	 * @param group The Group.
	 * @return A conversation instance of this Group.
	 */
	public ConversationGroupChat getConversation(Group group) {
		ConversationGroupChat conversation = null;
		for (Conversation conversation1 : conversations) {
			if (conversation1 instanceof ConversationGroupChat) {
				ConversationGroupChat conversationGroupChat = (ConversationGroupChat) conversation1;
				if (conversationGroupChat.getGroupId() == group.getId())
					conversation = conversationGroupChat;
			}
		}

		if (conversation == null) {
			conversation = newConversation(group);
		}
		return conversation;
	}

	public ConversationAnnouncements getAnnouncementsConversation() {
		for (Conversation conversation : conversations) {
			if (conversation instanceof ConversationAnnouncements) {
				// There is only one announcement conversation.
				// Or at least should be...
				return (ConversationAnnouncements) conversation;
			}
		}
		return newConversation();
	}

	public void deleteGroupConversation(int groupId) {
		Conversation con = null;
		for (Conversation conversation : conversations) {
			if (!(conversation instanceof ConversationGroupChat))
				continue;
			ConversationGroupChat c = (ConversationGroupChat) conversation;
			if (c.getGroupId() != groupId)
				continue;
			con = conversation;
			break;
		}
		if (con == null)
			return;
		deleteConversation(con);
	}

	public void sendConversationMessage(UUID uuid, String string) {
		String coloredMessage = string;
		if (The5zigMod.getDataManager().getProfile().getRank() != Rank.NONE) {
			coloredMessage = ChatColor.translateAlternateColorCodes('&', string);
		}
		final String message = coloredMessage;
		Friend friend = The5zigMod.getFriendManager().getFriend(uuid);
		final Conversation conversation = getConversation(friend);
		setConversationStatus(conversation, Message.MessageStatus.PENDING);
		final long time = System.currentTimeMillis();
		The5zigMod.getNetworkManager().sendPacket(new PacketMessageFriend(friend.getUniqueId(), string, time));

		final Message msg = new Message(conversation, -1, The5zigMod.getDataManager().getColoredName(), message, time, Message.MessageType.RIGHT);
		checkNewDay(conversation, msg);
		conversation.addMessage(msg);
		addChatLineToGui(conversation, msg);
		setConversationLastUsed(conversation);
		queueStatement(new Callback<Integer>() {
			               @Override
			               public void call(Integer callback) {
				               msg.setId(callback);
			               }
		               }, true, "INSERT INTO " + TABLE_CHAT_MESSAGES + "(conversationid, player, message, time, type) VALUES (?, ?, ?, ?, ?)", conversation.getId(),
				The5zigMod.getDataManager().getColoredName(), message, time, Message.MessageType.RIGHT.ordinal());
	}

	/**
	 * Called when receiving a new message from a friend.
	 *
	 * @param uuid     The UniqueId of the Friend.
	 * @param username The username of the Friend.
	 * @param message  The message that has been sent.
	 * @param time     The Send-Time of the message.
	 */
	public void handleFriendMessageReceive(UUID uuid, final String username, final String message, final long time) {
		final Friend friend = The5zigMod.getFriendManager().getFriend(uuid);
		The5zigMod.getNetworkManager().sendPacket(new PacketMessageFriendStatus(friend.getUniqueId(), Message.MessageStatus.DELIVERED));
		final Conversation conversation = getConversation(friend);
		conversation.setRead(false);

		String title = I18n.translate("chat.new_message", username);
		GuiConversations.resetScroll();
		if (!(The5zigMod.getVars().getCurrentScreen() instanceof GuiConversations) ||
				!(((GuiConversations) The5zigMod.getVars().getCurrentScreen()).getSelectedConversation() instanceof ConversationChat) ||
				!((ConversationChat) ((GuiConversations) The5zigMod.getVars().getCurrentScreen()).getSelectedConversation()).getFriendUUID().equals(friend.getUniqueId())) {
			if ((conversation.getBehaviour() == Conversation.Behaviour.DEFAULT && The5zigMod.getConfig().getBool("showMessages")) ||
					conversation.getBehaviour() == Conversation.Behaviour.SHOW) {
				The5zigMod.getOverlayMessage().displayMessage(title, message);
				if (The5zigMod.getDataManager().getAfkManager().isAfk())
					The5zigMod.getDataManager().getAfkManager().addNewMessage();
			}
		}
		if (!Display.isActive())
			The5zigMod.getTrayManager().displayMessage(ChatColor.stripColor(title), ChatColor.stripColor(message));

		final Message msg = new Message(conversation, -1, username, message, time, Message.MessageType.LEFT);
		checkNewDay(conversation, msg);
		conversation.addMessage(msg);
		addChatLineToGui(conversation, msg);
		setConversationLastUsed(conversation);
		queueStatement(new Callback<Integer>() {
			               @Override
			               public void call(Integer callback) {
				               msg.setId(callback);
			               }
		               }, true, "INSERT INTO " + TABLE_CHAT_MESSAGES + " (conversationid, player, message, time, type) VALUES (?, ?, ?, ?, ?)", conversation.getId(), username, message, time,
				Message.MessageType.LEFT.ordinal());
	}

	public void sendGroupMessage(Group group, String string) {
		String coloredMessage = string;
		if (The5zigMod.getDataManager().getProfile().getRank() != Rank.NONE) {
			coloredMessage = ChatColor.translateAlternateColorCodes('&', string);
		}
		final String message = coloredMessage;
		final Conversation conversation = getConversation(group);
		setConversationStatus(conversation, Message.MessageStatus.PENDING);
		The5zigMod.getNetworkManager().sendPacket(new PacketGroupChatMessage(group.getId(), string, System.currentTimeMillis()));

		final long time = System.currentTimeMillis();
		final Message msg = new Message(conversation, -1, The5zigMod.getDataManager().getColoredName(), message, time, Message.MessageType.RIGHT);
		checkNewDay(conversation, msg);
		conversation.addMessage(msg);
		addChatLineToGui(conversation, msg);
		setConversationLastUsed(conversation);
		queueStatement(new Callback<Integer>() {
			               @Override
			               public void call(Integer callback) {
				               msg.setId(callback);
			               }
		               }, true, "INSERT INTO " + TABLE_GROUP_CHAT_MESSAGES + " (conversationid, player, message, time, type) VALUES (?, ?, ?, ?, ?)", conversation.getId(),
				The5zigMod.getDataManager().getColoredName(), message, time, Message.MessageType.RIGHT.ordinal());
	}

	public void handleGroupChatMessage(Group group, final String username, final String message, final long time) {
		final Conversation conversation = getConversation(group);

		if (!(The5zigMod.getVars().getCurrentScreen() instanceof GuiConversations) ||
				!(((GuiConversations) The5zigMod.getVars().getCurrentScreen()).getSelectedConversation() instanceof ConversationGroupChat) ||
				((ConversationGroupChat) ((GuiConversations) The5zigMod.getVars().getCurrentScreen()).getSelectedConversation()).getGroupId() == group.getId()) {
			if ((conversation.getBehaviour() == Conversation.Behaviour.DEFAULT && The5zigMod.getConfig().getBool("showGroupMessages")) ||
					conversation.getBehaviour() == Conversation.Behaviour.SHOW) {
				The5zigMod.getOverlayMessage().displayMessage(I18n.translate("chat.new_group_message", group.getName()), message);
				The5zigMod.getDataManager().getAfkManager().addNewMessage();
			}
		}
		final Message msg = new Message(conversation, -1, username, message, time, Message.MessageType.LEFT);
		checkNewDay(conversation, msg);
		conversation.addMessage(msg);
		addChatLineToGui(conversation, msg);
		setConversationLastUsed(conversation);
		queueStatement(new Callback<Integer>() {
			               @Override
			               public void call(Integer callback) {
				               msg.setId(callback);
			               }
		               }, true, "INSERT INTO " + TABLE_GROUP_CHAT_MESSAGES + " (conversationid, player, message, time, type) VALUES (?, ?, ?, ?, ?)", conversation.getId(), username, message, time,
				Message.MessageType.LEFT.ordinal());
	}

	public void handleGroupBroadcast(Group group, final String message, final long time) {
		final Conversation conversation = getConversation(group);

		final Message msg = new Message(conversation, -1, "", message, time, Message.MessageType.CENTERED);
		checkNewDay(conversation, msg);
		conversation.addMessage(msg);
		addChatLineToGui(conversation, msg);
		setConversationLastUsed(conversation);
		queueStatement(new Callback<Integer>() {
			               @Override
			               public void call(Integer callback) {
				               msg.setId(callback);
			               }
		               }, true, "INSERT INTO " + TABLE_GROUP_CHAT_MESSAGES + " (conversationid, player, message, time, type) VALUES (?, ?, ?, ?, ?)", conversation.getId(), "", message, time,
				Message.MessageType.CENTERED.ordinal());
	}

	public void sendImage(UUID uuid, File imageFile) {
		if (imageFile.length() > FileUploadTask.MAX_LENGTH) {
			The5zigMod.getOverlayMessage().displayMessageAndSplit(
					I18n.translate("chat.image.too_large", Utils.bytesToReadable((long) Math.floor(FileUploadTask.MAX_LENGTH * 1.024 * 1.024))));
			return;
		}
		File mediaDir;
		final File mediaFile;
		String hash;
		try {
			mediaDir = FileUtils.createDir(new File(The5zigMod.getModDirectory(), "media/" + The5zigMod.getDataManager().getUniqueId().toString() + "/" + uuid.toString()));
			mediaFile = new File(mediaDir, hash = FileTransferManager.sha1(imageFile));
			org.apache.commons.io.FileUtils.copyFile(imageFile, mediaFile);
		} catch (Exception e) {
			The5zigMod.logger.error("Could not copy " + imageFile, e);
			return;
		}
		final Friend friend = The5zigMod.getFriendManager().getFriend(uuid);
		final Conversation conversation = getConversation(friend);
		setConversationStatus(conversation, Message.MessageStatus.PENDING);
		setConversationLastUsed(conversation);

		final ImageMessage.ImageData fileData = new ImageMessage.ImageData(FileMessage.Status.WAITING);
		fileData.setHash(hash);
		final long time = System.currentTimeMillis();

		final Message msg = new ImageMessage(conversation, -1, The5zigMod.getDataManager().getColoredName(), fileData, time, Message.MessageType.IMAGE);
		checkNewDay(conversation, msg);
		conversation.addMessage(msg);
		addChatLineToGui(conversation, msg);
		queueStatement(new Callback<Integer>() {
			               @Override
			               public void call(Integer callback) {
				               msg.setId(callback);
				               The5zigMod.getNetworkManager().sendPacket(new PacketFileTransferRequest(friend.getUniqueId(), callback, PacketFileTransferStart.Type.IMAGE, mediaFile.length()));
			               }
		               }, true, "INSERT INTO " + TABLE_CHAT_MESSAGES + " (conversationid, player, message, time, type) VALUES (?, ?, ?, ?, ?)", conversation.getId(),
				The5zigMod.getDataManager().getColoredName(), msg.getMessage(), time, Message.MessageType.IMAGE.ordinal());
	}

	public void sendAudio(final UUID uuid, final File audioFile) {
		if (audioFile.length() > FileUploadTask.MAX_LENGTH) {
			The5zigMod.getOverlayMessage().displayMessageAndSplit(
					I18n.translate("chat.audio.too_large", Utils.bytesToReadable((long) Math.floor(FileUploadTask.MAX_LENGTH * 1.024 * 1.024))));
			return;
		}
		The5zigMod.getAsyncExecutor().execute(new Runnable() {
			@Override
			public void run() {
				File mediaDir;
				final File mediaFile;
				final String hash;
				try {
					mediaDir = FileUtils.createDir(new File(The5zigMod.getModDirectory(), "media/" + The5zigMod.getDataManager().getUniqueId().toString() + "/" + uuid.toString()));
					mediaFile = new File(mediaDir, hash = FileTransferManager.sha1(audioFile));
					org.apache.commons.io.FileUtils.moveFile(audioFile, mediaFile);
				} catch (Exception e) {
					The5zigMod.logger.error("Could not create audio file", e);
					return;
				}

				The5zigMod.getScheduler().postToMainThread(new Runnable() {
					@Override
					public void run() {
						final Friend friend = The5zigMod.getFriendManager().getFriend(uuid);
						final Conversation conversation = getConversation(friend);
						setConversationStatus(conversation, Message.MessageStatus.PENDING);
						setConversationLastUsed(conversation);

						final AudioMessage.AudioData fileData = new AudioMessage.AudioData(FileMessage.Status.WAITING);
						fileData.setHash(hash);
						final long time = System.currentTimeMillis();

						final Message msg = new AudioMessage(conversation, -1, The5zigMod.getDataManager().getColoredName(), fileData, time, Message.MessageType.AUDIO);
						checkNewDay(conversation, msg);
						conversation.addMessage(msg);
						addChatLineToGui(conversation, msg);
						queueStatement(new Callback<Integer>() {
							               @Override
							               public void call(Integer callback) {
								               msg.setId(callback);
								               The5zigMod.getNetworkManager().sendPacket(
										               new PacketFileTransferRequest(friend.getUniqueId(), callback, PacketFileTransferStart.Type.AUDIO, mediaFile.length()));
							               }
						               }, true, "INSERT INTO " + TABLE_CHAT_MESSAGES + " (conversationid, player, message, time, type) VALUES (?, ?, ?, ?, ?)", conversation.getId(),
								The5zigMod.getDataManager().getColoredName(), msg.getMessage(), time, Message.MessageType.AUDIO.ordinal());
					}
				});
			}
		});
	}

	public void handleFileId(UUID uuid, int messageId, int fileID) {
		final Message message = getMessageById(uuid, messageId);
		if (message == null || !(message instanceof FileMessage)) {
			The5zigMod.logger.error("Could not find message for file id " + fileID);
			return;
		}

		((FileMessage) message).getFileData().setFileId(fileID);
		((FileMessage) message).saveData();
		queueStatement("UPDATE " + TABLE_CHAT_MESSAGES + " SET message=? WHERE id=?", message.getMessage(), message.getId());
	}

	public void handleFileRequest(UUID uuid, int fileId, PacketFileTransferStart.Type type, long length) {
		final Friend friend = The5zigMod.getFriendManager().getFriend(uuid);
		The5zigMod.getNetworkManager().sendPacket(new PacketMessageFriendStatus(friend.getUniqueId(), Message.MessageStatus.DELIVERED));
		final Conversation conversation = getConversation(friend);
		conversation.setRead(false);

		final String username = friend.getDisplayName();
		String title = I18n.translate("chat.new_file_request", username);

		final long time = System.currentTimeMillis();

		if (!(The5zigMod.getVars().getCurrentScreen() instanceof GuiConversations) ||
				!(((GuiConversations) The5zigMod.getVars().getCurrentScreen()).getSelectedConversation() instanceof ConversationChat) ||
				!((ConversationChat) ((GuiConversations) The5zigMod.getVars().getCurrentScreen()).getSelectedConversation()).getFriendUUID().equals(friend.getUniqueId())) {
			if ((conversation.getBehaviour() == Conversation.Behaviour.DEFAULT && The5zigMod.getConfig().getBool("showMessages")) ||
					conversation.getBehaviour() == Conversation.Behaviour.SHOW) {
				The5zigMod.getOverlayMessage().displayMessageAndSplit(title);
				if (The5zigMod.getDataManager().getAfkManager().isAfk())
					The5zigMod.getDataManager().getAfkManager().addNewMessage();
			}
		}
		if (!Display.isActive())
			The5zigMod.getTrayManager().displayMessage(ChatColor.stripColor(title), I18n.translate("chat.accept_file_transfer"));

		Message tmp;
		if (type == PacketFileTransferStart.Type.IMAGE) {
			final ImageMessage.ImageData fileData = new ImageMessage.ImageData(FileMessage.Status.REQUEST);
			fileData.setFileId(fileId);
			fileData.setLength(length);
			tmp = new ImageMessage(conversation, -1, username, fileData, time, Message.MessageType.IMAGE);
		} else {
			final AudioMessage.AudioData fileData = new AudioMessage.AudioData(FileMessage.Status.REQUEST);
			fileData.setFileId(fileId);
			fileData.setLength(length);
			tmp = new AudioMessage(conversation, -1, username, fileData, time, Message.MessageType.AUDIO);
		}
		final Message msg = tmp;
		checkNewDay(conversation, msg);
		conversation.addMessage(msg);
		addChatLineToGui(conversation, msg);
		setConversationLastUsed(conversation);
		queueStatement(new Callback<Integer>() {
			               @Override
			               public void call(Integer callback) {
				               msg.setId(callback);
			               }
		               }, true, "INSERT INTO " + TABLE_CHAT_MESSAGES + " (conversationid, player, message, time, type) VALUES (?, ?, ?, ?, ?)", conversation.getId(), username, msg.getMessage(), time,
				msg.getMessageType().ordinal());
	}

	public void handleFileResponse(int fileId, boolean accepted) {
		final FileMessage message = getMessageByFileId(fileId);
		if (message == null) {
			The5zigMod.logger.error("Could not find message for file id " + fileId);
			return;
		}
		if (!accepted) {
			message.getFileData().setStatus(FileMessage.Status.DENIED);
			message.saveData();
		} else {
			message.getFileData().setStatus(FileMessage.Status.ACCEPTED);
			message.saveData();
			try {
				The5zigMod.getDataManager().getFileTransferManager().initFileUpload(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		updateMessageText(message);
	}

	public void handleStartResponse(int fileId) {
		final FileMessage message = getMessageByFileId(fileId);
		if (message == null) {
			return;
		}
		message.getFileData().setStatus(FileMessage.Status.UPLOADING);
		message.saveData();
		The5zigMod.getDataManager().getFileTransferManager().startUpload(fileId);
	}

	public void handleFileStart(int fileId, int parts, int chunkSize) {
		final FileMessage message = getMessageByFileId(fileId);
		if (message == null) {
			return;
		}
		message.getFileData().setStatus(FileMessage.Status.DOWNLOADING);
		message.saveData();
		try {
			The5zigMod.getDataManager().getFileTransferManager().initFileDownload(fileId, parts, chunkSize, message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void handleFileChunk(int fileId, int partId, byte[] data) {
		try {
			final FileMessage message = getMessageByFileId(fileId);
			if (message == null)
				return;
			if (The5zigMod.getDataManager().getFileTransferManager().handleChunkDownload(fileId, partId, data, message)) {
				The5zigMod.getScheduler().postToMainThread(new Runnable() {
					@Override
					public void run() {
						message.getFileData().setStatus(FileMessage.Status.DOWNLOADED);
						message.saveData();
						if (The5zigMod.getVars().getCurrentScreen() instanceof GuiConversations) {
							GuiConversations gui = (GuiConversations) The5zigMod.getVars().getCurrentScreen();
							for (ChatLine chatLine : gui.chatLines) {
								if (chatLine instanceof ImageChatLine && chatLine.getMessage() == message) {
									ImageChatLine imageChatLine = (ImageChatLine) chatLine;
									imageChatLine.updateImage();
									break;
								}
							}
						}

						updateMessageText(message);
					}
				});
			}
		} catch (Exception e) {
			The5zigMod.logger.error("Could not handle file chunk!", e);
		}
	}

	public void handleFileAbort(int fileId) {
		final FileMessage message = getMessageByFileId(fileId);
		if (message == null)
			return;
		if (message.getFileData().isOwn()) {
			The5zigMod.getDataManager().getFileTransferManager().abortUpload(fileId);
			message.getFileData().setStatus(FileMessage.Status.UPLOAD_FAILED);
		} else {
			The5zigMod.getDataManager().getFileTransferManager().abortDownload(fileId);
			message.getFileData().setStatus(FileMessage.Status.DOWNLOAD_FAILED);
		}
		message.saveData();
		updateMessageText(message);
	}

	public void setImageUploaded(final FileMessage message) {
		message.setPercentage(1);
		message.getFileData().setStatus(FileMessage.Status.UPLOADED);
		message.saveData();
		updateMessageText(message);
	}

	public Message getMessageById(UUID uuid, int id) {
		for (Conversation conversation : conversations) {
			if (!(conversation instanceof ConversationChat) || !((ConversationChat) conversation).getFriendUUID().equals(uuid))
				continue;
			for (Message msg : conversation.getMessages()) {
				if (msg.getId() == id) {
					return msg;
				}
			}
		}
		return null;
	}

	public FileMessage getMessageByFileId(int fileId) {
		FileMessage result = null;
		for (Conversation conversation : conversations) {
			for (Message msg : conversation.getMessages()) {
				if (msg instanceof FileMessage && ((FileMessage) msg).getFileData().getFileId() == fileId) {
					if (result != null) {
						result.getFileData().setStatus(result.getFileData().isOwn() ? FileMessage.Status.UPLOAD_FAILED : FileMessage.Status.DOWNLOAD_FAILED);
						result.saveData();
						updateMessageText(result);
					}
					result = (FileMessage) msg;
				}
			}
		}
		if (result == null) {
			The5zigMod.logger.error("Could not find message for file id " + fileId);
			The5zigMod.getNetworkManager().sendPacket(new PacketFileTransferAbort(fileId));
		}
		return result;
	}

	public void updateMessageText(final Message message) {
		queueStatement("UPDATE " + TABLE_CHAT_MESSAGES + " SET message=? WHERE id=?", message.getMessage(), message.getId());
	}

	public void checkNewDay(final Conversation conversation, final Message newMessage) {
		if (!conversation.getMessages().isEmpty() && Utils.isSameDay(newMessage.getTime(), conversation.getMessages().get(conversation.getMessages().size() - 1).getTime()))
			return;
		final long time = newMessage.getTime() - 1;
		final Message dateMessage = new Message(conversation, -1, "", "", time, Message.MessageType.DATE);
		conversation.addMessage(dateMessage);
		addChatLineToGui(conversation, dateMessage);
		Callback<Integer> callback = new Callback<Integer>() {
			@Override
			public void call(Integer callback) {
				dateMessage.setId(callback);
			}
		};
		if (conversation instanceof ConversationAnnouncements) {
			queueStatement(callback, true, "INSERT INTO " + TABLE_ANNOUNCEMENTS_MESSAGES + " (message, time, type) VALUES (?, ?, ?)", "", time, Message.MessageType.DATE.ordinal());
		} else {
			queueStatement(callback, true, "INSERT INTO " + getMessagesTableNameByConversation(conversation) + "(conversationid, player, message, time, type) VALUES (?, ?, ?, ?," + " ?)",
					conversation.getId(), "", "", time, Message.MessageType.DATE.ordinal());
		}
	}

	/**
	 * Adds a ChatLine to GuiFriendsConversation, if this gui is currently Active and the correct conversation is selected.
	 *
	 * @param conversation The conversation where the chatline should be added.
	 * @param message      The message that should be added.
	 */
	public void addChatLineToGui(Conversation conversation, Message message) {
		if (The5zigMod.getVars().getCurrentScreen() instanceof GuiConversations) {
			GuiConversations gui = (GuiConversations) The5zigMod.getVars().getCurrentScreen();
			if (gui.getSelectedConversation().equals(conversation)) {
				gui.chatLines.add(ChatLine.fromMessage(message));
				gui.scrollToBottom();
				if (Display.isActive()) { // Check if Display is in Focus
					setConversationRead(conversation, true);
					return;
				}
			}
		}
		setConversationRead(conversation, false);
	}

	/**
	 * Adds new Announcement Messages if any new ones exist.
	 *
	 * @param announcements A List with all announcement messages.
	 */
	public void setAnnouncementMessages(List<Announcement> announcements) {
		if (announcements.isEmpty())
			return;
		final ConversationAnnouncements conversation = getAnnouncementsConversation();
		for (final Announcement announcement : announcements) {
			queueStatement(new Callback<Integer>() {
				@Override
				public void call(Integer callback) {
					Message announcementMessage = new Message(conversation, callback, "Announcement", announcement.getMessage(), announcement.getTime(), Message.MessageType.LEFT);
					checkNewDay(conversation, announcementMessage);
					conversation.addMessage(announcementMessage);
				}
			}, true, true, "INSERT INTO " + TABLE_ANNOUNCEMENTS_MESSAGES + " (message, time) VALUES (?, ?)", announcement.getMessage(), announcement.getTime());
		}
		if (conversation.getBehaviour() != Conversation.Behaviour.HIDE)
			The5zigMod.getOverlayMessage().displayMessage(ChatColor.YELLOW + I18n.translate("announcement.new", announcements.size()));
		if (The5zigMod.getVars().getCurrentScreen() instanceof GuiConversations) {
			GuiConversations gui = (GuiConversations) The5zigMod.getVars().getCurrentScreen();
			if (gui.getSelectedConversation().equals(conversation)) {
				gui.scrollToBottom();
				gui.onSelect(conversations.indexOf(gui.getSelectedConversation()), gui.getSelectedConversation(), false);
				setConversationRead(conversation, true);
			} else {
				setConversationRead(conversation, false);
			}
		} else {
			setConversationRead(conversation, false);
		}
		setConversationLastUsed(conversation);
	}

	public void addAnnouncementMessage(final String message, final long time) {
		final ConversationAnnouncements conversation = getAnnouncementsConversation();

		if (conversation.getBehaviour() != Conversation.Behaviour.HIDE) {
			The5zigMod.getOverlayMessage().displayMessage(I18n.translate("announcement.new", 1), message);
			The5zigMod.getDataManager().getAfkManager().addNewMessage();
		}

		queueStatement(new Callback<Integer>() {
			@Override
			public void call(Integer callback) {
				Message msg = new Message(conversation, callback, "Announcement", message, time, Message.MessageType.LEFT);
				checkNewDay(conversation, msg);
				conversation.addMessage(msg);
				addChatLineToGui(conversation, msg);
			}
		}, true, true, "INSERT INTO " + TABLE_ANNOUNCEMENTS_MESSAGES + " (message, time) VALUES (?, ?)", message, time);
		setConversationLastUsed(conversation);
	}

	/**
	 * Sets the send status of a conversation.
	 *
	 * @param conversation  The conversation where the status should be changed.
	 * @param messageStatus The new conversation status.
	 */
	public void setConversationStatus(final Conversation conversation, final Message.MessageStatus messageStatus) {
		Validate.notNull(conversation, "Conversation cannot be null");
		Validate.notNull(messageStatus, "Message Status cannot be null");
		if (conversation instanceof ConversationAnnouncements)
			return;
		conversation.setStatus(messageStatus);

		queueStatement("UPDATE " + getTableNameByConversation(conversation) + " SET status=? WHERE id=?", messageStatus.ordinal(), conversation.getId());
	}

	/**
	 * Updates the 'lastUsed'-Time of the Conversation to {@code System.currentTimeMillis()}.
	 *
	 * @param conversation The Conversation that should be updated.
	 */
	public void setConversationLastUsed(final Conversation conversation) {
		conversation.setLastUsed(System.currentTimeMillis());
		sortConversations();

		queueStatement("UPDATE " + getTableNameByConversation(conversation) + " SET lastused=? WHERE id=?", System.currentTimeMillis(), conversation.getId());
	}

	public void setBehaviour(final Conversation conversation, final Conversation.Behaviour behaviour) {
		conversation.setBehaviour(behaviour);

		queueStatement("UPDATE " + getTableNameByConversation(conversation) + " SET behaviour=? WHERE id=?", behaviour.ordinal(), conversation.getId());
	}

	/**
	 * Sets a conversion read or unread
	 *
	 * @param conversation The conversation that should be updated
	 * @param read         Should be the conversation read?
	 */
	public void setConversationRead(final Conversation conversation, final boolean read) {
		if (conversation.isRead() == read)
			return;
		conversation.setRead(read);
		if (!read) {
			sortConversations();
		} else {
			if (conversation instanceof ConversationChat && The5zigMod.getDataManager().getProfile().isShowMessageRead()) {
				The5zigMod.getNetworkManager().sendPacket(new PacketMessageFriendStatus(((ConversationChat) conversation).getFriendUUID(), Message.MessageStatus.READ));
			}
		}

		queueStatement("UPDATE " + getTableNameByConversation(conversation) + " SET read=? WHERE id=?", read, conversation.getId());
	}

	/**
	 * Sorts all conversations by the time they were last accessed.
	 */
	public void sortConversations() {
		if (The5zigMod.getVars().getCurrentScreen() instanceof GuiConversations) {
			GuiConversations gui = (GuiConversations) The5zigMod.getVars().getCurrentScreen();
			final Conversation selected = gui.getSelectedConversation();
			Collections.sort(conversations);
			Collections.sort(gui.conversations);
			gui.setCurrentConversation(selected);
		} else {
			Collections.sort(conversations);
		}
	}

	/**
	 * Deletes a conversation from the database
	 *
	 * @param conversation The conversation that should be removed
	 */
	public void deleteConversation(final Conversation conversation) {
		if (The5zigMod.getVars().getCurrentScreen() instanceof GuiConversations && ((GuiConversations) The5zigMod.getVars().getCurrentScreen()).getSelectedConversation().equals(
				conversation)) {
			GuiConversations gui = (GuiConversations) The5zigMod.getVars().getCurrentScreen();
			conversations.remove(conversation);
			gui.onSelect(conversations.indexOf(gui.getSelectedConversation()), gui.getSelectedConversation(), false);
		} else {
			conversations.remove(conversation);
		}
		sortConversations();

		queueStatement("DELETE FROM " + getTableNameByConversation(conversation) + " WHERE id=?", conversation.getId());
		if (conversation instanceof ConversationAnnouncements) {
			queueStatement("TRUNCATE TABLE " + getMessagesTableNameByConversation(conversation));
		} else {
			queueStatement("DELETE FROM " + getMessagesTableNameByConversation(conversation) + " WHERE conversationid=?", conversation.getId());
		}
	}

	/**
	 * Gets the SQL Table name by a Conversation instance.
	 *
	 * @param conversation The Conversation.
	 * @return The SQL Table name by the conversation.
	 */
	public static String getTableNameByConversation(Conversation conversation) {
		String table = TABLE_CHAT;
		if (conversation instanceof ConversationGroupChat)
			table = TABLE_GROUP_CHAT;
		if (conversation instanceof ConversationAnnouncements)
			table = TABLE_ANNOUNCEMENTS;
		return table;
	}

	/**
	 * Gets the Messages SQL Table name by a Conversation instance.
	 *
	 * @param conversation The Conversation.
	 * @return The Messages SQL Table name by the conversation.
	 */
	public static String getMessagesTableNameByConversation(Conversation conversation) {
		String table = TABLE_CHAT_MESSAGES;
		if (conversation instanceof ConversationGroupChat)
			table = TABLE_GROUP_CHAT_MESSAGES;
		if (conversation instanceof ConversationAnnouncements)
			table = TABLE_ANNOUNCEMENTS_MESSAGES;
		return table;
	}

}
