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

package eu.the5zig.mod.chat.network.packets;

import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.network.CryptManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.UUID;

public class PacketEncryption implements Packet {

	private PublicKey publicKey;
	private byte[] verifyToken;

	private byte[] secretKeyEncrypted;
	private byte[] verifyTokenEncrypted;

	public PacketEncryption(SecretKey secretKey, PublicKey publicKey, byte[] verifyToken) {
		this.secretKeyEncrypted = CryptManager.encryptData(publicKey, secretKey.getEncoded());
		this.verifyTokenEncrypted = CryptManager.encryptData(publicKey, verifyToken);
	}

	public PacketEncryption() {
	}

	@Override
	public void read(ByteBuf buffer) throws IOException {
		this.publicKey = CryptManager.decodePublicKey(PacketBuffer.readByteArray(buffer));
		this.verifyToken = PacketBuffer.readByteArray(buffer);
	}

	@Override
	public void write(ByteBuf buffer) throws IOException {
		PacketBuffer.writeByteArray(buffer, secretKeyEncrypted);
		PacketBuffer.writeByteArray(buffer, verifyTokenEncrypted);
	}

	@Override
	public void handle() {
		final SecretKey secretKey = CryptManager.createNewSharedKey();
		String hash = (new BigInteger(CryptManager.getServerIdHash("", publicKey, secretKey))).toString(16);
		MinecraftSessionService yggdrasil = new YggdrasilAuthenticationService(The5zigMod.getVars().getProxy(), UUID.randomUUID().toString()).createMinecraftSessionService();
		try {
			yggdrasil.joinServer(The5zigMod.getVars().getGameProfile(), The5zigMod.getDataManager().getSession(), hash);
		} catch (AuthenticationException e) {
			The5zigMod.getNetworkManager().disconnect(I18n.translate("connection.bad_login"));
			throw new RuntimeException(e);
		}
		The5zigMod.getNetworkManager().sendPacket(new PacketEncryption(secretKey, publicKey, verifyToken), new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture channelFuture) throws Exception {
				The5zigMod.getNetworkManager().enableEncryption(secretKey);
			}
		});
	}
}
