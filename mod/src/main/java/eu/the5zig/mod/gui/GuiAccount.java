/*
 * Copyright (c) 2019-2020 5zig Reborn
 * Copyright (c) 2015-2019 5zig
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

package eu.the5zig.mod.gui;

import com.mojang.authlib.exceptions.AuthenticationException;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.account.Account;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.elements.ITextfield;

/**
 * Created by RoccoDev on 2019-08-26.
 */
public class GuiAccount extends Gui {

    private ITextfield userField, passwordField;
    private Account account;

    public GuiAccount(Gui lastScreen) {
        super(lastScreen);
    }

    public GuiAccount(Account account, Gui lastScreen) {
        this(lastScreen);
        this.account = account;
    }

    @Override
    public void initGui() {
        addTextField(userField = The5zigMod.getVars().createTextfield(1, getWidth() / 2 - 150,
                getHeight() / 4, 300, 20));
        addTextField(passwordField = The5zigMod.getVars().createTextfield(2, getWidth() / 2 - 150,
                getHeight() / 2, 300, 20));
        userField.callSetMaxStringLength(254);

        addButton(The5zigMod.getVars().createButton(201, getWidth() / 2 - 100, getHeight() - 35, The5zigMod.getVars().translate("gui.done")));

        if(account != null) {
            if("legacy".equals(account.getAccountType()))
                userField.callSetText(account.getName());
            else userField.callSetText(account.getEmail());
        }

        passwordField.setIsPassword(true);
    }

    @Override
    protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawCenteredString(I18n.translate("account_manager.username"), getWidth() / 2, getHeight() / 4 - 10);
        drawCenteredString(I18n.translate("account_manager.password"), getWidth() / 2, getHeight() / 2 - 10);
    }

    @Override
    protected void actionPerformed(IButton button) {
        if(button.getId() == 201) {
            String user = userField.callGetText();
            String password = passwordField.callGetText();

            if(user == null || user.isEmpty()) {
                The5zigMod.getVars().displayScreen(lastScreen);
                return;
            }

            if(account != null) {
                if(password == null || password.isEmpty()) {
                    account.setName(user);
                }
                else {
                    account.setEmail(user);
                    account.setPassword(password);
                }

                The5zigMod.getAccountManager().save();
            }
            else {
                if (password == null || password.isEmpty()) {
                    The5zigMod.getAccountManager().addOfflineAccount(user);
                } else {
                    try {
                        The5zigMod.getAccountManager().addAccount(user, password);
                    } catch (AuthenticationException e) {
                        e.printStackTrace();
                        The5zigMod.getOverlayMessage().displayMessage("Auth error", "Bad login");
                        return;
                    }
                }
            }

            The5zigMod.getVars().displayScreen(lastScreen);
        }
    }

    @Override
    public String getTitleKey() {
        return "account_manager.edit";
    }
}
