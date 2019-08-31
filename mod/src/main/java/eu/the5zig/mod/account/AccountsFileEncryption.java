package eu.the5zig.mod.account;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.the5zig.mod.The5zigMod;
import org.apache.commons.io.IOUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

/**
 * Created by RoccoDev on 2019-08-25.
 */
public class AccountsFileEncryption {

    private static byte[] salt, iv;

    public static void encrypt(String password, JsonObject jsonIn) throws Exception {
        if(salt == null || iv == null) {
            SecureRandom rdm = new SecureRandom();
            salt = new byte[64];
            iv = new byte[16];

            rdm.nextBytes(salt);
            rdm.nextBytes(iv);
        }
        Cipher cipher = create(password, true);
        File out = new File(The5zigMod.getModDirectory(), "accounts.enc");
        FileOutputStream fos = new FileOutputStream(out);
        fos.write(salt);
        fos.write(iv);
        fos.write(cipher.doFinal(jsonIn.toString().getBytes(StandardCharsets.UTF_8)));
        fos.close();
    }

    public static JsonObject decrypt(String password) throws Exception {
        File in = new File(The5zigMod.getModDirectory(), "accounts.enc");

        FileInputStream fis = new FileInputStream(in);
        salt = new byte[64];
        iv = new byte[16];
        fis.read(salt);
        fis.read(iv);
        byte[] encrypted = new byte[(int) in.length() - (64 + 16)];
        fis.read(encrypted);
        fis.close();

        Cipher cipher = create(password, false);

        return new JsonParser().parse(IOUtils.toString(cipher.doFinal(encrypted), "UTF-8")).getAsJsonObject();
    }

    private static Cipher create(String password, boolean encrypt) throws Exception {

        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        KeySpec passwordBasedEncryptionKeySpec = new PBEKeySpec(password.toCharArray(), salt, 10000, 128);
        SecretKey secretKeyFromPBKDF2 = secretKeyFactory.generateSecret(passwordBasedEncryptionKeySpec);
        SecretKey key = new SecretKeySpec(secretKeyFromPBKDF2.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec spec = new IvParameterSpec(iv);

        cipher.init(encrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, key, spec);

        return cipher;
    }
}
