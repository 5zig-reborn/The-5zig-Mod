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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.config.items.*;
import eu.the5zig.mod.gui.elements.ButtonRow;
import eu.the5zig.mod.gui.elements.Clickable;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.elements.IGuiList;
import eu.the5zig.mod.modules.ActiveModuleItem;
import eu.the5zig.mod.modules.Module;
import eu.the5zig.mod.modules.ModuleItemPropertiesImpl;
import eu.the5zig.mod.modules.RenderSettingsImpl;
import eu.the5zig.mod.render.RenderLocation;
import eu.the5zig.mod.util.Keyboard;
import eu.the5zig.mod.util.SliderCallback;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class GuiModuleItems extends Gui implements Clickable<ActiveModuleItem> {

	private static final int START_INDEX = 500;

	private final Module module;

	private IGuiList<ActiveModuleItem> guiListItems;
	private int itemIndex;
	private List<ButtonRow> buttons = Lists.newArrayList();
	private HashMap<IButton, ConfigItem> buttonMap = Maps.newHashMap();

	private long lastMouseMoved;
	private int lastMouseX, lastMouseY;

	public GuiModuleItems(Gui lastScreen, Module module) {
		super(lastScreen);
		this.module = module;
	}

	@Override
	public void initGui() {
		addBottomDoneButton();
		guiListItems = The5zigMod.getVars().createGuiList(this, getWidth(), getHeight(), 32, getHeight() - 48 - 22, getWidth() / 2 - 180, getWidth() / 2 - 10, module.getItems());
		guiListItems.setRowWidth(160);
		guiListItems.setLeftbound(true);
		guiListItems.setDrawSelection(true);
		guiListItems.setScrollX(getWidth() / 2 - 15);
		guiListItems.callSetHeaderPadding(The5zigMod.getVars().getFontHeight());
		guiListItems.setHeader(I18n.translate("modules.settings.item.list.items"));

		IGuiList guiListSettings = The5zigMod.getVars().createGuiList(null, getWidth(), getHeight(), 32, getHeight() - 50 - 90, getWidth() / 2 + 10, getWidth() / 2 + 180, buttons);
		guiListSettings.setDrawSelection(false);
		guiListSettings.setLeftbound(true);
		guiListSettings.setRowWidth(160);
		guiListSettings.setScrollX(getWidth() / 2 + 175);
		guiListSettings.callSetHeaderPadding(The5zigMod.getVars().getFontHeight());
		guiListSettings.setHeader(I18n.translate("modules.settings.item.list.settings"));
		addGuiList(guiListSettings);
		addGuiList(guiListItems);

		guiListItems.setSelectedId(itemIndex);
		guiListItems.onSelect(guiListItems.getSelectedId(), guiListItems.getSelectedRow(), true);

		addButton(The5zigMod.getVars().createButton(1, getWidth() / 2 - 180, getHeight() - 48 - 20, 145, 20, I18n.translate("modules.settings.item.add")));
		addButton(The5zigMod.getVars().createButton(2, getWidth() / 2 - 30, getHeight() - 48 - 20, 20, 20, "-"));

		addButton(The5zigMod.getVars().createButton(10, getWidth() / 2 + 10, getHeight() - 48 - 90, 83, 20, I18n.translate("modules.settings.item.color") + ": " +
				(guiListItems.getSelectedRow() == null || guiListItems.getSelectedRow().getHandle().getProperties().getFormatting() == null ? I18n.translate("modules.settings.default") :
						I18n.translate("modules.settings.custom"))));
		addButton(The5zigMod.getVars().createButton(11, getWidth() / 2 + 97, getHeight() - 48 - 90, 83, 20, I18n.translate("modules.settings.item.prefix") + ": " +
				The5zigMod.toBoolean(guiListItems.getSelectedRow() == null || guiListItems.getSelectedRow().getHandle().getProperties().isShowPrefix())));
		addButton(The5zigMod.getVars().createButton(12, getWidth() / 2 + 10, getHeight() - 48 - 68, 20, 20, "▲"));
		addButton(The5zigMod.getVars().createButton(13, getWidth() / 2 + 35, getHeight() - 48 - 68, 20, 20, "▼"));
		addButton(The5zigMod.getVars().createButton(14, getWidth() / 2 + 60, getHeight() - 48 - 68, 120, 20, I18n.translate("modules.label")));
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 1) {
			The5zigMod.getVars().displayScreen(new GuiModuleAddItem(this, module));
		} else if (button.getId() == 2) {
			int currentIndex = guiListItems.getSelectedId();
			synchronized (module.getItems()) {
				if (!module.getItems().isEmpty()) {
					module.getItems().remove(currentIndex);
					The5zigMod.getModuleMaster().save();
				}
			}
			onSelect(guiListItems.getSelectedId(), guiListItems.getSelectedRow(), false);
		} else if (button.getId() == 10) {
			if (guiListItems.getSelectedRow() != null) {
				The5zigMod.getVars().displayScreen(new GuiModuleItemColor(this, guiListItems.getSelectedRow()));
			}
		} else if (button.getId() == 11) {
			if (guiListItems.getSelectedRow() != null) {
				boolean showPrefix = !guiListItems.getSelectedRow().getHandle().getProperties().isShowPrefix();
				guiListItems.getSelectedRow().getHandle().getProperties().setShowPrefix(showPrefix);
				The5zigMod.getModuleMaster().save();
				button.setLabel(I18n.translate("modules.settings.item.prefix") + ": " +
						The5zigMod.toBoolean(guiListItems.getSelectedRow() == null || guiListItems.getSelectedRow().getHandle().getProperties().isShowPrefix()));
			}
		} else if (button.getId() == 12) {
			if (move(-1)) {
				The5zigMod.getModuleMaster().save();
			}
		} else if (button.getId() == 13) {
			if (move(1)) {
				The5zigMod.getModuleMaster().save();
			}
		} else if (buttonMap.containsKey(button)) {
			ConfigItem item = buttonMap.get(button);
			if(item instanceof StringItem) {
				The5zigMod.getVars().displayScreen(new GuiCenteredTextfield(this, new CenteredTextfieldCallback() {
					@Override
					public void onDone(String text) {
						item.set(text);
						button.setLabel(item.translate());
						if(guiListItems.getSelectedRow() != null) {
							guiListItems.getSelectedRow().getHandle().settingsUpdated();
						}
						The5zigMod.getModuleMaster().save();
					}

					@Override
					public String title() {
						return null;
					}
				}));
				return;
			}
			item.next();
			item.action();
			button.setLabel(item.translate());
			if(guiListItems.getSelectedRow() != null) {
				guiListItems.getSelectedRow().getHandle().settingsUpdated();
			}
			if (item.hasChanged())
				The5zigMod.getModuleMaster().save();
		} else if(button.getId() == 14) {
			if(guiListItems.getSelectedRow() != null) {
				ActiveModuleItem row = guiListItems.getSelectedRow();
				The5zigMod.getVars().displayScreen(new GuiCenteredTextfield(this, new CenteredTextfieldCallback() {
					@Override
					public void onDone(String text) {
						row.getHandle().getProperties().setCustomLabel(text);
						The5zigMod.getModuleMaster().save();
					}

					@Override
					public String title() {
						return I18n.translate("modules.label.input");
					}
				}));
			}
		}
	}

	@Override
	public void onSelect(int id, ActiveModuleItem item, boolean doubleClick) {
		itemIndex = id;
		buttons.clear();
		buttonMap.clear();
		if (item == null) {
			return;
		}
		int index = START_INDEX;
		for (ConfigItem configItem : ((ModuleItemPropertiesImpl) item.getHandle().getProperties()).getSettings()) {
			IButton button = getButton(configItem, index++);
			buttons.add(new ButtonRow(button, null));
			buttonMap.put(button, configItem);
		}
	}

	private IButton getButton(ConfigItem item, int id) {
		if (item instanceof PlaceholderItem) {
			return null;
		}
		if (item instanceof SliderItem) {
			final SliderItem sliderItem = (SliderItem) item;
			return The5zigMod.getVars().createSlider(id, getWidth() / 2 + 10, 0, new SliderCallback() {
				@Override
				public String translate() {
					return sliderItem.translate();
				}

				@Override
				public float get() {
					return sliderItem.get();
				}

				@Override
				public void set(float value) {
					sliderItem.set(value);
				}

				@Override
				public float getMinValue() {
					return sliderItem.getMinValue();
				}

				@Override
				public float getMaxValue() {
					return sliderItem.getMaxValue();
				}

				@Override
				public int getSteps() {
					return sliderItem.getSteps();
				}

				@Override
				public String getCustomValue(float value) {
					return sliderItem.getCustomValue(value);
				}

				@Override
				public String getSuffix() {
					return sliderItem.getSuffix();
				}

				@Override
				public void action() {
					sliderItem.setChanged(true);
					sliderItem.action();
					The5zigMod.getModuleMaster().save();
					sliderItem.setChanged(false);
				}
			});
		}
		if (item instanceof SelectColorItem) {
			return The5zigMod.getVars().createColorSelector(id, getWidth() / 2 + 10, 0, 160, 20, item.translate(), GuiSettings.mapColor((SelectColorItem) item));
		}

		return The5zigMod.getVars().createButton(id, getWidth() / 2 + 10, 0, 160, 20, item.translate());
	}

	@Override
	protected void tick() {
		getButtonById(2).setEnabled(guiListItems.getSelectedRow() != null);
		getButtonById(10).setEnabled(guiListItems.getSelectedRow() != null);
		getButtonById(11).setEnabled(guiListItems.getSelectedRow() != null);
		getButtonById(12).setEnabled(guiListItems.getSelectedId() > 0);
		getButtonById(13).setEnabled(guiListItems.getSelectedId() < module.getItems().size() - 1);
		getButtonById(14).setEnabled(guiListItems.getSelectedRow() != null);
	}

	@Override
	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
		int x = getWidth() / 2 + 10;
		int y = getHeight() - 42 - 50;
		int width = 170;
		int height = 44;
		renderItemPreviewBox(x, y, width, height);
		if (guiListItems.getSelectedRow() != null) {
			ActiveModuleItem item = guiListItems.getSelectedRow();
			int itemHeight = item.getHandle().getHeight(true);
			int itemWidth = item.getHandle().getWidth(true);
			RenderSettingsImpl.assign(new RenderSettingsImpl(module.getScale()), item.getHandle());
			item.getHandle().render(x + (width - itemWidth) / 2, y + (height - itemHeight) / 2, RenderLocation.LEFT, true);
		}
	}

	@Override
	public void drawScreen0(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen0(mouseX, mouseY, partialTicks);
		ActiveModuleItem hover = guiListItems.getHoverItem(mouseX, mouseY);
		if (hover != null) {
			String key = "modules.item." + The5zigMod.getModuleItemRegistry().byItem(hover.getHandle().getClass()).getKey().toLowerCase(Locale.ROOT);
			if (I18n.has(key + ".desc")) {
				The5zigMod.getVars().getCurrentScreen().drawHoveringText(The5zigMod.getVars().splitStringToWidth(I18n.translate(key) + "\n" + I18n.translate(key + ".desc"), 140), mouseX,
						mouseY);
			}
		}

		if (lastMouseX != mouseX || lastMouseY != mouseY) {
			lastMouseX = mouseX;
			lastMouseY = mouseY;
			lastMouseMoved = System.currentTimeMillis();
		}
		if (System.currentTimeMillis() - lastMouseMoved > 700) {
			IButton hovered = getHoveredButton(mouseX, mouseY);
			if (hovered != null) {
				ConfigItem item = buttonMap.get(hovered);
				if (item != null) {
					String hoverText = item.getHoverText();

					List<String> lines = Lists.newArrayList();
					lines.add(hovered.getLabel());
					lines.addAll(The5zigMod.getVars().splitStringToWidth(hoverText, 150));
					if (!(item instanceof NonConfigItem) && Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
						lines.add(ChatColor.DARK_GRAY.toString() + ChatColor.ITALIC + I18n.translate("config.click_to_reset"));
					}
					drawHoveringText(lines, mouseX, mouseY);
				}
			}
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);
		tryReset();
	}

	@Override
	protected void mouseReleased(int x, int y, int state) {
		super.mouseReleased(x, y, state);
		tryReset();
	}

	private void tryReset() {
		if (Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
			IButton hovered = getHoveredButton(lastMouseX, lastMouseY);
			if (hovered != null && !(hovered instanceof NonConfigItem)) {
				ConfigItem item = buttonMap.get(hovered);
				item.reset();
				hovered.setLabel(item.translate());
				if (item.hasChanged())
					The5zigMod.getConfig().save();
			}
		}
	}

	private IButton getHoveredButton(int mouseX, int mouseY) {
		for (ButtonRow button : buttons) {
			if (button.button1 != null && isHovered(button.button1, mouseX, mouseY)) {
				return button.button1;
			} else if (button.button2 != null && isHovered(button.button2, mouseX, mouseY)) {
				return button.button2;
			}
		}
		return null;
	}

	private boolean isHovered(IButton button, int lastMouseX, int lastMouseY) {
		return lastMouseX >= button.getX() && lastMouseX <= button.getX() + button.callGetWidth() && lastMouseY >= button.getY() && lastMouseY <= button.getY() + button.callGetHeight();
	}

	private void renderItemPreviewBox(int x, int y, int width, int height) {
		Gui.drawRect(x, y, x + width, y + height, 0x88222222);
		Gui.drawRectOutline(x, y, x + width, y + height, 0xff000000);
	}

	@Override
	public String getTitleKey() {
		return "modules.settings.title";
	}

	private boolean move(int pos) {
		ActiveModuleItem item = guiListItems.getSelectedRow();
		if (item == null) {
			return false;
		}
		List<ActiveModuleItem> items = module.getItems();

		int currentIndex = items.indexOf(item);
		int nextIndex = currentIndex + pos;

		if (nextIndex >= 0 && nextIndex < items.size()) {
			Collections.swap(items, currentIndex, nextIndex);
			guiListItems.setSelectedId(nextIndex);
			return true;
		}
		return false;
	}
}
