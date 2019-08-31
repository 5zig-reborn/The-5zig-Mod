package eu.the5zig.mod.gui;

import com.mojang.authlib.exceptions.AuthenticationException;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.account.Account;
import eu.the5zig.mod.gui.elements.Clickable;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.elements.IGuiList;

/**
 * Created by RoccoDev on 2019-08-26.
 */
public class GuiAccountManager extends Gui implements Clickable<Account> {

    private IGuiList<Account> guiList;

    public GuiAccountManager(Gui lastScreen) {
        super(lastScreen);
    }

    @Override
    public void initGui() {
        // Accounts file still encrypted
        if(The5zigMod.getAccountManager().getAccounts() == null) {
            GuiCenteredTextfield field = new GuiCenteredTextfield(this, new CenteredTextfieldCallback() {
                @Override
                public void onDone(String text) {
                    The5zigMod.getAccountManager().setPassword(text);
                    try {
                        The5zigMod.getAccountManager().read();
                    } catch (Exception ignored) {
                        // Password was incorrect.
                    }
                }

                @Override
                public void onCancel(Gui lastScreen) {
                    The5zigMod.getVars().displayScreen(lastScreen.lastScreen);
                }

                @Override
                public String title() {
                    return The5zigMod.getAccountManager().isNewManager()
                            ? I18n.translate("account_manager.create")
                            : I18n.translate("account_manager.enter_password");
                }
            }, 16);
            field.setIsPassword(true);
            The5zigMod.getVars().displayScreen(field);
        }

        addButton(The5zigMod.getVars().createButton(200, 8, 6, 50, 20, I18n.translate("gui.back")));
        guiList = The5zigMod.getVars().createGuiList(this, getWidth(), getHeight(), 32,
                getHeight() - 32, 0, getWidth(), The5zigMod.getAccountManager().getAccounts());
        guiList.setRowWidth(250);
        guiList.setDrawSelection(true);
        addGuiList(guiList);

        addButton(The5zigMod.getVars().createButton(1, getWidth() / 2 - 190, getHeight() - 27, 90, 20, I18n.translate("account_manager.add_btn")));
        addButton(The5zigMod.getVars().createButton(2, getWidth() / 2 - 95, getHeight() - 27, 90, 20, I18n.translate("account_manager.remove")));
        addButton(The5zigMod.getVars().createButton(3, getWidth() / 2, getHeight() - 27, 90, 20, I18n.translate("account_manager.edit_btn")));
        addButton(The5zigMod.getVars().createButton(4, getWidth() / 2 + 95, getHeight() - 27, 90, 20, I18n.translate("account_manager.change_password")));
    }

    @Override
    protected void actionPerformed(IButton button) {
        if(button.getId() == 1) { // Add
            The5zigMod.getVars().displayScreen(new GuiAccount(this));
        }
        else if(button.getId() == 2) { // Remove
            synchronized (guiList.getRows()) {
                int selected = guiList.getSelectedId();
                The5zigMod.getAccountManager().getAccounts().remove(selected);
                guiList.calculateHeightMap();
            }
            The5zigMod.getAccountManager().save();
        }
        else if(button.getId() == 3) { // Edit
            The5zigMod.getVars().displayScreen(new GuiAccount(guiList.getSelectedRow(), this));
        }
        else if(button.getId() == 4) { // Change password
            GuiCenteredTextfield field = new GuiCenteredTextfield(this, new CenteredTextfieldCallback() {
                @Override
                public void onDone(String text) {
                    The5zigMod.getAccountManager().setPassword(text);
                    The5zigMod.getAccountManager().save();
                }

                @Override
                public String title() {
                    return I18n.translate("account_manager.password");
                }
            });
            field.setIsPassword(true);
            The5zigMod.getVars().displayScreen(field);
        }
    }

    @Override
    public String getTitleKey() {
        return "account_manager.title";
    }

    @Override
    public void onSelect(int id, Account row, boolean doubleClick) {
        if(!row.isClickable()) return;
        if(doubleClick) {
            try {
                The5zigMod.getAccountManager().login(row);
            } catch (AuthenticationException e) {
                e.printStackTrace();
                The5zigMod.getOverlayMessage().displayMessage("Auth error", "Bad login");
            }
        }
    }
}
