package com.yizzle.androidstudio.WebAPI;

import android.util.Base64;

import org.json.JSONObject;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * CryptoAPI
 *
 * Encryption and decryption
 */
public class CryptoAPI {
    public static final String CIPHER = "AES/CBC/PKCS5Padding";
    public static final int KEYSIZE = 16;

    private byte[] key;
    private SecretKeySpec keySpec;

    public CryptoAPI(String b64_symkey) {
        key = Base64.decode(b64_symkey, Base64.DEFAULT);
        keySpec = new SecretKeySpec(key, "AES");
    }

    public JSONObject AESENCRYPT(String plaintext) {
        JSONObject ret = new JSONObject();
        try {

            Cipher aes = Cipher.getInstance(CIPHER);
            aes.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = aes.doFinal(plaintext.getBytes());
            byte[] iv = aes.getIV();

            String encrypted64 = Base64.encodeToString(encrypted, Base64.NO_WRAP);
            String iv64 = Base64.encodeToString(iv, Base64.NO_WRAP);

            ret.put("encrypted", encrypted64);
            ret.put("iv", iv64);

            return ret;
        } catch(Exception e) {

        }

        return null;
    }

    public String AESDECRYPT(JSONObject encrypted) {
        String plaintext = new String();

        return plaintext;
    }
}
