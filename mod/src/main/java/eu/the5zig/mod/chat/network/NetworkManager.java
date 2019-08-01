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

package eu.the5zig.mod.chat.network;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.entity.Friend;
import eu.the5zig.mod.chat.network.packets.Packet;
import eu.the5zig.mod.chat.network.packets.PacketHandshake;
import eu.the5zig.mod.chat.network.util.CancelPacketHandleException;
import eu.the5zig.mod.gui.Gui;
import eu.the5zig.mod.gui.GuiParty;
import eu.the5zig.mod.gui.GuiPartyInviteMembers;
import eu.the5zig.mod.gui.GuiPartyManageMembers;
import eu.the5zig.util.minecraft.ChatColor;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.GenericFutureListener;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.net.InetAddress;
import java.nio.channels.UnresolvedAddressException;
import java.util.Queue;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class NetworkManager extends SimpleChannelInboundHandler<Packet> {

	private static final String HOST = The5zigMod.DEBUG ? "localhost" : "tcp.5zigreborn.eu";
	// 28.04.1999 :P
	private static final int PORT = 28499;
	public static NioEventLoopGroup CLIENT_NIO_EVENTLOOP;
	private static final int reconnectAdd = The5zigMod.random.nextInt(30);
	private static int reconnectTries;
	private static final int MAX_RECONNECT_TIME = 400;
	private static Protocol protocol;

	private final Queue<QueuedPacket> outboundPacketsQueue = Queues.newConcurrentLinkedQueue();
	private Channel channel;
	private boolean disconnected = false;
	private boolean reconnecting = false;
	private String disconnectReason = I18n.translate("connection.closed");
	private ConnectionState connectionState;
	private HeartbeatManager heartbeatManager;

	private NetworkEncoder enc;
	private NetworkDecoder dec;

	private NetworkManager() {
		initConnection();
	}

	public static NetworkManager connect() {
		return new NetworkManager();
	}

	public void initLoop() {
		if (CLIENT_NIO_EVENTLOOP == null) {
			try {
				CLIENT_NIO_EVENTLOOP = new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("5zig Netty Client").setDaemon(true).build());
			} catch (Throwable throwable) {
				The5zigMod.logger.error("Could not initialize Nio Event Loop Group! Possible Firewall or Antivirus-Software might be blocking outgoing connections!", throwable);
				if (The5zigMod.getConfig().getBool("showConnecting")) {
					The5zigMod.getOverlayMessage().displayMessage("The 5zig Mod", I18n.translate("connection.error"));
				}
				return;
			}
		}
	}

	private void initConnection() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (protocol == null) {
					protocol = new Protocol();
				}
				initLoop();
				if (The5zigMod.getConversationDatabase() == null) {
					The5zigMod.newConversationDatabase();
				}
				if (!The5zigMod.getConfig().getBool("connectToServer"))
					return;

				Bootstrap bootstrap = new Bootstrap().group(CLIENT_NIO_EVENTLOOP).handler(new ChannelInitializer() {
					protected void initChannel(Channel channel) {
						channel.pipeline()
								.addLast("timeout", new ReadTimeoutHandler(30))
								.addLast("splitter", new NetworkPrepender())
								.addLast("decoder", dec = new NetworkDecoder(NetworkManager.this))
								.addLast("prepender", new NetworkSplitter())
								.addLast("encoder", enc = new NetworkEncoder(NetworkManager.this))
								.addLast(NetworkManager.this);
					}
				}).channel(NioSocketChannel.class);
				try {
					The5zigMod.logger.info("Connecting to {}:{}", HOST, PORT);
					bootstrap.connect(InetAddress.getByName(HOST), PORT).syncUninterruptibly();
					setConnectState(ConnectionState.HANDSHAKE);
					sendPacket(new PacketHandshake());
				} catch (UnresolvedAddressException e) {
					The5zigMod.logger.error("Could not resolve hostname " + HOST, e);
					if (The5zigMod.getConfig().getBool("showConnecting")) {
						The5zigMod.getOverlayMessage().displayMessage(I18n.translate("connection.error"));
					}
					reconnect(The5zigMod.DEBUG ? 15 : 60);
				} catch (Throwable e) {
					The5zigMod.logger.error("An Exception occurred while connecting to " + HOST + ":" + PORT, e);
					if (The5zigMod.getConfig().getBool("showConnecting")) {
						The5zigMod.getOverlayMessage().displayMessage("The 5zig Mod", I18n.translate("connection.error"));
					}
					reconnect(The5zigMod.DEBUG ? 10 : 60);
				}
			}
		}, "5zig Netty Bootstrap").start();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
		if (isChannelOpen()) {
			try {
				packet.handle();
			} catch (CancelPacketHandleException ignored) {
			}
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		disconnect();
	}

	@Override
	public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
		super.channelActive(channelHandlerContext);
		this.channel = channelHandlerContext.channel();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		The5zigMod.logger.error("An Internal Exception occurred", cause);
		if (cause instanceof ReadTimeoutException)
			disconnect(I18n.translate("connection.timed_out"));
		else
			disconnect(I18n.translate("connection.internal_error"));
	}

	public void disconnect() {
		disconnect(I18n.translate("connection.closed"));
	}

	public void disconnect(String disconnectReason) {
		if (isChannelOpen()) {
			closeChannel();
			this.disconnectReason = disconnectReason;
			if (connectionState != ConnectionState.DISCONNECT) {
				setConnectState(ConnectionState.DISCONNECT);
			}
		}
	}

	public boolean checkDisconnected() {
		if (!hasNoChannel() && !this.isChannelOpen() && !this.disconnected) {
			this.disconnected = true;
			The5zigMod.logger.info("Disconnected: " + disconnectReason);
			if (The5zigMod.getConfig().getBool("showConnecting")) {
				The5zigMod.getOverlayMessage().displayMessageAndSplit(ChatColor.YELLOW + I18n.translate("connection.disconnected", disconnectReason));
			}

			The5zigMod.getDataManager().getNetworkStats().resetCurrent();

			reconnect();
			return true;
		}
		return false;
	}

	private void reconnect() {
		reconnect(The5zigMod.DEBUG ? 10 : 30);
	}

	/**
	 * Reconnects to the server with a random amount of seconds added to the time.
	 *
	 * @param time After how many seconds the client should reconnect to the server.
	 */
	private void reconnect(int time) {
		if (reconnecting)
			return;
		reconnecting = true;
		final int seconds = reconnectAdd + (int) (MAX_RECONNECT_TIME - (MAX_RECONNECT_TIME - time) * Math.pow(Math.E, -0.1 * reconnectTries));
		reconnectTries++;
		The5zigMod.logger.info("Reconnecting in {} seconds...", seconds);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(seconds * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
				if (The5zigMod.getNetworkManager() != null && The5zigMod.getNetworkManager().isConnected())
					return;
				The5zigMod.newNetworkManager();
			}
		}).start();
	}

	/**
	 * Checks timeouts and processes all packets received
	 */
	public void tick() {
		this.flushOutboundQueue();
		if (isChannelOpen())
			this.channel.flush();

		checkDisconnected();
	}

	/**
	 * Will iterate through the outboundPacketQueue and dispatch all Packets
	 */
	private void flushOutboundQueue() {
		if (isChannelOpen()) {
			while (!outboundPacketsQueue.isEmpty()) {
				QueuedPacket packet = outboundPacketsQueue.poll();
				dispatchPacket(packet.packet, packet.listeners);
			}
		}
	}

	public void sendPacket(Packet packet, GenericFutureListener... listeners) {
		if (isChannelOpen()) {
			this.flushOutboundQueue();
			this.dispatchPacket(packet, listeners);
		} else {
			outboundPacketsQueue.add(new QueuedPacket(packet, listeners));
		}
	}

	private void dispatchPacket(final Packet packet, final GenericFutureListener[] listeners) {
		if (checkDisconnected())
			return;
		try {
			if (protocol.getProtocol(getProtocol().getPacketId(packet)) != connectionState && protocol.getProtocol(getProtocol().getPacketId(packet)) != ConnectionState.ALL) {
				System.err.printf("Tried to send packet %s in wrong connection state (excpected %s, given %s)! Preventing disconnect!", packet.getClass().getSimpleName(), connectionState,
						getProtocol().getProtocol(getProtocol().getPacketId(packet)));
				return;
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		if (this.channel.eventLoop().inEventLoop()) {
			ChannelFuture future = channel.writeAndFlush(packet);
			if (listeners != null) {
				future.addListeners(listeners);
			}
			future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
		} else {
			this.channel.eventLoop().execute(new Runnable() {
				@Override
				public void run() {
					ChannelFuture future = channel.writeAndFlush(packet);
					if (listeners != null) {
						future.addListeners(listeners);
					}
					future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
				}
			});
		}
	}

	public boolean isChannelOpen() {
		return this.channel != null && this.channel.isOpen();
	}

	public boolean hasNoChannel() {
		return this.channel == null;
	}

	/**
	 * Closes the channel
	 */
	public void closeChannel() {
		if (this.channel.isOpen()) {
			this.channel.close().awaitUninterruptibly();
		}
	}

	public void enableEncryption(SecretKey secretKey) {
		enc.setEnc(new NettyEncryptionTranslator(CryptManager.createNetCipherInstance(Cipher.ENCRYPT_MODE, secretKey)));
		dec.setEnc(new NettyEncryptionTranslator(CryptManager.createNetCipherInstance(Cipher.DECRYPT_MODE, secretKey)));
		The5zigMod.logger.info("Enabled Encryption!");
	}

	public void setThreshold(int threshold) {
		if (threshold >= 0) {
			if (this.channel.pipeline().get("decompress") instanceof NetworkCompressionDecoder) {
				((NetworkCompressionDecoder) this.channel.pipeline().get("decompress")).setCompressionTreshold(threshold);
			} else {
				this.channel.pipeline().addBefore("decoder", "decompress", new NetworkCompressionDecoder(threshold));
			}

			if (this.channel.pipeline().get("compress") instanceof NetworkCompressionEncoder) {
				((NetworkCompressionEncoder) this.channel.pipeline().get("decompress")).setCompressionTreshold(threshold);
			} else {
				this.channel.pipeline().addBefore("encoder", "compress", new NetworkCompressionEncoder(threshold));
			}
		} else {
			if (this.channel.pipeline().get("decompress") instanceof NetworkCompressionDecoder) {
				this.channel.pipeline().remove("decompress");
			}

			if (this.channel.pipeline().get("compress") instanceof NetworkCompressionEncoder) {
				this.channel.pipeline().remove("compress");
			}
		}
	}

	/**
	 * Checks if connection state is play and if the client is not disconnected
	 *
	 * @return If the client is not disconnected and the connection state is play
	 */
	public boolean isConnected() {
		return isChannelOpen() && connectionState == ConnectionState.PLAY;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public HeartbeatManager getHeartbeatManager() {
		return heartbeatManager;
	}

	public ConnectionState getConnectionState() {
		return connectionState;
	}

	public void setConnectState(ConnectionState connectionState) {
		switch (connectionState) {
			case HANDSHAKE:
				this.connectionState = ConnectionState.HANDSHAKE;
				The5zigMod.logger.debug("Handshaking...");
				if (The5zigMod.getConfig().getBool("showConnecting")) {
					The5zigMod.getOverlayMessage().displayMessage("The 5zig Mod", I18n.translate("connection.connecting"));
				}
				break;
			case LOGIN:
				this.connectionState = ConnectionState.LOGIN;
				The5zigMod.logger.debug("Logging in...");
				if (The5zigMod.getConfig().getBool("showConnecting")) {
					The5zigMod.getOverlayMessage().displayMessage("The 5zig Mod", I18n.translate("connection.logging_in"));
				}
				break;
			case PLAY:
				this.connectionState = ConnectionState.PLAY;
				heartbeatManager = new HeartbeatManager();
				The5zigMod.logger.info("Connected after {} tries!", reconnectTries);
				reconnectTries = 0;
				if (The5zigMod.getConfig().getBool("showConnecting")) {
					The5zigMod.getOverlayMessage().displayMessage("The 5zig Mod", I18n.translate("connection.connected"));
				}
				if (The5zigMod.getDataManager().getProfile().isShowServer()) {
					The5zigMod.getDataManager().updateCurrentLobby();
				}
				break;
			case DISCONNECT:
				this.connectionState = ConnectionState.DISCONNECT;
				if (The5zigMod.getConfig().getBool("showConnecting")) {
					The5zigMod.getOverlayMessage().displayMessage("The 5zig Mod", disconnectReason);
				}
				for (Friend friend : The5zigMod.getFriendManager().getFriends()) {
					if (friend.getStatus() != Friend.OnlineStatus.OFFLINE) {
						friend.setStatus(Friend.OnlineStatus.OFFLINE);
						friend.setLastOnline(System.currentTimeMillis());
					}
				}
				The5zigMod.getPartyManager().setParty(null);
				The5zigMod.getPartyManager().getPartyInvitations().clear();
				if (The5zigMod.getVars().getCurrentScreen() instanceof GuiParty) {
					The5zigMod.getVars().getCurrentScreen().initGui0();
				}
				if (The5zigMod.getVars().getCurrentScreen() instanceof GuiPartyInviteMembers ||
						The5zigMod.getVars().getCurrentScreen() instanceof GuiPartyManageMembers) {
					Gui gui = The5zigMod.getVars().getCurrentScreen();
					while (!(gui instanceof GuiParty)) {
						if (gui.lastScreen == null) {
							break;
						}
						gui = gui.lastScreen;
					}
					The5zigMod.getVars().displayScreen(gui);
				}
				break;
			default:
				break;
		}
	}

	private class QueuedPacket {

		private Packet packet;
		private GenericFutureListener[] listeners;

		public QueuedPacket(Packet packet, GenericFutureListener[] listeners) {
			this.packet = packet;
			this.listeners = listeners;
		}
	}
}
