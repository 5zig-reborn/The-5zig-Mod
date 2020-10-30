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

package eu.the5zig.mod.gui.ts;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.gui.ChatLine;
import eu.the5zig.mod.config.items.BoolItem;
import eu.the5zig.mod.gui.Gui;
import eu.the5zig.mod.gui.GuiSettings;
import eu.the5zig.mod.gui.GuiTeamSpeakAuth;
import eu.the5zig.mod.gui.elements.*;
import eu.the5zig.mod.gui.list.GuiArrayList;
import eu.the5zig.mod.gui.ts.entries.*;
import eu.the5zig.mod.gui.ts.menu.*;
import eu.the5zig.mod.gui.ts.rows.TeamSpeakBannerRow;
import eu.the5zig.mod.gui.ts.rows.TeamSpeakChatLine;
import eu.the5zig.mod.render.Base64Renderer;
import eu.the5zig.mod.util.GLUtil;
import eu.the5zig.mod.util.GuiListChatCallback;
import eu.the5zig.mod.util.Keyboard;
import eu.the5zig.mod.util.Rectangle;
import eu.the5zig.teamspeak.TeamSpeak;
import eu.the5zig.teamspeak.api.*;
import eu.the5zig.util.Utils;
import eu.the5zig.util.minecraft.ChatColor;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Map;

public class GuiTeamSpeak extends Gui {

	private static final int OUTLINE_COLOR = 0xbbaaaaaa;

	/**
	 * Reference to TeamSpeak Client API
	 */
	private static final TeamSpeakClient teamSpeakClient = TeamSpeak.getClient();

	/**
	 * Current server info. Gets reset every tick.
	 */
	private ServerInfo serverInfo;
	/**
	 * Current own client info. Gets reset every tick.
	 */
	private Client self;
	/**
	 * All TS list entries. Gets reset every tick.
	 */
	private final List<GuiTeamSpeakEntry> entries = Lists.newArrayList();
	/**
	 * The height of each entry.
	 */
	private static final int ENTRY_HEIGHT = 12;
	/**
	 * Indicates how many entries can be rendered at once.
	 */
	private int entriesPerPage;

	// Bounds of all elements
	private Rectangle tabBox;
	private Rectangle ownStatusBox;
	private Rectangle channelBox;
	private Rectangle channelScrollBox;
	private Rectangle descriptionBox;
	private Rectangle chatBox;

	// Mouse location
	private int mouseX;
	private int mouseY;

	private TabView<ServerTab> serverTabView;
	private TabView<Chat> chatTabView;
	private Chat previousChat;
	/**
	 * The most top entry of the current view.
	 */
	private static GuiTeamSpeakEntry topEntry;
	/**
	 * The index of the most top entry of the current view.
	 */
	private static int topIndex;
	/**
	 * The selected entry.
	 */
	private static GuiTeamSpeakEntry selectedEntry;
	private int selectedEntryX, selectedEntryY;
	private boolean draggingSelectedEntry;
	/**
	 * Time time in millis of the first click.
	 */
	private long lastTimeEntryClicked;
	/**
	 * A List containing all collapsed channels.
	 */
	public static final List<Channel> collapsedChannels = Lists.newArrayList();
	public static boolean collapseAllChannels = false;

	/**
	 * Indicates, whether the player is currently scrolling though the entry list.
	 */
	private boolean scrolling;

	private final List<TeamSpeakButton> teamSpeakButtons = Lists.newArrayList();

	private IGuiList<Row> descriptionList;
	private IGuiList<ChatLine> chatList;

	private static final Map<Class<? extends GuiTeamSpeakEntry>, GuiTeamSpeakContextMenu> contextMenus = Maps.newLinkedHashMap();
	private GuiTeamSpeakContextMenu openedContextMenu;
	private Rectangle contextMenuBox;

	private boolean displayedAuthGui = false;

	public GuiTeamSpeak(Gui lastScreen) {
		super(lastScreen);
	}

