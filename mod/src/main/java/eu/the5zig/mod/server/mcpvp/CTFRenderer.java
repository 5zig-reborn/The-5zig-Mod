/**
 * Created by 5zig.
 * All rights reserved © 2015
 * <p/>
 * public class CTFRenderer extends Renderer {
 * <p/>
 * public CTFRenderer() {
 * }
 *
 * @Override public void render(DisplayRenderer renderer) {
 * Server server = The5zigMod.getDataManager().getServer();
 * if (!The5zigMod.getConfigManager().getConfig().showServerStats() || server == null || !(server instanceof ServerCTF))
 * return;
 * <p/>
 * ServerCTF serverctf = (ServerCTF) server;
 * if (The5zigMod.getConfigManager().getConfig().showModLabels()) {
 * renderer.addToDisplay(new PlaceholderRenderItem(getLocation()));
 * renderer.addToDisplay(new TitleRenderItem(getLocation(), renderer.PREFIX_COL + ChatColor.UNDERLINE + I18n.translate("ctf.title")));
 * }
 * if (serverctf.getTeam() != null) {
 * renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.team")) + serverctf.getTeam().getName()));
 * }
 * if (serverctf.getKit() != null) {
 * renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.class")) + serverctf.getKit()));
 * }
 * renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.kills")) + serverctf
 * .getKills() + renderer.BRACKETS_COL + " (" + renderer.MAIN_COL + serverctf.getKillstreak() + renderer.BRACKETS_COL + ")"));
 * renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.deaths")) + serverctf.getDeaths()));
 * renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.steals")) + serverctf.getSteals()));
 * renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.captures")) + serverctf.getCaptures()));
 * renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.recovers")) + serverctf.getRecovers()));
 * renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.flag")) + serverctf.getPlayerTeam().getFlag()));
 * }
 * @Override protected Configuration.Location getLocation() {
 * return The5zigMod.getConfigManager().getConfig().getServerStatsLocation();
 * }
 * }
 * <p/>
 * public class CTFRenderer extends Renderer {
 * <p/>
 * public CTFRenderer() {
 * }
 * @Override public void render(DisplayRenderer renderer) {
 * Server server = The5zigMod.getDataManager().getServer();
 * if (!The5zigMod.getConfigManager().getConfig().showServerStats() || server == null || !(server instanceof ServerCTF))
 * return;
 * <p/>
 * ServerCTF serverctf = (ServerCTF) server;
 * if (The5zigMod.getConfigManager().getConfig().showModLabels()) {
 * renderer.addToDisplay(new PlaceholderRenderItem(getLocation()));
 * renderer.addToDisplay(new TitleRenderItem(getLocation(), renderer.PREFIX_COL + ChatColor.UNDERLINE + I18n.translate("ctf.title")));
 * }
 * if (serverctf.getTeam() != null) {
 * renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.team")) + serverctf.getTeam().getName()));
 * }
 * if (serverctf.getKit() != null) {
 * renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.class")) + serverctf.getKit()));
 * }
 * renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.kills")) + serverctf
 * .getKills() + renderer.BRACKETS_COL + " (" + renderer.MAIN_COL + serverctf.getKillstreak() + renderer.BRACKETS_COL + ")"));
 * renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.deaths")) + serverctf.getDeaths()));
 * renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.steals")) + serverctf.getSteals()));
 * renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.captures")) + serverctf.getCaptures()));
 * renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.recovers")) + serverctf.getRecovers()));
 * renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.flag")) + serverctf.getPlayerTeam().getFlag()));
 * }
 * @Override protected Configuration.Location getLocation() {
 * return The5zigMod.getConfigManager().getConfig().getServerStatsLocation();
 * }
 * }
 */
/**
 public class CTFRenderer extends Renderer {

 public CTFRenderer() {
 }

 @Override public void render(DisplayRenderer renderer) {
 Server server = The5zigMod.getDataManager().getServer();
 if (!The5zigMod.getConfigManager().getConfig().showServerStats() || server == null || !(server instanceof ServerCTF))
 return;

 ServerCTF serverctf = (ServerCTF) server;
 if (The5zigMod.getConfigManager().getConfig().showModLabels()) {
 renderer.addToDisplay(new PlaceholderRenderItem(getLocation()));
 renderer.addToDisplay(new TitleRenderItem(getLocation(), renderer.PREFIX_COL + ChatColor.UNDERLINE + I18n.translate("ctf.title")));
 }
 if (serverctf.getTeam() != null) {
 renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.team")) + serverctf.getTeam().getName()));
 }
 if (serverctf.getKit() != null) {
 renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.class")) + serverctf.getKit()));
 }
 renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.kills")) + serverctf
 .getKills() + renderer.BRACKETS_COL + " (" + renderer.MAIN_COL + serverctf.getKillstreak() + renderer.BRACKETS_COL + ")"));
 renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.deaths")) + serverctf.getDeaths()));
 renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.steals")) + serverctf.getSteals()));
 renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.captures")) + serverctf.getCaptures()));
 renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.recovers")) + serverctf.getRecovers()));
 renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.flag")) + serverctf.getPlayerTeam().getFlag()));
 }

 @Override protected Configuration.Location getLocation() {
 return The5zigMod.getConfigManager().getConfig().getServerStatsLocation();
 }
 }
 */