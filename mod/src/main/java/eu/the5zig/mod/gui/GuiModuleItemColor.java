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
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.elements.ButtonRow;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.elements.IGuiList;
import eu.the5zig.mod.modules.ActiveModuleItem;
import eu.the5zig.mod.modules.ModuleItemFormattingImpl;
import eu.the5zig.mod.util.ColorSelectorCallback;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.Arrays;
import java.util.List;

public class GuiModuleItemColor extends Gui {

	private static final List<ChatColor> FORMATTINGS = Arrays.asList(ChatColor.RESET, ChatColor.BOLD, ChatColor.ITALIC, ChatColor.UNDERLINE);

	private final ActiveModuleItem item;

	private List<ButtonRow> buttons = Lists.newArrayList();
	private List<IButton> colorButtons = Lists.newArrayList();

	public GuiModuleItemColor(Gui lastScreen, ActiveModuleItem item) {
		super(lastScreen);
		this.item = item;
	}

	@Override
	public void initGui() {
		addBottomDoneButton();

		IGuiList guiList = The5zigMod.getVars().createGuiList(null, getWidth(), getHeight(), 32, getHeight() - 48, 0, getWidth(), buttons);
		guiList.setDrawSelection(false);
		addGuiList(guiList);

		buttons.clear();
		buttons.add(new ButtonRow(The5zigMod.getVars().createButton(1, getWidth() / 2 - 75, 0, 150, 20,
				I18n.translate("modules.settings.item.color") + ": " + (item.getHandle().getProperties().getFormatting() == null ? I18n.translate("modules.settings.default") : I18n.translate("modules.settings.custom"))),
				null));

		IButton button1 = The5zigMod.getVars().createButton(10, getWidth() / 2 - 155, 0, 150, 20, I18n.translate("modules.settings.item.prefix_formatting") + ": " +
				(item.getHandle().getProperties().getFormatting() == null || item.getHandle().getProperties().getFormatting().getPrefixFormatting() == null ? I18n.translate("modules.settings.default") : item.getHandle().getProperties().getFormatting().getPrefixFormatting().name()));
		IButton button2 = The5zigMod.getVars().createColorSelector(11, getWidth() / 2 + 5, 0, 150, 20, I18n.translate("modules.settings.item.prefix_color"), new ColorSelectorCallback() {
			@Override
			public ChatColor getColor() {
				return item.getHandle().getProperties().getFormatting() == null || item.getHandle().getProperties().getFormatting().getPrefixColor() == null ? ChatColor.WHITE : item.getHandle().getProperties().getFormatting().getPrefixColor();
			}

			@Override
			public void setColor(ChatColor color) {
				((ModuleItemFormattingImpl) item.getHandle().getProperties().getFormatting()).setPrefixColor(color);
				The5zigMod.getModuleMaster().save();
			}
		});
		buttons.add(new ButtonRow(button1, button2));
		colorButtons.add(button1);
		colorButtons.add(button2);
		IButton button3 = The5zigMod.getVars().createButton(12, getWidth() / 2 - 155, 0, 150, 20, I18n.translate("modules.settings.item.main_formatting") + ": " +
				(item.getHandle().getProperties().getFormatting() == null || item.getHandle().getProperties().getFormatting().getMainFormatting() == null ? I18n.translate("modules.settings.default") : item.getHandle().getProperties().getFormatting().getMainFormatting().name()));
		IButton button4 = The5zigMod.getVars().createColorSelector(13, getWidth() / 2 + 5, 0, 150, 20, I18n.translate("modules.settings.item.main_color"), new ColorSelectorCallback() {
			@Override
			public ChatColor getColor() {
				return item.getHandle().getProperties().getFormatting() == null || item.getHandle().getProperties().getFormatting().getMainColor() == null ? ChatColor.WHITE : item.getHandle().getProperties().getFormatting().getMainColor();
			}

			@Override
			public void setColor(ChatColor color) {
				((ModuleItemFormattingImpl) item.getHandle().getProperties().getFormatting()).setMainColor(color);
				The5zigMod.getModuleMaster().save();
			}
		});
		buttons.add(new ButtonRow(button3, button4));
		colorButtons.add(button3);
		colorButtons.add(button4);
		String prefix = item.getHandle().getProperties().getFormatting() == null
				? I18n.translate("spotify.token.not_set")
				: "#" + Integer.toString(item.getHandle().getProperties().getFormatting().getPrefixRgb(), 16);
		String main = item.getHandle().getProperties().getFormatting() == null
				? I18n.translate("spotify.token.not_set")
				: "#" + Integer.toString(item.getHandle().getProperties().getFormatting().getMainRgb(), 16);
		IButton prefixRgb = The5zigMod.getVars().createButton(14, getWidth() / 2 - 155, 0, 150, 20,
				I18n.translate("config.formatting.rgb_prefix") + ": " + prefix);
		IButton mainRgb = The5zigMod.getVars().createButton(15, getWidth() / 2 + 5, 0, 150, 20,
				I18n.translate("config.formatting.rgb_main") + ": " + main);
		buttons.add(new ButtonRow(prefixRgb, mainRgb));
		colorButtons.add(prefixRgb);
		colorButtons.add(mainRgb);
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 1) {
			if (item.getHandle().getProperties().getFormatting() == null) {
				item.getHandle().getProperties().setFormatting(new ModuleItemFormattingImpl(null, null, null, null));
			} else {
				item.getHandle().getProperties().setFormatting(null);
			}
			The5zigMod.getModuleMaster().save();
			button.setLabel(I18n.translate("modules.settings.item.color") + ": " +
					(item.getHandle().getProperties().getFormatting() == null ? I18n.translate("modules.settings.default") : I18n.translate("modules.settings.custom")));
		}
		if (button.getId() == 10) {
			if (item.getHandle().getProperties().getFormatting().getPrefixFormatting() == null) {
				((ModuleItemFormattingImpl) item.getHandle().getProperties().getFormatting()).setPrefixFormatting(FORMATTINGS.get(0));
			} else if (item.getHandle().getProperties().getFormatting().getPrefixFormatting() == FORMATTINGS.get(FORMATTINGS.size() - 1)) {
				((ModuleItemFormattingImpl) item.getHandle().getProperties().getFormatting()).setPrefixFormatting(null);
			} else {
				((ModuleItemFormattingImpl) item.getHandle().getProperties().getFormatting()).setPrefixFormatting(FORMATTINGS
						.get(FORMATTINGS.indexOf(item.getHandle().getProperties().getFormatting().getPrefixFormatting()) + 1));
			}
			The5zigMod.getModuleMaster().save();
			button.setLabel(I18n.translate("modules.settings.item.prefix_formatting") + ": " +
					(item.getHandle().getProperties().getFormatting() == null || item.getHandle().getProperties().getFormatting().getPrefixFormatting() == null
							? I18n.translate("modules.settings.default") : item.getHandle().getProperties().getFormatting().getPrefixFormatting().name()));
		}
		if (button.getId() == 12) {
			if (item.getHandle().getProperties().getFormatting().getMainFormatting() == null) {
				((ModuleItemFormattingImpl) item.getHandle().getProperties().getFormatting()).setMainFormatting(FORMATTINGS.get(0));
			} else if (item.getHandle().getProperties().getFormatting().getMainFormatting() == FORMATTINGS.get(FORMATTINGS.size() - 1)) {
				((ModuleItemFormattingImpl) item.getHandle().getProperties().getFormatting()).setMainFormatting(null);
			} else {
				((ModuleItemFormattingImpl) item.getHandle().getProperties().getFormatting()).setMainFormatting(FORMATTINGS
						.get(FORMATTINGS.indexOf(item.getHandle().getProperties().getFormatting().getMainFormatting()) + 1));
			}
			The5zigMod.getModuleMaster().save();
			button.setLabel(I18n.translate("modules.settings.item.main_formatting") + ": " +
					(item.getHandle().getProperties().getFormatting() == null || item.getHandle().getProperties().getFormatting().getMainFormatting() == null
							? I18n.translate("modules.settings.default") : item.getHandle().getProperties().getFormatting().getMainFormatting().name()));
		}
		if (button.getId() == 14) {
			The5zigMod.getVars().displayScreen(new GuiCenteredTextfield(this, new CenteredTextfieldCallback() {
				@Override
				public void onDone(String text) {
					((ModuleItemFormattingImpl) item.getHandle().getProperties().getFormatting()).setPrefixRgb(Integer.parseInt(text.replace("#", ""), 16));
					The5zigMod.getModuleMaster().save();
				}

				@Override
				public String title() {
					return null;
				}
			}));
		}
		if (button.getId() == 15) {
			The5zigMod.getVars().displayScreen(new GuiCenteredTextfield(this, new CenteredTextfieldCallback() {
				@Override
				public void onDone(String text) {
					((ModuleItemFormattingImpl) item.getHandle().getProperties().getFormatting()).setMainRgb(Integer.parseInt(text.replace("#", ""), 16));
					The5zigMod.getModuleMaster().save();
				}

				@Override
				public String title() {
					return null;
				}
			}));
		}
	}

	@Override
	protected void tick() {
		for (IButton colorButton : colorButtons) {
			colorButton.setEnabled(item.getHandle().getProperties().getFormatting() != null);
		}
	}

	@Override
	public String getTitleKey() {
		return "modules.settings.title";
	}
}
