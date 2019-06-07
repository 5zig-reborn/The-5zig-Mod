/**
 * Created by 5zig.
 * All rights reserved © 2015
 * <p/>
 * public class HGRenderer extends Renderer {
 * <p/>
 * private final int INVINCIBLE_TIME = 120;
 *
 * @Override public void render(DisplayRenderer renderer) {
 * if (!The5zigMod.getConfigManager().getConfig().showServerStats())
 * return;
 * Server server = The5zigMod.getDataManager().getServer();
 * if (server == null || !(server instanceof ServerHG))
 * return;
 * ServerHG serverhg = (ServerHG) server;
 * if (The5zigMod.getConfigManager().getConfig().showModLabels()) {
 * renderer.addToDisplay(new PlaceholderRenderItem(getLocation()));
 * renderer.addToDisplay(new TitleRenderItem(getLocation(), renderer.PREFIX_COL + ChatColor.UNDERLINE + I18n.translate("hg.title")));
 * }
 * renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.kit")) + serverhg.getKit()));
 * if (!serverhg.isStarted())
 * return;
 * <p/>
 * if (serverhg.showInvincibilityWearsOffTime()) {
 * int time = INVINCIBLE_TIME - (int) ((System.currentTimeMillis() - serverhg.getTimeStarted()) / 1000);
 * String text = renderer.PREFIX_COL + I18n.translate("ingame.invincibility_wears_off", time);
 * largeTextRenderer.render(text);
 * }
 * renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.time")) + serverhg.getTime()));
 * renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.kills")) + serverhg.getKills()));
 * if (serverhg.getKillstreak() != 0) {
 * renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.killstreak")) + serverhg.getKillstreak()));
 * }
 * if (serverhg.getFeast() != null) {
 * if (serverhg.getFeast().getRemainingTime() == null) {
 * renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.feast")) + serverhg.getFeast().getCoordinates()));
 * } else {
 * renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.feast")) + serverhg.getFeast().getCoordinates() + " - " + serverhg.getFeast()
 * .getRemainingTime()));
 * }
 * }
 * if (serverhg.getMiniFeasts().size() != 0) {
 * Iterator<MiniFeast> it = serverhg.getMiniFeasts().iterator();
 * while (it.hasNext()) {
 * MiniFeast feast = it.next();
 * if (feast.isOver()) {
 * it.remove();
 * continue;
 * }
 * renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.minifeast")) + feast.getCoordinates()));
 * }
 * }
 * }
 * @Override protected Configuration.Location getLocation() {
 * return The5zigMod.getConfigManager().getConfig().getServerStatsLocation();
 * }
 * }
 * <p/>
 * public class HGRenderer extends Renderer {
 * <p/>
 * private final int INVINCIBLE_TIME = 120;
 * @Override public void render(DisplayRenderer renderer) {
 * if (!The5zigMod.getConfigManager().getConfig().showServerStats())
 * return;
 * Server server = The5zigMod.getDataManager().getServer();
 * if (server == null || !(server instanceof ServerHG))
 * return;
 * ServerHG serverhg = (ServerHG) server;
 * if (The5zigMod.getConfigManager().getConfig().showModLabels()) {
 * renderer.addToDisplay(new PlaceholderRenderItem(getLocation()));
 * renderer.addToDisplay(new TitleRenderItem(getLocation(), renderer.PREFIX_COL + ChatColor.UNDERLINE + I18n.translate("hg.title")));
 * }
 * renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.kit")) + serverhg.getKit()));
 * if (!serverhg.isStarted())
 * return;
 * <p/>
 * if (serverhg.showInvincibilityWearsOffTime()) {
 * int time = INVINCIBLE_TIME - (int) ((System.currentTimeMillis() - serverhg.getTimeStarted()) / 1000);
 * String text = renderer.PREFIX_COL + I18n.translate("ingame.invincibility_wears_off", time);
 * largeTextRenderer.render(text);
 * }
 * renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.time")) + serverhg.getTime()));
 * renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.kills")) + serverhg.getKills()));
 * if (serverhg.getKillstreak() != 0) {
 * renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.killstreak")) + serverhg.getKillstreak()));
 * }
 * if (serverhg.getFeast() != null) {
 * if (serverhg.getFeast().getRemainingTime() == null) {
 * renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.feast")) + serverhg.getFeast().getCoordinates()));
 * } else {
 * renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.feast")) + serverhg.getFeast().getCoordinates() + " - " + serverhg.getFeast()
 * .getRemainingTime()));
 * }
 * }
 * if (serverhg.getMiniFeasts().size() != 0) {
 * Iterator<MiniFeast> it = serverhg.getMiniFeasts().iterator();
 * while (it.hasNext()) {
 * MiniFeast feast = it.next();
 * if (feast.isOver()) {
 * it.remove();
 * continue;
 * }
 * renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.minifeast")) + feast.getCoordinates()));
 * }
 * }
 * }
 * @Override protected Configuration.Location getLocation() {
 * return The5zigMod.getConfigManager().getConfig().getServerStatsLocation();
 * }
 * }
 */

/**
 public class HGRenderer extends Renderer {

 private final int INVINCIBLE_TIME = 120;

 @Override public void render(DisplayRenderer renderer) {
 if (!The5zigMod.getConfigManager().getConfig().showServerStats())
 return;
 Server server = The5zigMod.getDataManager().getServer();
 if (server == null || !(server instanceof ServerHG))
 return;
 ServerHG serverhg = (ServerHG) server;
 if (The5zigMod.getConfigManager().getConfig().showModLabels()) {
 renderer.addToDisplay(new PlaceholderRenderItem(getLocation()));
 renderer.addToDisplay(new TitleRenderItem(getLocation(), renderer.PREFIX_COL + ChatColor.UNDERLINE + I18n.translate("hg.title")));
 }
 renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.kit")) + serverhg.getKit()));
 if (!serverhg.isStarted())
 return;

 if (serverhg.showInvincibilityWearsOffTime()) {
 int time = INVINCIBLE_TIME - (int) ((System.currentTimeMillis() - serverhg.getTimeStarted()) / 1000);
 String text = renderer.PREFIX_COL + I18n.translate("ingame.invincibility_wears_off", time);
 largeTextRenderer.render(text);
 }
 renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.time")) + serverhg.getTime()));
 renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.kills")) + serverhg.getKills()));
 if (serverhg.getKillstreak() != 0) {
 renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.killstreak")) + serverhg.getKillstreak()));
 }
 if (serverhg.getFeast() != null) {
 if (serverhg.getFeast().getRemainingTime() == null) {
 renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.feast")) + serverhg.getFeast().getCoordinates()));
 } else {
 renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.feast")) + serverhg.getFeast().getCoordinates() + " - " + serverhg.getFeast()
 .getRemainingTime()));
 }
 }
 if (serverhg.getMiniFeasts().size() != 0) {
 Iterator<MiniFeast> it = serverhg.getMiniFeasts().iterator();
 while (it.hasNext()) {
 MiniFeast feast = it.next();
 if (feast.isOver()) {
 it.remove();
 continue;
 }
 renderer.addToDisplay(new StringRenderItem(getLocation(), renderer.getPrefix(I18n.translate("ingame.minifeast")) + feast.getCoordinates()));
 }
 }
 }

 @Override protected Configuration.Location getLocation() {
 return The5zigMod.getConfigManager().getConfig().getServerStatsLocation();
 }
 }
 */