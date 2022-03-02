package com.example.gameescalator;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.gameescalator.Fragments.ProfileFragment;
import com.example.gameescalator.Home.Home;
import com.example.gameescalator.User.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private LottieAnimationView lottieStars;
    private ImageView appLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        firebaseAuth = FirebaseAuth.getInstance();
        lottieStars = findViewById(R.id.splash_screen_five_stars);
        appLogo = findViewById(R.id.app_logo);

        lottieStars.playAnimation();

        lottieStars.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        appLogo.setVisibility(View.VISIBLE);

                    }
                }, 1250);

                super.onAnimationStart(animation);
            }
        });

        lottieStars.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (firebaseAuth.getCurrentUser() != null)
                            startActivity(new Intent(getApplicationContext(),Home.class));
                        else startActivity(new Intent(getApplicationContext(),MainActivity.class));

                    }
                }, 1000);

                super.onAnimationEnd(animation);
            }
        });
    }
}