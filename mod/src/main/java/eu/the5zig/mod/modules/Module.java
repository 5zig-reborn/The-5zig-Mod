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

package eu.the5zig.mod.modules;

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.Version;
import eu.the5zig.mod.asm.Transformer;
import eu.the5zig.mod.config.items.ColorFormattingItem;
import eu.the5zig.mod.gui.Gui;
import eu.the5zig.mod.gui.elements.Row;
import eu.the5zig.mod.gui.list.GuiArrayList;
import eu.the5zig.mod.render.DisplayRenderer;
import eu.the5zig.mod.render.RenderLocation;
import eu.the5zig.mod.server.GameMode;
import eu.the5zig.mod.server.GameServer;
import eu.the5zig.mod.server.RegisteredServerInstance;
import eu.the5zig.mod.server.ServerInstance;
import eu.the5zig.mod.server.timolia.ServerTimolia;
import eu.the5zig.mod.util.GLUtil;
import eu.the5zig.util.minecraft.ChatColor;
import org.apache.commons.lang3.Validate;

public class Module implements Row {

	private boolean enabled = true;

	private String id;
	private String name;
	private String translation;
	private String server;
	private ServerInstance serverInstance;
	private boolean showLabel;
	private ModuleLabelFormatting labelFormatting;
	private ModuleLocation location;
	private AnchorPoint anchorPoint;
	private float locationX;
	private float locationY;
	private GuiArrayList<ActiveModuleItem> items = new GuiArrayList<>();
	private boolean shouldRender = true;
	private RenderType renderType;
	private float scale = 1.0f;
	private float boxOpacity;

	private int calculatedWidth = -1;
	private int calculatedHeight = -1;

	public Module(String id, String name, String translation, String server, boolean showLabel, ModuleLocation location, AnchorPoint anchorPoint, float locationX, float locationY, float scale,
	              float boxOpacity) {
		this.id = id;
		this.name = name;
		this.translation = translation;
		this.server = server;
		if (server != null) {
			serverInstance = The5zigMod.getDataManager().getServerInstanceRegistry().byConfigName(server);
		} else {
			serverInstance = null;
		}
		this.showLabel = showLabel;
		this.location = location;
		this.anchorPoint = anchorPoint;
		this.locationX = locationX;
		this.locationY = locationY;
		this.scale = scale;
		this.boxOpacity = boxOpacity;
	}

