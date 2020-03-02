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
import eu.the5zig.mod.gui.elements.StaticModulePreviewRow;
import eu.the5zig.mod.modules.Module;
import eu.the5zig.mod.util.SliderCallback;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class GuiModuleSettings extends Gui {

	private final Module module;

	private List<ButtonRow> settings = Lists.newArrayList();

	public GuiModuleSettings(Gui lastScreen, Module module) {
		super(lastScreen);
		this.module = module;
	}

	@Override
	public void initGui() {
		addBottomDoneButton();

		IGuiList guiList = The5zigMod.getVars().createGuiList(null, getWidth(), getHeight(), 32, getHeight() - 48, getWidth() / 2 - 180, getWidth() / 2 - 10, settings);
		guiList.setRowWidth(160);
		guiList.setLeftbound(true);
		guiList.setDrawSelection(false);
		guiList.setScrollX(getWidth() / 2 - 15);
		guiList.callSetHeaderPadding(The5zigMod.getVars().getFontHeight());
		guiList.setHeader(I18n.translate("modules.settings.list.title"));
		addGuiList(guiList);

		settings.clear();
		settings.add(new ButtonRow(The5zigMod.getVars().createButton(1, getWidth() / 2 - 180, 0, 160, 20, The5zigMod.getVars().shortenToWidth(I18n.translate("modules.settings.id"), 100) +
				": \"" +
				The5zigMod.getVars()
						.shortenToWidth(module.getId(), 145 - The5zigMod.getVars().getStringWidth(The5zigMod.getVars().shortenToWidth(I18n.translate("modules.settings.id"), 100) + ": " +
								"\"\"")) + "\""), null));
		settings.add(new ButtonRow(The5zigMod.getVars().createButton(2, getWidth() / 2 - 180, 0, 160, 20, The5zigMod.getVars().shortenToWidth(I18n.translate("modules.settings.name"), 100) +
				": " +
				(module.getName() == null || module.getName().isEmpty() ? I18n.translate("modules.settings.none") : "\"" + The5zigMod.getVars().shortenToWidth(module.getName(),
						145 - The5zigMod.getVars().getStringWidth(The5zigMod.getVars().shortenToWidth(I18n.translate("modules.settings.name"), 100) + ": \"\"")) +
						"\"")), null));
		settings.add(new ButtonRow(
				The5zigMod.getVars().createButton(3, getWidth() / 2 - 180, 0, 160, 20, The5zigMod.getVars().shortenToWidth(I18n.translate("modules.settings.translation"), 100) + ": " +
						(module.getTranslation() == null || module.getTranslation().isEmpty() ? I18n.translate("modules.settings.none") : "\"" + The5zigMod.getVars().shortenToWidth(
								module.getTranslation(),
								145 - The5zigMod.getVars().getStringWidth(The5zigMod.getVars().shortenToWidth(I18n.translate("modules.settings.translation"), 100) + ": \"\"")) + "\"")),
				null));
		settings.add(new ButtonRow(The5zigMod.getVars().createButton(4, getWidth() / 2 - 180, 0, 160, 20, I18n.translate("modules.settings.items", module.getItems().size())), null));
		settings.add(new ButtonRow(
				The5zigMod.getVars().createButton(5, getWidth() / 2 - 180, 0, 160, 20, I18n.translate("modules.settings.show_label") + ": " + The5zigMod.toBoolean(module.isShowLabel())),
				null));
		settings.add(new ButtonRow(The5zigMod.getVars().createButton(6, getWidth() / 2 - 180, 0, 160, 20, I18n.translate("modules.settings.label.color") + ": " +
				(module.getLabelFormatting() == null ? I18n.translate("modules.settings.default") : I18n.translate("modules.settings.custom"))), null));
		settings.add(new ButtonRow(The5zigMod.getVars().createButton(7, getWidth() / 2 - 180, 0, 160, 20,
				I18n.translate("modules.location") + ": " + I18n.translate("modules.location." + module.getLocation().toString().toLowerCase(Locale.ROOT))), null));
		settings.add(new ButtonRow(The5zigMod.getVars().createButton(8, getWidth() / 2 - 180, 0, 160, 20, I18n.translate("modules.settings.server") + ": " +
				(module.getServer() == null || module.getServer().isEmpty() || The5zigMod.getDataManager().getServerInstanceRegistry().byConfigName(module.getServer()) == null ? I18n.translate("modules.settings.none") :
						The5zigMod.getDataManager().getServerInstanceRegistry().byConfigName(module.getServer()).getName())), null));
		settings.add(new ButtonRow(The5zigMod.getVars().createButton(9, getWidth() / 2 - 180, 0, 160, 20,
				I18n.translate("modules.settings.render") + ": " + (module.getRenderType() == null ? I18n.translate("modules.settings.none") : module.getRenderType())), null));
		settings.add(new ButtonRow(The5zigMod.getVars().createSlider(10, getWidth() / 2 - 180, 0, 160, 20, new SliderCallback() {
			@Override
			public String translate() {
				return I18n.translate("modules.settings.scale");
			}

			@Override
			public float get() {
				return module.getScale();
			}

			@Override
			public void set(float value) {
				module.setScale(value);
			}

			@Override
			public float getMinValue() {
				return 0.5f;
			}

			@Override
			public float getMaxValue() {
				return 1.5f;
			}

			@Override
			public int getSteps() {
				return -1;
			}

			@Override
			public String getCustomValue(float value) {
				return Math.round((value * (getMaxValue() - getMinValue()) + getMinValue()) * 100) + getSuffix();
			}

			@Override
			public String getSuffix() {
				return "%";
			}

			@Override
			public void action() {
				The5zigMod.getModuleMaster().save();

			}
		}), null));
		settings.add(new ButtonRow(The5zigMod.getVars().createSlider(11, getWidth() / 2 - 180, 0, 160, 20, new SliderCallback() {
			@Override
			public String translate() {
				return I18n.translate("modules.settings.box_opacity");
			}

			@Override
			public float get() {
				return module.getBoxOpacity();
			}

			@Override
			public void set(float value) {
				module.setBoxOpacity(value);
			}

			@Override
			public float getMinValue() {
				return 0.0f;
			}

			@Override
			public float getMaxValue() {
				return 1.0f;
			}

			@Override
			public int getSteps() {
				return -1;
			}

			@Override
			public String getCustomValue(float value) {
				return Math.round((value * (getMaxValue() - getMinValue()) + getMinValue()) * 100) + getSuffix();
			}

			@Override
			public String getSuffix() {
				return "%";
			}

			@Override
			public void action() {
				The5zigMod.getModuleMaster().save();

			}
		}), null));

		IGuiList modulePreviewList = The5zigMod.getVars().createGuiList(null, getWidth(), getHeight(), 32, getHeight() - 48, getWidth() / 2 + 10, getWidth() / 2 + 180,
				Collections.singletonList(new StaticModulePreviewRow(module, getWidth() / 2 + 10, 33, 170, getHeight() - 48 - 34)));
		modulePreviewList.setLeftbound(true);
		modulePreviewList.setDrawSelection(false);
		modulePreviewList.setScrollX(getWidth() / 2 + 175);
		addGuiList(modulePreviewList);
	}

	@Override
	protected void actionPerformed(final IButton button) {
		if (button.getId() == 1) {
			The5zigMod.getVars().displayScreen(new GuiCenteredTextfield(this, new CenteredTextfieldCallback() {
				@Override
				public void onDone(String text) {
					if (!text.isEmpty()) {
						for (Module m : The5zigMod.getModuleMaster().getModules()) {
							if (m.getId().equals(text)) {
								The5zigMod.getOverlayMessage().displayMessageAndSplit(ChatColor.RED + I18n.translate("modules.settings.id.already_used"));
								return;
							}
						}
						module.setId(text);
					}
				}

				@Override
				public String title() {
					return I18n.translate("modules.settings.id.change");
				}
			}, module.getId(), -1, 100));
		}
		if (button.getId() == 2) {
			The5zigMod.getVars().displayScreen(new GuiCenteredTextfield(this, new CenteredTextfieldCallback() {
				@Override
				public void onDone(String text) {
					if (text.isEmpty()) {
						module.setName(null);
					} else {
						module.setName(text);
					}
				}

				@Override
				public String title() {
					return I18n.translate("modules.settings.name.change");
				}
			}, module.getName() == null ? "" : module.getName(), -1, 100));
		}
		if (button.getId() == 3) {
			The5zigMod.getVars().displayScreen(new GuiCenteredTextfield(this, new CenteredTextfieldCallback() {
				@Override
				public void onDone(String text) {
					if (text.isEmpty()) {
						module.setTranslation(null);
					} else {
						module.setTranslation(text);
					}
				}

				@Override
				public String title() {
					return I18n.translate("modules.settings.translation.change");
				}
			}, module.getTranslation() == null ? "" : module.getTranslation(), -1, 100));
		}
		if (button.getId() == 4) {
			The5zigMod.getVars().displayScreen(new GuiModuleItems(this, module));
		}
		if (button.getId() == 5) {
			module.setShowLabel(!module.isShowLabel());
			button.setLabel(I18n.translate("modules.settings.show_label") + ": " + The5zigMod.toBoolean(module.isShowLabel()));
		}
		if (button.getId() == 6) {
			The5zigMod.getVars().displayScreen(new GuiModuleLabelColor(this, module));
		}
		if (button.getId() == 7) {
			The5zigMod.getVars().displayScreen(new GuiModuleLocation(this, module));
		}
		if (button.getId() == 8) {
			The5zigMod.getVars().displayScreen(new GuiModuleServer(this, module));
		}
		if (button.getId() == 9) {
			The5zigMod.getVars().displayScreen(new GuiModuleRender(this, module));
		}
	}

	@Override
	public String getTitleKey() {
		return "modules.settings.title";
	}
}
