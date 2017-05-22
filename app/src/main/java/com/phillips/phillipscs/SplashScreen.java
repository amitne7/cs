package com.phillips.phillipscs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.widget.TextView;

/**
 * Created by dell on 07-Apr-17.
 */

public class SplashScreen extends Activity {
    //Splash Screen Timeout
    private static int SCREEN_TIME_OUT = 3000;
    TextView mTvProductName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        mTvProductName = (TextView) findViewById(R.id.tvProduct);
        SpannableString sProductName = new SpannableString(getResources().getString(R.string.app_name));
        sProductName.setSpan(new TypefaceSpan(this, "CentraleSans-Light.otf"), 0, sProductName.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvProductName.setText(sProductName);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        }, SCREEN_TIME_OUT);


    }



}

