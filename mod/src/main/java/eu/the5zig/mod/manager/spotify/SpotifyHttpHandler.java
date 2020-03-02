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

package eu.the5zig.mod.manager.spotify;

import eu.the5zig.util.io.http.HttpResponseCallback;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpotifyHttpHandler extends SimpleChannelInboundHandler<HttpObject> {

	private final HttpResponseCallback callback;
	private final StringBuilder buffer = new StringBuilder();
	private int responseCode = 500;
	private AtomicBoolean hasResponded = new AtomicBoolean(false);

	public SpotifyHttpHandler(HttpResponseCallback callback) {
		this.callback = callback;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		try {
			if (hasResponded.compareAndSet(false, true)) {
				callback.call(buffer.toString(), responseCode, cause);
			}
		} finally {
			ctx.channel().close();
		}
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
		if (msg instanceof HttpResponse) {
			HttpResponse response = (HttpResponse) msg;
			responseCode = response.getStatus().code();

			if (responseCode == HttpResponseStatus.NO_CONTENT.code()) {
				done(ctx);
				return;
			}

			if (responseCode != HttpResponseStatus.OK.code()) {
				throw new IllegalStateException("Expected HTTP response 200 OK, got " + response.getStatus());
			}
		}
		if (msg instanceof HttpContent) {
			HttpContent content = (HttpContent) msg;
			buffer.append(content.content().toString(Charset.forName("UTF-8")));

			if (msg instanceof LastHttpContent) {
				done(ctx);
			}
		}
	}

	private void done(ChannelHandlerContext ctx) {
		try {
			if (hasResponded.compareAndSet(false, true)) {
				callback.call(buffer.toString(), responseCode, null);
			}
		} finally {
			ctx.channel().close();
		}
	}
}
