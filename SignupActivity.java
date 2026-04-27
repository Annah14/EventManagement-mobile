package com.example.annaheventsls;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

import com.example.annaheventsls.models.ApiResponse;
import com.example.annaheventsls.models.AuthResponseData;
import com.example.annaheventsls.models.RegisterRequest;
import com.example.annaheventsls.network.ApiClient;
import com.example.annaheventsls.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword, etConfirmPassword;
    private CheckBox cbTerms;
    private SessionManager sessionManager;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        sessionManager = new SessionManager(this);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        Button btnSignup = findViewById(R.id.btnSignupAction);
        TextView tvLogin = findViewById(R.id.tvLoginLink);
        cbTerms = findViewById(R.id.termsCheck);
        ImageView ivEyeSignup = findViewById(R.id.icEyeSignup);
        ImageView ivEyeConfirm = findViewById(R.id.icEyeConfirm);
        ImageView ivBackground = findViewById(R.id.ivBackgroundSignup);

        if (ivBackground != null) {
            Glide.with(this)
                .load("https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?auto=format&fit=crop&w=1600&q=80")
                .centerCrop()
                .into(ivBackground);
        }

        if (btnSignup != null) {
            btnSignup.setOnClickListener(v -> validateAndSignup());
        }

        if (tvLogin != null) {
            tvLogin.setOnClickListener(v -> {
                Intent intent = new Intent(SignupActivity.this, loginActivity.class);
                startActivity(intent);
                finish();
            });
        }

        if (ivEyeSignup != null && etPassword != null) {
            ivEyeSignup.setOnClickListener(v -> {
                isPasswordVisible = !isPasswordVisible;
                toggleVisibility(etPassword, ivEyeSignup, isPasswordVisible);
            });
        }

        if (ivEyeConfirm != null && etConfirmPassword != null) {
            ivEyeConfirm.setOnClickListener(v -> {
                isConfirmPasswordVisible = !isConfirmPasswordVisible;
                toggleVisibility(etConfirmPassword, ivEyeConfirm, isConfirmPasswordVisible);
            });
        }
    }

    private void toggleVisibility(EditText editText, ImageView imageView, boolean isVisible) {
        if (isVisible) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            imageView.setImageResource(R.drawable.ic_eye);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            imageView.setImageResource(R.drawable.ic_eye_off);
        }
        editText.setSelection(editText.getText().length());
    }

    private void validateAndSignup() {
        String fullname = etFullName != null ? etFullName.getText().toString().trim() : "";
        String email = etEmail != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword != null ? etPassword.getText().toString().trim() : "";
        String confirmPassword = etConfirmPassword != null ? etConfirmPassword.getText().toString().trim() : "";

        if (fullname.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        } else if (cbTerms != null && !cbTerms.isChecked()) {
            Toast.makeText(this, "Please accept Terms & Conditions", Toast.LENGTH_SHORT).show();
        } else {
            ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
            RegisterRequest request = new RegisterRequest(fullname, email, password);

            apiService.register(request).enqueue(new Callback<ApiResponse<AuthResponseData>>() {
                @Override
                public void onResponse(Call<ApiResponse<AuthResponseData>> call, Response<ApiResponse<AuthResponseData>> response) {
                    if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                        AuthResponseData data = response.body().getData();
                        String token = data.getUser().getApiToken();
                        String fullName = data.getUser().getFullname();

                        sessionManager.setLogin(true, email, token, fullName);
                        Toast.makeText(SignupActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignupActivity.this, DashboardActivity.class);
                        intent.putExtra("USER_EMAIL", email);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMsg = "Registration failed.";
                        if (response.body() != null && response.body().getMessage() != null) {
                            errorMsg = response.body().getMessage();
                        }
                        Toast.makeText(SignupActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<AuthResponseData>> call, Throwable t) {
                    Toast.makeText(SignupActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
