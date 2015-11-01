package com.yizzle.mpcp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yizzle.mpcp.WebAPI.*;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;

public class MainActivity extends AppCompatActivity {

    //Display elements
    private Button button;
    private ProgressBar progressBar;
    private TextView protocol_text;
    private TextView username_text;

    private BaseProtocol.Retval handShare = null;
    public static BaseProtocol handshakeProtocol = new TestProtocol();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        button = (Button) findViewById(R.id.start_button);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        protocol_text = (TextView) findViewById(R.id.protocol_text);
        protocol_text.append(handshakeProtocol.toString());

        username_text = (TextView) findViewById(R.id.username_text);

        try {
            AppData.certStream = new BufferedInputStream(getAssets().open("certificate.crt"));
        } catch(Exception e) {
            AppData.certStream = null;
        }

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.authdata), Context.MODE_PRIVATE);
        String username = sharedPref.getString(getString(R.string.username), AppData.username);
        String authcode = sharedPref.getString(getString(R.string.authcode), AppData.authcode);
        Log.d("LOGIN", "UN + auth" + username + " " + authcode);

        if(authcode == null) {
            username_text.setText("ERROR: Not logged in!");
            username_text.setTextColor(Color.RED);
            button.setText("Log in");
            final Intent it = new Intent(this, LoginActivity.class);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(it, 1);
                }
            });
            return;
        }
        username_text.append(username);
        AppData.username = username;
        AppData.authcode = authcode;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_switch_user) {
            Intent it = new Intent(this, LoginActivity.class);
            startActivityForResult(it, 1);
        }

        return super.onOptionsItemSelected(item);
    }

    public void startSecureSession(View view) {
        //Start session
        if(handShare == null) {
            new CallProtocol().execute();
            return;
        }

        //Start activity if session is started
        Intent it = new Intent(this, BankActivity.class);
        startActivity(it);
        recreate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode == Activity.RESULT_OK) {
            this.recreate();
        }
    }

    private class CallProtocol extends AsyncTask<Void, Void,  BaseProtocol.Retval> {

        private String errorMessage = null;

        @Override
        protected void onPreExecute() {
            button.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected BaseProtocol.Retval doInBackground(Void... params) {
            try {
                return handshakeProtocol.start();
            } catch(Exception e) {
                errorMessage = e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(BaseProtocol.Retval response) {
            button.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);

            //Success
            if(response != null) {
                try {
                    handShare = response;
                    AppData.sessionid = handShare.getSessionId();
                    AppData.symkey = handShare.getSymkey();

                    button.setText("Go to account");

                    protocol_text.setText("Success!");
                    protocol_text.setTextColor(Color.rgb(34, 139, 34));
                } catch(Exception e) {
                    errorMessage = e.getMessage();
                }
                return;
            }

            //Failure
            protocol_text.setText("Connection error");
            protocol_text.setTextColor(Color.RED);
            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
            dialog.setTitle("Error message");
            dialog.setMessage(errorMessage);
            dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Dismiss",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            dialog.show();
        }
    }
}
