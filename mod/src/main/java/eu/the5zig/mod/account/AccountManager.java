package eu.the5zig.mod.account;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.mojang.authlib.Agent;
import com.mojang.authlib.AuthenticationService;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.event.AccountSwitchEvent;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by RoccoDev on 2019-08-25.
 */
public class AccountManager {

    private static ExecutorService POOL;

    private static Gson gson;
    private static Type accountsType;

    private boolean encrypted;
    private String password;

    private boolean isNewManager;

    private ArrayList<Account> accounts;

    private UserAuthentication userAuth;

    static {
        gson = new Gson();
        accountsType = new TypeToken<List<Account>>(){}.getType();

        POOL = Executors.newFixedThreadPool(1);
    }

    public AccountManager() {
        // The client generates a random UUID for authentication.
        UUID uuid = UUID.randomUUID();

        AuthenticationService service = new YggdrasilAuthenticationService(The5zigMod.getVars().getProxy(), uuid.toString());
        userAuth = service.createUserAuthentication(Agent.MINECRAFT);
        service.createMinecraftSessionService();

        isNewManager = !new File(The5zigMod.getModDirectory(), "accounts.enc").exists();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isNewManager() {
        return isNewManager;
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public void addAccount(String email, String password) throws AuthenticationException {
        userAuth.logOut();
        userAuth.setUsername(email);
        userAuth.setPassword(password);

        userAuth.logIn();
        String token = userAuth.getAuthenticatedToken();

        Account account = new Account(email, password, userAuth.getSelectedProfile().getId().toString(),
                userAuth.getSelectedProfile().getName(),
                userAuth.getUserType().getName());
        accounts.add(account);

        GameProfile profile = userAuth.getSelectedProfile();
        The5zigMod.getVars().setSession(profile.getName(), profile.getId().toString(),
                token, userAuth.getUserType().getName());
        updateSettings();
        account.setSelected(true);
        try {
            save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addOfflineAccount(String username) {
        userAuth.logOut();
        Account account = new Account(null, null, username, username, "legacy");
        accounts.add(account);

        The5zigMod.getVars().setSession(username, username, "0", "legacy");
        updateSettings();
        account.setSelected(true);
        try {
            save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void login(Account account) throws AuthenticationException {
        userAuth.logOut();
        if("legacy".equals(account.getAccountType())) {
            userAuth.setUsername(account.getName());
            The5zigMod.getVars().setSession(account.getName(), account.getName(), "0", "legacy");
        }
        else {
            userAuth.setUsername(account.getEmail());
            userAuth.setPassword(account.getPassword());

            userAuth.logIn();

            GameProfile profile = userAuth.getSelectedProfile();

            The5zigMod.getVars().setSession(profile.getName(), profile.getId().toString(), userAuth.getAuthenticatedToken(),
                    userAuth.getUserType().getName());

            account.setName(profile.getName());
            try {
                save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        account.setSelected(true);
        updateSettings();
        The5zigMod.getOverlayMessage().displayMessage("Login Success", "Logged in as " + account.getName());
    }

    public void save() {
        JsonObject json = new JsonObject();

        json.add("accounts", gson.toJsonTree(accounts, accountsType));

        POOL.execute(() -> {
            try {
                AccountsFileEncryption.encrypt(password, json);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void updateSettings() {
        The5zigMod.reloadDataManager();
        The5zigMod.getNetworkManager().reloadNow();
        The5zigMod.getListener().fireEvent(new AccountSwitchEvent());
        if(The5zigMod.getVars().getServer() != null) {
            String host = The5zigMod.getVars().getServer();
            int port = 25565;
            if(host.contains(":")) {
                String[] data = host.split(":");
                host = data[0];
                port = Integer.parseInt(data[1]);
            }
            The5zigMod.getVars().joinServer(host, port);
        }
    }

    public void read() throws Exception {
        if(isNewManager) {
            create();
            return;
        }

        JsonObject json = AccountsFileEncryption.decrypt(password);
        accounts = gson.fromJson(json.getAsJsonArray("accounts"), accountsType);

        Optional<Account> self = accounts.stream().filter(account -> {
            if(The5zigMod.getDataManager().getGameProfile().getId() != null) {
                return The5zigMod.getDataManager().getGameProfile().getId().toString().equals(account.getUuid());
            }
            return false;
        }).findAny();
        self.ifPresent(account -> account.setSelected(true));
    }

    private void create() throws Exception {
        accounts = new ArrayList<>();
        new File(The5zigMod.getModDirectory(), "accounts.enc").createNewFile();
        save();
    }

}
