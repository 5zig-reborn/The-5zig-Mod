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

package eu.the5zig.teamspeak.net;

import eu.the5zig.teamspeak.response.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.common.collect.*;
import eu.the5zig.teamspeak.request.*;
import io.netty.util.concurrent.*;
import io.netty.channel.*;

public class TeamSpeakHandler extends SimpleChannelInboundHandler<TeamSpeakResponse>
{
    private final TeamSpeakNetworkManager networkManager;
    private Channel channel;
    private final Queue<OutboundPacket> QUEUE;
    
    TeamSpeakHandler(final TeamSpeakNetworkManager networkManager) {
        this.QUEUE = new ConcurrentLinkedQueue<>();
        this.networkManager = networkManager;
    }
    
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        this.channel = ctx.channel();
        this.flushOutboundQueue();
    }
    
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        this.networkManager.disconnect(null);
    }
    
    protected void channelRead0(final ChannelHandlerContext channelHandlerContext, final TeamSpeakResponse response) throws Exception {
        this.networkManager.handleResponse(response);
    }
    
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        this.networkManager.disconnect(cause);
    }
    
    public void closeChannel() {
        if (this.channel != null && this.channel.isOpen()) {
            this.channel.close().awaitUninterruptibly();
        }
    }
    
    public void send(final Request request, final GenericFutureListener... futureListeners) {
        if (this.channel != null && this.channel.isOpen()) {
            final ChannelFuture channelFuture = this.channel.writeAndFlush((Object)request);
            channelFuture.addListeners(futureListeners);
        }
        else {
            this.QUEUE.add(new OutboundPacket(request, futureListeners));
        }
    }
    
    private void flushOutboundQueue() {
        while (!this.QUEUE.isEmpty()) {
            final OutboundPacket outboundPacket = this.QUEUE.poll();
            this.send(outboundPacket.request, outboundPacket.futureListeners);
        }
    }
    
    private static class OutboundPacket
    {
        private Request request;
        private GenericFutureListener[] futureListeners;
        
        public OutboundPacket(final Request request, final GenericFutureListener... futureListeners) {
            this.request = request;
            this.futureListeners = futureListeners;
        }
    }
}
