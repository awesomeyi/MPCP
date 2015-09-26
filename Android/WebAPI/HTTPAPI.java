package com.yizzle.androidstudio.WebAPI;

import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;

/**
 * HTTPAPI
 *
 * Easy sending and recieving for HTTP requests
 */
public class HTTPAPI {

    private static final String dest = "http://mpcp.no-ip.org/API/";

    public static JSONObject JSONPOST(String action, JSONObject jparam) throws Exception {
        String param = jparam.toString();

        URL url = new URL(dest + action);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setFixedLengthStreamingMode(param.getBytes().length);

        conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
        conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

        conn.connect();

        //Send JSON
        OutputStream os = new BufferedOutputStream(conn.getOutputStream());
        os.write(param.getBytes());
        os.flush();
        os.close();

        int responseCode = conn.getResponseCode();
        InputStream is = conn.getInputStream();

        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        String response = s.hasNext() ? s.next() : "";
        Log.d("SESSION", response);
        JSONObject jresponse = new JSONObject(response);
        return jresponse;
    }
}
