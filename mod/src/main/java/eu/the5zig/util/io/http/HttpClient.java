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

package eu.the5zig.util.io.http;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class HttpClient {

	public static final int TIMEOUT = 5000;
	private static final Cache<String, InetAddress> addressCache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();

	@SuppressWarnings("UnusedAssignment")
	public static void get(String url, EventLoopGroup eventLoop, final HttpResponseCallback callback) {
		Preconditions.checkNotNull(url, "url");
		Preconditions.checkNotNull(eventLoop, "eventLoop");
		Preconditions.checkNotNull(callback, "callBack");

		final URI uri = URI.create(url);

		Preconditions.checkNotNull(uri.getScheme(), "scheme");
		Preconditions.checkNotNull(uri.getHost(), "host");
		boolean ssl = uri.getScheme().equals("https");
		int port = uri.getPort();
		if (port == -1) {
			if (uri.getScheme().equals("http"))
				port = 80;
			else if (uri.getScheme().equals("https"))
				port = 443;
			else
				throw new IllegalArgumentException("Unknown scheme " + uri.getScheme());
		}

		InetAddress inetHost = addressCache.getIfPresent(uri.getHost());
		if (inetHost == null) {
			try {
				inetHost = InetAddress.getByName(uri.getHost());
			} catch (UnknownHostException ex) {
				callback.call(null, -1, ex);
				return;
			}
			addressCache.put(uri.getHost(), inetHost);
		}

		ChannelFutureListener future = new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					String path = uri.getRawPath() + ((uri.getRawQuery() == null) ? "" : "?" + uri.getRawQuery());

					HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, path);
					request.headers().set(HttpHeaders.Names.HOST, uri.getHost());

					future.channel().writeAndFlush(request);
				} else {
					callback.call(null, -1, future.cause());
				}
			}
		};

		new Bootstrap().channel(NioSocketChannel.class).group(eventLoop).handler(new HttpInitializer(callback, ssl, uri.getHost(), port)).
				option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT).remoteAddress(inetHost, port).connect().addListener(future);
	}

}