	public void render(DisplayRenderer renderer, int x, int y, boolean dummy) {
		if (getItemRenderCount(dummy) == 0) {
			return;
		}
		int maxWidth = getMaxWidth(dummy);
		int totalHeight = getTotalHeight(dummy) - (int) (6 * scale);
		RenderLocation renderLocation;
		if (location == ModuleLocation.TOP_LEFT || location == ModuleLocation.CENTER_LEFT || location == ModuleLocation.BOTTOM_LEFT) {
			renderLocation = RenderLocation.LEFT;
		} else if (location == ModuleLocation.TOP_RIGHT || location == ModuleLocation.CENTER_RIGHT || location == ModuleLocation.BOTTOM_RIGHT) {
			renderLocation = RenderLocation.RIGHT;
		} else {
			renderLocation = anchorPoint.toRenderLocation();
		}
		x /= scale;
		y /= scale;
		totalHeight /= scale;
		maxWidth /= scale;

		if (scale != 1.0f) {
			GLUtil.pushMatrix();
			GLUtil.scale(scale, scale, scale);
			DisplayRenderer.largeTextRenderer.setScaleModifyFactor(scale);
		}
		if (boxOpacity > 0) {
			if (location == ModuleLocation.CUSTOM) {
				if (anchorPoint == AnchorPoint.TOP_LEFT) {
					Gui.drawRect(x, y, x + maxWidth, y + totalHeight, (int) (boxOpacity * 255) << 24);
				} else if (anchorPoint == AnchorPoint.TOP_CENTER) {
					Gui.drawRect(x - maxWidth / 2, y, x + maxWidth / 2, y + totalHeight, (int) (boxOpacity * 255) << 24);
				} else if (anchorPoint == AnchorPoint.TOP_RIGHT) {
					Gui.drawRect(x - maxWidth, y, x, y + totalHeight, (int) (boxOpacity * 255) << 24);
				} else if (anchorPoint == AnchorPoint.CENTER_LEFT) {
					Gui.drawRect(x, y - totalHeight / 2, x + maxWidth, y + totalHeight / 2, (int) (boxOpacity * 255) << 24);
				} else if (anchorPoint == AnchorPoint.CENTER_CENTER) {
					Gui.drawRect(x - maxWidth / 2, y - totalHeight / 2, x + maxWidth / 2, y + totalHeight / 2, (int) (boxOpacity * 255) << 24);
				} else if (anchorPoint == AnchorPoint.CENTER_RIGHT) {
					Gui.drawRect(x - maxWidth, y - totalHeight / 2, x, y + totalHeight / 2, (int) (boxOpacity * 255) << 24);
				} else if (anchorPoint == AnchorPoint.BOTTOM_LEFT) {
					Gui.drawRect(x, y - totalHeight, x + maxWidth, y, (int) (boxOpacity * 255) << 24);
				} else if (anchorPoint == AnchorPoint.BOTTOM_CENTER) {
					Gui.drawRect(x - maxWidth / 2, y - totalHeight, x + maxWidth / 2, y, (int) (boxOpacity * 255) << 24);
				} else if (anchorPoint == AnchorPoint.BOTTOM_RIGHT) {
					Gui.drawRect(x - maxWidth, y - totalHeight, x, y, (int) (boxOpacity * 255) << 24);
				}
			} else {
				Gui.drawRect(x, y, x + maxWidth, y + totalHeight, (int) (boxOpacity * 255) << 24);
			}
		}

		if (isShowLabel()) {
			String displayName = getDisplayName();
			int color = getLabelColor();

			int labelWidth = The5zigMod.getVars().getStringWidth(displayName);
			if (location == ModuleLocation.CUSTOM) {
				if (anchorPoint == AnchorPoint.TOP_LEFT) {
					The5zigMod.getVars().drawString(displayName, x, y, color);
				} else if (anchorPoint == AnchorPoint.TOP_CENTER) {
					The5zigMod.getVars().drawString(displayName, x - labelWidth / 2, y, color);
				} else if (anchorPoint == AnchorPoint.TOP_RIGHT) {
					The5zigMod.getVars().drawString(displayName, x - labelWidth, y, color);
				} else if (anchorPoint == AnchorPoint.CENTER_LEFT) {
					The5zigMod.getVars().drawString(displayName, x, y - totalHeight / 2, color);
				} else if (anchorPoint == AnchorPoint.CENTER_CENTER) {
					The5zigMod.getVars().drawString(displayName, x - labelWidth / 2, y - totalHeight / 2, color);
				} else if (anchorPoint == AnchorPoint.CENTER_RIGHT) {
					The5zigMod.getVars().drawString(displayName, x - labelWidth, y - totalHeight / 2, color);
				} else if (anchorPoint == AnchorPoint.BOTTOM_LEFT) {
					The5zigMod.getVars().drawString(displayName, x, y - totalHeight, color);
				} else if (anchorPoint == AnchorPoint.BOTTOM_CENTER) {
					The5zigMod.getVars().drawString(displayName, x - labelWidth / 2, y - totalHeight, color);
				} else if (anchorPoint == AnchorPoint.BOTTOM_RIGHT) {
					The5zigMod.getVars().drawString(displayName, x - labelWidth, y - totalHeight, color);
				}
			} else {
				if (renderLocation == RenderLocation.CENTERED) {
					The5zigMod.getVars().drawString(displayName, x - (labelWidth / 2), y, color);
				} else if (renderLocation == RenderLocation.RIGHT) {
					The5zigMod.getVars().drawString(displayName, x + (maxWidth - labelWidth), y, color);
				} else {
					The5zigMod.getVars().drawString(displayName, x, y, color);
				}
			}
			y += 12;
		}
		for (ActiveModuleItem item : items) {
			if (item.getHandle().shouldRender(dummy)) {
				int itemX, itemY;
				int itemWidth = item.getHandle().getWidth(dummy);
				if (location == ModuleLocation.CUSTOM) {
					if (anchorPoint == AnchorPoint.TOP_LEFT) {
						itemX = x;
						itemY = y;
					} else if (anchorPoint == AnchorPoint.TOP_CENTER) {
						itemX = x - itemWidth / 2;
						itemY = y;
					} else if (anchorPoint == AnchorPoint.TOP_RIGHT) {
						itemX = x - itemWidth;
						itemY = y;
					} else if (anchorPoint == AnchorPoint.CENTER_LEFT) {
						itemX = x;
						itemY = y - totalHeight / 2;
					} else if (anchorPoint == AnchorPoint.CENTER_CENTER) {
						itemX = x - itemWidth / 2;
						itemY = y - totalHeight / 2;
					} else if (anchorPoint == AnchorPoint.CENTER_RIGHT) {
						itemX = x - itemWidth;
						itemY = y - totalHeight / 2;
					} else if (anchorPoint == AnchorPoint.BOTTOM_LEFT) {
						itemX = x;
						itemY = y - totalHeight;
					} else if (anchorPoint == AnchorPoint.BOTTOM_CENTER) {
						itemX = x - itemWidth / 2;
						itemY = y - totalHeight;
					} else if (anchorPoint == AnchorPoint.BOTTOM_RIGHT) {
						itemX = x - itemWidth;
						itemY = y - totalHeight;
					} else {
						continue;
					}
				} else {
					if (renderLocation == RenderLocation.CENTERED) {
						itemX = x - itemWidth / 2;
						itemY = y;
					} else if (renderLocation == RenderLocation.RIGHT) {
						itemX = x + (maxWidth - itemWidth);
						itemY = y;
					} else {
						itemX = x;
						itemY = y;
					}
				}
				item.getHandle().renderSettings = new RenderSettingsImpl(scale);
				item.getHandle().render(itemX, itemY, renderLocation, dummy);
				y += item.getHandle().getHeight(dummy);
			}
		}
		calculatedWidth = -1;
		calculatedHeight = -1;
		if (scale != 1.0f) {
			GLUtil.popMatrix();
		}
	}

