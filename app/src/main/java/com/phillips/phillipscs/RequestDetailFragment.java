package com.phillips.phillipscs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.map.ObjectMapper;

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
 * A fragment representing a single request detail screen.
 * This fragment is either contained in a {@link RequestListActivity}
 * in two-pane mode (on tablets) or a {@link RequestDetailActivity}
 * on handsets.
 */
public class RequestDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM = "item";
    public static final String ARG_ITEM_ID = "item_id";
    public RequestModel mRequestModel = null;
    private View mProgressView;
    private LinearLayout mLLMainLayout;
    private TextView tvStatus;
    private TextView tvDate;
    private TextView tvCustomerName;
    private TextView tvCustomerPhone;
    private TextView tvCustomerEmail;
    private TextView tvCustomerAddress;
    private TextView tvLogPartner;
    private TextView tvLogEmail;
    private TextView tvLogPhone;
    private TextView tvMovementType;
    private Button btnStatus;
    private Button btnCapture;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RequestDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM)) {

            mRequestModel = (RequestModel) (getArguments().getSerializable(ARG_ITEM));

            Activity activity = this.getActivity();
            Typeface tfLight= Typeface.createFromAsset(activity.getAssets(), "fonts/CentraleSans-Light.otf");
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            appBarLayout.setCollapsedTitleTypeface(tfLight);
            appBarLayout.setExpandedTitleTypeface(tfLight);
            if (appBarLayout != null && mRequestModel != null) {
                //SpannableString ss = new SpannableString(mRequestModel.getRequestNumber());
                //ss.setSpan(new TypefaceSpan(activity, "CentraleSans-Light.otf"), 0, ss.length(),
               //         Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                appBarLayout.setTitle(mRequestModel.getRequestNumber());
            }
        } else if (getArguments().containsKey(ARG_ITEM_ID)) {
            String reqId = getArguments().getString(ARG_ITEM_ID);
            new DetailRequestAsync().execute(reqId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.request_detail, container, false);
        Typeface tfRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/CentraleSans-Regular.otf");
        Typeface tfBold= Typeface.createFromAsset(getActivity().getAssets(), "fonts/CentraleSans-Bold.otf");
        mLLMainLayout = (LinearLayout) rootView.findViewById(R.id.ll_req_detail);

        tvStatus = (TextView) rootView.findViewById(R.id.tv_detail_status);
        tvStatus.setTypeface(tfRegular);

        tvDate = (TextView) rootView.findViewById(R.id.tv_detail_date);
        tvDate.setTypeface(tfRegular);

        tvCustomerName = (TextView) rootView.findViewById(R.id.tv_customer_name);
        tvCustomerName.setTypeface(tfRegular);

        tvCustomerPhone = (TextView) rootView.findViewById(R.id.tv_customer_phone);
        tvCustomerPhone.setTypeface(tfRegular);

        tvCustomerEmail = (TextView) rootView.findViewById(R.id.tv_customer_email);
        tvCustomerEmail.setTypeface(tfRegular);

        tvCustomerAddress = (TextView) rootView.findViewById(R.id.tv_customer_address);
        tvCustomerAddress.setTypeface(tfRegular);

        tvLogPartner = (TextView) rootView.findViewById(R.id.tv_logistics_partner);
        tvLogPartner.setTypeface(tfRegular);

        tvLogEmail = (TextView) rootView.findViewById(R.id.tv_log_email);
        tvLogEmail.setTypeface(tfRegular);

        tvLogPhone = (TextView) rootView.findViewById(R.id.tv_log_phone);
        tvLogPhone.setTypeface(tfRegular);

        btnStatus = (Button) rootView.findViewById(R.id.btn_status);
        btnStatus.setTypeface(tfRegular);
        btnStatus.setText("");

        btnCapture = (Button) rootView.findViewById(R.id.btn_capture);
        btnCapture.setTypeface(tfRegular);
        btnCapture.setText("Capture Proof");

        tvMovementType = (TextView) rootView.findViewById(R.id.tv_movement_type);
        tvMovementType.setTypeface(tfRegular);

        mProgressView = rootView.findViewById(R.id.login_progress);

        if (mRequestModel != null) {
            initViews();
        } else {
            mLLMainLayout.setVisibility(View.GONE);
        }

        return rootView;
    }

    void initViews() {
        tvStatus.setText(tvStatus.getText().toString().concat(mRequestModel.getStatus()));
        tvDate.setText(tvDate.getText().toString().concat(mRequestModel.getDate()));
        tvMovementType.setText(tvMovementType.getText().toString().concat(mRequestModel.getMovementType()));
        tvCustomerName.setText(tvCustomerName.getText().toString().concat(mRequestModel.getCustomerName()));
        if (mRequestModel.getCustomerPhone() != null) {
            tvCustomerPhone.setText(tvCustomerPhone.getText().toString().concat(mRequestModel.getCustomerPhone()));
        }
        tvCustomerEmail.setText(tvCustomerEmail.getText().toString().concat(mRequestModel.getCustomerEmail()));
        tvCustomerAddress.setText(tvCustomerAddress.getText().toString().concat(mRequestModel.getCustomerAddress()));
        tvLogPartner.setText(tvLogPartner.getText().toString().concat(mRequestModel.getLogCompany()));
        tvLogEmail.setText(tvLogEmail.getText().toString().concat(mRequestModel.getLogEmail()));
        tvLogPhone.setText(tvLogPhone.getText().toString().concat(mRequestModel.getLogPhone()));

        String userRole = getActivity().getSharedPreferences(Constants.PREF_PHILLIPS, Context.MODE_PRIVATE).getString(Constants.USER_ROLE, null);
        if (userRole != null && userRole.equals("LOGISTIC_PARTNERS_USER_ROLE")) {
            btnStatus.setVisibility(View.VISIBLE);
        } else {
            btnStatus.setVisibility(View.GONE);
            btnCapture.setVisibility(View.GONE);
        }
        btnStatus.setText(mRequestModel.getRequestActionName());

        if (mRequestModel.getProofOfStatusRequired() == 1) {
            btnCapture.setVisibility(View.VISIBLE);
        } else {
            btnCapture.setVisibility(View.GONE);
        }

        if (mRequestModel.getStatus().equals("Delivered")) {
            btnStatus.setVisibility(View.GONE);
        }

        btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRequestModel.getRemarkForDelayRequired() == 1) {
                    showRemarksDialog(mRequestModel.getRequestID(), String.valueOf(mRequestModel.getRequestActionId()));
                } else {
                    String userId = getActivity().getSharedPreferences(Constants.PREF_PHILLIPS, Context.MODE_PRIVATE).getString(Constants.USER_ID, null);
                    if (Utility.isNetworkAvailable(new WeakReference<Context>(getActivity()))) {
                        new DetailsAsync().execute(userId, mRequestModel.getRequestID(), String.valueOf(mRequestModel.getRequestActionId()));
                    } else {
                        Toast.makeText(getActivity(), "Internet not connected", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                getActivity().startActivityForResult(takePictureIntent, Constants.REQ_DETAIL);
            }
        });
    }

    private class DetailsAsync extends AsyncTask<String, Void, BaseModel> {
        String response = "";

        @Override
        protected BaseModel doInBackground(String... params) {

            BaseModel model = null;
            try {
                URL url = new URL(Constants.URL_HOST + "philips_cs_spare_management/request/delivery_update");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                List<NameValuePair> paramsPairs = new ArrayList<>();
                paramsPairs.add(new BasicNameValuePair("user_id", params[0]));
                paramsPairs.add(new BasicNameValuePair("request_id", params[1]));
                paramsPairs.add(new BasicNameValuePair("status", params[2]));
                if (params[3] != null) {
                    paramsPairs.add(new BasicNameValuePair("request_remarks", params[3]));
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

                /*getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Server Response: " + response, Toast.LENGTH_LONG).show();
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

            if (baseModel != null && !baseModel.isError()) {
                showProgress(false);
                Toast.makeText(getActivity(), "Status updated successfully", Toast.LENGTH_LONG).show();
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            } else {
                Toast.makeText(getActivity(), "Unable to process request.\nPlease try again later", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
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

    private class DetailRequestAsync extends AsyncTask<String, Void, BaseModel> {

        String response = "";

        @Override
        protected BaseModel doInBackground(String... params) {

            BaseModel model = null;
            try {
                URL url = new URL(Constants.URL_HOST + "philips_cs_spare_management/request/get_request_information");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                List<NameValuePair> paramsPairs = new ArrayList<>();
                paramsPairs.add(new BasicNameValuePair("request_id", params[0]));

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

                /*getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Server Response: " + response, Toast.LENGTH_LONG).show();
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

            if (baseModel != null && !baseModel.isError()) {
                showProgress(false);
                Toast.makeText(getActivity(), "Request Details", Toast.LENGTH_LONG).show();
                mRequestModel = baseModel.getRequestModel();
                mLLMainLayout.setVisibility(View.VISIBLE);
                initViews();
            } else {
                Toast.makeText(getActivity(), "Unable to process request.\nPlease try again later", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
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


    /*private String getButtonText(String status) {
        if (status.equals("New Request")) {
            return "Reached At Warehouse";
        } else if (status.equals("Reached At Warehouse")) {
            return "Pickup By Courier";
        } else if (status.equals("Pickup By Courier")) {
            return "Out For Delivery";
        } else if (status.equals("Out For Delivery")) {
            return "Delivered";
        }

        return null;
    }
*/
    /*private void sendStatus(String status) {
        String statusParam = "0";
        if (status.equals("Delivered")) {
            statusParam = "9";
        } else if (status.equals("Reached At Warehouse")) {
            statusParam = "6";
        } else if (status.equals("Pickup By Courier")) {
            statusParam = "7";
        } else if (status.equals("Out For Delivery")) {
            statusParam = "8";
        }
        String userId = getActivity().getSharedPreferences(Constants.PREF_PHILLIPS, Context.MODE_PRIVATE).getString(Constants.USER_ID, null);
        if (Utility.isNetworkAvailable(new WeakReference<Context>(getActivity()))) {
            new DetailsAsync().execute(userId, mRequestModel.getRequestID(), statusParam);
        } else {
            Toast.makeText(getActivity(), "Internet not connected", Toast.LENGTH_LONG).show();
        }
    }*/

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

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
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQ_DETAIL) {
            if (resultCode == Activity.RESULT_OK) {

            }
        }
    }

    private void showRemarksDialog(final String requestId, final String actionId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View alertView = getActivity().getLayoutInflater().inflate(R.layout.remarks_alert, null);
        final EditText etRemarks = (EditText) alertView.findViewById(R.id.et_remarks);
        builder.setView(alertView)
                // Add action buttons
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String userId = getActivity().getSharedPreferences(Constants.PREF_PHILLIPS, Context.MODE_PRIVATE).getString(Constants.USER_ID, null);
                        if (Utility.isNetworkAvailable(new WeakReference<Context>(getActivity()))) {
                            new DetailsAsync().execute(userId, requestId, actionId, etRemarks.getText().toString().trim());
                        } else {
                            Toast.makeText(getActivity(), "Internet not connected", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
