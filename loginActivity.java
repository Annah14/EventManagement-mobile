package com.example.annaheventsls;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

import com.example.annaheventsls.models.ApiResponse;
import com.example.annaheventsls.models.AuthResponseData;
import com.example.annaheventsls.models.LoginRequest;
import com.example.annaheventsls.network.ApiClient;
import com.example.annaheventsls.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class loginActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        EditText etEmail = findViewById(R.id.icEmail);
        EditText etPassword = findViewById(R.id.editTextPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvCreateAccount = findViewById(R.id.tvCreateAccount);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);
        ImageView ivEye = findViewById(R.id.icEye);
        ImageView ivBackground = findViewById(R.id.ivBackgroundLogin);

        if (ivBackground != null) {
            Glide.with(this)
                .load("https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?auto=format&fit=crop&w=1600&q=80")
                .centerCrop()
                .into(ivBackground);
        }

        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Please enter your email and password.", Toast.LENGTH_SHORT).show();
                } else {
                    ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
                    LoginRequest request = new LoginRequest(email, password);
                    
                    apiService.login(request).enqueue(new Callback<ApiResponse<AuthResponseData>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<AuthResponseData>> call, Response<ApiResponse<AuthResponseData>> response) {
                            if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                                AuthResponseData data = response.body().getData();
                                String role = data.getUser().getRole();
                                String token = data.getUser().getApiToken();
                                String fullName = data.getUser().getFullname();
                                
                                sessionManager.setLogin(true, email, token, fullName);
                                
                                Intent intent;
                                if ("admin".equalsIgnoreCase(role)) {
                                    Toast.makeText(loginActivity.this, "Admin Login Successful", Toast.LENGTH_SHORT).show();
                                    intent = new Intent(loginActivity.this, AdminDashboardActivity.class);
                                } else {
                                    Toast.makeText(loginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    intent = new Intent(loginActivity.this, DashboardActivity.class);
                                }
                                intent.putExtra("USER_EMAIL", email);
                                startActivity(intent);
                                finish();
                            } else {
                                String errorMsg = "Invalid login details.";
                                if (response.body() != null && response.body().getMessage() != null) {
                                    errorMsg = response.body().getMessage();
                                }
                                Toast.makeText(loginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<AuthResponseData>> call, Throwable t) {
                            Toast.makeText(loginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

        if (tvCreateAccount != null) {
            tvCreateAccount.setOnClickListener(v -> {
                startActivity(new Intent(this, SignupActivity.class));
            });
        }

        if (tvForgotPassword != null) {
            tvForgotPassword.setOnClickListener(v -> Toast.makeText(this, "Forgot password clicked", Toast.LENGTH_SHORT).show());
        }

        if (ivEye != null) {
            ivEye.setOnClickListener(v -> {
                isPasswordVisible = !isPasswordVisible;
                if (isPasswordVisible) {
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    ivEye.setImageResource(R.drawable.ic_eye);
                } else {
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ivEye.setImageResource(R.drawable.ic_eye_off);
                }
                etPassword.setSelection(etPassword.getText().length());
            });
        }
    }
}