	public int getMaxWidth(boolean dummy) {
		if (calculatedWidth >= 0) {
			return calculatedWidth;
		}
		int max = 0;
		for (ActiveModuleItem item : items) {
			if (item.getHandle().shouldRender(dummy)) {
				int width = item.getHandle().getWidth(dummy);
				if (width > max) {
					max = width;
				}
			}
		}
		if (isShowLabel()) {
			int width = The5zigMod.getVars().getStringWidth(getDisplayName());
			if (width > max) {
				max = width;
			}
		}
		return (int) (max * scale);
	}

	public int getTotalHeight(boolean dummy) {
		if (calculatedHeight >= 0) {
			return calculatedHeight;
		}
		int height = 0;
		for (ActiveModuleItem item : items) {
			if (item.getHandle().shouldRender(dummy)) {
				height += item.getHandle().getHeight(dummy);
			}
		}
		if (isShowLabel()) {
			height += 12;
		}
		return (int) ((height + 6) * scale);
	}

	private int getItemRenderCount(boolean dummy) {
		int count = 0;
		for (ActiveModuleItem item : items) {
			if (item.getHandle().shouldRender(dummy)) {
				count++;
			}
		}
		return count;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the id of the module.
	 * @see #setId(String)
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id of the module.
	 *
	 * @param id the new id of the module.
	 * @see #getId()
	 */
	public void setId(String id) {
		Validate.notNull(id);

		this.id = id;
		The5zigMod.getModuleMaster().save();
	}

	/**
	 * @return the name of the module.
	 * @see #setName(String)
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the module.
	 *
	 * @param name the new name of the module.
	 * @see #getName()
	 */
	public void setName(String name) {
		this.name = name;
		The5zigMod.getModuleMaster().save();
	}

	/**
	 * @return the translation key of the module.
	 * @see #setTranslation(String)
	 */
	public String getTranslation() {
		return translation;
	}

	/**
	 * Sets the translation key of the the module.
	 *
	 * @param translation the new translation key of the module.
	 * @see #getTranslation()
	 */
	public void setTranslation(String translation) {
		this.translation = translation;
		The5zigMod.getModuleMaster().save();
	}

	/**
	 * @return the server when the module should be rendered.
	 * @see #setServer(String)
	 */
	public String getServer() {
		return server;
	}

	/**
	 * Sets the server when the module should be rendered.
	 *
	 * @param server the new server when the module should be rendered.
	 * @see #getServer()
	 */
	public void setServer(String server) {
		this.server = server;
		if (server != null) {
			serverInstance = The5zigMod.getDataManager().getServerInstanceRegistry().byConfigName(server);
		} else {
			serverInstance = null;
		}
		The5zigMod.getModuleMaster().save();
	}

	/**
	 * @return the display name of the module.
	 */
	public String getDisplayName() {
		String displayName;
		if (renderType == RenderType.TIMOLIA_TOURNAMENT) {
			RegisteredServerInstance instance = The5zigMod.getDataManager().getServerInstanceRegistry().getRegisteredInstance("timolia");
			if (instance.isOnServer() && instance.getCurrentGameMode() instanceof ServerTimolia.PvP && ((ServerTimolia.PvP) instance.getCurrentGameMode()).getTournament() != null) {
				displayName = ChatColor.UNDERLINE + I18n.translate("ingame.tournament", ((ServerTimolia.PvP) instance.getCurrentGameMode()).getTournament().getHost());
			} else {
				displayName = ChatColor.UNDERLINE + I18n.translate("ingame.tournament", "/");
			}
		} else {
			String currentName = getName();
			if (serverInstance != null) {
				String name = currentName;
				if (name != null && The5zigMod.getDataManager().getServer() instanceof GameServer) {
					GameMode gameMode = ((GameServer) The5zigMod.getDataManager().getServer()).getGameMode();
					name = name.replace("%gamemode%", (gameMode == null ? serverInstance.getName() : gameMode.getName()));
				} else {
					name = serverInstance.getName();
				}
				displayName = ChatColor.UNDERLINE + name;
			} else if (getTranslation() != null && !getTranslation().isEmpty()) {
				displayName = ChatColor.UNDERLINE + I18n.translate(getTranslation());
			} else if (currentName == null || currentName.isEmpty()) {
				displayName = ChatColor.UNDERLINE + getId();
			} else {
				currentName = currentName.replace("%mcversion%", Version.MCVERSION).replace("%forge%", Transformer.FORGE ? "Forge" : "");
				if (currentName.contains("%version%")) {
					displayName = currentName.replace("%version%", Version.VERSION);
				} else {
					displayName = ChatColor.UNDERLINE + currentName;
				}
			}
		}

		ChatColor mainFormatting = labelFormatting != null && labelFormatting.getMainFormatting() != null ? labelFormatting.getMainFormatting() : The5zigMod.getConfig().get("formattingPrefix", ColorFormattingItem.class).get();
		displayName = (mainFormatting == ChatColor.RESET ? "" : mainFormatting.toString()) + displayName;
		return displayName;
	}

	private int getLabelColor() {
		return labelFormatting != null ? labelFormatting.getMainRgb() : The5zigAPI.getAPI().getFormatting().getPrefixRgb();
	}

	/**
	 * @return true, if the title of the module gets rendered.
	 * @see #setShowLabel(boolean)
	 */
	public boolean isShowLabel() {
		return showLabel;
	}

	/**
	 * Sets, whether the title of the module should get rendered.
	 *
	 * @param showLabel true, if the title of the module should get rendered.
	 * @see #isShowLabel()
	 */
	public void setShowLabel(boolean showLabel) {
		this.showLabel = showLabel;
		The5zigMod.getModuleMaster().save();
	}

	public ModuleLabelFormatting getLabelFormatting() {
		return labelFormatting;
	}

	public void setLabelFormatting(ModuleLabelFormatting labelFormatting) {
		this.labelFormatting = labelFormatting;
		The5zigMod.getModuleMaster().save();
	}

	/**
	 * @return the location of the module.
	 */
	public ModuleLocation getLocation() {
		return location;
	}

	/**
	 * Sets the location of this module.
	 *
	 * @param location the location.
	 */
	public void setLocation(ModuleLocation location) {
		this.location = location;
		this.anchorPoint = null;
		this.locationX = 0;
		this.locationY = 0;
		The5zigMod.getModuleMaster().save();
	}

	public AnchorPoint getAnchorPoint() {
		return anchorPoint;
	}

	public void setAnchorPoint(AnchorPoint anchorPoint) {
		this.anchorPoint = anchorPoint;
	}

	public float getLocationX() {
		return locationX;
	}

	public void setLocationX(float locationX) {
		this.locationX = locationX;
	}

	public float getLocationY() {
		return locationY;
	}

	public void setLocationY(float locationY) {
		this.locationY = locationY;
	}

	public void addItem(AbstractModuleItem item) {
		items.add(new ActiveModuleItem(item));
	}

	public GuiArrayList<ActiveModuleItem> getItems() {
		return items;
	}

	public boolean isShouldRender() {
		if (!enabled) {
			return false;
		}
		if (renderType == RenderType.TIMOLIA_TOURNAMENT) {
			RegisteredServerInstance instance = The5zigMod.getDataManager().getServerInstanceRegistry().getRegisteredInstance("timolia");
			return instance.isOnServer() && instance.getCurrentGameMode() instanceof ServerTimolia.PvP && ((ServerTimolia.PvP) instance.getCurrentGameMode()).getTournament() != null;
		}
		return serverInstance != null ? The5zigMod.getDataManager().getServer() != null && The5zigMod.getDataManager().getServerInstanceRegistry().getRegisteredInstance(
				serverInstance.getConfigName()).isOnServer() && getItemRenderCount(false) != 0 : shouldRender && getItemRenderCount(false) != 0;
	}

	public RenderType getRenderType() {
		return renderType;
	}

	public void setRenderType(RenderType renderType) {
		this.renderType = renderType;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public float getBoxOpacity() {
		return boxOpacity;
	}

	public void setBoxOpacity(float boxOpacity) {
		this.boxOpacity = boxOpacity;
	}

	@Override
	public void draw(int x, int y) {
		The5zigMod.getVars().drawString(The5zigMod.getVars().shortenToWidth(getId(), 145), x + 2, y + 2);
	}

	@Override
	public int getLineHeight() {
		return 16;
	}

	public enum RenderType {
		TIMOLIA_TOURNAMENT, CUSTOM_SERVER
	}
}
