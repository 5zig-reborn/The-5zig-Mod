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

package eu.the5zig.mod.api;

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.Version;
import eu.the5zig.mod.event.EventHandler;
import eu.the5zig.mod.event.PayloadEvent;
import eu.the5zig.mod.event.ServerJoinEvent;
import eu.the5zig.util.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @see <a href="https://gist.github.com/5zig/35e0854504edda418f4b">https://gist.github.com/5zig/35e0854504edda418f4b</a>
 */
public class SettingListener {

	@EventHandler
	public void onServerJoin(ServerJoinEvent event) {
		PayloadUtils.sendPayload(Utils.versionCompare(Version.MCVERSION, "1.13") >= 0 ? "minecraft:register" : "REGISTER", PayloadUtils.writeString(Unpooled.buffer(), PayloadUtils.SETTING_CHANNEL));
		PayloadUtils.sendPayload(PayloadUtils.SETTING_CHANNEL, Unpooled.buffer().writeByte(Version.APIVERSION));
	}

	@EventHandler
	public void onPayloadReceive(PayloadEvent event) {
		String channel = event.getChannel();
		ByteBuf packetData = event.getPayload();
		if (!PayloadUtils.SETTING_CHANNEL.equals(channel) || The5zigMod.getDataManager().getServer() == null)
			return;
		byte setting = packetData.readByte(); // 0x00 = everything, 0x01 = potion effects, 0x02 = potion indicator, 0x04 = armor, 0x08 = saturation, 0x10 = entity health, 0x20 = auto reconnect
		The5zigMod.logger.info("Received payload on setting channel: " + setting);
		if ((setting & 0x01) != 0) {
			The5zigMod.getDataManager().getServer().setRenderPotionEffects(false);
			if (The5zigMod.getModuleMaster().isItemActive("POTIONS"))
				message(I18n.translate("api.setting_disabled.potion_hud"));
		} else {
			The5zigMod.getDataManager().getServer().setRenderPotionEffects(true);
		}
		if ((setting & 0x02) != 0) {
			The5zigMod.getDataManager().getServer().setRenderPotionIndicator(false);
			if (The5zigMod.getConfig().getBool("showPotionIndicator"))
				message(I18n.translate("api.setting_disabled.potion_indicator"));
		} else {
			The5zigMod.getDataManager().getServer().setRenderPotionIndicator(true);
		}
		if ((setting & 0x04) != 0) {
			The5zigMod.getDataManager().getServer().setRenderArmor(false);
			if (The5zigMod.getModuleMaster().isItemActive("MAIN_HAND") || The5zigMod.getModuleMaster().isItemActive("OFF_HAND") || The5zigMod.getModuleMaster().isItemActive("HELMET") ||
					The5zigMod.getModuleMaster().isItemActive("CHESTPLATE") || The5zigMod.getModuleMaster().isItemActive("LEGGINGS") || The5zigMod.getModuleMaster().isItemActive("BOOTS") ||
					The5zigMod.getModuleMaster().isItemActive("ARROWS") || The5zigMod.getModuleMaster().isItemActive("SOUPS"))
				message(I18n.translate("api.setting_disabled.armor_hud"));
		} else {
			The5zigMod.getDataManager().getServer().setRenderArmor(true);
		}
		if ((setting & 0x08) != 0) {
			The5zigMod.getDataManager().getServer().setRenderSaturation(false);
			if (The5zigMod.getConfig().getBool("showSaturation"))
				message(I18n.translate("api.setting_disabled.saturation"));
		} else {
			The5zigMod.getDataManager().getServer().setRenderSaturation(true);
		}
		if ((setting & 0x10) != 0) {
			// flag 0x10 is the entity health flag and only used by older mod versions
//			The5zigMod.getDataManager().getServer().setRenderEntityHealth(false);
//			if (The5zigMod.getConfig().getBool("showEntityHealth"))
//				message(I18n.translate("api.setting_disabled.entity_health"));
		} else {
//			The5zigMod.getDataManager().getServer().setRenderEntityHealth(true);
		}
		if ((setting & 0x20) != 0) {
			The5zigMod.getDataManager().getServer().setAutoReconnecting(false);
		} else {
			The5zigMod.getDataManager().getServer().setAutoReconnecting(true);
		}
	}

	private void message(String setting) {
		The5zigMod.getVars().messagePlayer(The5zigMod.getRenderer().getPrefix("The 5zig Mod") + I18n.translate("api.setting_disabled", setting));
	}
}
