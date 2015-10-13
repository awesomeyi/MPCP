package com.yizzle.mpcp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.yizzle.mpcp.WebAPI.SessionAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CreateTransfer extends AppCompatActivity {

    private Spinner account_spinner;
    private EditText amount_field;
    private EditText phone_field;
    private TextView error_text;

    private JSONArray jaccounts;
    private HashMap<String, String> lookup = new HashMap<>();
    private ArrayList<String> accounts = new ArrayList<>();
    private boolean finish = false;

    private void error(String message) {
        error_text.setText(message);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_transfer);

        account_spinner = (Spinner) findViewById(R.id.account_spinner);
        amount_field = (EditText) findViewById(R.id.amount_field);
        phone_field = (EditText) findViewById(R.id.phone_field);
        error_text = (TextView) findViewById(R.id.error_text);

        String[] default_acc = {"Open"};
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, default_acc);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        account_spinner.setAdapter(adapter);
        new FetchData().execute();
    }

    public void createTransfer(View view) throws Exception {
        String cur_acc = account_spinner.getSelectedItem().toString();
        String acc_id = lookup.get(cur_acc);
        String amount = amount_field.getText().toString();
        amount += "00";
        if(amount.equals("")) {
            this.error("Enter a username");
            return;
        }
        String phone = phone_field.getText().toString();
        if(phone.equals("")) {
            this.error("Enter a phone number");
            return;
        }
        Log.d("TRANSFER", acc_id + " " + amount + " " + phone);
        JSONObject send = new JSONObject();
        send.put("accountid", acc_id);
        send.put("destNumber", phone);
        send.put("amount", amount);
        new Transfer().execute(send);
    }

    private class FetchData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                SessionAPI session = new SessionAPI(AppData.authcode, AppData.sessionid, AppData.symkey);
                JSONObject jret = session.execute("bank/accounts");
                jaccounts = jret.optJSONArray("message");

            } catch(Exception e) {
                String err = e.getMessage();
                Log.d("SESSION", "error: " + err);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            try {
                for (int i = 0; i < jaccounts.length(); ++i) {
                    JSONObject account = jaccounts.getJSONObject(i);
                    String name = account.getString("name");
                    String id = Integer.toString(account.getInt("accountid"));
                    accounts.add(name);
                    lookup.put(name, id);
                }
                ArrayAdapter<CharSequence> adapter = new ArrayAdapter(CreateTransfer.this,
                        android.R.layout.simple_spinner_dropdown_item, accounts);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                account_spinner.setAdapter(adapter);
            } catch(Exception e) {
                Log.d("SESSION", e.getMessage());
            }
        }
    }

    private class Transfer extends AsyncTask<JSONObject, Void, Void> {
        private JSONObject response;

        @Override
        protected Void doInBackground(JSONObject... params) {
            try {
                SessionAPI session = new SessionAPI(AppData.authcode, AppData.sessionid, AppData.symkey);
                JSONObject send = params[0];
                response = session.execute("bank/transfer/create", send);

            } catch(Exception e) {
                String err = e.getMessage();
                Log.d("SESSION", "error: " + err);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            try {
                String status = response.getString("status");
                String message = response.getString("message");
                if(status.equals("Failure")) {
                    CreateTransfer.this.error(message);
                } else {
                    Intent ret = new Intent();
                    CreateTransfer.this.setResult(RESULT_OK, ret);
                    CreateTransfer.this.finish();
                }

            } catch(Exception e) {
                Log.d("SESSION", e.getMessage());
            }
        }
    }
}
