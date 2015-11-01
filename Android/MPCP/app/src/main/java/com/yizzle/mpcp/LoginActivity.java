package com.yizzle.mpcp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yizzle.mpcp.WebAPI.SecureAPI;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {


    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView username_field;
    private EditText password_field;
    private TextView error_text;
    private View mProgressView;
    private View mLoginFormView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        username_field = (AutoCompleteTextView) findViewById(R.id.username_field);

        password_field = (EditText) findViewById(R.id.password_field);
        password_field.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        error_text = (TextView) findViewById(R.id.error_text);
    }


    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        username_field.setError(null);
        password_field.setError(null);

        // Store values at the time of the login attempt.
        String username = username_field.getText().toString();
        String password = password_field.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            password_field.setError(getString(R.string.error_invalid_password));
            focusView = password_field;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            username_field.setError(getString(R.string.error_field_required));
            focusView = username_field;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(username, password);
            mAuthTask.execute((Void) null);
        }
    }


    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void saveAuth(String username, String authcode) {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.authdata), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.authcode), authcode);
        editor.putString(getString(R.string.username), username);
        editor.commit();
        AppData.authcode = authcode;
        AppData.username = username;
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Void> {

        private HashMap<String, String> send;
        private JSONObject response;
        private String username;

        UserLoginTask(String username, String password) {
            send = new HashMap<>();
            send.put("username", username);
            send.put("password", password);
            this.username = username;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                SecureAPI secureAPI = AppData.getSecureAPI();
                response = secureAPI.HTTPSPOST("login", send);
            } catch (Exception e) {
                Log.d("LOGIN", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            mAuthTask = null;
            showProgress(false);
            try {
                if (response.getString("status").equals("Success")) {
                    String authcode = response.getString("message");

                    Intent ret = new Intent();
                    LoginActivity.this.setResult(RESULT_OK, ret);
                    LoginActivity.this.saveAuth(username, authcode);
                    LoginActivity.this.finish();
                } else {
                    error_text.append(response.getString("message"));
                    error_text.setVisibility(View.VISIBLE);
                }
            } catch(Exception ex) {
                Log.d("LOGIN", ex.getMessage());
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

