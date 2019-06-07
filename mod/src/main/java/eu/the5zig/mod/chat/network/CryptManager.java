/*
 * Original: Copyright (c) 2015-2019 5zig [MIT]
 * Current: Copyright (c) 2019 5zig Reborn [GPLv3+]
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

package eu.the5zig.mod.chat.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class CryptManager {

	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Generate a new shared secret AES key from a secure random source
	 */
	public static SecretKey createNewSharedKey() {
		try {
			KeyGenerator keygenerator = KeyGenerator.getInstance("AES");
			keygenerator.init(128);
			return keygenerator.generateKey();
		} catch (NoSuchAlgorithmException nosuchalgorithmexception) {
			throw new Error(nosuchalgorithmexception);
		}
	}

	/**
	 * Generates RSA KeyPair
	 */
	public static KeyPair generateKeyPair() {
		try {
			KeyPairGenerator keypairgenerator = KeyPairGenerator.getInstance("RSA");
			keypairgenerator.initialize(1024);
			return keypairgenerator.generateKeyPair();
		} catch (NoSuchAlgorithmException nosuchalgorithmexception) {
			nosuchalgorithmexception.printStackTrace();
			LOGGER.error("Key pair generation failed!");
			return null;
		}
	}

	/**
	 * Compute a serverId hash for use by sendSessionRequest()
	 */
	public static byte[] getServerIdHash(String serverId, PublicKey publicKey, SecretKey secretKey) {
		try {
			return digestOperation("SHA-1", serverId.getBytes("ISO_8859_1"), secretKey.getEncoded(), publicKey.getEncoded());
		} catch (UnsupportedEncodingException unsupportedencodingexception) {
			unsupportedencodingexception.printStackTrace();
			return null;
		}
	}

	/**
	 * Compute a message digest on arbitrary byte[] data
	 */
	private static byte[] digestOperation(String algorithm, byte[]... data) {
		try {
			MessageDigest messagedigest = MessageDigest.getInstance(algorithm);

			for (byte[] abyte : data) {
				messagedigest.update(abyte);
			}

			return messagedigest.digest();
		} catch (NoSuchAlgorithmException nosuchalgorithmexception) {
			nosuchalgorithmexception.printStackTrace();
			return null;
		}
	}

	/**
	 * Create a new PublicKey from encoded X.509 data
	 */
	public static PublicKey decodePublicKey(byte[] encodedKey) {
		try {
			EncodedKeySpec encodedkeyspec = new X509EncodedKeySpec(encodedKey);
			KeyFactory keyfactory = KeyFactory.getInstance("RSA");
			return keyfactory.generatePublic(encodedkeyspec);
		} catch (NoSuchAlgorithmException ignored) {
		} catch (InvalidKeySpecException ignored) {
		}

		LOGGER.error("Public key reconstitute failed!");
		return null;
	}

	/**
	 * Decrypt shared secret AES key using RSA private key
	 */
	public static SecretKey decryptSharedKey(PrivateKey key, byte[] secretKeyEncrypted) {
		return new SecretKeySpec(decryptData(key, secretKeyEncrypted), "AES");
	}

	/**
	 * Encrypt byte[] data with RSA public key
	 */
	public static byte[] encryptData(Key key, byte[] data) {
		return cipherOperation(1, key, data);
	}

	/**
	 * Decrypt byte[] data with RSA private key
	 */
	public static byte[] decryptData(Key key, byte[] data) {
		return cipherOperation(2, key, data);
	}

	/**
	 * Encrypt or decrypt byte[] data using the specified key
	 */
	private static byte[] cipherOperation(int opMode, Key key, byte[] data) {
		try {
			return createTheCipherInstance(opMode, key.getAlgorithm(), key).doFinal(data);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}

		LOGGER.error("Cipher data failed!");
		return null;
	}

	/**
	 * Creates the Cipher Instance.
	 */
	private static Cipher createTheCipherInstance(int opMode, String transformation, Key key) {
		try {
			Cipher cipher = Cipher.getInstance(transformation);
			cipher.init(opMode, key);
			return cipher;
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}

		LOGGER.error("Cipher creation failed!");
		return null;
	}

	/**
	 * Creates an Cipher instance using the AES/CFB8/NoPadding algorithm. Used for protocol encryption.
	 */
	public static Cipher createNetCipherInstance(int opMode, Key key) {
		try {
			Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
			cipher.init(opMode, key, new IvParameterSpec(key.getEncoded()));
			return cipher;
		} catch (GeneralSecurityException generalsecurityexception) {
			throw new RuntimeException(generalsecurityexception);
		}
	}
}
