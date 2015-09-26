package com.yizzle.androidstudio.WebAPI;

import android.util.Log;

import org.json.JSONObject;

/**
 * com.yizzle.androidstudio.WebAPI
 */
public class SessionAPI {
    private CryptoAPI crypto;
    private String authcode;
    private String serverAction;

    public SessionAPI(String authcode, String sessionid, String b64_symkey) {
        this.authcode = authcode;
        this.crypto = new CryptoAPI(b64_symkey);
        this.serverAction = "session?sessionid=" + sessionid;
    }

    public JSONObject execute(String action) throws Exception {
        return this.execute(action, new JSONObject());
    }

    public JSONObject execute(String action, JSONObject params) throws Exception {
        JSONObject request = new JSONObject();
        request.put("authcode", authcode);
        request.put("action", action);
        request.put("parameters", params);

        JSONObject encrypted = crypto.AESENCRYPT(request.toString());
        JSONObject completeReq = new JSONObject();
        completeReq.put("data", encrypted);

        JSONObject response = HTTPAPI.JSONPOST(serverAction, completeReq);
        Log.d("SESSION", response.toString());

        if(!response.getString("status").equals("success")) {
            return null;
        }
        JSONObject resData = response.getJSONObject("data");
        return new JSONObject(crypto.AESDECRYPT(resData));
    }
}
