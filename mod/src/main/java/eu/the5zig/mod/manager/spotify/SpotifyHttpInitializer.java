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

package eu.the5zig.mod.manager.spotify;

import eu.the5zig.util.io.http.HttpResponseCallback;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

public class SpotifyHttpInitializer extends ChannelInitializer<Channel> {

	private static final TrustManager[] INSECURE_TRUST_MANAGER = new TrustManager[]{new X509TrustManager() {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(X509Certificate[] certs, String authType) {
		}

		public void checkServerTrusted(X509Certificate[] certs, String authType) {
		}
	}
	};

	private final HttpResponseCallback callback;
	private final boolean ssl;
	private final String host;
	private final int port;
	private int timeout;
	private boolean ignoreInvalidCertificates;

	public SpotifyHttpInitializer(HttpResponseCallback callback, boolean ssl, String host, int port, int timeout, boolean ignoreInvalidCertificates) {
		this.callback = callback;
		this.ssl = ssl;
		this.host = host;
		this.port = port;
		this.timeout = timeout;
		this.ignoreInvalidCertificates = ignoreInvalidCertificates;
	}

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ch.pipeline().addLast("timeout", new ReadTimeoutHandler(timeout, TimeUnit.MILLISECONDS));
		if (ssl) {
			SSLEngine engine;
			if (ignoreInvalidCertificates) {
				engine = createInsecureSSLEngine();
			} else {
				try {
					Class<?> sslContextClass = Class.forName("io.netty.handler.ssl.SslContext");
					Object clientContext = sslContextClass.getMethod("newClientContext").invoke(null);
					engine = (SSLEngine) clientContext.getClass().getMethod("newEngine", ByteBufAllocator.class, String.class, int.class).invoke(clientContext, ch.alloc(), host, port);
				} catch (Exception ignored) {
					engine = createInsecureSSLEngine();
				}
			}
			ch.pipeline().addLast("ssl", new SslHandler(engine));
		}
		ch.pipeline().addLast("http", new HttpClientCodec());
		ch.pipeline().addLast("handler", new SpotifyHttpHandler(callback));
	}

	private SSLEngine createInsecureSSLEngine() throws KeyManagementException, NoSuchAlgorithmException {
		SSLContext context = SSLContext.getInstance("SSL");
		context.init(null, INSECURE_TRUST_MANAGER, new SecureRandom());
		SSLEngine engine = context.createSSLEngine();
		engine.setUseClientMode(true);
		return engine;
	}

}

