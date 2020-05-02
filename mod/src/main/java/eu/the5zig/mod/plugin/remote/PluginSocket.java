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

import eu.the5zig.mod.The5zigMod;
import org.apache.commons.io.IOUtils;

import javax.crypto.Cipher;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PluginSocket {
    private ServerSocket inner;
    private PublicKey decryptionKey;

    private GuiPluginSocket display;

    public static void startListening() {
        GuiPluginSocket gui = new GuiPluginSocket(The5zigMod.getVars().getCurrentScreen());
        PluginSocket socket = new PluginSocket(gui);
        new Thread(() -> {
            try {
                socket.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        The5zigMod.getVars().displayScreen(gui);
    }

    private PluginSocket(GuiPluginSocket display) {
        this.display = display;
        display.setSocket(this);
    }

    public void start() throws Exception {
        inner = new ServerSocket(31415, 1, InetAddress.getByName("127.0.0.1"));
        byte[] derBytes = IOUtils.toByteArray(getClass().getResourceAsStream("/plugin_socket.pub"));

        KeyFactory factory = KeyFactory.getInstance("RSA");
        decryptionKey = factory.generatePublic(new X509EncodedKeySpec(derBytes));
        listen();
    }

    private void listen() throws Exception {
        display.setState(GuiPluginSocket.DownloadState.LISTENING);
        try(Socket activeConnection = inner.accept()) {
            try(InputStream stream = activeConnection.getInputStream()) {
                try(InputStreamReader isr = new InputStreamReader(stream)) {
                    try(BufferedReader buf = new BufferedReader(isr)) {
                        HttpData data = dataFromHttp(buf.readLine());
                        if(data == null) return;
                        String base64 = data.pluginId;
                        writeResponse(activeConnection.getOutputStream(), data.httpVer);
                        byte[] realData = Base64.getDecoder().decode(base64);
                        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                        cipher.init(Cipher.DECRYPT_MODE, decryptionKey);
                        byte[] dec = cipher.doFinal(realData);
                        int pluginId = ByteBuffer.wrap(dec).getInt();
                        display.setState(GuiPluginSocket.DownloadState.DOWNLOADING_INFO);
                        RemotePluginDownloader dl = new RemotePluginDownloader();
                        display.setDownloader(dl);
                        RemotePlugin plugin = dl.download(pluginId);
                        display.setPreview(plugin);
                    }
                }
            }
        }
        inner.close();
    }

    private void writeResponse(OutputStream stream, String httpVer) throws IOException {
        stream.write(("HTTP/" + httpVer + " 200 OK\n\n Please follow the instructions on the 5zig mod.").getBytes(StandardCharsets.UTF_8));
        stream.flush();
    }

    private HttpData dataFromHttp(String in) {
        Pattern p = Pattern.compile("GET /plugin/(.*) HTTP/(.*)");
        Matcher m = p.matcher(in);
        if(m.find()) {
            HttpData data = new HttpData();
            data.pluginId = m.group(1);
            data.httpVer = m.group(2);
            return data;
        }
        return null;
    }

    public void stop() {
        try {
            inner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class HttpData {
        public String pluginId, httpVer;
    }
}
