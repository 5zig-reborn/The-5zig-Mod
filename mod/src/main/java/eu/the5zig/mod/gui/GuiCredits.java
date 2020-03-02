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
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.elements.BasicRow;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.elements.IGuiList;
import eu.the5zig.mod.gui.elements.Row;
import eu.the5zig.util.LinkedProperties;
import eu.the5zig.util.Utils;

import java.io.IOException;
import java.util.*;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class GuiCredits extends Gui {

	private static final LinkedHashMap<String, List<String>> credits = Maps.newLinkedHashMap();

	static {
		try {
			LinkedProperties properties = new LinkedProperties();
			properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("credits.txt"));

			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				credits.put("credits." + entry.getKey(), Arrays.asList(String.valueOf(entry.getValue()).split(", ")));
			}
		} catch (IOException e) {
			e.printStackTrace();
			credits.put("Error", Collections.singletonList(e.getMessage()));
		}
	}

	private List<Row> rows = Lists.newArrayList();

	public GuiCredits(Gui lastScreen) {
		super(lastScreen);
	}

	@Override
	public void initGui() {
		addButton(The5zigMod.getVars().createButton(200, getWidth() / 2 - 100, getHeight() - 35, The5zigMod.getVars().translate("gui.done")));

		rows.clear();

		for (final Map.Entry<String, List<String>> entry : credits.entrySet()) {
			for (int i = 0; i < entry.getValue().size(); i++) {
				final String credit = entry.getValue().get(i);
				if (i == 0) {
					rows.add(new Row() {
						@Override
						public int getLineHeight() {
							return 12;
						}

						@Override
						public void draw(int x, int y) {
							The5zigMod.getVars().drawString(I18n.translate(entry.getKey()) + ": ", getWidth() / 2 - 100, y + 2);
							The5zigMod.getVars().drawString(credit, getWidth() / 2, y + 2);
						}
					});
				} else {
					rows.add(new Row() {
						@Override
						public int getLineHeight() {
							return 12;
						}

						@Override
						public void draw(int x, int y) {
							The5zigMod.getVars().drawString(credit, getWidth() / 2, y + 2);
						}
					});
				}
				if (i + 1 == entry.getValue().size()) {
					rows.add(new BasicRow(""));
				}
			}
		}

		The5zigMod.getAsyncExecutor().execute(() -> {
			String in = Utils.downloadFile("https://secure.5zigreborn.eu/credits");
			JsonArray array = new JsonParser().parse(in).getAsJsonArray();
			for(int i = 0; i < array.size(); i++) {
				String user = array.get(i).getAsString();
				if(i == 0) {
					rows.add(new Row() {
						@Override
						public int getLineHeight() {
							return 12;
						}

						@Override
						public void draw(int x, int y) {
							The5zigMod.getVars().drawString(I18n.translate("credits.patrons") + ": ", getWidth() / 2 - 100, y + 2);
							The5zigMod.getVars().drawString(user, getWidth() / 2, y + 2);
						}
					});
				}
				else {
					rows.add(new Row() {
						@Override
						public int getLineHeight() {
							return 12;
						}

						@Override
						public void draw(int x, int y) {
							The5zigMod.getVars().drawString(user, getWidth() / 2, y + 2);
						}
					});
				}
			}
		});

		IGuiList guiList = The5zigMod.getVars().createGuiList(null, getWidth(), getHeight(), 50, getHeight() - 50, 0, getWidth(), rows);
		guiList.setLeftbound(true);
		guiList.setDrawSelection(false);
		guiList.setScrollX(getWidth() / 2 + 120);
		addGuiList(guiList);
	}

	@Override
	protected void actionPerformed(IButton button) {

	}

	@Override
	public String getTitleKey() {
		return "credits.title";
	}

}
