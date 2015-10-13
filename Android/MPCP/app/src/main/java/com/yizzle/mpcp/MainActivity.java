package com.yizzle.mpcp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    //Display elements
    private Button button;
    private ProgressBar progressBar;
    private TextView text;

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
        text = (TextView) findViewById(R.id.protocol_text);

        text.append(handshakeProtocol.toString());
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
        if (id == R.id.action_settings) {
            return true;
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

                    text.setText("Success!");
                    text.setTextColor(Color.rgb(34, 139, 34));
                } catch(Exception e) {
                    errorMessage = e.getMessage();
                }
                return;
            }

            //Failure
            text.setText("Connection error");
            text.setTextColor(Color.RED);
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
