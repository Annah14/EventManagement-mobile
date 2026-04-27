package com.example.annaheventsls;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Load background image from Unsplash
        ImageView ivBackground = findViewById(R.id.ivBackgroundSplash);
        if (ivBackground != null) {
            Glide.with(this)
                .load("https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?auto=format&fit=crop&w=1600&q=80")
                .centerCrop()
                .into(ivBackground);
        }

        SessionManager sessionManager = new SessionManager(this);

        // Get the container to animate everything together
        LinearLayout splashContainer = findViewById(R.id.splashContainer);
        
        // Load the Netflix-style zoom animation
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash_animation);
        if (splashContainer != null) {
            splashContainer.startAnimation(animation);
        }

        // Transition after the animation completes
        new Handler().postDelayed(() -> {
            Intent intent;
            if (sessionManager.isLoggedIn()) {
                // User is already logged in, go straight to Dashboard
                String email = sessionManager.getUserEmail();
                if (email != null && email.equalsIgnoreCase("admin@annah.com")) {
                    intent = new Intent(SplashActivity.this, AdminDashboardActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, DashboardActivity.class);
                    intent.putExtra("USER_EMAIL", email);
                }
            } else {
                // Not logged in, go to the Welcome/Landing screen (MainActivity)
                intent = new Intent(SplashActivity.this, MainActivity.class);
            }
            startActivity(intent);
            
            // Smooth cross-fade transition
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            
            finish();
        }, 2100); // 2.1 seconds
    }
}
