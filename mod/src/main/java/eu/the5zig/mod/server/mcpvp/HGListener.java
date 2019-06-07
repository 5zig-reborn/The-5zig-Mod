/**
 * Created by 5zig.
 * All rights reserved © 2015
 * <p/>
 * public class HGListener extends GameListener {
 * <p/>
 * public HGListener() {
 * super(ServerHG.class);
 * }
 *
 * @Override public void onServerJoin(String host, int port) {
 * LastServerConfig config = The5zigMod.getConfigManager().getLastServerConfig();
 * DataManager dataManager = The5zigMod.getDataManager();
 * <p/>
 * if (host.toLowerCase(Locale.ROOT).endsWith("mc-hg.com")) {
 * if (config.getLastServer() instanceof ServerHG && config.getLastServer().getHost().equals(host) && !host.equalsIgnoreCase("mc-hg.com")) {
 * dataManager.setServer(config.getLastServer());
 * return;
 * }
 * dataManager.setServer(new ServerHG(host, port));
 * }
 * }
 * @Override public void onMatch(String key, PatternResult match) {
 * ServerHG server = (ServerHG) The5zigMod.getDataManager().getServer();
 * if (key.equals("hg.kit") || key.equals("hg.kit.surprise")) {
 * server.setKit(match.get(0));
 * }
 * if (key.equals("hg.start")) {
 * server.onStart();
 * }
 * if (The5zigMod.getConfigManager().getConfig().getBool("showCompassTarget")) {
 * if (key.equals("hg.track.player")) {
 * The5zigMod.getGuiIngame().showTextAboveHotbar("Tracking: " + match.get(0));
 * }
 * if (key.equals("hg.track.feast")) {
 * The5zigMod.getGuiIngame().showTextAboveHotbar("Tracking: Feast");
 * }
 * }
 * if (key.equals("hg.feast.start")) {
 * int x = Integer.parseInt(match.get(0));
 * int y = Integer.parseInt(match.get(1));
 * server.setFeast(new Feast(x, y));
 * }
 * if (key.equals("hg.minifeast")) {
 * int xMin = Integer.parseInt(match.get(0));
 * int xMax = Integer.parseInt(match.get(1));
 * int zMin = Integer.parseInt(match.get(2));
 * int zMax = Integer.parseInt(match.get(3));
 * server.addMiniFeast(new MiniFeast(xMin, xMax, zMin, zMax));
 * }
 * if (key.equals("hg.kill.1") || key.equals("hg.kill.2") || key.equals("hg.kill.3") || key.equals("hg.kill.4")) {
 * if (match.get(0).equals(The5zigMod.getDataManager().getUsername())) {
 * server.setKills(server.getKills() + 1);
 * server.addKillstreak();
 * }
 * }
 * }
 * <p/>
 * }
 * <p/>
 * public class HGListener extends GameListener {
 * <p/>
 * public HGListener() {
 * super(ServerHG.class);
 * }
 * @Override public void onServerJoin(String host, int port) {
 * LastServerConfig config = The5zigMod.getConfigManager().getLastServerConfig();
 * DataManager dataManager = The5zigMod.getDataManager();
 * <p/>
 * if (host.toLowerCase(Locale.ROOT).endsWith("mc-hg.com")) {
 * if (config.getLastServer() instanceof ServerHG && config.getLastServer().getHost().equals(host) && !host.equalsIgnoreCase("mc-hg.com")) {
 * dataManager.setServer(config.getLastServer());
 * return;
 * }
 * dataManager.setServer(new ServerHG(host, port));
 * }
 * }
 * @Override public void onMatch(String key, PatternResult match) {
 * ServerHG server = (ServerHG) The5zigMod.getDataManager().getServer();
 * if (key.equals("hg.kit") || key.equals("hg.kit.surprise")) {
 * server.setKit(match.get(0));
 * }
 * if (key.equals("hg.start")) {
 * server.onStart();
 * }
 * if (The5zigMod.getConfigManager().getConfig().getBool("showCompassTarget")) {
 * if (key.equals("hg.track.player")) {
 * The5zigMod.getGuiIngame().showTextAboveHotbar("Tracking: " + match.get(0));
 * }
 * if (key.equals("hg.track.feast")) {
 * The5zigMod.getGuiIngame().showTextAboveHotbar("Tracking: Feast");
 * }
 * }
 * if (key.equals("hg.feast.start")) {
 * int x = Integer.parseInt(match.get(0));
 * int y = Integer.parseInt(match.get(1));
 * server.setFeast(new Feast(x, y));
 * }
 * if (key.equals("hg.minifeast")) {
 * int xMin = Integer.parseInt(match.get(0));
 * int xMax = Integer.parseInt(match.get(1));
 * int zMin = Integer.parseInt(match.get(2));
 * int zMax = Integer.parseInt(match.get(3));
 * server.addMiniFeast(new MiniFeast(xMin, xMax, zMin, zMax));
 * }
 * if (key.equals("hg.kill.1") || key.equals("hg.kill.2") || key.equals("hg.kill.3") || key.equals("hg.kill.4")) {
 * if (match.get(0).equals(The5zigMod.getDataManager().getUsername())) {
 * server.setKills(server.getKills() + 1);
 * server.addKillstreak();
 * }
 * }
 * }
 * <p/>
 * }
 */
/**
 public class HGListener extends GameListener {

 public HGListener() {
 super(ServerHG.class);
 }

 @Override public void onServerJoin(String host, int port) {
 LastServerConfig config = The5zigMod.getConfigManager().getLastServerConfig();
 DataManager dataManager = The5zigMod.getDataManager();

 if (host.toLowerCase(Locale.ROOT).endsWith("mc-hg.com")) {
 if (config.getLastServer() instanceof ServerHG && config.getLastServer().getHost().equals(host) && !host.equalsIgnoreCase("mc-hg.com")) {
 dataManager.setServer(config.getLastServer());
 return;
 }
 dataManager.setServer(new ServerHG(host, port));
 }
 }

 @Override public void onMatch(String key, PatternResult match) {
 ServerHG server = (ServerHG) The5zigMod.getDataManager().getServer();
 if (key.equals("hg.kit") || key.equals("hg.kit.surprise")) {
 server.setKit(match.get(0));
 }
 if (key.equals("hg.start")) {
 server.onStart();
 }
 if (The5zigMod.getConfigManager().getConfig().getBool("showCompassTarget")) {
 if (key.equals("hg.track.player")) {
 The5zigMod.getGuiIngame().showTextAboveHotbar("Tracking: " + match.get(0));
 }
 if (key.equals("hg.track.feast")) {
 The5zigMod.getGuiIngame().showTextAboveHotbar("Tracking: Feast");
 }
 }
 if (key.equals("hg.feast.start")) {
 int x = Integer.parseInt(match.get(0));
 int y = Integer.parseInt(match.get(1));
 server.setFeast(new Feast(x, y));
 }
 if (key.equals("hg.minifeast")) {
 int xMin = Integer.parseInt(match.get(0));
 int xMax = Integer.parseInt(match.get(1));
 int zMin = Integer.parseInt(match.get(2));
 int zMax = Integer.parseInt(match.get(3));
 server.addMiniFeast(new MiniFeast(xMin, xMax, zMin, zMax));
 }
 if (key.equals("hg.kill.1") || key.equals("hg.kill.2") || key.equals("hg.kill.3") || key.equals("hg.kill.4")) {
 if (match.get(0).equals(The5zigMod.getDataManager().getUsername())) {
 server.setKills(server.getKills() + 1);
 server.addKillstreak();
 }
 }
 }

 }*/
