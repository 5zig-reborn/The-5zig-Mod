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

package eu.the5zig.mod.gui;

import com.google.common.collect.Lists;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.GroupMember;
import eu.the5zig.mod.chat.entity.Conversation;
import eu.the5zig.mod.chat.entity.Message;
import eu.the5zig.mod.chat.gui.ChatLine;
import eu.the5zig.mod.chat.network.packets.PacketPartyStatus;
import eu.the5zig.mod.chat.party.Party;
import eu.the5zig.mod.chat.party.PartyManager;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.elements.IGuiList;
import eu.the5zig.mod.gui.elements.ITextfield;
import eu.the5zig.mod.gui.elements.Row;
import eu.the5zig.mod.util.GuiListChatCallback;
import eu.the5zig.mod.util.Keyboard;
import eu.the5zig.util.Utils;
import eu.the5zig.util.minecraft.ChatColor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class GuiParty extends Gui {

	public final List<ChatLine> chatLines = Lists.newArrayList();
	public IGuiList<ChatLine> chatList;

	public GuiParty(Gui lastScreen) {
		super(lastScreen instanceof GuiFriends ? lastScreen : new GuiFriends(lastScreen));
	}

	@Override
	public void initGui() {
		PartyManager partyManager = The5zigMod.getPartyManager();
		Party party = partyManager.getParty();
		if (party != null) {
			for (GroupMember groupMember : party.getMembers()) {
				groupMember.setMaxWidth(98);
			}

			chatList = The5zigMod.getVars().createGuiListChat(getWidth(), getHeight(), 54, getHeight() - 30, 0, getWidth() - 110, getWidth() - 115, chatLines, new GuiListChatCallback() {
				@Override
				public boolean drawDefaultBackground() {
					return false;
				}

				@Override
				public Object getResourceLocation() {
					return null;
				}

				@Override
				public int getImageWidth() {
					return 0;
				}

				@Override
				public int getImageHeight() {
					return 0;
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
			addGuiList(chatList);

			IGuiList<GroupMember> memberList = The5zigMod.getVars().createGuiList(null, 220, getHeight(), 32, getHeight() - 4, getWidth() - 100, getWidth() - 2, party.getMembers());
			memberList.setHeader(I18n.translate("party.members"));
			memberList.callSetHeaderPadding(8);
			memberList.setRowWidth(220);
			memberList.setDrawSelection(false);
			memberList.setScrollX(getWidth() - 7);
			addGuiList(memberList);

			addTextField(The5zigMod.getVars().createTextfield(1, 4, getHeight() - 24, getWidth() - 180, 20, 256));
			getTextfieldById(1).callSetText(party.getPartyConversation().getCurrentMessage());
			addButton(The5zigMod.getVars().createButton(100, getWidth() - 170, getHeight() - 24, 60, 20, I18n.translate("chat.send")));
			getButtonById(100).setEnabled(false);

			boolean admin = false;
			for (GroupMember groupMember : party.getMembers()) {
				if (groupMember.getUniqueId().equals(The5zigMod.getDataManager().getUniqueId())) {
					admin = groupMember.isAdmin();
					break;
				}
			}
			if (admin || party.getOwner().getUniqueId().equals(The5zigMod.getDataManager().getUniqueId())) {
				addButton(The5zigMod.getVars().createButton(3, getWidth() - 110 - 200, 30, 100, 20, I18n.translate("party.manage_members")));
			}
			addButton(The5zigMod.getVars().createButton(4, getWidth() - 110 - 100, 30, 100, 20,
					party.getOwner().getUniqueId().equals(The5zigMod.getDataManager().getUniqueId()) ? I18n.translate("party.delete") : I18n.translate("party.leave")));
			reloadChatMessages();
		} else {
			addButton(The5zigMod.getVars().createButton(1, getWidth() / 2 - 152, getHeight() / 2 + 20, 150, 20, I18n.translate("party.create")));
			addButton(
					The5zigMod.getVars().createButton(2, getWidth() / 2 + 2, getHeight() / 2 + 20, 150, 20, I18n.translate("party.invitations", partyManager.getPartyInvitations().size())));
		}

		addButton(The5zigMod.getVars().createButton(200, 8, 6, 50, 20, I18n.translate("gui.back")));

	}

	private void reloadChatMessages() {
		chatLines.clear();
		Party party = The5zigMod.getPartyManager().getParty();
		if (party == null) {
			return;
		}
		Conversation conversation = party.getPartyConversation();
		List<Message> messages = conversation.getMessages();

		boolean overMax = messages.size() > conversation.getMaxMessages();
		int start = overMax ? messages.size() - conversation.getMaxMessages() : 0;
		int end = messages.size();
		for (int i1 = start; i1 < end; i1++) {
			Message message = messages.get(i1);
			chatLines.add(ChatLine.fromMessage(message));
		}

		chatList.scrollToBottom();
	}

	public int getChatBoxWidth() {
		return Math.max(100, getWidth() - 110);
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 1) {
			The5zigMod.getVars().displayScreen(new GuiPartyInviteMembers(this));
		} else if (button.getId() == 2) {
			The5zigMod.getVars().displayScreen(new GuiPartyInvitations(this));
		} else if (button.getId() == 3) {
			The5zigMod.getVars().displayScreen(new GuiPartyManageMembers(this, The5zigMod.getPartyManager().getParty()));
		} else if (button.getId() == 4) {
			The5zigMod.getPartyManager().setParty(null);
			The5zigMod.getNetworkManager().sendPacket(new PacketPartyStatus(PacketPartyStatus.Action.DELETE));
			The5zigMod.getScheduler().postToMainThread(new Runnable() {
				@Override
				public void run() {
					initGui0();
				}
			}, true);
		} else if (button.getId() == 100) {
			if (!The5zigMod.getNetworkManager().isConnected())
				return;
			Party party = The5zigMod.getPartyManager().getParty();
			if (party == null) {
				return;
			}
			Conversation conversation = party.getPartyConversation();
			ITextfield textfield = getTextfieldById(1);
			String text = textfield.callGetText();
			text = StringUtils.normalizeSpace(text);
			if (text == null || text.isEmpty())
				return;
			The5zigMod.getNetworkManager().sendPacket(new PacketPartyStatus(PacketPartyStatus.Action.CHAT, text));
			Message message = new Message(conversation, 0, The5zigMod.getDataManager().getColoredName(), text, System.currentTimeMillis(), Message.MessageType.RIGHT);
			conversation.addLastSentMessage(text);
			The5zigMod.getPartyManager().addMessage(message);
			textfield.callSetText("");
			getButtonById(100).setEnabled(false);
		}
	}

	@Override
	protected void onKeyType(char character, int key) {
		Party party = The5zigMod.getPartyManager().getParty();
		if (party == null) {
			return;
		}
		Conversation conversation = party.getPartyConversation();
		ITextfield textfield = getTextfieldById(1);
		if (key == Keyboard.KEY_UP) {
			textfield.callSetText(conversation.getPreviousSentMessage());
		}
		if (key == Keyboard.KEY_DOWN) {
			textfield.callSetText(conversation.getNextSentMessage());
		}
	}

	@Override
	protected void tick() {
		IButton button = getButtonById(2);
		if (button != null) {
			button.setLabel(I18n.translate("party.invitations", The5zigMod.getPartyManager().getPartyInvitations().size()));
		}
		Party party = The5zigMod.getPartyManager().getParty();
		if (party != null) {
			party.getPartyConversation().setCurrentMessage(getTextfieldById(1).callGetText());
			getButtonById(100).setEnabled(The5zigMod.getNetworkManager().isConnected() && getTextfieldById(1).callGetText().length() > 0);
		}
	}

	@Override
	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
		Party party = The5zigMod.getPartyManager().getParty();
		if (party == null) {
			List<String> splitStringToWidth = The5zigMod.getVars().splitStringToWidth(I18n.translate("party.help"), getWidth() - 60);
			for (int i = 0, splitStringToWidthSize = splitStringToWidth.size(); i < splitStringToWidthSize; i++) {
				String line = splitStringToWidth.get(i);
				drawCenteredString(line, getWidth() / 2, getHeight() / 6 + 30 + i * 12);
			}
		} else {
			The5zigMod.getVars().drawString(I18n.translate("party.from", party.getOwner().getUsername()), 4, 40);
		}
	}

	@Override
	public String getTitleKey() {
		return "party.title";
	}
}
