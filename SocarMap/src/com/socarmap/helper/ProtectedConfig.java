package com.socarmap.helper;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import android.os.StrictMode;
import android.util.Base64;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class ProtectedConfig {
	private static final char[] PASSWORD = "TNq1WnagpjwnPjH".toCharArray();
	private static final byte[] SALT = { (byte) 0xde, (byte) 0x33, (byte) 0x10,
			(byte) 0x12, (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12, };
	private static Session session;

	private static byte[] base64Decode(String property) throws IOException {
		// NB: This class is internal, and you probably should use another impl
		return Base64.decode(property, Base64.DEFAULT);
	}

	private static String base64Encode(byte[] bytes) {
		// NB: This class is internal, and you probably should use another impl
		return Base64.encodeToString(bytes, Base64.DEFAULT);
	}

	public static void connectSSH() {
		JSch jsch = new JSch();

		try {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.permitAll().build());

			String serverProperties = "IO6zmslxjc72+44Oo5PGFNUKnWtvRVu62XUvhaAMdImudGRqzWRIIdXPUo+m/avYJSGSeVbGkKtR8whlDxQAKseYW80AG00xsqW1+HusBORXQhy2IXSTTZUXKvYJKuvf8RaBUXRU7mfOf8UCFQdW7A==";
			serverProperties = ProtectedConfig.decrypt(serverProperties);
			final Properties pr = new Properties();
			pr.load(new StringReader(serverProperties));
			session = jsch.getSession(pr.getProperty("username"),
					pr.getProperty("server"),
					Integer.valueOf(pr.getProperty("port")));
			UserInfo ui = new UserInfo() {

				@Override
				public String getPassphrase() {

					return null;
				}

				@Override
				public String getPassword() {

					return pr.getProperty("password");
				}

				@Override
				public boolean promptPassphrase(String arg0) {

					return true;
				}

				@Override
				public boolean promptPassword(String arg0) {

					return true;
				}

				@Override
				public boolean promptYesNo(String arg0) {

					return true;
				}

				@Override
				public void showMessage(String arg0) {

				}
			};

			session.setUserInfo(ui);

			session.connect(1000);

			session.setPortForwardingL(8787, pr.getProperty("mapserver"),
					Integer.valueOf(pr.getProperty("mapserverport")));
		} catch (Throwable e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}

	public static String decrypt(String property)
			throws GeneralSecurityException, IOException {
		SecretKeyFactory keyFactory = SecretKeyFactory
				.getInstance("PBEWithMD5AndDES");
		SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
		Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
		pbeCipher
				.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
		return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
	}

	public static void disconnect() {
		try {
			session.disconnect();
		} catch (Throwable e) {
		}
	}

	public static String encrypt(String property)
			throws GeneralSecurityException, UnsupportedEncodingException {
		SecretKeyFactory keyFactory = SecretKeyFactory
				.getInstance("PBEWithMD5AndDES");
		SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
		Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
		pbeCipher
				.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
		return base64Encode(pbeCipher.doFinal(property.getBytes("UTF-8")));
	}

	// public static void main(String[] args) throws Exception {
	// String originalPassword = "server=213.131.52.166\n" + "port=2000\n"
	// + "username=root\n" + "password=socar43478260";
	// System.out.println("Original password: " + originalPassword);
	// String encryptedPassword = encrypt(originalPassword);
	// System.out.println("Encrypted password: " + encryptedPassword);
	// String decryptedPassword = decrypt(encryptedPassword);
	// System.out.println("Decrypted password: " + decryptedPassword);
	// }
}
