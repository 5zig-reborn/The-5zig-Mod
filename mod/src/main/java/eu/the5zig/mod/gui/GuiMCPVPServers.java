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
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.server.mcpvp.MCPVPServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public abstract class GuiMCPVPServers extends Gui implements Runnable {

	protected List<MCPVPServer> servers = Lists.newArrayList();
	protected boolean isLoadingServers;

	public GuiMCPVPServers(Gui lastScreen) {
		super(lastScreen);
	}

	protected void refreshServers() {
		servers.clear();
		ping();
	}

	private void ping() {
		new Thread(this, "The5zigMod | MCPVP Server Ping").start();
	}

	protected void joinServer(int id) {
		if (id < 0 || id > servers.size())
			return;
		MCPVPServer server = this.servers.get(id);
		The5zigMod.getVars().joinServer(server.getIP(), 25565);
	}

	protected void joinServer(String ip) {
		The5zigMod.getVars().joinServer(ip, 25565);
	}

	public String getTitleKey() {
		return "mcpvp_servers.title";
	}

	public void run() {
		this.isLoadingServers = true;
		StringBuilder jsonString = new StringBuilder();
		HttpURLConnection connection;
		BufferedReader br = null;
		InputStreamReader isr = null;
		try {
			URL url = new URL("http://apps.minecraftpvp.com/api/ping.json");
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			connection.setRequestProperty("Accept-Charset", "UTF-8");
			connection.connect();
			String encoding = connection.getContentEncoding();
			if (connection.getResponseCode() == 200) {
				if ((encoding != null) && (encoding.equalsIgnoreCase("gzip"))) {
					isr = new InputStreamReader(new GZIPInputStream(connection.getInputStream()), Charset.forName("UTF-8"));
				} else if ((encoding != null) && (encoding.equalsIgnoreCase("deflate"))) {
					isr = new InputStreamReader(new InflaterInputStream(connection.getInputStream(), new Inflater(true)), Charset.forName("GB2312"));
				} else {
					isr = new InputStreamReader(connection.getInputStream(), Charset.forName("GB2312"));
				}
			} else {
				isr = new InputStreamReader(connection.getErrorStream());
			}
			br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null) {
				jsonString.append(line);
			}
			if (connection.getResponseCode() == 200) {
				Gson gson = new Gson();
				JsonParser jsonParser = new JsonParser();
				JsonArray jsonElement = (JsonArray) jsonParser.parse(jsonString.toString());
				this.servers.clear();
				Set<MCPVPServer> set = new HashSet<MCPVPServer>();
				for (JsonElement p : jsonElement) {
					MCPVPServer server = gson.fromJson(p, MCPVPServer.class);
					set.add(server);
				}
				addServers(set);
				sort(this.servers);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (isr != null) {
				try {
					isr.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		this.isLoadingServers = false;
	}

	protected abstract void sort(List<MCPVPServer> paramList);

	protected abstract void addServers(Set<MCPVPServer> paramSet);
}
