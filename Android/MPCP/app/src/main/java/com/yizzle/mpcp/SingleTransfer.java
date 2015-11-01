package com.yizzle.mpcp;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.yizzle.mpcp.WebAPI.SessionAPI;

import org.json.JSONObject;


public class SingleTransfer extends Fragment {
    private static final String ARG_PARAM = "strjson";

    private JSONObject transfer;
    private TextView destination_field;
    private TextView amount_field;
    private Button accept_button;
    private Button cancel_button;


    private OnFragmentInteractionListener mListener;

    public static SingleTransfer newInstance(String strjson) {
        SingleTransfer fragment = new SingleTransfer();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, strjson);
        fragment.setArguments(args);
        return fragment;
    }

    public SingleTransfer() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String param = getArguments().getString(ARG_PARAM);
            Log.d("SESSION", param);
            try {
                transfer = new JSONObject(param);
            } catch(Exception ex) {

            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_transfer, container, false);
        destination_field = (TextView) view.findViewById(R.id.destination_field);
        amount_field = (TextView) view.findViewById(R.id.amount_field);
        accept_button = (Button) view.findViewById(R.id.accept_button);
        accept_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CancelTransfer().execute("bank/transfer/accept");

            }
        });

        cancel_button = (Button) view.findViewById(R.id.cancel_button);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CancelTransfer().execute("bank/transfer/cancel");

            }
        });
        try {
            String destString = "";
            if (transfer.getString("type").equals("requested")) {
                destString += "(From) ";
                accept_button.setVisibility(View.INVISIBLE);
            } else {
                destString += "(To) ";
            }
            destString += transfer.getString("username");
            destination_field.setText(destString);

            String amount = transfer.getString("amount");
            amount_field.setText("$" + amount.substring(0, amount.length() - 2));

        } catch(Exception ex) {

        }

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction();
    }

    private class CancelTransfer extends AsyncTask<String, Void, Void> {

        private JSONObject jret;
        @Override
        protected Void doInBackground(String... params) {
            try {
                SessionAPI session = new SessionAPI(AppData.authcode, AppData.sessionid, AppData.symkey);
                JSONObject send = new JSONObject();
                send.put("transferid", Integer.toString(transfer.getInt("transferid")));

                jret = session.execute(params[0], send);
            } catch(Exception e) {
                String err = e.getMessage();
                Log.d("SESSION", "error2: " + err);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            mListener.onFragmentInteraction();
            Log.d("TRANSFER", jret.toString());
        }
    }

}