	@Override
	public void initGui() {
		tabBox = new Rectangle(65, 12, getWidth() - 5 - 65 - 80, 16);
		ownStatusBox = new Rectangle(tabBox.getX() + tabBox.getWidth() + 2, tabBox.getY(), 76, 16);
		channelBox = new Rectangle(5, 40, getWidth() / 2 + 30, getHeight() - 40 - 100);
		channelScrollBox = new Rectangle(channelBox.getX() + channelBox.getWidth() + 2, channelBox.getY(), 8, channelBox.getHeight());
		descriptionBox = new Rectangle(channelScrollBox.getX() + channelScrollBox.getWidth() + 2, channelBox.getY(), getWidth() - (channelScrollBox.getX() + channelScrollBox.getWidth()) - 7,
				channelBox.getHeight());
		chatBox = new Rectangle(5, channelBox.getY() + channelBox.getHeight(), getWidth() - 10, getHeight() - 20 - (channelBox.getY() + channelBox.getHeight()));
		entriesPerPage = channelBox.getHeight() / ENTRY_HEIGHT;

		serverTabView = new TabView<ServerTab>(tabBox.getX(), tabBox.getY(), tabBox.getWidth(), 100, tabBox.getHeight(), 0xffaaaaaa, 0xff333333) {
			@Override
			protected void drawElement(ServerTab serverTab, int x, int y, int maxWidth) {
				y += 4;
				x += 2;
				maxWidth -= 4;
				if (serverTab.getServerInfo() == null) {
					The5zigMod.getVars().drawString(The5zigMod.getVars().shortenToWidth(I18n.translate("teamspeak.not_connected"), maxWidth), x, y);
				} else {
					String title = serverTab.getServerInfo().getName() == null ? serverTab.getServerInfo().getIp() : serverTab.getServerInfo().getName();
					if (serverTab.getSelf() != null) {
						The5zigMod.getVars().bindTexture(The5zigMod.TEAMSPEAK_ICONS);
						GuiTeamSpeakClient.drawSpeechBubble(serverTab.getSelf(), x, y - 1);
						The5zigMod.getVars().drawString(The5zigMod.getVars().shortenToWidth(title, maxWidth - 14), x + 14, y);
					} else {
						The5zigMod.getVars().drawString(The5zigMod.getVars().shortenToWidth(title, maxWidth), x, y);
					}
				}
			}

			@Override
			protected void onSelectElement(ServerTab element) {
				element.setSelected();
			}

			@Override
			protected boolean canBeClosed(ServerTab element) {
				return false;
			}
		};

		chatList = The5zigMod.getVars().createGuiListChat(getWidth(), getHeight(), chatBox.getY(), chatBox.getY() + chatBox.getHeight(), chatBox.getX(), chatBox.getX() + chatBox.getWidth(),
				getWidth() - 10, Lists.<ChatLine>newArrayList(), new GuiListChatCallback() {
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
						TeamSpeakChatLine chatLine = (TeamSpeakChatLine) row;
						List<String> lines = The5zigMod.getVars().splitStringToWidth(chatLine.getMessageContent(), chatBox.getWidth() - 8);
						int yy = 0;
						for (String line : lines) {
							int maxChatLineX = left + The5zigMod.getVars().getStringWidth(line);
							int minChatLineY = minY + yy;
							int maxChatLineY = minChatLineY + 9;
							if (mouseX >= left && mouseX <= maxChatLineX && y > minChatLineY && y <= maxChatLineY) {
								String[] words = line.split(" "); // Split the Line into Words
								StringBuilder builder = new StringBuilder();
								for (String word : words) {
									builder.append(word);
									int wordX = The5zigMod.getVars().getStringWidth(builder.toString()) + left;
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
		chatList.setLeftbound(true);
		chatList.setBottomPadding(10);
		addGuiList(chatList);

		chatTabView = new TabView<Chat>(chatBox.getX(), chatBox.getY() + chatBox.getHeight() - 10, chatBox.getWidth(), 100, 10, 0xffaaaaaa, 0xff333333) {
			@Override
			protected void drawElement(Chat element, int x, int y, int maxWidth) {
				switch (element.getType()) {
					case CLIENT:
						PrivateChat privateChat = (PrivateChat) element;
						The5zigMod.getVars().bindTexture(The5zigMod.TEAMSPEAK_ICONS);
						Gui.drawModalRectWithCustomSizedTexture(x + 2, y, 5 * 128 / 12, 7 * 128 / 12, 128 / 12, 128 / 12, 2048 / 12, 2048 / 12);
						The5zigMod.getVars().drawString(The5zigMod.getVars().shortenToWidth(privateChat.getClient().getName(), maxWidth - 18), x + 16, y + 2);
						break;
					case CHANNEL:
						if (self != null && self.getChannel() != null) {
							The5zigMod.getVars().bindTexture(The5zigMod.TEAMSPEAK_ICONS);
							Gui.drawModalRectWithCustomSizedTexture(x + 2, y, 6 * 128 / 12, 1 * 128 / 12, 128 / 12, 128 / 12, 2048 / 12, 2048 / 12);
							x += 14;
							maxWidth -= 14;
							The5zigMod.getVars().drawString(The5zigMod.getVars().shortenToWidth(self.getChannel().getName(), maxWidth - 4), x + 2, y + 2);
						} else {
							The5zigMod.getVars().drawString(I18n.translate("teamspeak.chat.channel"), x + 2, y + 2);
						}
						break;
					case SERVER:
						if (serverInfo != null) {
							if (serverInfo.getIcon() != null) {
								Base64Renderer renderer = Base64Renderer.getRenderer(serverInfo.getIcon(), "ts/server_" + serverInfo.getUniqueId());
								renderer.renderImage(x + 2, y, 10, 10);
								x += 14;
								maxWidth -= 14;
							}
							String title = serverInfo.getName() == null ? serverInfo.getIp() : serverInfo.getName();
							The5zigMod.getVars().drawString(The5zigMod.getVars().shortenToWidth(title, maxWidth - 4), x + 2, y + 2);
						} else {
							The5zigMod.getVars().drawString(I18n.translate("teamspeak.chat.server"), x + 2, y + 2);
						}
						break;
					case POKE:
						The5zigMod.getVars().drawString(The5zigMod.getVars().shortenToWidth(I18n.translate("teamspeak.poke_chat"), maxWidth - 4), x + 2, y + 2);
						break;
					default:
						break;
				}
			}

			@Override
			protected void onSelectElement(Chat element) {
			}

			@Override
			protected boolean canBeClosed(Chat element) {
				return element.getType() == MessageTargetMode.CLIENT || element.getType() == MessageTargetMode.POKE;
			}

			@Override
			protected void onClose(Chat element) {
				ServerTab selectedTab = TeamSpeak.getClient().getSelectedTab();
				if (selectedTab != null) {
					if (element.getType() == MessageTargetMode.CLIENT) {
						selectedTab.removePrivateChat((PrivateChat) element);
					} else {
						selectedTab.resetPokeChat();
					}
				}
			}
		};

		descriptionList = The5zigMod.getVars().createGuiList(null, descriptionBox.getWidth(), getHeight(), descriptionBox.getY(), descriptionBox.getY() + descriptionBox.getHeight(),
				descriptionBox.getX(), descriptionBox.getX() + descriptionBox.getWidth(), new GuiArrayList<>());
		descriptionList.setDrawSelection(false);
		descriptionList.setScrollX(descriptionBox.getX() + descriptionBox.getWidth() - 5);
		descriptionList.setLeftbound(true);
		addGuiList(descriptionList);

		addTextField(The5zigMod.getVars().createTextfield(100, chatBox.getX(), chatBox.getY() + chatBox.getHeight() + 4, chatBox.getWidth() - 52, 12, 1024));
		addButton(The5zigMod.getVars().createButton(100, chatBox.getX() + chatBox.getWidth() - 50, chatBox.getY() + chatBox.getHeight() + 1, 50, 18, I18n.translate("chat.send")));

		teamSpeakButtons.clear();
		// away
		teamSpeakButtons.add(new TeamSpeakButton(ownStatusBox.getX(), ownStatusBox.getY(), 10, 0) {
			@Override
			public void onClick() {
				ServerTab selectedTab = TeamSpeak.getClient().getSelectedTab();
				if (selectedTab == null || selectedTab.getSelf() == null) {
					return;
				}
				selectedTab.getSelf().setAway(!isSelected());
			}

			@Override
			protected boolean isSelected() {
				ServerTab selectedTab = TeamSpeak.getClient().getSelectedTab();
				return selectedTab != null && selectedTab.getSelf() != null && selectedTab.getSelf().isAway();
			}
		});
		// input deactivated
		// currently not working with client query ;(
//		teamSpeakButtons.add(new GuiTeamSpeakButton(ownStatusBox.getX() + 20, ownStatusBox.getY(), 4, 0) {
//			@Override
//			public void onClick() {
//				ServerTab selectedTab = TeamSpeak.getClient().getSelectedTab();
//				if (selectedTab == null || selectedTab.getSelf() == null) {
//					return;
//				}
//				selectedTab.getSelf().setInputDeactivated(false);
//			}
//
//			@Override
//			protected boolean callIsSelected() {
//				return false;
//			}
//
//			@Override
//			protected boolean isEnabled() {
//				ServerTab selectedTab = TeamSpeak.getClient().getSelectedTab();
//				return selectedTab != null && selectedTab.getSelf() != null && !selectedTab.getSelf().hasInputHardware();
//			}
//		});
		// input muted
		teamSpeakButtons.add(new TeamSpeakButton(ownStatusBox.getX() + 20, ownStatusBox.getY(), 6, 5) {
			@Override
			public void onClick() {
				ServerTab selectedTab = TeamSpeak.getClient().getSelectedTab();
				if (selectedTab == null || selectedTab.getSelf() == null) {
					return;
				}
				selectedTab.getSelf().setInputMuted(!isSelected());
			}

			@Override
			protected boolean isSelected() {
				ServerTab selectedTab = TeamSpeak.getClient().getSelectedTab();
				return selectedTab != null && selectedTab.getSelf() != null && selectedTab.getSelf().isInputMuted();
			}
		});
		// output muted
		teamSpeakButtons.add(new TeamSpeakButton(ownStatusBox.getX() + 40, ownStatusBox.getY(), 8, 6) {
			@Override
			public void onClick() {
				ServerTab selectedTab = TeamSpeak.getClient().getSelectedTab();
				if (selectedTab == null || selectedTab.getSelf() == null) {
					return;
				}
				selectedTab.getSelf().setOutputMuted(!isSelected());
			}

			@Override
			protected boolean isSelected() {
				ServerTab selectedTab = TeamSpeak.getClient().getSelectedTab();
				return selectedTab != null && selectedTab.getSelf() != null && selectedTab.getSelf().isOutputMuted();
			}
		});
		// settings
		teamSpeakButtons.add(new TeamSpeakButton(ownStatusBox.getX() + 60, ownStatusBox.getY(), 11, 8) {
			@Override
			protected void onClick() {
				The5zigMod.getVars().displayScreen(new GuiSettings(GuiTeamSpeak.this, "teamspeak"));
			}

			@Override
			protected boolean isSelected() {
				return false;
			}
		});

		if (!The5zigMod.getConfig().getBool("tsEnabled")) {
			addButton(The5zigMod.getVars().createButton(50, getWidth() / 2 - 100, chatBox.getY() + chatBox.getHeight() - 30, I18n.translate("teamspeak.enable_button")));
		}

		addButton(The5zigMod.getVars().createButton(200, 4, 10, 50, 20, The5zigMod.getVars().translate("gui.done")));
		scrollTo(0);
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 100) {
			ITextfield textfield = getTextfieldById(100);
			if (textfield.callGetText().length() <= 1024 && chatTabView.selectedElement != null && chatTabView.selectedElement.getType() != MessageTargetMode.POKE) {
				chatTabView.selectedElement.sendMessage(textfield.callGetText());
				textfield.callSetText("");
			}
		}
		if (button.getId() == 50) {
			BoolItem item = The5zigMod.getConfig().get("tsEnabled", BoolItem.class);
			item.set(true);
			item.action();
			The5zigMod.getConfig().save();
			initGui0();
		}
	}

	@Override
	protected void tick() {
		this.serverTabView.elements.clear();
		ServerTab serverTab = teamSpeakClient.getSelectedTab();
		if (serverTab != null) {
			this.serverTabView.elements.addAll(teamSpeakClient.getServerTabs());
			ServerTab lastTab = this.serverTabView.selectedElement;
			this.serverTabView.selectedElement = serverTab;
			if (serverTab.getServerInfo() != null) {
				this.serverInfo = serverTab.getServerInfo();
				this.self = serverTab.getSelf();
				TeamSpeakEntryList entryList = findEntries(serverTab.getChannels());
				collapseAllChannels = false;
				this.entries.clear();
				this.entries.add(new GuiTeamSpeakServer(this.serverInfo, entryList.getChannelCount(), entryList.getClientCount()));
				this.entries.addAll(entryList.getEntries());

				if (!this.entries.isEmpty()) {
					if ((topEntry == null || (lastTab != null && lastTab != serverTab)) && self != null) {
						topEntry = createChannelEntry(self.getChannel());
					}
					if (topEntry != null) {
						int index = this.entries.indexOf(topEntry);
						if (index == -1) {
							topIndex = Math.max(0, Math.min(topIndex, this.entries.size() - 1));
							topEntry = this.entries.get(topIndex);
						} else {
							if (index + entriesPerPage > this.entries.size()) {
								topIndex = Math.max(0, this.entries.size() - entriesPerPage);
							} else {
								topIndex = index;
							}
						}
					}
				} else {
					topIndex = 0;
					topEntry = null;
				}

				descriptionList.getRows().clear();
				descriptionList.getRows().add(new TeamSpeakBannerRow(serverInfo, descriptionBox.getWidth() - 10));
				if ((selectedEntry == null || (lastTab != null && lastTab != serverTab)) && self != null) {
					int index = this.entries.indexOf(new GuiTeamSpeakClientSelf(self, serverInfo.getUniqueId()));
					if (index != -1) {
						selectedEntry = this.entries.get(index);
					}
				}
				if (selectedEntry != null) {
					int index = entries.indexOf(selectedEntry); // all entries are recreated every tick, while selected entry stays the same. this is a little workaround...
					if (index == -1) {
						selectedEntry = this.entries.get(0);
						index = 0;
					}
					List<? extends Row> description = entries.get(index).getDescription(descriptionList.getWidth() - 10);
					if (description != null) {
						descriptionList.getRows().addAll(description);
					}
				}

				int previousContentHeight = chatList.callGetContentHeight();
				long lastMessageTime = chatList.getRows().isEmpty() ? 0 : chatList.getRows().get(chatList.getRows().size() - 1).getMessage().getTime();
				chatList.getRows().clear();
				int previousSize = chatTabView.elements.size();
				chatTabView.elements.clear();
				chatTabView.elements.add(serverTab.getServerChat());
				chatTabView.elements.add(serverTab.getChannelChat());
				if (!serverTab.getPokeChat().getMessages().isEmpty()) {
					chatTabView.elements.add(serverTab.getPokeChat());
				}
				chatTabView.elements.addAll(serverTab.getPrivateChats());
				if (chatTabView.selectedElement == null || chatTabView.elements.indexOf(chatTabView.selectedElement) == -1) {
					chatTabView.selectedElement = chatTabView.elements.get(0);
				}
				if (previousSize < chatTabView.elements.size()) {
					chatTabView.selectedElement = chatTabView.elements.get(chatTabView.elements.size() - 1);
				}
				for (Message message : chatTabView.selectedElement.getMessages()) {
					chatList.getRows().add(new TeamSpeakChatLine(message, chatBox.getWidth() - 8));
				}
				long newLastMessageTime = chatList.getRows().isEmpty() ? 0 : chatList.getRows().get(chatList.getRows().size() - 1).getMessage().getTime();
				if ((chatTabView.selectedElement != previousChat) ||
						(chatList.getCurrentScroll() + (chatList.getBottom() - chatList.getTop() - 4) >= previousContentHeight && newLastMessageTime != lastMessageTime)) {
					chatList.scrollToBottom();
				}

				if (openedContextMenu == null) {
					if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
						scrollTo(3);
					} else if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
						scrollTo(-3);
					}
				}
			} else {
				reset();
			}
		} else {
			reset();
			serverTabView.elements.add(new DummyServerTab());
		}
		previousChat = chatTabView.selectedElement;
	}

	@Override
	protected void handleMouseInput() {
		if (!this.entries.isEmpty()) {
			// scroll
//			if ((channelBox.contains(mouseX, mouseY) || channelScrollBox.contains(mouseX, mouseY)) && openedContextMenu == null) {
//				int dWheel = Mouse.getEventDWheel();
//				if (dWheel != 0) {
//					if (dWheel > 0) {
//						dWheel = -1;
//					} else if (dWheel < 0) {
//						dWheel = 1;
//					}
//					scrollTo(dWheel * 3);
//				}
//			}
//			if (Mouse.isButtonDown(0) && openedContextMenu == null) {
//				if (scrolling || channelScrollBox.contains(mouseX, mouseY)) {
//					scrolling = true;
//					float scrollHeight = Math.min(1, (float) entriesPerPage / (float) (entries.size())) * (float) channelScrollBox.callGetHeight();
//					scrollHeight = Math.max(10, scrollHeight);
//					float scrollY = Math.max(channelScrollBox.getY(), Math.min(mouseY - 4, channelScrollBox.getY() + channelScrollBox.callGetHeight() - scrollHeight));
//					float scroll = (scrollY - channelScrollBox.getY()) / ((float) channelScrollBox.callGetHeight() - scrollHeight);
//					topIndex = Math.max(0, Math.min((int) (scroll * (entries.size() - entriesPerPage)), entries.size() - entriesPerPage));
//					topEntry = entries.get(topIndex);
//				}
//				if (selectedEntry != null && !draggingSelectedEntry && selectedEntry instanceof MovableEntry && selectedEntryX != -1 && selectedEntryY != -1 &&
//						(mouseX != selectedEntryX || mouseY != selectedEntryY)) {
//					draggingSelectedEntry = true;
//				}
//			} else {
//				scrolling = false;
//				selectedEntryX = selectedEntryY = -1;
//				if (draggingSelectedEntry) {
//					draggingSelectedEntry = false;
//					DraggingPosition draggingPosition = getDraggingPosition(mouseX, mouseY);
//					if (draggingPosition != null) {
//						((MovableEntry) selectedEntry).moveEntryTo(draggingPosition.channel.getChannel(), draggingPosition.location);
//					}
//				}
//			}
//
//			// channel list
//			if (Mouse.getEventButtonState() && !scrolling) {
//				if (openedContextMenu != null && contextMenuBox != null) {
//					if (contextMenuBox.contains(mouseX, mouseY)) {
//						int entryIndex = (mouseY - contextMenuBox.getY()) / 12;
//						if (entryIndex >= 0 && entryIndex < openedContextMenu.entries.size()) {
//							GuiTeamSpeakContextMenuEntry entry = openedContextMenu.entries.get(entryIndex);
//							//noinspection unchecked
//							entry.onClick(selectedEntry);
//						}
//					}
//					openedContextMenu = null;
//					contextMenuBox = null;
//				} else if (channelBox.contains(mouseX, mouseY)) {
//					int entryIndex = topIndex + (int) Math.floor((float) (mouseY - channelBox.getY()) / ENTRY_HEIGHT);
//					if (entryIndex >= 0 && entryIndex < entries.size()) {
//						GuiTeamSpeakEntry entry = entries.get(entryIndex);
//
//						int xOffset = entry.getXOffset();
//						if (entry instanceof GuiTeamSpeakChannel && mouseX >= channelBox.getX() + xOffset - 10 && mouseX <= channelBox.getX() + xOffset) {
//							Channel channel = ((GuiTeamSpeakChannel) entry).getChannel();
//							if (collapsedChannels.contains(channel)) {
//								collapsedChannels.remove(channel);
//							} else {
//								collapsedChannels.add(channel);
//							}
//						} else {
//							selectedEntry = entry;
//							selectedEntryX = mouseX;
//							selectedEntryY = mouseY;
//							int eventButton = Mouse.getEventButton();
//							if (eventButton == 0) {
//								boolean doubleClick = entry.equals(selectedEntry) && System.currentTimeMillis() - lastTimeEntryClicked < 250;
//								entry.onClick(doubleClick);
//								lastTimeEntryClicked = System.currentTimeMillis();
//							} else if (eventButton == 1) {
//								for (Class<? extends GuiTeamSpeakEntry> entryClass : contextMenus.keySet()) {
//									if (entryClass.isAssignableFrom(entry.getClass())) {
//										openedContextMenu = contextMenus.get(entryClass);
//										contextMenuBox = new Rectangle(mouseX, mouseY, 150, openedContextMenu.entries.size() * 12 + 2);
//										while (contextMenuBox.getX() + contextMenuBox.callGetWidth() > callGetWidth()) {
//											contextMenuBox.setX(contextMenuBox.getX() - 1);
//										}
//										while (contextMenuBox.getY() + contextMenuBox.callGetHeight() > callGetHeight()) {
//											contextMenuBox.setY(contextMenuBox.getY() - 1);
//										}
//										break;
//									}
//								}
//							}
//						}
//					}
//				}
//			}
			// TODO
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		serverTabView.mouseClicked(mouseX, mouseY);
		chatTabView.mouseClicked(mouseX, mouseY);
		for (TeamSpeakButton teamSpeakButton : teamSpeakButtons) {
			teamSpeakButton.mouseClicked(mouseX, mouseY);
		}
	}

	@Override
	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;

		if (!The5zigMod.getConfig().getBool("tsEnabled")) {
			The5zigMod.getVars().drawCenteredString(I18n.translate("teamspeak.not_enabled"), getWidth() / 2, chatBox.getY() + 10);
		} else if (The5zigMod.getDataManager().isTsRequiresAuth() && !displayedAuthGui) {
			displayedAuthGui = true;
			The5zigMod.getVars().displayScreen(new GuiTeamSpeakAuth(this));
		} else if (!The5zigMod.getDataManager().isTsRequiresAuth()) {
			displayedAuthGui = false;
		}

		drawRectOutline(channelBox.getX(), channelBox.getY(), channelScrollBox.getX() + channelScrollBox.getWidth(), channelBox.getY() + channelBox.getHeight(), OUTLINE_COLOR);
		drawRectOutline(descriptionBox.getX(), descriptionBox.getY(), descriptionBox.getX() + descriptionBox.getWidth(), descriptionBox.getY() + descriptionBox.getHeight(), OUTLINE_COLOR);
		drawRectOutline(chatBox.getX(), chatBox.getY() + 1, chatBox.getX() + chatBox.getWidth(), chatBox.getY() + chatBox.getHeight(), OUTLINE_COLOR);

		serverTabView.draw(mouseX, mouseY);
		chatTabView.draw(mouseX, mouseY);

		renderTabEntries(mouseX, mouseY);
		renderTabScroll();
		drawTeamSpeakButtons();
	}

	@Override
	public void drawScreen0(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen0(mouseX, mouseY, partialTicks);
		drawContextMenu(mouseX, mouseY);
	}

	@Override
	protected void onEscapeType() {
		if (draggingSelectedEntry) {
			draggingSelectedEntry = false;
			selectedEntryX = selectedEntryY = -1;
		} else {
			super.onEscapeType();
		}
	}

	@Override
	public String getTitleName() {
		return "";
	}

	private void renderTabEntries(int mouseX, int mouseY) {
		MovableEntry dragging = selectedEntry != null && selectedEntry instanceof MovableEntry && draggingSelectedEntry ? (MovableEntry) selectedEntry : null;
		for (int i = topIndex; i < Math.min(topIndex + entriesPerPage, entries.size()); i++) {
			GuiTeamSpeakEntry entry = entries.get(i);

			int entryX = channelBox.getX();
			int entryY = channelBox.getY() + (i - topIndex) * ENTRY_HEIGHT;
			int entryWidth = channelBox.getWidth();

			boolean hover = openedContextMenu == null && mouseX >= entryX && mouseX <= entryX + entryWidth && mouseY >= entryY && mouseY < entryY + ENTRY_HEIGHT;
			int xOffset = entry.getXOffset();
			boolean arrowHovered = false;
			if (entry instanceof GuiTeamSpeakChannel && ((GuiTeamSpeakChannel) entry).canBeCollapsed()) {
				arrowHovered = mouseX >= entryX + xOffset - 10 && mouseX < entryX + xOffset && mouseY >= entryY + 2 && mouseY <= entryY + ENTRY_HEIGHT - 2;
				GLUtil.pushMatrix();
				The5zigMod.getVars().bindTexture(The5zigMod.TEAMSPEAK_ICONS);
				GL11.glTranslatef(entryX + xOffset - 8, entryY + 1, 0);
				if (!collapsedChannels.contains(((GuiTeamSpeakChannel) entry).getChannel())) {
					GL11.glRotatef(90, 0, 0, 1);
					GL11.glTranslatef(2, -7, 0);
				}
				The5zigMod.getVars().drawString(">", 0, 0, arrowHovered ? 0x00aaff : 0xffffff);
				GLUtil.popMatrix();
			}
			if (!arrowHovered && hover) {
				Gui.drawRect(entryX + xOffset, entryY, entryX + entryWidth, entryY + ENTRY_HEIGHT, 0x99c0ddff);
			} else if (entry.equals(selectedEntry)) {
				Gui.drawRect(entryX + xOffset, entryY, entryX + entryWidth, entryY + ENTRY_HEIGHT, 0x99ddddff);
			}

			GLUtil.color(1, 1, 1, 1);
			entry.render(entryX + xOffset, entryY, entryWidth - xOffset, ENTRY_HEIGHT);
		}
		if (dragging != null) {
			DraggingPosition draggingPosition = getDraggingPosition(mouseX, mouseY);
			if (draggingPosition != null) {
				int drawX = channelBox.getX();
				int drawY = channelBox.getY() + (int) Math.floor((float) (mouseY - channelBox.getY()) / ENTRY_HEIGHT) * ENTRY_HEIGHT;
				switch (draggingPosition.location) {
					case ABOVE:
						Gui.drawRect(drawX + draggingPosition.channel.getXOffset(), drawY, drawX + channelBox.getWidth(), drawY + 1, 0xffffffff);
						break;
					case INSIDE:
						Gui.drawRectInline(drawX + draggingPosition.channel.getXOffset() - 1, drawY, drawX + channelBox.getWidth() + 1, drawY + ENTRY_HEIGHT, 0xffffffff);
						break;
					case BELOW:
						Gui.drawRect(drawX + draggingPosition.channel.getXOffset(), drawY + ENTRY_HEIGHT - 1, drawX + channelBox.getWidth(), drawY + ENTRY_HEIGHT, 0xffffffff);
						break;
					default:
						break;
				}
			}
			dragging.renderDragging(mouseX, mouseY, channelBox.getWidth(), ENTRY_HEIGHT);
		}
		The5zigMod.getVars().bindTexture(The5zigMod.TEAMSPEAK_ICONS);
		for (int i = topIndex; i < Math.min(topIndex + entriesPerPage, entries.size()); i++) {
			GuiTeamSpeakEntry entry = entries.get(i);

			int entryX = channelBox.getX();
			int entryY = channelBox.getY() + (i - topIndex) * ENTRY_HEIGHT;
			int entryWidth = channelBox.getWidth();
			int xOffset = entry.getXOffset();

			GLUtil.color(1, 1, 1, 1);
			entry.renderIcons(entryX + xOffset, entryY, entryWidth - xOffset, ENTRY_HEIGHT);
		}
		if (dragging != null) {
			dragging.renderDraggingIcons(mouseX, mouseY, channelBox.getWidth(), ENTRY_HEIGHT);
		}
	}

	private void renderTabScroll() {
		drawRect(channelScrollBox.getX(), channelScrollBox.getY(), channelScrollBox.getX() + channelScrollBox.getWidth(), channelScrollBox.getY() + channelScrollBox.getHeight(), 0x44eeeeee);

		float scroll = (float) topIndex / (float) (entries.size() - entriesPerPage);
		float scrollHeight = Math.min(1, (float) entriesPerPage / (float) (entries.size())) * (float) channelScrollBox.getHeight();
		scrollHeight = Math.max(10, scrollHeight);
		float scrollY = channelScrollBox.getY() + scroll * ((float) channelScrollBox.getHeight() - scrollHeight);

		drawRect(channelScrollBox.getX(), scrollY, channelScrollBox.getX() + channelScrollBox.getWidth(), scrollY + scrollHeight, 0xffcccccc);
	}

	private void drawTeamSpeakButtons() {
		drawRect(ownStatusBox.getX() - 1, ownStatusBox.getY() - 1, ownStatusBox.getX() + ownStatusBox.getWidth() + 1, ownStatusBox.getY() + ownStatusBox.getHeight() + 1, 0xff444444);
		drawRect(ownStatusBox.getX(), ownStatusBox.getY(), ownStatusBox.getX() + ownStatusBox.getWidth(), ownStatusBox.getY() + ownStatusBox.getHeight(), 0xffffffff);
		The5zigMod.getVars().bindTexture(The5zigMod.TEAMSPEAK_ICONS);
		for (TeamSpeakButton teamSpeakButton : teamSpeakButtons) {
			teamSpeakButton.draw();
		}
	}

	private void drawContextMenu(int mouseX, int mouseY) {
		if (openedContextMenu != null && contextMenuBox != null) {
			drawRect(contextMenuBox.getX(), contextMenuBox.getY(), contextMenuBox.getX() + contextMenuBox.getWidth(), contextMenuBox.getY() + contextMenuBox.getHeight(), 0xffaaaaaa);
			for (int i = 0; i < openedContextMenu.entries.size(); i++) {
				GuiTeamSpeakContextMenuEntry entry = openedContextMenu.entries.get(i);
				if (mouseX >= contextMenuBox.getX() && mouseX <= contextMenuBox.getX() + contextMenuBox.getWidth() && mouseY >= contextMenuBox.getY() + 2 + i * 12 &&
						mouseY < contextMenuBox.getY() + 2 + (i + 1) * 12) {
					drawRect(contextMenuBox.getX() + 1, contextMenuBox.getY() + 1 + i * 12, contextMenuBox.getX() + contextMenuBox.getWidth() - 1, contextMenuBox.getY() + 1 + (i + 1) * 12,
							0x885599fa);
				}
				entry.draw(contextMenuBox.getX() + 2, contextMenuBox.getY() + 2 + i * 12);
			}
			The5zigMod.getVars().bindTexture(The5zigMod.TEAMSPEAK_ICONS);
			for (int i = 0; i < openedContextMenu.entries.size(); i++) {
				GuiTeamSpeakContextMenuEntry entry = openedContextMenu.entries.get(i);
				entry.drawIcon(contextMenuBox.getX() + 2, contextMenuBox.getY() + 2 + i * 12);
			}
		}
	}

	private TeamSpeakEntryList findEntries(List<? extends Channel> channels) {
		TeamSpeakEntryList result = new TeamSpeakEntryList();
		for (Channel channel : channels) {
			result.getEntries().add(createChannelEntry(channel));
			result.setChannelCount(result.getChannelCount() + 1);
			List<? extends Client> clients = channel.getClients();
			result.setClientCount(result.getClientCount() + clients.size());

			TeamSpeakEntryList childList = findEntries(channel.getChildren());
			result.setChannelCount(result.getChannelCount() + childList.getChannelCount());
			result.setClientCount(result.getClientCount() + childList.getClientCount());
			if (collapseAllChannels) {
				collapsedChannels.add(channel);
			} else if (!collapsedChannels.contains(channel)) {
				for (Client client : clients) {
					if (client.getType() == ClientType.NORMAL) {
						if (client == self) {
							result.getEntries().add(new GuiTeamSpeakClientSelf(client, serverInfo.getUniqueId()));
						} else {
							result.getEntries().add(new GuiTeamSpeakClient(client, serverInfo.getUniqueId()));
						}
					}
				}
				result.getEntries().addAll(childList.getEntries());
			}
		}
		return result;
	}

	private GuiTeamSpeakChannel createChannelEntry(Channel channel) {
		if (channel.getType() == ChannelType.CSPACER) {
			return new GuiTeamSpeakChannelCSpacer(channel, channel.getFormattedName());
		} else if (channel.getType() == ChannelType.SPACER) {
			return new GuiTeamSpeakChannelSpacer(channel, channel.getFormattedName());
		} else if (channel.getType() == ChannelType.SPACER_TEXT) {
			return new GuiTeamSpeakChannelSpacerText(channel, channel.getFormattedName());
		} else {
			return new GuiTeamSpeakChannelDefault(channel, serverInfo.getUniqueId());
		}
	}

	private DraggingPosition getDraggingPosition(int mouseX, int mouseY) {
		if (mouseX >= channelBox.getX() && mouseX <= channelBox.getX() + channelBox.getWidth()) {
			int entryIndex = topIndex + (int) Math.floor((float) (mouseY - channelBox.getY()) / ENTRY_HEIGHT);
			if (selectedEntry instanceof MovableEntry && entryIndex >= 0 && entryIndex < entries.size()) {
				GuiTeamSpeakEntry entry = entries.get(entryIndex);
				if (entry instanceof GuiTeamSpeakChannel) {
					Channel channel = ((GuiTeamSpeakChannel) entry).getChannel();
					MovableEntry.DragLocation location;
					if (selectedEntry instanceof GuiTeamSpeakChannel) {
						if ((mouseY - channelBox.getY()) % ENTRY_HEIGHT < ENTRY_HEIGHT / 4) {
							location = MovableEntry.DragLocation.ABOVE;
						} else if ((mouseY - channelBox.getY()) % ENTRY_HEIGHT > ENTRY_HEIGHT / 4 * 3) {
							location = MovableEntry.DragLocation.BELOW;
						} else {
							location = MovableEntry.DragLocation.INSIDE;
						}
					} else {
						location = MovableEntry.DragLocation.INSIDE;
					}
					if (((MovableEntry) selectedEntry).canBeMovedTo(channel, location)) {
						return new DraggingPosition((GuiTeamSpeakChannel) entry, location);
					}
				}
			}
		}
		return null;
	}

	private void reset() {
		entries.clear();
		serverInfo = null;
		self = null;
		descriptionList.getRows().clear();
		collapsedChannels.clear();
		chatList.getRows().clear();
		openedContextMenu = null;
		contextMenuBox = null;
		topEntry = null;
		topIndex = 0;
		selectedEntry = null;
	}

	private void scrollTo(int scroll) {
		if (entries.isEmpty()) {
			return;
		}
		int index = entries.indexOf(topEntry);
		index = Math.max(0, Math.min(index + scroll, entries.size() - entriesPerPage));
		topEntry = entries.get(index);
		topIndex = index;
	}

	static {
		contextMenus.put(GuiTeamSpeakServer.class, new GuiTeamSpeakContextMenuServer());
		contextMenus.put(GuiTeamSpeakChannel.class, new GuiTeamSpeakContextMenuChannel());
		contextMenus.put(GuiTeamSpeakClientSelf.class, new GuiTeamSpeakContextMenuClientSelf());
		contextMenus.put(GuiTeamSpeakClient.class, new GuiTeamSpeakContextMenuClient());
	}

}
