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

package eu.the5zig.mod.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.GroupMember;
import eu.the5zig.mod.chat.entity.*;
import eu.the5zig.mod.chat.gui.AudioChatLine;
import eu.the5zig.mod.chat.gui.ChatLine;
import eu.the5zig.mod.chat.gui.ViewMoreRow;
import eu.the5zig.mod.gui.elements.*;
import eu.the5zig.mod.render.Base64Renderer;
import eu.the5zig.mod.util.AudioCallback;
import eu.the5zig.mod.util.FileSelectorCallback;
import eu.the5zig.mod.util.GuiListChatCallback;
import eu.the5zig.mod.util.Keyboard;
import eu.the5zig.util.Callback;
import eu.the5zig.util.Utils;
import eu.the5zig.util.minecraft.ChatColor;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;

public class GuiConversations extends Gui implements Clickable<Conversation> {

	private static int lastSelected = 0;
	private static float currentScroll = -1;
	private static final Base64Renderer base64Renderer = new Base64Renderer();
	public final List<Conversation> conversations = Lists.newArrayList();
	private String searchText;
	/**
	 * List with all chatLines
	 */
	public final List<ChatLine> chatLines = Collections.synchronizedList(new ArrayList<ChatLine>());
	protected IGuiList<Conversation> conversationList;
	public IGuiList<? extends Row> chatList;
	private int chatboxWidth = 100;

	@SuppressWarnings("unused")
	private AudioCallback audioCallback;

	public GuiConversations(Gui lastScreen) {
		super(lastScreen instanceof GuiFriends ? lastScreen : new GuiFriends(lastScreen));
		this.audioCallback = new AudioCallback() {
			@Override
			public void done(File audioFile) {
				Conversation selectedConversation = getSelectedConversation();
				if (selectedConversation == null || !(selectedConversation instanceof ConversationChat))
					return;
				if (!The5zigMod.getNetworkManager().isConnected())
					return;
				The5zigMod.getConversationManager().sendAudio(((ConversationChat) selectedConversation).getFriendUUID(), audioFile);
			}
		};
	}

	@Override
	public void initGui() {
		addButton(The5zigMod.getVars().createButton(1, getWidth() - 80 - 10, getHeight() - 26, 80, 20, I18n.translate("chat.send")));
		addButton(The5zigMod.getVars().createButton(21, 2, getHeight() - 16 - 10, 98, 20, I18n.translate("chat.conversation_settings")));
		addTextField(The5zigMod.getVars().createTextfield(300, 100 + 10, getHeight() - 26, getWidth() - (100 + 10) - 80 - 10 - 5, 20, 256));

		this.conversationList = The5zigMod.getVars().createGuiList(this, 110, getHeight(), 56, getHeight() - 16 - 36, 0, 100, conversations);
		conversationList.setLeftbound(true);
		conversationList.setScrollX(95);
		this.chatList = The5zigMod.getVars().createGuiListChat(getWidth(), getHeight(), 60, getHeight() - 26 - 12, 100 + 10, getWidth() - 10, getWidth() - 15, chatLines,
				new GuiListChatCallback() {
					@Override
					public boolean drawDefaultBackground() {
						return false;
					}

					@Override
					public Object getResourceLocation() {
						return The5zigMod.getDataManager().getChatBackgroundManager().getChatBackground();
					}

					@Override
					public int getImageWidth() {
						return The5zigMod.getDataManager().getChatBackgroundManager().getImageWidth();
					}

					@Override
					public int getImageHeight() {
						return The5zigMod.getDataManager().getChatBackgroundManager().getImageWidth();
					}

					@Override
					public void chatLineClicked(Row row, int mouseX, int y, int minY, int left) {
						ChatLine chatLine = (ChatLine) row;
						List<String> lines = The5zigMod.getVars().splitStringToWidth(chatLine.getMessage().toString(), chatLine.getMaxMessageWidth());
						int yy = 0;
						for (int i1 = 0, linesSize = lines.size(); i1 < linesSize; i1++) {
							String line = lines.get(i1);
							int minChatLineX = chatLine.getMessage().getMessageType() == Message.MessageType.LEFT ? left + 4 : getWidth() - 22 - The5zigMod.getVars().getStringWidth(line);
							if (i1 == linesSize - 1) {
								String time = ChatColor.GRAY + Utils.convertToTimeWithMinutes(chatLine.getMessage().getTime());
								int timeWidth = (int) (The5zigMod.getVars().getStringWidth(time) * chatLine.STATUS_SCALE);
								if (chatLine.getMessage().getMessageType() == Message.MessageType.RIGHT)
									minChatLineX -= timeWidth + 6;
							}
							int maxChatLineX = minChatLineX + The5zigMod.getVars().getStringWidth(line);
							int minChatLineY = minY + yy;
							int maxChatLineY = minChatLineY + 9;
							if (mouseX >= minChatLineX && mouseX <= maxChatLineX && y > minChatLineY && y <= maxChatLineY) {
								String[] words = line.split(" "); // Split the Line into Words
								StringBuilder builder = new StringBuilder();
								for (String word : words) {
									builder.append(word);
									int wordX = The5zigMod.getVars().getStringWidth(builder.toString()) + minChatLineX;
									if (wordX >= mouseX && wordX <= mouseX + The5zigMod.getVars().getStringWidth(word)) {
										// loop through all urls that have been found in the message and look, if it contains the current word.
										for (String url : Utils.matchURL(chatLine.getMessage().toString())) {
											if (url.contains(ChatColor.stripColor(word))) {
												Utils.openURL(url);
												return;
											}
										}
										// Open URL if found in current word.
										Utils.openURLIfFound(ChatColor.stripColor(word));
										break;
									}
									builder.append(" ");
								}
								break;
							}

							yy += chatLine.LINE_HEIGHT;
						}
					}
				});
		this.chatboxWidth = Math.max(100, getWidth() - 10 - (100 + 10));
		scrollToBottom();

		IPlaceholderTextfield textfield = The5zigMod.getVars().createTextfield(I18n.translate("gui.search"), 9991, 2, 36, 96, 16);
		if (!Strings.isNullOrEmpty(searchText)) {
			textfield.callSetText(searchText);
		}
		addTextField(textfield);

		if (currentScroll > -1) {
			chatList.scrollTo(currentScroll);
		}

		addButton(The5zigMod.getVars().createButton(200, 8, 6, 50, 20, I18n.translate("gui.back")));

		tick();
		conversationList.setSelectedId(lastSelected);
		conversationList.onSelect(conversationList.getRows().indexOf(getSelectedConversation()), getSelectedConversation(), false);
	}

