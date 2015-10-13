package com.yizzle.androidstudio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedInputStream;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {

    public final static String RESPONSE = "app.response";
    public static InputStream crtStream = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            crtStream = new BufferedInputStream(getAssets().open("certificate.crt"));
        } catch(Exception e) {
            crtStream = null;
        }
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
            openSettings();
        }

        return super.onOptionsItemSelected(item);
    }

    public void openSettings() {
        Intent it = new Intent(this, SettingsActivity.class);
        startActivity(it);
    }

    public void sendMessage(View view) {
        EditText edt = (EditText) findViewById(R.id.edit_message);
        String mes = edt.getText().toString();

        Intent it = new Intent(this, ResponseActivity.class);
        it.putExtra(RESPONSE, mes);
        startActivity(it);
    }

}
