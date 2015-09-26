package com.yizzle.androidstudio.WebAPI;

import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
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

    public JSONObject AESENCRYPT(String plaintext) throws Exception {
        Cipher aes = Cipher.getInstance(CIPHER);
        aes.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = aes.doFinal(plaintext.getBytes("UTF-8"));
        byte[] iv = aes.getIV();

        String encrypted64 = Base64.encodeToString(encrypted, Base64.NO_WRAP);
        String iv64 = Base64.encodeToString(iv, Base64.NO_WRAP);

        JSONObject ret = new JSONObject();
        ret.put("encrypted", encrypted64);
        ret.put("iv", iv64);

        return ret;
    }

    public String AESDECRYPT(JSONObject encrypted) throws Exception {
        Log.d("SESSION", encrypted.toString());
        byte[] iv = Base64.decode(encrypted.getString("iv"), Base64.NO_WRAP);
        byte[] raw = Base64.decode(encrypted.getString("encrypted"), Base64.NO_WRAP);

        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher aes = Cipher.getInstance(CIPHER);
        aes.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        byte[] decrypted = aes.doFinal(raw);
        return new String(decrypted, "UTF-8");
    }
}
