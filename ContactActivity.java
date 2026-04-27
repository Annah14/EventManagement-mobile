package com.example.annaheventsls;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.annaheventsls.models.ApiResponse;
import com.example.annaheventsls.models.ContactRequest;
import com.example.annaheventsls.network.ApiClient;
import com.example.annaheventsls.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactActivity extends AppCompatActivity {

    private SessionManager session;
    private EditText etName, etEmail, etSubject, etMessage;
    private Button btnSend;
    private TextView tvSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        session = new SessionManager(this);

        ImageView ivBg = findViewById(R.id.ivContactBg);
        etName = findViewById(R.id.etContactName);
        etEmail = findViewById(R.id.etContactEmail);
        etSubject = findViewById(R.id.etContactSubject);
        etMessage = findViewById(R.id.etContactMessage);
        btnSend = findViewById(R.id.btnSendMessage);
        tvSuccess = findViewById(R.id.tvSuccessMessage);

        if (ivBg != null) {
            Glide.with(this)
                .load("https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?auto=format&fit=crop&w=1600&q=80")
                .centerCrop()
                .into(ivBg);
        }

        // Auto-fill user info if logged in
        if (session.isLoggedIn()) {
            String userEmail = session.getUserEmail();
            etEmail.setText(userEmail);
            etName.setText(session.getFullName());
        }

        btnSend.setOnClickListener(v -> sendInquiry());
    }

    private void sendInquiry() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String subject = etSubject.getText().toString().trim();
        String message = etMessage.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || subject.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        ContactRequest request = new ContactRequest(name, email, subject, message);

        apiService.contact(request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    // Mirror website behavior: hide form elements or show success msg
                    btnSend.setVisibility(View.GONE);
                    etName.setEnabled(false);
                    etEmail.setEnabled(false);
                    etSubject.setEnabled(false);
                    etMessage.setEnabled(false);
                    
                    tvSuccess.setVisibility(View.VISIBLE);
                    Toast.makeText(ContactActivity.this, "Inquiry Sent!", Toast.LENGTH_SHORT).show();
                } else {
                    String error = "Failed to send inquiry.";
                    if (response.body() != null && response.body().getMessage() != null) error = response.body().getMessage();
                    Toast.makeText(ContactActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(ContactActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
