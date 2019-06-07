/**
 * Created by 5zig.
 * All rights reserved © 2015
 * <p/>
 * public class MCPVPListener extends ServerListener {
 * <p/>
 * public MCPVPListener(ServerInstanceRegistry serverInstance) {
 * super(serverInstance);
 * registerListener(new HGListener());
 * }
 *
 * @Override public void onServerJoin(String host, int port) {
 * for (ServerMCPVP.Server serverType : ServerMCPVP.Server.values()) {
 * if (host.toLowerCase(Locale.ROOT).endsWith(serverType.getIp())) {
 * The5zigMod.getDataManager().setServer(new ServerMCPVP(host, port));
 * sendAndIgnore("/ip", "server_address");
 * if (The5zigMod.getConfig().getMCPvPAutoTag() != Configuration.MCPvPTag.DEFAULT) {
 * The5zigMod.getVars().sendMessage("/tag " + The5zigMod.getConfigManager().getConfig().getMCPvPAutoTag());
 * }
 * break;
 * }
 * }
 * super.onServerJoin(host, port);
 * }
 * @Override public void onMatch(String key, PatternResult match) {
 * if (key.equals("server_address")) {
 * if (The5zigMod.getDataManager().getServer().getHost().equals(match.get(0)) && (!(The5zigMod.getDataManager().getServer() instanceof ServerHG) || ((ServerHG) The5zigMod.getDataManager()
 * .getServer()).getKitField() != null))
 * return;
 * super.onServerJoin(match.get(0), 25565);
 * if (!The5zigMod.getDataManager().getServer().getHost().equals(match.get(0)))
 * The5zigMod.getDataManager().setServer(new ServerMCPVP(match.get(0), 25565));
 * if (The5zigMod.getDataManager().getServer() instanceof ServerCTF) {
 * The5zigMod.getVars().sendMessage("/kit heavy");
 * }
 * if (The5zigMod.getDataManager().getServer() instanceof ServerHG) {
 * sendAndIgnore("/kit backup", "hg.kit");
 * }
 * }
 * if (key.equals("now_logged_in")) {
 * onServerJoin("mcpvp.com", 25565);
 * }
 * }
 * <p/>
 * }
 * <p/>
 * public class MCPVPListener extends ServerListener {
 * <p/>
 * public MCPVPListener(ServerInstanceRegistry serverInstance) {
 * super(serverInstance);
 * registerListener(new HGListener());
 * }
 * @Override public void onServerJoin(String host, int port) {
 * for (ServerMCPVP.Server serverType : ServerMCPVP.Server.values()) {
 * if (host.toLowerCase(Locale.ROOT).endsWith(serverType.getIp())) {
 * The5zigMod.getDataManager().setServer(new ServerMCPVP(host, port));
 * sendAndIgnore("/ip", "server_address");
 * if (The5zigMod.getConfig().getMCPvPAutoTag() != Configuration.MCPvPTag.DEFAULT) {
 * The5zigMod.getVars().sendMessage("/tag " + The5zigMod.getConfigManager().getConfig().getMCPvPAutoTag());
 * }
 * break;
 * }
 * }
 * super.onServerJoin(host, port);
 * }
 * @Override public void onMatch(String key, PatternResult match) {
 * if (key.equals("server_address")) {
 * if (The5zigMod.getDataManager().getServer().getHost().equals(match.get(0)) && (!(The5zigMod.getDataManager().getServer() instanceof ServerHG) || ((ServerHG) The5zigMod.getDataManager()
 * .getServer()).getKitField() != null))
 * return;
 * super.onServerJoin(match.get(0), 25565);
 * if (!The5zigMod.getDataManager().getServer().getHost().equals(match.get(0)))
 * The5zigMod.getDataManager().setServer(new ServerMCPVP(match.get(0), 25565));
 * if (The5zigMod.getDataManager().getServer() instanceof ServerCTF) {
 * The5zigMod.getVars().sendMessage("/kit heavy");
 * }
 * if (The5zigMod.getDataManager().getServer() instanceof ServerHG) {
 * sendAndIgnore("/kit backup", "hg.kit");
 * }
 * }
 * if (key.equals("now_logged_in")) {
 * onServerJoin("mcpvp.com", 25565);
 * }
 * }
 * <p/>
 * }
 */
/**
 public class MCPVPListener extends ServerListener {

 public MCPVPListener(ServerInstanceRegistry serverInstance) {
 super(serverInstance);
 registerListener(new HGListener());
 }

 @Override public void onServerJoin(String host, int port) {
 for (ServerMCPVP.Server serverType : ServerMCPVP.Server.values()) {
 if (host.toLowerCase(Locale.ROOT).endsWith(serverType.getIp())) {
 The5zigMod.getDataManager().setServer(new ServerMCPVP(host, port));
 sendAndIgnore("/ip", "server_address");
 if (The5zigMod.getConfig().getMCPvPAutoTag() != Configuration.MCPvPTag.DEFAULT) {
 The5zigMod.getVars().sendMessage("/tag " + The5zigMod.getConfigManager().getConfig().getMCPvPAutoTag());
 }
 break;
 }
 }
 super.onServerJoin(host, port);
 }

 @Override public void onMatch(String key, PatternResult match) {
 if (key.equals("server_address")) {
 if (The5zigMod.getDataManager().getServer().getHost().equals(match.get(0)) && (!(The5zigMod.getDataManager().getServer() instanceof ServerHG) || ((ServerHG) The5zigMod.getDataManager()
 .getServer()).getKitField() != null))
 return;
 super.onServerJoin(match.get(0), 25565);
 if (!The5zigMod.getDataManager().getServer().getHost().equals(match.get(0)))
 The5zigMod.getDataManager().setServer(new ServerMCPVP(match.get(0), 25565));
 if (The5zigMod.getDataManager().getServer() instanceof ServerCTF) {
 The5zigMod.getVars().sendMessage("/kit heavy");
 }
 if (The5zigMod.getDataManager().getServer() instanceof ServerHG) {
 sendAndIgnore("/kit backup", "hg.kit");
 }
 }
 if (key.equals("now_logged_in")) {
 onServerJoin("mcpvp.com", 25565);
 }
 }

 }
 */