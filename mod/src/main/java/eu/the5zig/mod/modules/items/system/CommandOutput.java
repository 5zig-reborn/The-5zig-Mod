/*
 * Copyright (c) 2019-2020 5zig Reborn
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

package eu.the5zig.mod.modules.items.system;

import com.google.common.base.Strings;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.modules.StringItem;
import eu.the5zig.mod.render.RenderLocation;
import eu.the5zig.mod.util.ScrollingText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandOutput extends StringItem {

    private long lastRefresh;
    private String value, last;
    private ScrollingText text;

    @Override
    public void registerSettings() {
        getProperties().addSetting("command", "");
        getProperties().addSetting("regex", "");
        getProperties().addSetting("refresh", "", 5.0f, 5f, 500.0f, 1);
        getProperties().addSetting("scrolling", "", 10f, 0f, 500f, 1);
    }

    @Override
    protected Object getValue(boolean dummy) {
        return value;
    }

    @Override
    public void render(int x, int y, RenderLocation renderLocation, boolean dummy) {
        int scrollingThreshold = (int)(float) getProperties().getSetting("scrolling").get();
        if(scrollingThreshold == 0 || value == null || value.length() < scrollingThreshold) {
            super.render(x, y, renderLocation, dummy);
            return;
        }
        The5zigMod.getVars().drawString(getPrefix(), x, y);
        if(text != null) text.render(x + The5zigMod.getVars().getStringWidth(getPrefix()), y);
    }



    private void refresh() {
        String cmdSetting = (String) getProperties().getSetting("command").get();
        if(cmdSetting == null || cmdSetting.isEmpty()) return;
        String regexSetting = (String) getProperties().getSetting("regex").get();
        String[] cmd = cmdSetting.split(" ");
        The5zigMod.getAsyncExecutor().execute(() -> {
            Runtime rt = Runtime.getRuntime();
            try {
                Process proc = rt.exec(cmd);
                try(InputStreamReader reader = new InputStreamReader(proc.getInputStream())) {
                    try(BufferedReader buf = new BufferedReader(reader)) {
                        String result = buf.readLine();
                        if(regexSetting == null || regexSetting.isEmpty()) {
                            value = result;
                            return;
                        }
                        Pattern regex = Pattern.compile(regexSetting);
                        Matcher matcher = regex.matcher(result);
                        StringBuilder sb = new StringBuilder();
                        if(matcher.find()) {
                            for(int i = 1; i <= matcher.groupCount(); i++) {
                                sb.append(matcher.group(i)).append(" ");
                            }
                            value = sb.toString().trim();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                value = "Error";
            }
        });
    }

    @Override
    public int getHeight(boolean dummy) {
        return 10;
    }

    @Override
    public boolean shouldRender(boolean dummy) {
        if(dummy) return true;
        int refreshRate = (int)(float) getProperties().getSetting("refresh").get();
        if(lastRefresh == 0 || System.currentTimeMillis() - lastRefresh >= refreshRate * 1000) {
            lastRefresh = System.currentTimeMillis();
            doRefresh(false);
        }
        return value != null && !value.trim().isEmpty();
    }

    private void doRefresh(boolean forced) {
        refresh();
        int scrollingThreshold = (int)(float) getProperties().getSetting("scrolling").get();
        if(forced || last == null || !last.equals(value)) {
            int width = The5zigMod.getVars().getStringWidth(Strings.repeat("A", scrollingThreshold));
            text = new ScrollingText(value, width, 10, 0x0, 0xffffffff);
        }
        last = value;
    }

    @Override
    public void settingsUpdated() {
        doRefresh(true);
    }
}
