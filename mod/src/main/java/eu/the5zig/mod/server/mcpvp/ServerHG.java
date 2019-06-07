/**
 * public class ServerHG extends ServerMCPVP {
 * <p/>
 * private boolean started;
 * private String kit;
 * private Long millisStarted = -1l;
 * private Long lastKillstreakTime = -1l;
 * private Integer killstreak = 0;
 * private Integer kills = 0;
 * private Feast feast;
 * private ArrayList<MiniFeast> minifeasts = new ArrayList<MiniFeast>();
 * <p/>
 * public ServerHG(String host, int port) {
 * super(host, port);
 * The5zigMod.getConfigManager().getLastServerConfig().setLastServer(this);
 * The5zigMod.getConfigManager().saveLastServers();
 * started = false;
 * }
 * <p/>
 * public String getKit() {
 * return kit == null ? I18n.translate("ingame.kit.none") : kit;
 * }
 * <p/>
 * public void setKit(String kit) {
 * this.kit = kit;
 * The5zigMod.getConfigManager().saveLastServers();
 * }
 * <p/>
 * public String getKitField() {
 * return kit;
 * }
 * <p/>
 * public int getKills() {
 * return kills;
 * }
 * <p/>
 * public void setKills(int kills) {
 * this.kills = kills;
 * The5zigMod.getConfigManager().saveLastServers();
 * }
 * <p/>
 * public boolean showInvincibilityWearsOffTime() {
 * return The5zigMod.getConfigManager().getConfig().isShowInvincibilityWearsOffTime() && (System.currentTimeMillis() - millisStarted) > 90000 && (System
 * .currentTimeMillis() - millisStarted) < 120000;
 * }
 * <p/>
 * public long getTimeStarted() {
 * return millisStarted;
 * }
 * <p/>
 * public String getTime() {
 * long millis = System.currentTimeMillis() - millisStarted;
 * return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
 * }
 * <p/>
 * public void addKillstreak() {
 * lastKillstreakTime = System.currentTimeMillis();
 * killstreak = getKillstreak() + 1;
 * The5zigMod.getConfigManager().saveLastServers();
 * }
 * <p/>
 * public int getKillstreak() {
 * if (System.currentTimeMillis() - lastKillstreakTime <= 30000)
 * return killstreak;
 * killstreak = 0;
 * return 0;
 * }
 * <p/>
 * public boolean isStarted() {
 * return started;
 * }
 * <p/>
 * public Feast getFeast() {
 * return feast;
 * }
 * <p/>
 * public void setFeast(Feast feast) {
 * this.feast = feast;
 * The5zigMod.getConfigManager().saveLastServers();
 * }
 * <p/>
 * public void addMiniFeast(MiniFeast feast) {
 * for (MiniFeast miniFeast : getMiniFeasts())
 * if (miniFeast.equals(feast))
 * return;
 * minifeasts.add(feast);
 * The5zigMod.getConfigManager().saveLastServers();
 * }
 * <p/>
 * public void removeMiniFeast(MiniFeast feast) {
 * minifeasts.remove(feast);
 * The5zigMod.getConfigManager().saveLastServers();
 * }
 * <p/>
 * public ArrayList<MiniFeast> getMiniFeasts() {
 * return minifeasts;
 * }
 * <p/>
 * public void onStart() {
 * if (kit == null)
 * kit = "None";
 * millisStarted = System.currentTimeMillis();
 * started = true;
 * minifeasts = new ArrayList<MiniFeast>();
 * The5zigMod.getConfigManager().saveLastServers();
 * }
 * <p/>
 * }
 */