	@Override
	public void onSelect(int id, Conversation row, boolean doubleClick) {
		synchronized (chatLines) {
			chatLines.clear();
		}
		if (row == null)
			return;
		lastSelected = conversationList.getRows().indexOf(row);
		getTextfieldById(300).callSetText(row.getCurrentMessage());
		getButtonById(1).setEnabled(false);
		List<Message> messages = row.getMessages();
		if (row.hasUnloadedMessages()) {
			synchronized (chatLines) {
				chatLines.add(new ViewMoreRow(100 + getChatBoxWidth() / 2, new Callback<IButton>() {
					private boolean pressed = false;

					@Override
					public void call(IButton button) {
						if (pressed)
							return;
						pressed = true;
						Conversation conversation = getSelectedConversation();
						final IGuiList guiList = chatList;
						final float currentScroll = guiList.callGetContentHeight() - guiList.getCurrentScroll(); // store the current scroll. Normally, somewhere around 0.0.
						The5zigMod.getConversationManager().increaseMaxMessages(conversation, new Runnable() {
							@Override
							public void run() {
								// restart.
								conversationList.onSelect(conversationList.getRows().indexOf(getSelectedConversation()), getSelectedConversation(), false); // recalculate all rows.
//								guiList.calculateHeightMap(); // calculate heightmap.
								float newScroll = guiList.getCurrentScroll() - currentScroll; // get position of heighest row before the increase simply by using the heightmap.
								guiList.scrollTo(newScroll); // scroll to calculated height.
							}
						}); // change max messages count. Do not store it in SQL, since it should reset on
					}
				}));
			}
		}

		boolean overMax = messages.size() > row.getMaxMessages();
		int start = overMax ? messages.size() - row.getMaxMessages() : 0;
		int end = messages.size();
		synchronized (chatLines) {
			for (int i1 = start; i1 < end; i1++) {
				Message message = messages.get(i1);
				chatLines.add(ChatLine.fromMessage(message));
			}
		}

		scrollToBottom();
		The5zigMod.getConversationManager().setConversationRead(row, true);
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		this.conversationList.mouseClicked(x, y);
		chatList.mouseClicked(x, y);
		super.mouseClicked(x, y, button);
	}

