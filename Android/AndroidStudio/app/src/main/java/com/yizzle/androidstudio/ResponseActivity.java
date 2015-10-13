package com.yizzle.androidstudio;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import com.yizzle.androidstudio.WebAPI.BaseProtocol;
import com.yizzle.androidstudio.WebAPI.CryptoAPI;
import com.yizzle.androidstudio.WebAPI.SecureAPI;
import com.yizzle.androidstudio.WebAPI.SessionAPI;
import com.yizzle.androidstudio.WebAPI.TestProtocol;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.Callable;

public class ResponseActivity extends AppCompatActivity {

    private TextView responseText;

    private String processResponse(String response) {
        return "Hello " + response + "!";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response);

        Intent it = getIntent();
        String response = this.processResponse(it.getStringExtra(MainActivity.RESPONSE));
        responseText = (TextView) findViewById(R.id.view_response);

        new CallProtocol().execute();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class CallProtocol extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {
            try {
                String authcode = "27b2442b7a7d430d36ebb72f008b96929a5c178ae50d5219f4f746f8adfa1ec8";
                TestProtocol tp = new TestProtocol();
                BaseProtocol.Retval ret = tp.execute();
                String b64_symkey = ret.getSymkey();
                String sessionid = ret.getSessionId();

                Log.d("SESSION", b64_symkey + " " + sessionid);
                SessionAPI session = new SessionAPI(authcode, sessionid, b64_symkey);
                JSONObject jparams = new JSONObject();
                jparams.put("transferid", 60);
                return session.execute("bank/transfer/cancel", jparams);
            } catch(Exception e) {
                Log.d("SESSION", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            if(response != null) {
                try {
                    responseText.setText(response.getString("status") + ": " + response.getString("message"));
                } catch(Exception e) {

                }
            }
        }
    }

    /*private class CallProtocol extends AsyncTask<Callable<BaseProtocol.Retval>, Void, BaseProtocol.Retval> {

        @Override
        protected BaseProtocol.Retval doInBackground(Callable<BaseProtocol.Retval>... params) {
            Callable<BaseProtocol.Retval> func = params[0];
            try {
                return func.call();
            } catch(Exception e) {
                Log.d("SESSION", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(BaseProtocol.Retval response) {
            if(response != null) {
                responseText.setText(response.getSessionId() + " " + response.getSymkey());
            }
        }
    } */
}
