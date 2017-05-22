package com.phillips.phillipscs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private TextView mPageHead;
    private TextView txtEmailError;
    private View mProgressView;
    private View mLoginFormView;
    private Toolbar mToolbar;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        String userID = getSharedPreferences(Constants.PREF_PHILLIPS, MODE_PRIVATE).getString(Constants.USER_ID, null);
        gcmRegisteration();

        if (userID != null) {
            startActivity(new Intent(LoginActivity.this, RequestListActivity.class));
            finish();
        } else {
            Typeface tfLight = Typeface.createFromAsset(getAssets(), "fonts/CentraleSans-Light.otf");
            Typeface tfBold = Typeface.createFromAsset(getAssets(), "fonts/CentraleSans-Bold.otf");
            Typeface tfRegular = Typeface.createFromAsset(getAssets(), "fonts/CentraleSans-Regular.otf");

            mPageHead = (TextView) findViewById(R.id.tvPage_heading);
            mPageHead.setTypeface(tfLight);
            mEmailView = (EditText) findViewById(R.id.email);
            mEmailView.setTypeface(tfLight);

            txtEmailError = (TextView) findViewById(R.id.mailError);
            txtEmailError.setTypeface(tfLight);

            mPasswordView = (EditText) findViewById(R.id.password);
            mPasswordView.setTypeface(tfLight);
            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.login || id == EditorInfo.IME_NULL) {
                        if (Utility.isNetworkAvailable(new WeakReference<Context>(LoginActivity.this))) {
                            attemptLogin();
                        } else {
                            Toast.makeText(LoginActivity.this, "Internet not connected", Toast.LENGTH_LONG).show();
                        }
                        return true;
                    }
                    return false;
                }
            });

            Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
            mEmailSignInButton.setTypeface(tfRegular);
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Utility.isNetworkAvailable(new WeakReference<Context>(LoginActivity.this))) {
                        attemptLogin();
                    } else {
                        Toast.makeText(LoginActivity.this, "Internet not connected", Toast.LENGTH_LONG).show();
                    }
                }
            });

            mLoginFormView = findViewById(R.id.login_form);
            mProgressView = findViewById(R.id.login_progress);
        }

        mToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(mToolbar);
        SpannableString s = new SpannableString(getResources().getString(R.string.app_name));
        s.setSpan(new TypefaceSpan(this, "CentraleSans-Light.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((TextView) findViewById(R.id.app_toolbar_title)).setText(s);
    }

    @Override
    protected void onResume() {

        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(RegistrationIntentService.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        txtEmailError.setVisibility(View.GONE);
        txtEmailError.setText("");

        // Store values at the time of the login attempt.
        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;



        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            //mEmailView.setError(getString(R.string.error_field_required));
            txtEmailError.setText("Error");
            txtEmailError.setVisibility(View.VISIBLE);
            focusView = mEmailView;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            txtEmailError.setVisibility(View.GONE);
            showProgress(true);
            try {
                loginTask(email, password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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

    public void loginTask(String email, String password) throws JSONException {

        new LoginAsync().execute(email, password, getSharedPreferences(Constants.PREF_PHILLIPS, MODE_PRIVATE).getString(Constants.GCM_TOKEN, null));
        /*RequestParams params = new RequestParams();
        params.put("user_login_name", email);
        params.put("user_login_password", password);
        params.put("user_app_token", "jsjjdsooo0200");
        PhillipsRestClient.post("http://evolution-matrix.com/philips_cs_spare_management/login/login", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                showProgress(false);
                Toast.makeText(LoginActivity.this, statusCode + " ", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showProgress(false);
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        });

        PhillipsRestClient.get("http://evolution-matrix.com/philips_cs_spare_management/request/get_request_list?logistic_partner_user_id=1", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                showProgress(false);
                Toast.makeText(LoginActivity.this, statusCode + " ", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showProgress(false);
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        });*/


    }

    private class LoginAsync extends AsyncTask<String, Void, BaseModel> {

        String response = "";

        @Override
        protected BaseModel doInBackground(String... params) {


            BaseModel model = null;
            try {
                URL url = new URL(Constants.URL_HOST + "philips_cs_spare_management/login/login");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                List<NameValuePair> paramsPairs = new ArrayList<>();
                paramsPairs.add(new BasicNameValuePair("user_login_name", params[0]));
                paramsPairs.add(new BasicNameValuePair("user_login_password", params[1]));
                if (params[2] != null) {
                    paramsPairs.add(new BasicNameValuePair("user_app_token", params[2]));
                }
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(paramsPairs));
                writer.flush();
                writer.close();
                os.close();

                conn.connect();

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                } else {
                    response = "";

                }

                /*runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "Server Response: " + response, Toast.LENGTH_LONG).show();
                    }
                });*/

                model = new ObjectMapper().readValue(response, BaseModel.class);
                if (model != null) {
                    model.setResponseCode(responseCode);
                }

                Log.e(LoginActivity.class.getName(), response);
                /*
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                int statusCode = urlConnection.getResponseCode();
                if (statusCode == 200) {
                    InputStream it = new BufferedInputStream(urlConnection.getInputStream());
                    InputStreamReader read = new InputStreamReader(it);
                    BufferedReader buff = new BufferedReader(read);
                    StringBuilder dta = new StringBuilder();
                    String chunks;
                    while ((chunks = buff.readLine()) != null) {
                        dta.append(chunks);
                    }
                } else {
                    //Handle else
                }*/
            } catch (Exception e) {
                e.printStackTrace();
            }
            return model;
        }

        @Override
        protected void onPostExecute(BaseModel baseModel) {
            super.onPostExecute(baseModel);
            showProgress(false);

            if (baseModel != null && baseModel.getLoginModel() != null) {
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREF_PHILLIPS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.USER_ID, baseModel.getLoginModel().getUserID());
                editor.putString(Constants.USER_ROLE, baseModel.getLoginModel().getUserRoleType());
                editor.apply();

                Intent intent = new Intent(LoginActivity.this, RequestListActivity.class);
                startActivity(intent);
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }

        }

        private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;

            for (NameValuePair pair : params) {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            }

            return result.toString();
        }
    }

    private void gcmRegisteration() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(RegistrationIntentService.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    Log.e(LoginActivity.class.getName(), "Reg Success");
                } else {
                    Log.e(LoginActivity.class.getName(), "Reg fail");
                }
            }
        };

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(LoginActivity.class.getName(), "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

}

