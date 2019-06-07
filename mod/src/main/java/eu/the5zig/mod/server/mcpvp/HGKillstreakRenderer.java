/**
 * Created by 5zig.
 * All rights reserved © 2015
 * <p/>
 * public class HGKillstreakRenderer extends Renderer {
 *
 * @Override public void render(DisplayRenderer renderer) {
 * Server server = The5zigMod.getDataManager().getServer();
 * if (!(server instanceof ServerHG))
 * return;
 * <p/>
 * ServerHG serverHG = ((ServerHG) server);
 * if (serverHG.getKillstreak() <= 1)
 * return;
 * <p/>
 * String text = null;
 * if (serverHG.getKillstreak() == 2)
 * text = I18n.translate("ingame.killstreak.double");
 * else if (serverHG.getKillstreak() == 3)
 * text = I18n.translate("ingame.killstreak.triple");
 * else if (serverHG.getKillstreak() == 4)
 * text = I18n.translate("ingame.killstreak.quadruple");
 * else if (serverHG.getKillstreak() >= 5)
 * text = I18n.translate("ingame.killstreak.multi");
 * if (text == null)
 * return;
 * largeTextRenderer.render(renderer.PREFIX_COL + text);
 * }
 * @Override protected Configuration.Location getLocation() {
 * return null;
 * }
 * }
 * <p/>
 * public class HGKillstreakRenderer extends Renderer {
 * @Override public void render(DisplayRenderer renderer) {
 * Server server = The5zigMod.getDataManager().getServer();
 * if (!(server instanceof ServerHG))
 * return;
 * <p/>
 * ServerHG serverHG = ((ServerHG) server);
 * if (serverHG.getKillstreak() <= 1)
 * return;
 * <p/>
 * String text = null;
 * if (serverHG.getKillstreak() == 2)
 * text = I18n.translate("ingame.killstreak.double");
 * else if (serverHG.getKillstreak() == 3)
 * text = I18n.translate("ingame.killstreak.triple");
 * else if (serverHG.getKillstreak() == 4)
 * text = I18n.translate("ingame.killstreak.quadruple");
 * else if (serverHG.getKillstreak() >= 5)
 * text = I18n.translate("ingame.killstreak.multi");
 * if (text == null)
 * return;
 * largeTextRenderer.render(renderer.PREFIX_COL + text);
 * }
 * @Override protected Configuration.Location getLocation() {
 * return null;
 * }
 * }
 */
/**
 public class HGKillstreakRenderer extends Renderer {

@Override public void render(DisplayRenderer renderer) {
Server server = The5zigMod.getDataManager().getServer();
if (!(server instanceof ServerHG))
return;

ServerHG serverHG = ((ServerHG) server);
if (serverHG.getKillstreak() <= 1)
return;

String text = null;
if (serverHG.getKillstreak() == 2)
text = I18n.translate("ingame.killstreak.double");
else if (serverHG.getKillstreak() == 3)
text = I18n.translate("ingame.killstreak.triple");
else if (serverHG.getKillstreak() == 4)
text = I18n.translate("ingame.killstreak.quadruple");
else if (serverHG.getKillstreak() >= 5)
text = I18n.translate("ingame.killstreak.multi");
if (text == null)
return;
largeTextRenderer.render(renderer.PREFIX_COL + text);
}

@Override protected Configuration.Location getLocation() {
return null;
}
}*/
