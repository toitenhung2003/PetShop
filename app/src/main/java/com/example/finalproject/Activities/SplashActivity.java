package com.example.finalproject.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.finalproject.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initData();
    }

    private void initData() {
        ImageView imgBg = (ImageView) findViewById(R.id.img_bg);
        ImageView imgLogo = (ImageView) findViewById(R.id.img_logo);
        ImageView imgAppName = (ImageView) findViewById(R.id.img_app_name);
        LottieAnimationView lottieAnim = (LottieAnimationView) findViewById(R.id.lottie_anim);

        imgBg.animate().translationY(2000).setDuration(1000).setStartDelay(9000);
        imgLogo.animate().translationY(-1500).setDuration(1000).setStartDelay(9000);
        imgAppName.animate().translationY(-1500).setDuration(1000).setStartDelay(9000);
        lottieAnim.animate().translationY(-1500).setDuration(1000).setStartDelay(9000);

        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }, 5000);
    }
}