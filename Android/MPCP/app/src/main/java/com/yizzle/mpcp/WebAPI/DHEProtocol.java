package com.yizzle.mpcp.WebAPI;

import org.json.JSONObject;

/**
 * Diffie Hellman Key Exchange Protocol
 *
 * "DHE"
 */
public class DHEProtocol extends BaseProtocol {
    private String sessionId = null;
    private String symkey = null;

    public static String name = "DHE";

    protected String sessionAction() {
        return INITIAL_ACTION + "?sessionid=" + this.sessionId;
    }

    private Retval step1() throws Exception {
        JSONObject jparam = new JSONObject();
        jparam.put("step", 1);
        jparam.put("data", name);
        JSONObject jresponse = HTTPAPI.JSONPOST(INITIAL_ACTION, jparam);
        return step2(jresponse);
    }

    private Retval step2(JSONObject jstart) throws Exception {
        this.sessionId = jstart.getString("data");
        JSONObject jparam = new JSONObject();
        jparam.put("step", 2);

        JSONObject jdata = new JSONObject();
        jdata.put("secret", "testtesttest");

        jparam.put("data", jdata);
        JSONObject jresponse = HTTPAPI.JSONPOST(sessionAction(), jparam);
        return step3(jresponse);
    }

    private Retval step3(JSONObject jstart) throws Exception {
        this.symkey = jstart.getString("data");
        JSONObject jparam = new JSONObject();

        CryptoAPI crypto = new CryptoAPI(this.symkey);
        JSONObject jdata = crypto.AESENCRYPT(CONFIRM);

        jparam.put("step", 3);
        jparam.put("data", jdata);

        JSONObject jresponse = HTTPAPI.JSONPOST(sessionAction(), jparam);
        if(SUCCESS.equals(jresponse.getString("data"))) {
            return new Retval(this.sessionId, this.symkey);
        }
        return null;
    }

    @Override
    public Retval execute() {
        try {
            return step1();
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public Retval start() throws Exception {
        return step1();
    }

    @Override
    public String toString() {
        return "Diffie Hellman (DHE)";
    }
}
