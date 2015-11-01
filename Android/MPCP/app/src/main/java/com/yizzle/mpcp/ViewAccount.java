package com.yizzle.mpcp;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yizzle.mpcp.WebAPI.SessionAPI;

import org.json.JSONArray;
import org.json.JSONObject;


public class ViewAccount extends Fragment {

    private JSONObject allTransfers;
    private JSONArray allAccounts;
    private LinearLayout account_table;
    private LinearLayout transfer_table;
    private ProgressBar reload;

    public void reload() {
        account_table.removeAllViews();
        transfer_table.removeAllViews();
        new FetchData().execute();
    }

    public static ViewAccount newInstance() {
        return new ViewAccount();
    }

    public ViewAccount() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new FetchData().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_account, container, false);

        account_table = (LinearLayout) view.findViewById(R.id.account_table);
        transfer_table = (LinearLayout) view.findViewById(R.id.transfer_table);
        reload = (ProgressBar) view.findViewById(R.id.reload);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class FetchData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            if(reload != null)
                reload.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                SessionAPI session = new SessionAPI(AppData.authcode, AppData.sessionid, AppData.symkey);
                JSONObject jret;

                jret = session.execute("bank/transfers");
                allTransfers = jret.getJSONObject("message");

                jret = session.execute("bank/accounts");
                allAccounts = jret.getJSONArray("message");

            } catch(Exception e) {
                String err = e.getMessage();
                Log.d("SESSION", "error2: " + err);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            try {
                for (int i = 0; i < allAccounts.length(); ++i) {
                    JSONObject account = allAccounts.getJSONObject(i);
                    TextView txtView = new TextView(getContext());
                    txtView.setText(account.getString("name") + " (" + account.getString("bankname") + ")");
                    txtView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                    txtView.setTextColor(Color.BLACK);
                    account_table.addView(txtView);

                    txtView = new TextView(getContext());
                    txtView.setText("$" + account.getInt("balance") / 100);
                    txtView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    account_table.addView(txtView);
                }
                JSONArray reqtrans = allTransfers.getJSONArray("requested");
                for(int i = 0; i < reqtrans.length(); ++i) {
                    JSONObject curobj = reqtrans.getJSONObject(i);
                    curobj.put("type", "requested");
                    if(curobj.getInt("complete") == 0) {
                        ViewAccount.this.getFragmentManager().beginTransaction().add(transfer_table.getId(), SingleTransfer.newInstance(curobj.toString())).commit();
                    }
                }
                JSONArray rectrans = allTransfers.getJSONArray("received");
                for(int i = 0; i < rectrans.length(); ++i) {
                    JSONObject curobj = rectrans.getJSONObject(i);
                    curobj.put("type", "received");
                    if(curobj.getInt("complete") == 0) {
                        ViewAccount.this.getFragmentManager().beginTransaction().add(transfer_table.getId(), SingleTransfer.newInstance(curobj.toString())).commit();
                    }
                }
            } catch(Exception e) {
                Log.d("SESSION ERROR", e.getMessage());
            }
            if(reload != null)
                reload.setVisibility(View.INVISIBLE);
        }
    }

}
