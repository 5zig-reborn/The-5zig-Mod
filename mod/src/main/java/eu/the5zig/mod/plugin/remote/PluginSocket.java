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

package eu.the5zig.mod.plugin.remote;

import org.apache.commons.io.IOUtils;

import javax.crypto.Cipher;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

public class PluginSocket {
    private ServerSocket inner;
    private Socket activeConnection;
    private PublicKey decryptionKey;

    public void start() throws Exception {
        inner = new ServerSocket(31415, 50, InetAddress.getByName("127.0.0.1"));
        byte[] keyBytes = IOUtils.toByteArray(getClass().getResource("/plugin_sock.pub"));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        decryptionKey = factory.generatePublic(spec);
        listen();
    }

    private void listen() throws Exception {
        // Limit concurrent connections to 1
        if(activeConnection == null) {
            activeConnection = inner.accept();
            byte[] data = IOUtils.toByteArray(activeConnection.getInputStream());
            Cipher cipher = Cipher.getInstance("RSA/None/PKCS1");
            cipher.init(Cipher.DECRYPT_MODE, decryptionKey);
            byte[] dec = cipher.doFinal(data);
            int pluginId = ByteBuffer.wrap(dec).getInt();
            System.out.println(pluginId);
        }
    }
}
