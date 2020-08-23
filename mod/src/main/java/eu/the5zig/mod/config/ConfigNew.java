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

package eu.the5zig.mod.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.entity.Friend;
import eu.the5zig.mod.chat.entity.Profile;
import eu.the5zig.mod.config.items.*;
import eu.the5zig.mod.gui.*;
import eu.the5zig.mod.gui.ts.GuiTeamSpeak;
import eu.the5zig.mod.manager.keyboard.KeyboardController;
import eu.the5zig.mod.render.BracketsFormatting;
import eu.the5zig.mod.util.FileSelectorCallback;
import eu.the5zig.util.Utils;
import eu.the5zig.util.minecraft.ChatColor;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Main config. All settings are registered into {@link #items} and stored together with their key. Each {@link ConfigItem} contains a setting, as well as a method to serialize and
 * deserialize the setting and some other methods.
 */
public class ConfigNew {

	/**
	 * The file of the configuration.
	 */
	private final File file;

	/**
	 * The parsed json object.
	 */
	private JsonObject root;
	/**
	 * A map that contains all config keys together with their {@link ConfigItem} class.
	 */
	private LinkedHashMap<String, ConfigItem> items = Maps.newLinkedHashMap();

	/**
	 * Initializes the configuration and loads it from the specified file.
	 *
	 * @param file the file of the configuration.
	 */
	public ConfigNew(File file) {
		this.file = file;
		addDefaultItems();

		try {
			load(FileUtils.readFileToString(file));
		} catch (Exception e) {
			The5zigMod.logger.warn("Error loading config! Creating new one...", e);
		}
		save(true);
	}

	protected void addDefaultItems() {
		add(new IntItem("version", null, 6));
		add(new BoolItem("debug", null, false));
		add(new StringItem("language", null, "en_US"));
		add(new StringListItem("disabled_plugins", null, new ArrayList<String>()));

		add(new BoolItem("showMod", "main", true));
		add(new PercentSliderItem("scale", "main", 1f, 0.5f, 1.5f, -1));
		add(new BoolItem("reportCrashes", "main", true));
		add(new DisplayCategoryItem("display", "main", "display"));
		add(new DisplayScreenItem("modules", "main", GuiModules.class));
		add(new DisplayCategoryItem("server", "main", "server"));
		add(new DisplayScreenItem("teamspeak", "main", GuiTeamSpeak.class));

		add(new BoolItem("discord", "main", true));

		add(new PlaceholderItem("main"));
		add(new PlaceholderItem("main"));
		add(new DisplayScreenItem("cape_settings", "main", GuiCapeSettings.class));
//		add(new EnumItem<Updater.UpdateType>("autoUpdate", "main", Updater.UpdateType.SAME_VERSION, Updater.UpdateType.class));
		add(new DisplayScreenItem("coordinate_clipboard", "main", GuiCoordinatesClipboard.class));
		add(new DisplayScreenItem("language_screen", "main", GuiLanguage.class) {
			@Override
			public String translate() {
				return I18n.getCurrentLanguage().equals(Locale.US) ? super.translate() :
						(super.translate() + "/" + I18n.translate("config.main.language_screen", true));
			}
		});
		add(new ActionItem("reset_config", "main", new Runnable() {
			@Override
			public void run() {
				The5zigMod.getVars().displayScreen(new GuiYesNo(The5zigMod.getVars().getCurrentScreen(), new YesNoCallback() {
					@Override
					public void onDone(boolean yes) {
						if (!yes)
							return;
						reset();
						The5zigMod.getOverlayMessage().displayMessageAndSplit(ChatColor.YELLOW + I18n.translate("config.reset"));
						The5zigMod.getVars().displayScreen(new GuiSettings(The5zigMod.getVars().getCurrentScreen().lastScreen, "main"));
					}

					@Override
					public String title() {
						return I18n.translate("config.main.reset.title");
					}
				}));
			}
		}));

		add(new DisplayCategoryItem("formatting", "display", "formatting"));
		add(new DisplayCategoryItem("hud", "display", "hud"));
		add(new SliderItem("maxOverlays", "", "display", 4, 1, 10, 1) {
			@Override
			public void action() {
				The5zigMod.getVars().updateOverlayCount(get().intValue());
			}
		});
		add(new SliderItem("overlayTexture", "", "display", 0, 0, 3, 1) {
			@Override
			public void action() {
				The5zigMod.getOverlayMessage().displayMessageAndSplit(ChatColor.WHITE + I18n.translate("config.display.overlay_texture.changed"), this);
			}

			@Override
			public String getCustomValue(float value) {
				return String.valueOf((int) (get() + 1));
			}
		});
		add(new SliderItem("zoomFactor", "", "display", 4, 2, 12, -1) {
			@Override
			public String getCustomValue(float value) {
				return Utils.getShortenedFloat(get(), 1) + "x";
			}
		});
		/*add(new SliderItem("crosshairDistance", "m", "display", 0, 0, 150, 5) {
			@Override
			public String getCustomValue(float value) {
				if (value == 0)
					return The5zigMod.toBoolean(false);
				return super.getCustomValue(value);
			}
		});*/

		add(new BoolItem("showChatSymbols", "display", true));
		add(new BoolItem("showLastServer", "display", true));
		add(new BoolItem("showCustomModels", "display", true));
		add(new DisplayCategoryItem("keyboard_settings", "display", "keyboard_settings"));
		add(new SliderItem("maxChatLines", "", "display", 250, 50, 1000, 50) {
			@Override
			public String getSuffix() {
				return " " + I18n.translate(getTranslationPrefix() + "." + getCategory() + "." + Utils.upperToDash(getKey()) + ".lines");
			}
		});
		add(new BoolItem("staticFov", "display", false));
		add(new DisplayCategoryItem("chat_time_prefix", "display", "chat_time_prefix"));
		add(new BoolItem("transparentChatBackground", "display", false));
		add(new BoolItem("transparentPauseMenu", "display", false));
		add(new BoolItem("showOwnNameTag", "display", false));
		add(new HexColorItem("highlightWordsColor", "display", "0xffe338"));

		add(new ColorFormattingItem("formattingPrefix", "formatting", ChatColor.RESET));
		add(new SelectColorItem("colorPrefix", "formatting", ChatColor.GOLD) {
			@Override
			public void action() {
				super.action();
				ConfigNew.this.get("rgbPrefix").set("");
				save();
			}
		});
		add(new ColorFormattingItem("formattingMain", "formatting", ChatColor.RESET));
		add(new SelectColorItem("colorMain", "formatting", ChatColor.WHITE) {
			@Override
			public void action() {
				super.action();
				ConfigNew.this.get("rgbMain").set("");
				save();
			}
		});
		add(new EnumItem<BracketsFormatting>("formattingBrackets", "formatting", BracketsFormatting.ARROW, BracketsFormatting.class) {
			@Override
			public String translateValue() {
				return get().getFirst() + get().getLast();
			}
		});
		add(new SelectColorItem("colorBrackets", "formatting", ChatColor.GRAY));
		add(new SliderItem("numberPrecision", "", "formatting", 1, 0, 4, 1) {
			@Override
			public String getSuffix() {
				return " " + I18n.translate("config.display.digits");
			}
		});

		add(new BoolItem("showVanillaPotionIndicator", "hud", true));
		add(new BoolItem("showPotionIndicator", "hud", false) {
			@Override
			public boolean isRestricted() {
				return !The5zigMod.getVars().isFancyGraphicsEnabled();
			}
		});
		add(new BoolItem("coloredEquipmentDurability", "hud", false));
		add(new BoolItem("showSaturation", "hud", false));
		add(new BoolItem("showFoodHealAmount", "hud", true));
		add(new BoolItem("showHotbarNumbers", "hud", false));

		add(new EnumItem<KeyboardController.Device>("controlKeyboard", "keyboard_settings", KeyboardController.Device.NONE, KeyboardController.Device.class) {
			@Override
			public void action() {
				The5zigMod.getDataManager().getKeyboardManager().updateDevice();
			}
		});
		add(new BoolItem("resetKeysOnUnfocus", "keyboard_settings", true) {
			@Override
			public boolean isRestricted() {
				return !The5zigMod.getDataManager().getKeyboardManager().isKeyboardInitialized();
			}
		});
		add(new HexColorItem("backgroundLedColor", "keyboard_settings", "0xffffff") {
			@Override
			public boolean isRestricted() {
				return !The5zigMod.getDataManager().getKeyboardManager().isKeyboardInitialized();
			}

			@Override
			public void action() {
				super.action();
				The5zigMod.getDataManager().getKeyboardManager().updateActiveLedKeys(true);
			}
		});
		add(new EnumItem<KeyboardController.KeyGroup>("activeLedKeys", "keyboard_settings", KeyboardController.KeyGroup.SPECIAL, KeyboardController.KeyGroup.class) {
			@Override
			public void action() {
				The5zigMod.getDataManager().getKeyboardManager().updateActiveLedKeys(true);
			}

			@Override
			public boolean isRestricted() {
				return !The5zigMod.getDataManager().getKeyboardManager().isKeyboardInitialized();
			}
		});
		add(new BoolItem("showLedHealth", "keyboard_settings", true) {
			@Override
			public void action() {
				The5zigMod.getDataManager().getKeyboardManager().updateShowHealth(true);
			}

			@Override
			public boolean isRestricted() {
				return !The5zigMod.getDataManager().getKeyboardManager().isKeyboardInitialized();
			}
		});
		add(new BoolItem("showLedArmor", "keyboard_settings", true) {
			@Override
			public void action() {
				The5zigMod.getDataManager().getKeyboardManager().updateShowArmor(true);
			}

			@Override
			public boolean isRestricted() {
				return !The5zigMod.getDataManager().getKeyboardManager().isKeyboardInitialized();
			}
		});
		add(new BoolItem("showLedPotions", "keyboard_settings", false) {
			@Override
			public void action() {
				The5zigMod.getDataManager().getKeyboardManager().updateShowPotionColor(true);
			}

			@Override
			public boolean isRestricted() {
				return !The5zigMod.getDataManager().getKeyboardManager().isKeyboardInitialized();
			}
		});
		add(new BoolItem("showLedDamageFlash", "keyboard_settings", false) {
			@Override
			public boolean isRestricted() {
				return !The5zigMod.getDataManager().getKeyboardManager().isKeyboardInitialized();
			}
		});

		add(new BoolItem("chatTimePrefixEnabled", "chat_time_prefix", false));
		add(new SelectColorItem("chatTimePrefixColor", "chat_time_prefix", ChatColor.WHITE) {
			@Override
			public boolean isRestricted() {
				return !getBool("chatTimePrefixEnabled");
			}
		});
		add(new EnumItem<BracketsFormatting>("chatTimePrefixBracketsFormatting", "chat_time_prefix", BracketsFormatting.ARROW2, BracketsFormatting.class) {
			@Override
			public String translateValue() {
				return get().getFirst() + get().getLast();
			}

			@Override
			public boolean isRestricted() {
				return !getBool("chatTimePrefixEnabled");
			}
		});
		add(new SelectColorItem("chatTimePrefixBracketsColor", "chat_time_prefix", ChatColor.WHITE) {
			@Override
			public boolean isRestricted() {
				return !getBool("chatTimePrefixEnabled");
			}
		});
		add(new StringItem("chatTimePrefixTimeFormat", "chat_time_prefix", "HH:mm") {
			@Override
			public boolean isRestricted() {
				return !getBool("chatTimePrefixEnabled");
			}
		});

		add(new DisplayCategoryItem("general", "server", "server_general"));
//		add(new DisplayCategoryItem("timolia", "server", "server_timolia"));
//		add(new DisplayCategoryItem("playminity", "server", "server_playminity"));
//		add(new DisplayCategoryItem("gommehd", "server", "server_gommehd"));
//		add(new DisplayCategoryItem("bergwerk", "server", "server_bergwerk"));
//		add(new DisplayCategoryItem("mineplex", "server", "server_mineplex"));
//		add(new DisplayCategoryItem("venicraft", "server", "server_venicraft"));
		add(new DisplayCategoryItem("hypixel", "server", "server_hypixel"));
		add(new StringListItem("highlight_words", "server", Collections.<String>emptyList()));
		add(new DisplayCategoryItem("2nd_chat", "server", "2nd_chat"));
		add(new DisplayScreenItem("chatmessage_filter", "server", GuiChatFilter.class));
		add(new DisplayScreenItem("text_replacement", "server", GuiTextReplacementList.class));
		add(new DisplayScreenItem("text_macros", "server", GuiTextMacroList.class));
		add(new DisplayScreenItem("name_history", "server", GuiNameHistory.class));
		add(new DisplayScreenItem("join_text", "server", GuiJoinTextList.class));

		add(new BoolItem("notifyOnName", "server_general", true));
		add(new SliderItem("autoServerReconnect", "", "server_general", 0, 4, 30, 1) {
			@Override
			public String getCustomValue(float value) {
				if (value > 0)
					return super.getCustomValue(value);
				return I18n.translate("config.server_general.auto_server_reconnect.off");
			}

			@Override
			public String getSuffix() {
				return " " + I18n.translate("config.server_general.auto_server_reconnect.sec");
			}
		});
		add(new BoolItem("macCmdDrop", "server_general", false) {
			{
				setRestricted(Utils.getPlatform() != Utils.Platform.MAC);
			}
		});
		add(new BoolItem("showCompassTarget", "server_general", true));
		add(new BoolItem("showLargeKillstreaks", "server_general", true));
		add(new BoolItem("allowModPluginRequests", "server_general", true));
		add(new BoolItem("mojangStatus", "server_general", true));
		add(new BoolItem("confirmDisconnect", "server_general", false));

		add(new BoolItem("autoHypixelApiKey", "server_hypixel", true));
		add(new DisplayScreenItem("stats", "server_hypixel", GuiHypixelStats.class));
		add(new DisplayScreenItem("guild", "server_hypixel", GuiHypixelGuild.class));
		add(new DisplayScreenItem("friends", "server_hypixel", GuiHypixelFriends.class));

		add(new BoolItem("2ndChatVisible", "2nd_chat", true) {
			@Override
			public void action() {
				if (!get()) {
					The5zigMod.getVars().get2ndChat().clear();
				}
			}
		});
		add(new PercentSliderItem("2ndChatOpacity", "2nd_chat", 1f, .1f, 1f, -1));
		add(new PercentSliderItem("2ndChatScale", "2nd_chat", 1f, 0f, 1f, -1) {
			@Override
			public String getCustomValue(float value) {
				return value == 0 ? The5zigMod.toBoolean(false) : super.getCustomValue(value);
			}

			@Override
			public void action() {
				The5zigMod.getVars().get2ndChat().refreshChat();
			}
		});
		add(new SliderItem("2ndChatHeightFocused", "px", "2nd_chat", 180f, 20f, 180f, 1) {
			@Override
			public void action() {
				The5zigMod.getVars().get2ndChat().refreshChat();
			}
		});
		add(new SliderItem("2ndChatHeightUnfocused", "px", "2nd_chat", 90f, 20f, 180f, 1) {
			@Override
			public void action() {
				The5zigMod.getVars().get2ndChat().refreshChat();
			}
		});
		add(new SliderItem("2ndChatWidth", "px", "2nd_chat", 170f, 40f, 320f, 1) {
			@Override
			public void action() {
				The5zigMod.getVars().get2ndChat().refreshChat();
			}
		});
		add(new BoolItem("2ndChatTextLeftbound", "2nd_chat", true));

		add(new BoolItem("connectToServer", "profile_settings", true) {
			@Override
			public void action() {
				if (get()) {
					The5zigMod.newNetworkManager();
				} else if (The5zigMod.getNetworkManager() != null) {
					The5zigMod.getNetworkManager().disconnect();
				}
			}
		});
		add(new BoolItem("showConnecting", "profile_settings", false));
		add(new OnlineStatusItem("onlineStatus", "profile_settings", (float) Friend.OnlineStatus.ONLINE.ordinal()));
		add(new SliderItem("afkTime", "", "profile_settings", 10, 0, 60, 5) {
			@Override
			public String getCustomValue(float value) {
				if (value > 0)
					return super.getCustomValue(value);
				return I18n.translate("config.profile_settings.never");
			}

			@Override
			public String getSuffix() {
				return " " + I18n.translate("config.profile_settings.afk_time.min");
			}
		});
		add(new NonConfigItem("show_server", "profile_settings") {
			@Override
			public void action() {
				The5zigMod.getDataManager().getProfile().setShowServer(!The5zigMod.getDataManager().getProfile().isShowServer());
			}

			@Override
			public String translate() {
				return I18n.translate(getTranslationPrefix() + "." + getCategory() + "." + getKey()) + ": " + The5zigMod.toBoolean(The5zigMod.getDataManager().getProfile().isShowServer());
			}
		});
		add(new NonConfigItem("show_messages_read", "profile_settings") {
			@Override
			public void action() {
				The5zigMod.getDataManager().getProfile().setShowMessageRead(!The5zigMod.getDataManager().getProfile().isShowMessageRead());
			}

			@Override
			public String translate() {
				return I18n.translate(getTranslationPrefix() + "." + getCategory() + "." + getKey()) + ": " + The5zigMod.toBoolean(
						The5zigMod.getDataManager().getProfile().isShowMessageRead());
			}
		});
		add(new NonConfigItem("show_friend_requests", "profile_settings") {
			@Override
			public void action() {
				The5zigMod.getDataManager().getProfile().setShowFriendRequests(!The5zigMod.getDataManager().getProfile().isShowFriendRequests());
			}

			@Override
			public String translate() {
				return I18n.translate(getTranslationPrefix() + "." + getCategory() + "." + getKey()) + ": " + The5zigMod.toBoolean(
						The5zigMod.getDataManager().getProfile().isShowFriendRequests());
			}
		});
		add(new NonConfigItem("show_country", "profile_settings") {
			@Override
			public void action() {
				The5zigMod.getDataManager().getProfile().setShowCountry(!The5zigMod.getDataManager().getProfile().isShowCountry());
			}

			@Override
			public String translate() {
				return I18n.translate(getTranslationPrefix() + "." + getCategory() + "." + getKey()) + ": " + The5zigMod.toBoolean(The5zigMod.getDataManager().getProfile().isShowCountry());
			}
		});
		add(new SelectColorItem("display_color", "profile_settings", ChatColor.WHITE) {

			@Override
			public ChatColor get() {
				Profile profile = The5zigMod.getDataManager().getProfile();
				ChatColor displayColor = profile.getDisplayColor();
				return displayColor == null || displayColor == ChatColor.RESET ? ChatColor.getByChar(profile.getRank().get(0).getColorCode().charAt(1)) : displayColor;
			}

			@Override
			public void action() {
				The5zigMod.getDataManager().getProfile().setDisplayColor(super.get());
			}

			@Override
			public void deserialize(JsonObject object) {
			}

			@Override
			public void serialize(JsonObject object) {
			}
		});

		add(new BoolItem("showMessages", "chat_settings", true));
		add(new BoolItem("showGroupMessages", "chat_settings", false));
		add(new BoolItem("showOnlineMessages", "chat_settings", true));
		add(new BoolItem("playMessageSounds", "chat_settings", true));
		add(new BoolItem("showTrayNotifications", "chat_settings", true) {
			@Override
			public void action() {
				if (get()) {
					The5zigMod.getTrayManager().create();
				} else {
					The5zigMod.getTrayManager().destroy();
				}
			}
		});
		add(new EnumItem<GuiConversations.BackgroundType>("chatBackgroundType", "chat_settings", GuiConversations.BackgroundType.TRANSPARENT, GuiConversations.BackgroundType.class) {
			@Override
			@SuppressWarnings("unchecked")
			public void action() {
				if (get() == GuiConversations.BackgroundType.TRANSPARENT) {
					The5zigMod.getDataManager().getChatBackgroundManager().resetBackgroundImage();
					items.get("chatBackgroundLocation").set(null);
					save();
				}
			}
		});
		add(new ConfigItem<String>("chatBackgroundLocation", "chat_settings", null) {

			@Override
			public void deserialize(JsonObject object) {
				set(object.get(getKey()).getAsString());
			}

			@Override
			public void serialize(JsonObject object) {
				object.addProperty(getKey(), get());
			}

			@Override
			public void next() {
			}

			@Override
			public void action() {
				The5zigMod.getVars().displayScreen(new GuiFileSelector(The5zigMod.getVars().getCurrentScreen(), new FileSelectorCallback() {
					@Override
					public void onDone(File file) {
						if (file == null) {
							The5zigMod.getDataManager().getChatBackgroundManager().resetBackgroundImage();
						} else {
							set(file.getAbsolutePath());
							The5zigMod.getDataManager().getChatBackgroundManager().reloadBackgroundImage();
						}
						The5zigMod.getOverlayMessage().displayMessage(I18n.translate("config.chat_settings.background.selected"));
					}

					@Override
					public String getTitle() {
						return "The 5zig Mod - " + I18n.translate("config.chat_settings.title");
					}
				}, "png", "jpg"));
			}

			@Override
			public String translate() {
				return I18n.translate(getTranslationPrefix() + "." + getCategory() + "." + Utils.upperToDash(getKey()));
			}

			@Override
			public boolean isRestricted() {
				return getEnum("chatBackgroundType", GuiConversations.BackgroundType.class) != GuiConversations.BackgroundType.IMAGE;
			}
		});

		add(new BoolItem("tsEnabled", "teamspeak", true));
		add(new StringItem("tsAuthKey", null, null));
		add(new BoolItem("tsTextMessagesInChat", "teamspeak", true));
		add(new BoolItem("tsChannelEventsInChat", "teamspeak", true));
		add(new BoolItem("tsDmOverlay", "teamspeak", true));
		add(new DisplayScreenItem("tsAuthGui", "teamspeak", GuiTeamSpeakAuth.class));

		add(new EnumItem<Friend.Sortation>("friendSortation", "friend_list", Friend.Sortation.ONLINE, Friend.Sortation.class) {
			@Override
			public void action() {
				The5zigMod.getFriendManager().sortFriends();
			}
		});

		// [REBORN] New config start

		add(new BoolItem("plugin_update", null, true));
		add(new BoolItem("pingOnTab", "display", false));

		add(new DisplayCategoryItem("spotify", "display", "spotify"));
		add(new StringItem("refresh_token", "spotify", "") {
			@Override
			public String translateValue() {
				return I18n.translate(get().isEmpty() ? "spotify.token.not_set" : "spotify.token.set");
			}

			@Override
			public int getMaxLength() {
				return 500;
			}

			@Override
			public void action() {
				String pair = get();
				if(!pair.contains("/")) return;
				The5zigMod.getDataManager().getSpotifyManager().setTokens(pair);
			}
		});
		add(new StringItem("spotify_auth_token", null, ""));

		add(new StringItem("hypixel_api_key", "server_hypixel", "") {
			@Override
			public String translate() {
				return I18n.translate("config.server_hypixel.hypixel_api_key");
			}

			@Override
			public int getMaxLength() {
				return 36;
			}

			@Override
			public void action() {
				try {
					The5zigMod.getHypixelAPIManager().setKey(UUID.fromString(get()));
				}
				catch (IllegalArgumentException ex) {
					ex.printStackTrace();
				}
			}
		});

		add(new BoolItem("ping_on_serverlist", "display", false));
		add(new StringItem("rgbMain", "formatting", "") {
			@Override
			public String translateValue() {
				return I18n.translate(get().isEmpty() ? "spotify.token.not_set" : "#" + get());
			}

			@Override
			public void set(String value) {
				super.set(value.replace("#", ""));
			}
		});
		add(new StringItem("rgbPrefix", "formatting", "") {
			@Override
			public String translateValue() {
				return I18n.translate(get().isEmpty() ? "spotify.token.not_set" : "#" + get());
			}

			@Override
			public void set(String value) {
				super.set(value.replace("#", ""));
			}
		});
		add(new BoolItem("rewardTags", "profile_settings", true));
		// [REBORN] New config end
	}

	/**
	 * Adds a new {@link ConfigItem} to the item registry.
	 *
	 * @param item the {@link ConfigItem} that should be registered.
	 * @throws IllegalArgumentException if another item with the same key already has been registered before.
	 */
	protected void add(ConfigItem item) {
		if (items.containsKey(item.getKey()))
			throw new IllegalArgumentException("Config registry already contains key " + item.getKey());
		items.put(item.getKey(), item);
	}

	/**
	 * Tries to find a {@link ConfigItem} by its key.
	 *
	 * @param key the unique key of the {@link ConfigItem}.
	 * @return a {@link ConfigItem} or {@code null}, if no item could be found by the specified key.
	 */
	public ConfigItem get(String key) {
		ConfigItem item = items.get(key);
		if (item == null)
			The5zigMod.logger.warn("Could not find " + key + " in config!");
		return item;
	}

	/**
	 * Tries to find a {@link ConfigItem} by its key.
	 *
	 * @param key      the unique key of the {@link ConfigItem}.
	 * @param classOfT the class of the (primitive) type of the {@link ConfigItem}'s setting.
	 * @param <T>      the (primitive) type of the {@link ConfigItem}'s setting.
	 * @return a {@link ConfigItem} or {@code null}, if no item could be found by the specified key.
	 */
	public <T extends ConfigItem> T get(String key, Class<T> classOfT) {
		ConfigItem item = items.get(key);
		if (item == null || !classOfT.isAssignableFrom(item.getClass()))
			The5zigMod.logger.warn("Could not find " + key + " in config!");
		return classOfT.cast(item);
	}

	/**
	 * Tries to get the value of an integer setting by its key.
	 *
	 * @param key the key of the setting.
	 * @return the integer value of the setting or {@code 0}, if the value of the setting is not an integer or the setting does not exist.
	 */
	public int getInt(String key) {
		ConfigItem item = items.get(key);
		if (item == null) {
			The5zigMod.logger.warn("Could not find " + key + " in config!");
		}
		return item instanceof IntItem ? ((IntItem) item).get() : item instanceof SliderItem ? ((SliderItem) item).get().intValue() : 0;
	}

	/**
	 * Tries to get the value of a float setting by its key.
	 *
	 * @param key the key of the setting.
	 * @return the float value of the setting or {@code 0.0f}, if the value of the setting is not a float or the setting does not exist.
	 */
	public float getFloat(String key) {
		ConfigItem item = items.get(key);
		if (item == null) {
			The5zigMod.logger.warn("Could not find " + key + " in config!");
		}
		return item instanceof FloatItem ? ((FloatItem) item).get() : 0;
	}

	/**
	 * Tries to get the value of a string setting by its key.
	 *
	 * @param key the key of the setting.
	 * @return the string value of the setting or {@code false}, if the value of the setting is not a boolean or the setting does not exist.
	 */
	public boolean getBool(String key) {
		ConfigItem item = items.get(key);
		if (item == null) {
			The5zigMod.logger.warn("Could not find " + key + " in config!");
		}
		return item instanceof BoolItem ? ((BoolItem) item).get() : false;
	}

	/**
	 * Tries to get the value of a string setting by its key.
	 *
	 * @param key the key of the setting.
	 * @return the string value of the setting or {@code null}, if the value of the setting is not a string or the setting does not exist.
	 */
	public String getString(String key) {
		ConfigItem item = items.get(key);
		if (item == null) {
			The5zigMod.logger.warn("Could not find " + key + " in config!");
		}
		return item instanceof StringItem ? ((StringItem) item).get() : null;
	}

	/**
	 * Tries to get the value of an {@link Enum} setting by its key.
	 *
	 * @param key      the key of the setting.
	 * @param classOfT the class of the {@link Enum}-setting.
	 * @param <T>      the type of the {@link Enum}.
	 * @return the enum value of the setting or {@code null}, if the value of the setting is not an {@link Enum} or the setting does not exist.
	 */
	public <T extends Enum> T getEnum(String key, Class<T> classOfT) {
		ConfigItem item = items.get(key);
		if (item == null) {
			The5zigMod.logger.warn("Could not find " + key + " in config!");
		}
		return item == null ? null : classOfT.cast(item.get());
	}

	/**
	 * Tries to get the value of a string list setting by its key.
	 *
	 * @param key the key of the setting.
	 * @return the string list value of the setting or {@code null}, if the value of the setting is not a string or the setting does not exist.
	 */
	public List<String> getStringList(String key) {
		ConfigItem item = items.get(key);
		if (item == null) {
			The5zigMod.logger.warn("Could not find " + key + " in config!");
		}
		return item instanceof StringListItem ? ((StringListItem) item).get() : null;
	}


	/**
	 * Tries to load & parse the configuration from a json string.
	 *
	 * @param json the json string of the config.
	 */
	private void load(String json) {
		if (root != null) {
			throw new IllegalStateException("Config already loaded!");
		}

		JsonParser parser = new JsonParser();
		JsonElement parse = parser.parse(json);
		if (parse == null || parse.isJsonNull())
			throw new RuntimeException("Config not found!");

		root = parse.getAsJsonObject();

		for (ConfigItem item : items.values()) {
			try {
				item.deserialize(root);
			} catch (Exception e) {
				The5zigMod.logger.debug("Error deserializing item " + item, e);
				item.reset();
			}
		}
		//The5zigMod.logger.debug("Loaded {} config items!", items.size());
	}

	/**
	 * Saves the configuration in a separate Thread.
	 */
	public void save() {
		save(false);
	}

	/**
	 * Saves the configuration.
	 *
	 * @param sync true, if the configuration should be saved in the same Thread.
	 */
	public void save(boolean sync) {
		boolean changed = false;
		boolean newConfig = false;
		if (root == null) {
			root = new JsonObject();
			newConfig = true;
			The5zigMod.logger.info("Creating new config...");
		}
		for (ConfigItem item : items.values()) {
			if (newConfig || item.hasChanged()) {
				item.serialize(root);
				item.setChanged(false);
				changed = true;
			}
		}
		if (!changed)
			return;

		if (sync) {
			writeRoot();
		} else {
			The5zigMod.getAsyncExecutor().execute(new Runnable() {
				@Override
				public void run() {
					writeRoot();
				}
			});
		}
	}

	/**
	 * Writes the {@link JsonObject} to the {@link #file}.
	 */
	private synchronized void writeRoot() {
		try {
			String json = The5zigMod.prettyGson.toJson(root);
			FileWriter writer = new FileWriter(file);
			writer.write(json);
			writer.close();
		} catch (IOException e) {
			The5zigMod.logger.warn("Could not update Config File!", e);
		}
	}

	/**
	 * Resets the configuration.
	 */
	public void reset() {
		for (ConfigItem item : items.values()) {
			item.reset();
		}
		root = null;
		save();
	}

	/**
	 * Tries to get all items or a specific category.
	 *
	 * @param category the category.
	 * @return a list that contains every {@link ConfigItem} of a specific category.
	 */
	public List<ConfigItem> getItems(String category) {
		List<ConfigItem> result = Lists.newArrayList();
		for (ConfigItem item : items.values()) {
			if (category.equals(item.getCategory()))
				result.add(item);
		}
		return result;
	}

	/**
	 * @return a list that contains every registered {@link ConfigItem}.
	 */
	public List<ConfigItem> getItems() {
		return Lists.newArrayList(items.values());
	}

}
