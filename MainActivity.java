package com.example.annaheventsls;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            // Check if user is admin or regular user
            // Since we don't store role in SessionManager yet, let's add it or just go to Dashboard
            // Wait, I should probably check the email for admin@annah.com as a shortcut or better yet, store role.
            String email = sessionManager.getUserEmail();
            Intent intent;
            if (email != null && email.equalsIgnoreCase("admin@annah.com")) {
                intent = new Intent(this, AdminDashboardActivity.class);
            } else {
                intent = new Intent(this, DashboardActivity.class);
            }
            startActivity(intent);
            finish();
            return;
        }
        
        setContentView(R.layout.activity_main);

        // Load background image from Unsplash
        ImageView ivBackground = findViewById(R.id.ivBackgroundMain);
        if (ivBackground != null) {
            Glide.with(this)
                .load("https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?auto=format&fit=crop&w=1600&q=80")
                .centerCrop()
                .into(ivBackground);
        }

        // This is the Entry/Landing screen
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnSignup = findViewById(R.id.btnSignup);

        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, loginActivity.class);
                startActivity(intent);
            });
        }

        if (btnSignup != null) {
            btnSignup.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
            });
        }
    }
}
