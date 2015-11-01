package com.yizzle.mpcp;

import com.yizzle.mpcp.WebAPI.SecureAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * Store temp data
 */
public class AppData {
    private static SecureAPI secureAPI = null;

    public static String authcode = null;
    public static String username = null;
    public static String symkey = null;
    public static String sessionid = null;
    public static InputStream certStream = null;

    public static SecureAPI getSecureAPI() throws Exception{
        if (secureAPI == null) {
            secureAPI = new SecureAPI(certStream);
        }
        return secureAPI;
    }

    public static void saveAuth(String authcode) {
        AppData.authcode = authcode;
    }
}
