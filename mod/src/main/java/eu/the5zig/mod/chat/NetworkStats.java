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

package eu.the5zig.mod.chat;

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.sql.NetworkStatsEntity;
import eu.the5zig.util.Container;
import eu.the5zig.util.Utils;
import eu.the5zig.util.db.Database;
import io.netty.buffer.ByteBuf;

import java.util.List;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class NetworkStats {

	private Database sql;
	private long lastSaveTime;

	private int totalPacketsSent;
	private int currentPacketsSent;
	private int totalPacketsReceived;
	private int currentPacketsReceived;
	private long totalBytesSent;
	private long currentBytesSent;
	private long totalBytesReceived;
	private long currentBytesReceived;
	private long since;

	private Container<Integer> connectedClients = new Container<Integer>(0);
	private Container<Integer> maxClients = new Container<Integer>(0);
	private Container<Long> upTime = new Container<Long>(0L);
	private Container<Integer> ping = new Container<Integer>(0);
	private Container<Integer> lag = new Container<Integer>(0);
	private Container<String> serverUpTime = new Container<String>() {
		@Override
		public String getValue() {
			return Utils.convertToTimeWithDays(System.currentTimeMillis() - getStartTime().getValue());
		}
	};

	public NetworkStats() {
	}

	public void init(Database sql) {
		this.sql = sql;

		sql.update("CREATE TABLE IF NOT EXISTS network_stats (key VARCHAR(20), value VARCHAR(20))");

		List<NetworkStatsEntity> entityBaseList = sql.get(NetworkStatsEntity.class).query("SELECT * FROM network_stats").getAll();
		if (entityBaseList.isEmpty()) {
			sql.update("INSERT INTO network_stats (key, value) VALUES ('packetsSent', '0'), ('packetsReceived', '0'), ('bytesSent', '0'), ('bytesReceived', '0'), ('since', ?)",
					System.currentTimeMillis());
			since = System.currentTimeMillis();
		} else {
			for (NetworkStatsEntity entity : entityBaseList) {
				String key = entity.getKey();
				String value = entity.getValue();

				if ("packetsSent".equals(key))
					totalPacketsSent = Integer.parseInt(value);
				if ("packetsReceived".equals(key))
					totalPacketsReceived = Integer.parseInt(value);
				if ("bytesSent".equals(key))
					totalBytesSent = Long.parseLong(value);
				if ("bytesReceived".equals(key))
					totalBytesReceived = Long.parseLong(value);
				if ("since".equals(key))
					since = Long.parseLong(value);
			}
		}
	}

	public void tick() {
		if (System.currentTimeMillis() - lastSaveTime > 1000 * 60) {
			saveStats();
			lastSaveTime = System.currentTimeMillis();
		}
	}

	private void saveStats() {
		The5zigMod.getAsyncExecutor().execute(new Runnable() {
			@Override
			public void run() {
				The5zigMod.logger.debug("Saving Network Stats...");
				sql.update("UPDATE network_stats SET value=? WHERE key=?", String.valueOf(totalPacketsSent), "packetsSent");
				sql.update("UPDATE network_stats SET value=? WHERE key=?", String.valueOf(totalPacketsReceived), "packetsReceived");
				sql.update("UPDATE network_stats SET value=? WHERE key=?", String.valueOf(totalBytesSent), "bytesSent");
				sql.update("UPDATE network_stats SET value=? WHERE key=?", String.valueOf(totalBytesReceived), "bytesReceived");
			}
		});
	}

	public void onPacketSend(ByteBuf buf) {
		totalPacketsSent++;
		currentPacketsSent++;
		totalBytesSent += buf.readableBytes();
		currentBytesSent += buf.readableBytes();
	}

	public void onPacketReceive(ByteBuf buf) {
		totalPacketsReceived++;
		currentPacketsReceived++;
		totalBytesReceived += buf.readableBytes();
		currentBytesReceived += buf.readableBytes();
	}

	public int getTotalPacketsSent() {
		return totalPacketsSent;
	}

	public int getTotalPacketsReceived() {
		return totalPacketsReceived;
	}

	public int getCurrentPacketsSent() {
		return currentPacketsSent;
	}

	public int getCurrentPacketsReceived() {
		return currentPacketsReceived;
	}

	public String getTotalBytesSent() {
		return Utils.bytesToReadable(totalBytesSent);
	}

	public String getTotalBytesReceived() {
		return Utils.bytesToReadable(totalBytesReceived);
	}

	public String getCurrentBytesSent() {
		return Utils.bytesToReadable(currentBytesSent);
	}

	public String getCurrentBytesReceived() {
		return Utils.bytesToReadable(currentBytesReceived);
	}

	public String getBytesTotal() {
		return Utils.bytesToReadable(totalBytesReceived + totalBytesSent);
	}

	public int getPacketsTotal() {
		return totalPacketsReceived + totalPacketsSent;
	}

	public String since() {
		return Utils.convertToDate(since).replace("Today", I18n.translate("profile.today").replace("Yesterday", I18n.translate("profile.yesterday")));
	}

	public Container<Integer> getConnectedClients() {
		return connectedClients;
	}

	public void setConnectedClients(int connectedClients) {
		this.connectedClients.setValue(connectedClients);
	}

	public Container<Long> getStartTime() {
		return upTime;
	}

	public void setStartTime(long upTime) {
		this.upTime.setValue(upTime);
	}

	public Container<Integer> getPing() {
		return ping;
	}

	public void setPing(int ping) {
		this.ping.setValue(ping);
	}

	public Container<Integer> getMaxClients() {
		return maxClients;
	}

	public void setMaxClients(int maxClients) {
		this.maxClients.setValue(maxClients);
	}

	public Container<Integer> getLag() {
		return lag;
	}

	public void setLag(int lag) {
		this.lag.setValue(lag);
	}

	public Container<String> getServerUpTime() {
		return serverUpTime;
	}

	public void resetCurrent() {
		currentPacketsSent = 0;
		currentPacketsReceived = 0;
		currentBytesSent = 0;
		currentBytesReceived = 0;
	}
}