package com.phillips.phillipscs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
//import android.text.style.TypefaceSpan;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import com.phillips.phillipscs.TypefaceSpan;

import javax.net.ssl.HttpsURLConnection;

/**
 * An activity representing a list of requests. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RequestDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RequestListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private List<RequestModel> mRequestModels = new ArrayList<>();
    private View mProgressView;
    private RecyclerView mRecyclerView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_list);
        mProgressView = findViewById(R.id.login_progress);
        mRecyclerView = (RecyclerView) findViewById(R.id.request_list);
//        FloatingActionButton btnLogout = (FloatingActionButton) findViewById(R.id.logout);
//        btnLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getSharedPreferences(Constants.PREF_PHILLIPS, MODE_PRIVATE).edit().clear().apply();
//                startActivity(new Intent(RequestListActivity.this, LoginActivity.class));
//                finish();
//            }
//        });
        assert mRecyclerView != null;
        setupRecyclerView(mRecyclerView, mRequestModels);
        fetchRequestData();

        if (findViewById(R.id.request_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);
        SpannableString s = new SpannableString(getResources().getString(R.string.app_name));
        s.setSpan(new TypefaceSpan(this, "CentraleSans-Light.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((TextView) findViewById(R.id.app_toolbar_title)).setText(s);

// Update the action bar title with the TypefaceSpan instance
        //setTitle(s);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        Typeface tfLight = Typeface.createFromAsset(this.getAssets(), "fonts/CentraleSans-Light.otf");
        SpannableStringBuilder title = new SpannableStringBuilder(this.getString(R.string.logout));
        title.setSpan(tfLight, 0, title.length(), 0);
        MenuItem menuItem = menu.findItem(R.id.logout); // OR THIS
        menuItem.setTitle(title);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                getSharedPreferences(Constants.PREF_PHILLIPS, MODE_PRIVATE).edit().clear().apply();
                startActivity(new Intent(RequestListActivity.this, LoginActivity.class));
                finish();
                return (true);


        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchRequestData() {
        String userID = getSharedPreferences(Constants.PREF_PHILLIPS, MODE_PRIVATE).getString(Constants.USER_ID, null);
        if (userID != null) {
            if (Utility.isNetworkAvailable(new WeakReference<Context>(RequestListActivity.this))) {
                new RequestAsync().execute(userID);
            } else {
                Toast.makeText(RequestListActivity.this, "Internet not connected", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "No data for this user", Toast.LENGTH_LONG).show();
        }

    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<RequestModel> requestModels) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(requestModels));
    }

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

    private class RequestAsync extends AsyncTask<String, Void, BaseModel> {

        String response = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            showProgress(true);
        }

        @Override
        protected BaseModel doInBackground(String... params) {

            BaseModel model = null;
            try {
                URL url = new URL(Constants.URL_HOST + "philips_cs_spare_management/request/get_request_list?logistic_partner_user_id=" + params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
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
                        Toast.makeText(RequestListActivity.this, "Server Response: " + response, Toast.LENGTH_LONG).show();
                    }
                });*/

                model = new ObjectMapper().readValue(response, BaseModel.class);
                if (model != null) {
                    model.setResponseCode(responseCode);
                }

                Log.e(LoginActivity.class.getName(), response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return model;
        }

        @Override
        protected void onPostExecute(BaseModel baseModel) {
            super.onPostExecute(baseModel);
            showProgress(false);

            if (baseModel != null) {

                mRequestModels = baseModel.getRequestModels();
                mRecyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(mRequestModels));
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

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<RequestModel> mValues;

        public SimpleItemRecyclerViewAdapter(List<RequestModel> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.request_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mItem = mValues.get(position);
            SpannableString ss = new SpannableString(mValues.get(position).getMovementType() + ": " + mValues.get(position).getRequestNumber());
            ss.setSpan(new TypefaceSpan(getBaseContext(), "CentraleSans-Bold.otf"), 0, ss.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            holder.mTVReqNumber.setText(mValues.get(position).getRequestNumber());
            holder.mTVReqMovement.setText(ss);
            SpannableString sReqDate = new SpannableString(mValues.get(position).getDate());
            sReqDate.setSpan(new TypefaceSpan(getBaseContext(), "CentraleSans-Hairline.otf"), 0, sReqDate.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.mTVReqDate.setText(sReqDate);
            SpannableString sReqCustomer = new SpannableString(mValues.get(position).getCustomerName());
            sReqCustomer.setSpan(new TypefaceSpan(getBaseContext(), "CentraleSans-Regular.otf"), 0, sReqCustomer.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.mTVReqCustomer.setText(sReqCustomer);
//            holder.mTVMovementType.setText(mValues.get(position).getMovementType());
            holder.mBtnStatus.setText(mValues.get(position).getRequestActionName());

            if (mValues.get(position).getProofOfStatusRequired() == 0) {
                holder.mBtnProof.setVisibility(View.GONE);
            }

            /*int colorCode = Color.GRAY;
            if (mValues.get(position).getStatus().equals("New Request")) {
                colorCode = ContextCompat.getColor(RequestListActivity.this, android.R.color.holo_red_dark);
//                holder.mStatusView.setText("N");
            } else if (mValues.get(position).getStatus().equals("Reached At Warehouse")) {
                colorCode = ContextCompat.getColor(RequestListActivity.this, android.R.color.holo_orange_dark);
//                holder.mStatusView.setText("W");
            } else if (mValues.get(position).getStatus().equals("Pickup By Courier")) {
                colorCode = ContextCompat.getColor(RequestListActivity.this, android.R.color.holo_blue_dark);
//                holder.mStatusView.setText("P");
            } else if (mValues.get(position).getStatus().equals("Out For Delivery")) {
                colorCode = ContextCompat.getColor(RequestListActivity.this, android.R.color.holo_green_dark);
//                holder.mStatusView.setText("O");
            } else if (mValues.get(position).getStatus().equals("Delivered")) {
                colorCode = ContextCompat.getColor(RequestListActivity.this, android.R.color.darker_gray);
//                holder.mStatusView.setText("D");
            }

            holder.mStatusColorView.setBackgroundColor(colorCode);
            int[][] states = new int[][]{
                    new int[]{android.R.attr.state_enabled}
            };

            int[] colors = new int[]{
                    colorCode
            };*/
//            holder.mStatusView.setSupportBackgroundTintList(new ColorStateList(states, colors));
//            holder.mTVReqMovement.setVisibility(View.GONE);

            holder.mBtnProof.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePictureIntent, Constants.REQ_DETAIL);
                }
            });

            holder.mBtnStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mValues.get(position).getRemarkForDelayRequired() == 1) {
                        showRemarksDialog(mValues.get(position).getRequestID(), String.valueOf(mValues.get(position).getRequestActionId()));
                    } else {
                        String userId = getSharedPreferences(Constants.PREF_PHILLIPS, Context.MODE_PRIVATE).getString(Constants.USER_ID, null);
                        if (Utility.isNetworkAvailable(new WeakReference<Context>(RequestListActivity.this))) {
                            new DetailsAsync().execute(userId, mValues.get(position).getRequestID(), String.valueOf(mValues.get(position).getRequestActionId()));
                        } else {
                            Toast.makeText(RequestListActivity.this, "Internet not connected", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, RequestDetailActivity.class);
                    intent.putExtra(RequestDetailFragment.ARG_ITEM, holder.mItem);

                    RequestListActivity.this.startActivityForResult(intent, Constants.REQ_DETAIL);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        @Override
        public long getItemId(int position) {
            return Long.parseLong(mValues.get(position).getRequestID());
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            //            public final TextView mTVReqNumber;
            public final TextView mTVReqMovement;
            public final TextView mTVReqDate;
            public final TextView mTVReqCustomer;
            //            public final TextView mTVMovementType;
            public final Button mBtnStatus;
            public final ImageButton mBtnProof;

            //            public final AppCompatTextView mStatusView;
            public final TextView mStatusColorView;

            public RequestModel mItem;
            Typeface tfBold = Typeface.createFromAsset(getAssets(), "fonts/CentraleSans-Bold.otf");
            Typeface tfRegular = Typeface.createFromAsset(getAssets(), "fonts/CentraleSans-Regular.otf");


            public ViewHolder(View view) {
                super(view);
                mView = view;
//                mTVReqNumber = (TextView) view.findViewById(R.id.tv_req_number);
                mTVReqMovement = (TextView) view.findViewById(R.id.tv_req_movement);
                mTVReqDate = (TextView) view.findViewById(R.id.tv_date);
                mTVReqCustomer = (TextView) view.findViewById(R.id.tv_customer);
//                mTVMovementType = (TextView) view.findViewById(R.id.tv_logistics);
                mStatusColorView = (TextView) view.findViewById(R.id.status_view);
                mBtnProof = (ImageButton) view.findViewById(R.id.btn_proof);
                mBtnStatus = (Button) view.findViewById(R.id.btn_status);
                mBtnStatus.setTypeface(tfRegular);
                mBtnStatus.setText("");
//                mStatusView = (AppCompatTextView) view.findViewById(R.id.img_status_color);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQ_DETAIL) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
            }
        }
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
                Toast.makeText(RequestListActivity.this, "Status updated successfully", Toast.LENGTH_LONG).show();
                fetchRequestData();
            } else {
                Toast.makeText(RequestListActivity.this, "Unable to process request.\nPlease try again later", Toast.LENGTH_LONG).show();
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

    private void showRemarksDialog(final String requestId, final String actionId) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Typeface tfLight = Typeface.createFromAsset(this.getAssets(), "fonts/CentraleSans-Light.otf");
        Typeface tfBold = Typeface.createFromAsset(getAssets(), "fonts/CentraleSans-Bold.otf");
        Typeface tfRegular = Typeface.createFromAsset(getAssets(), "fonts/CentraleSans-Regular.otf");
        View alertView = getLayoutInflater().inflate(R.layout.remarks_alert, null);
        final EditText etRemarks = (EditText) alertView.findViewById(R.id.et_remarks);
        etRemarks.setTypeface(tfLight);

        final Button btPositive = (Button) alertView.findViewById(R.id.btn_alert_positive);
        btPositive.setTypeface(tfRegular);

        btPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = getSharedPreferences(Constants.PREF_PHILLIPS, Context.MODE_PRIVATE).getString(Constants.USER_ID, null);
                if (Utility.isNetworkAvailable(new WeakReference<Context>(RequestListActivity.this))) {
                    new DetailsAsync().execute(userId, requestId, actionId, etRemarks.getText().toString().trim());
                } else {
                    Toast.makeText(RequestListActivity.this, "Internet not connected", Toast.LENGTH_LONG).show();
                }
            }
        });


        final Button btnNegative = (Button) alertView.findViewById(R.id.btn_alert_negative);
        btnNegative.setTypeface(tfRegular);


        final TextView tvRemarks = (TextView) alertView.findViewById(R.id.tv_alertTitle);
        tvRemarks.setTypeface(tfLight);

        builder.setView(alertView);

        final AlertDialog alert = builder.create();
        alert.show();

        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
    }
}
