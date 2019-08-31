package eu.the5zig.mod.account;

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.Gui;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.elements.RowExtended;
import eu.the5zig.mod.render.Base64Renderer;
import eu.the5zig.util.minecraft.ChatColor;

/**
 * Created by RoccoDev on 2019-08-25.
 */
public class Account implements RowExtended {

    private String email, password;
    private String uuid, name;
    private String accountType;

    // Rendering
    private transient Base64Renderer renderer;

    public Account(String email, String password, String uuid, String name, String accountType) {
        this.email = email;
        this.password = password;
        this.uuid = uuid;
        this.name = name;
        this.accountType = accountType;

        initRenderer();
    }

    public void initRenderer() {
        if(renderer == null)
            renderer = new Base64Renderer();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private transient int x, y;
    private transient boolean clickable, selected;

    public boolean isClickable() {
        return clickable;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        if(selected) {
            The5zigMod.getAccountManager().getAccounts().forEach(acc -> acc.setSelected(false));
        }
        this.selected = selected;
    }

    @Override
    public void draw(int x, int y) {

    }

    @Override
    public int getLineHeight() {
        return 32;
    }

    @Override
    public void draw(int x, int y, int slotHeight, int mouseX, int mouseY) {
        this.x = x;
        this.y = y;

        if (isSelected()) {
            Gui.drawRect(x - 15, y, x + 230, y + slotHeight, 0x2200ffff);
            clickable = false;
        }
        else clickable = true;

        initRenderer();

        String base64EncodedSkin = The5zigMod.getSkinManager().getBase64EncodedSkin(uuid);
        if (renderer.getBase64String() != null && base64EncodedSkin == null) {
            renderer.reset();
        } else if (base64EncodedSkin != null && !base64EncodedSkin.equals(renderer.getBase64String())) {
            renderer.setBase64String(base64EncodedSkin, "player_skin/" + uuid);
        }
        renderer.renderImage(x, y + 2, 24, 24);

        String displayName = ChatColor.BOLD + name;

        The5zigMod.getVars().drawString(displayName, x + 38, y + 2);

        char type = "legacy".equals(accountType) ? '7' : 'a';
        String accType = "§" + type + I18n.translate("account_manager.type." + accountType);
        The5zigMod.getVars().drawString(accType, x + 38, y + 4 + The5zigMod.getVars().getFontHeight());
    }

    @Override
    public IButton mousePressed(int mouseX, int mouseY) {
        return null;
    }
}