	/**
	 * Handle mouse input
	 */
	@Override
	public void handleMouseInput() {
		conversationList.callHandleMouseInput();
		chatList.callHandleMouseInput();
	}

	@Override
	public void mouseDragged0(double v, double v1, int i, double v2, double v3) {
		super.mouseDragged0(v, v1, i, v2, v3);
		conversationList.callMouseDragged(v, v1, i, v2, v3);
		chatList.callMouseDragged(v, v1, i, v2, v3);
	}

	@Override
	public void mouseScrolled0(double v) {
		super.mouseScrolled0(v);
		conversationList.callMouseScrolled(v);
		chatList.callMouseScrolled(v);
	}

	@Override
	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
		// draw guilists
		this.conversationList.callDrawScreen(mouseX, mouseY, partialTicks);
		IGuiList guiList = chatList;
		guiList.callDrawScreen(mouseX, mouseY, partialTicks);
		if (getSelectedConversation() != null && getSelectedConversation() instanceof ConversationChat)
			drawAvatar();
		int x = getSelectedConversation() == null || (!(getSelectedConversation() instanceof ConversationChat)) ? 110 : 135;
		The5zigMod.getVars().drawString(getConversationName(), x, 36);
		The5zigMod.getVars().drawString(getConversationDescription(), x, 48);
	}

	private void drawAvatar() {
		if (getSelectedConversation() == null || !(getSelectedConversation() instanceof ConversationChat))
			return;
		ConversationChat conversationChat = (ConversationChat) getSelectedConversation();
		String base64EncodedSkin = The5zigMod.getSkinManager().getBase64EncodedSkin(conversationChat.getFriendUUID());
		if (base64Renderer.getBase64String() != null && base64EncodedSkin == null) {
			base64Renderer.reset();
		} else if (base64EncodedSkin != null && !base64EncodedSkin.equals(base64Renderer.getBase64String())) {
			base64Renderer.setBase64String(base64EncodedSkin, "player_skin/" + conversationChat.getFriendUUID());
		}
		int width = 20, height = 20;
		base64Renderer.renderImage(110, 36, width, height);
	}

	private String getConversationName() {
		Conversation selectedConversation = getSelectedConversation();
		if (selectedConversation == null)
			return I18n.translate("chat.no_conversations");
		if (selectedConversation instanceof ConversationChat) {
			String name = ((ConversationChat) selectedConversation).getFriendName();
			UUID uuid = ((ConversationChat) selectedConversation).getFriendUUID();
			if (!The5zigMod.getFriendManager().isFriend(uuid))
				return name;
			String displayName = The5zigMod.getFriendManager().getFriend(uuid).getDisplayName();
			if (The5zigMod.getDataManager().getChatTypingManager().isTyping(uuid))
				displayName += " " + ChatColor.GRAY + I18n.translate("friend.typing");
			return displayName;
		}
		if (selectedConversation instanceof ConversationGroupChat) {
			ConversationGroupChat conversationGroupChat = (ConversationGroupChat) selectedConversation;
			Group group = The5zigMod.getGroupChatManager().getGroup(conversationGroupChat.getGroupId());
			if (group != null) {
				return group.getName();
			} else {
				return conversationGroupChat.getName();
			}
		}
		if (selectedConversation instanceof ConversationAnnouncements) {
			return I18n.translate("announcement.short_desc");
		}
		return I18n.translate("error");
	}

	private String getConversationDescription() {
		Conversation selectedConversation = getSelectedConversation();
		if (selectedConversation == null)
			return "";
		if (selectedConversation instanceof ConversationChat) {
			UUID uuid = ((ConversationChat) selectedConversation).getFriendUUID();
			if (!The5zigMod.getFriendManager().isFriend(uuid))
				return ChatColor.RED + I18n.translate("connection.offline");
			Friend friend = The5zigMod.getFriendManager().getFriend(uuid);
			String status;
			if (friend.getStatus() == Friend.OnlineStatus.AWAY)
				status = friend.getStatus().getDisplayName();
			else if (friend.getStatus() == Friend.OnlineStatus.OFFLINE)
				status = ChatColor.GRAY + ChatColor.ITALIC.toString() + I18n.translate("friend.info.last_seen", friend.getLastOnline());
			else
				status = friend.getStatus().getDisplayName();
			return status;
		}
		if (selectedConversation instanceof ConversationGroupChat) {
			ConversationGroupChat conversationGroupChat = (ConversationGroupChat) selectedConversation;
			Group group = The5zigMod.getGroupChatManager().getGroup(conversationGroupChat.getGroupId());
			if (group == null)
				return ChatColor.DARK_GRAY + I18n.translate("group.unknown");
			String result = ChatColor.GRAY + ChatColor.ITALIC.toString();
			int maxWidth = getWidth() - 130;
			List<GroupMember> members = group.getMembers();
			for (int i = 0, membersSize = members.size(); i < membersSize; i++) {
				GroupMember member = members.get(i);
				String str = " " + I18n.translate("group.more_members", (membersSize - i));
				if (The5zigMod.getVars().getStringWidth(result + member.getUsername() + str) > maxWidth) {
					result += str;
					break;
				}
				if (i > 0)
					result += ", ";
				result += member.getUsername();
			}
			return result;
		}
		if (selectedConversation instanceof ConversationAnnouncements) {
			return "";
		}
		return I18n.translate("error");
	}

	public int getChatBoxWidth() {
		return chatboxWidth;
	}

	@Override
	protected void onKeyType(char character, int key) {
		if (getSelectedConversation() == null)
			return;
		ITextfield textfield = getTextfieldById(300);
		if (key == Keyboard.KEY_UP) {
			textfield.callSetText(getSelectedConversation().getPreviousSentMessage());
		}
		if (key == Keyboard.KEY_DOWN) {
			textfield.callSetText(getSelectedConversation().getNextSentMessage());
		}
	}

	@Override
	protected void tick() {
		if (getSelectedConversation() != null) {
			getSelectedConversation().setCurrentMessage(getTextfieldById(300).callGetText());
		}
		enableDisableButtons();
		doGroupChatStuff();
		currentScroll = chatList.getCurrentScroll();

		searchText = getTextfieldById(9991).callGetText();

		conversations.clear();
		for (Conversation conversation : The5zigMod.getConversationManager().getConversations()) {
			if (searchText == null || (conversation instanceof ConversationChat && ((ConversationChat) conversation).getFriendName().toLowerCase(Locale.ROOT).contains(
					searchText.toLowerCase(Locale.ROOT))) || (conversation instanceof ConversationGroupChat && ((ConversationGroupChat) conversation).getName().toLowerCase(Locale.ROOT)
					.contains(searchText.toLowerCase(Locale.ROOT))) || (conversation instanceof ConversationAnnouncements && I18n.translate("announcement.short_desc").toLowerCase(
					Locale.ROOT).contains(searchText.toLowerCase(Locale.ROOT)))) {
				conversations.add(conversation);
			}
		}
	}

	private void doGroupChatStuff() {
		Conversation conversation = getSelectedConversation();
		IButton info = getButtonById(50);
		if (conversation == null || !(conversation instanceof ConversationGroupChat) || The5zigMod.getGroupChatManager().getGroup(((ConversationGroupChat) conversation).getGroupId()) ==
				null) {
			if (info != null)
				removeButton(info);
		} else {
			int strWidth = The5zigMod.getVars().getStringWidth(getConversationName());
			if (info == null)
				addButton(The5zigMod.getVars().createStringButton(50, 110 + strWidth + 4, 35, The5zigMod.getVars().getStringWidth(String.format("[%s]", I18n.translate("group.info"))), 10,
						String.format("[%s]", I18n.translate("group.info"))));
			else
				getButtonById(50).setX(110 + strWidth + 4);
		}
	}

	private void enableDisableButtons() {
		ITextfield textfield = getTextfieldById(300);
		IButton sendButton = getButtonById(1);
		sendButton.setEnabled(The5zigMod.getNetworkManager().isConnected() && textfield.callGetText().length() > 0 && getSelectedConversation() != null &&
				!(getSelectedConversation() instanceof ConversationAnnouncements));
		getButtonById(21).setEnabled(getSelectedConversation() != null);
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 1) {
			if (!The5zigMod.getNetworkManager().isConnected())
				return;
			Conversation conversation = getSelectedConversation();
			if (conversation == null)
				return;
			ITextfield textfield = getTextfieldById(300);
			String text = textfield.callGetText();
			text = StringUtils.normalizeSpace(text);
			if (text == null || text.isEmpty())
				return;
			if (conversation instanceof ConversationChat) {
				ConversationChat conversationChat = (ConversationChat) conversation;
				UUID friendUUID = conversationChat.getFriendUUID();
				if (The5zigMod.getFriendManager().isBlocked(friendUUID)) {
					The5zigMod.getOverlayMessage().displayMessageAndSplit(ChatColor.RED + I18n.translate("conn.block.blocked", conversationChat.getFriendName()));
					textfield.callSetText("");
					getButtonById(1).setEnabled(false);
					return;
				}
				if (!The5zigMod.getFriendManager().isFriend(friendUUID)) {
					The5zigMod.getOverlayMessage().displayMessageAndSplit(ChatColor.RED + I18n.translate("conn.user.not_friend_anymore", conversationChat.getFriendName()));
					textfield.callSetText("");
					getButtonById(1).setEnabled(false);
					return;
				}
				The5zigMod.getConversationManager().sendConversationMessage(conversationChat.getFriendUUID(), text);
				conversation.addLastSentMessage(text);
				textfield.callSetText("");
				getButtonById(1).setEnabled(false);
				if (The5zigMod.getConfig().getBool("playMessageSounds"))
					The5zigMod.getVars().playSound("the5zigmod", "chat.message.send", 1);
			}
			if (conversation instanceof ConversationGroupChat) {
				ConversationGroupChat conversationGroupChat = (ConversationGroupChat) conversation;
				Group group = The5zigMod.getGroupChatManager().getGroup(conversationGroupChat.getGroupId());
				if (group == null) {
					textfield.callSetText("");
					return;
				}
				The5zigMod.getConversationManager().sendGroupMessage(group, text);
				conversation.addLastSentMessage(text);
				textfield.callSetText("");
				getButtonById(1).setEnabled(false);
				if (The5zigMod.getConfig().getBool("playMessageSounds"))
					The5zigMod.getVars().playSound("the5zigmod", "chat.message.send", 1);
			}
		}
		if (button.getId() == 21) {
			Conversation selected = getSelectedConversation();
			if (selected == null)
				return;
			The5zigMod.getVars().displayScreen(new GuiConversationSettings(this, selected));
		}
		if (button.getId() == 50) {
			if (!(getSelectedConversation() instanceof ConversationGroupChat))
				return;
			ConversationGroupChat conversation = (ConversationGroupChat) getSelectedConversation();
			Group group = The5zigMod.getGroupChatManager().getGroup(conversation.getGroupId());
			if (group == null)
				return;
			The5zigMod.getVars().displayScreen(new GuiGroupChatInfo(this, group));
		}
		if (button.getId() == 70) {
			if (!(getSelectedConversation() instanceof ConversationChat))
				return;
			final UUID uuid = ((ConversationChat) getSelectedConversation()).getFriendUUID();
			The5zigMod.getVars().displayScreen(new GuiFileSelector(this, new FileSelectorCallback() {
				@Override
				public void onDone(File file) {
					if (!The5zigMod.getNetworkManager().isConnected())
						return;
					The5zigMod.getConversationManager().sendImage(uuid, file);
				}

				@Override
				public String getTitle() {
					return "The 5zig Mod - " + I18n.translate("chat.select_image");
				}
			}, new File(The5zigMod.getVars().getMinecraftDataDirectory(), "screenshots"), "png", "jpg"));
		}
	}

	@Override
	protected void guiClosed() {
		for (Row row : chatList.getRows()) {
			if (row instanceof AudioChatLine)
				((AudioChatLine) row).close();
		}
	}

	@Override
	public String getTitleName() {
		return ChatColor.BOLD + String.format(I18n.translate("friend.chats") + " | %s",
				The5zigMod.getNetworkManager().isConnected() ? ChatColor.GREEN + ChatColor.BOLD.toString() + I18n.translate("friend.connected") :
						ChatColor.RED + ChatColor.BOLD.toString() + I18n.translate("friend.disconnected"));
	}

	public void scrollToBottom() {
		IGuiList guiList = chatList;
		guiList.scrollToBottom();
	}

	public void setCurrentConversation(Conversation conversation) {
		synchronized (conversationList.getRows()) {
			List rows = conversationList.getRows();
			conversationList.setSelectedId(rows.indexOf(conversation));
		}
		onSelect(conversationList.getSelectedId(), conversationList.getSelectedRow(), true);
	}

	public Conversation getSelectedConversation() {
		return conversationList.getSelectedRow();
	}

	public static void resetScroll() {
		currentScroll = -1;
	}

	public enum BackgroundType {

		TRANSPARENT, IMAGE

	}
}
