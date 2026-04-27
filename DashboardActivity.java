package com.example.annaheventsls;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.example.annaheventsls.models.ApiResponse;
import com.example.annaheventsls.models.Booking;
import com.example.annaheventsls.models.Inquiry;
import com.example.annaheventsls.network.ApiClient;
import com.example.annaheventsls.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sessionManager = new SessionManager(this);

        // UI References
        TextView tvWelcome = findViewById(R.id.tvDashboardWelcome);
        TextView tvTotal = findViewById(R.id.tvTotalBookings);
        TextView tvPending = findViewById(R.id.tvPendingBookings);
        TextView tvApproved = findViewById(R.id.tvApprovedBookings);
        TextView tvProfileName = findViewById(R.id.tvProfileName);
        TextView tvProfileEmail = findViewById(R.id.tvProfileEmail);
        
        LinearLayout bookingsContainer = findViewById(R.id.bookingsListContainer);
        ImageView ivBackground = findViewById(R.id.ivBackgroundDashboard);
        Button btnLogout = findViewById(R.id.btnLogoutDashboard);

        if (ivBackground != null) {
            Glide.with(this)
                .load("https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?auto=format&fit=crop&w=1600&q=80")
                .centerCrop()
                .into(ivBackground);
        }

        String userEmail = sessionManager.getUserEmail();
        String fullName = sessionManager.getFullName();

        if (userEmail != null) {
            String firstName = fullName.contains(" ") ? fullName.split(" ")[0] : fullName;
            tvWelcome.setText(getString(R.string.welcome_back_name, firstName));
            tvProfileName.setText(fullName);
            tvProfileEmail.setText(userEmail);

            fetchBookings(tvTotal, tvPending, tvApproved, bookingsContainer);
            fetchEnquiries(findViewById(R.id.enquiriesListContainer));
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                sessionManager.logout();
                Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
        
        View btnNewBooking = findViewById(R.id.btnNewBookingAction);
        if (btnNewBooking != null) {
            btnNewBooking.setOnClickListener(v -> startActivity(new Intent(this, BookingActivity.class)));
        }

        View btnContact = findViewById(R.id.btnContactAction);
        if (btnContact != null) {
            btnContact.setOnClickListener(v -> startActivity(new Intent(this, ContactActivity.class)));
        }
        
        View btnViewGallery = findViewById(R.id.btnViewGallery);
        if (btnViewGallery != null) {
            btnViewGallery.setOnClickListener(v -> startActivity(new Intent(this, GalleryActivity.class)));
        }
    }

    private void fetchBookings(TextView total, TextView pending, TextView approved, LinearLayout container) {
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        apiService.getUserBookings().enqueue(new Callback<ApiResponse<List<Booking>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Booking>>> call, Response<ApiResponse<List<Booking>>> response) {
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    List<Booking> bookings = response.body().getData();
                    updateStats(bookings, total, pending, approved);
                    displayBookings(bookings, container);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Booking>>> call, Throwable t) {
                // Silently fail or show toast
            }
        });
    }

    private void updateStats(List<Booking> bookings, TextView total, TextView pending, TextView approved) {
        int totalCount = bookings.size();
        int pendingCount = 0;
        int approvedCount = 0;

        for (Booking b : bookings) {
            String status = b.getStatus();
            if ("Approved".equalsIgnoreCase(status)) approvedCount++;
            else if ("Pending".equalsIgnoreCase(status)) pendingCount++;
        }

        total.setText(String.valueOf(totalCount));
        pending.setText(String.valueOf(pendingCount));
        approved.setText(String.valueOf(approvedCount));
    }

    private void displayBookings(List<Booking> bookings, LinearLayout container) {
        if (container == null) return;
        container.removeAllViews();

        if (bookings.isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("No recent bookings found.");
            tvEmpty.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            tvEmpty.setAlpha(0.6f);
            tvEmpty.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tvEmpty.setPadding(0, 50, 0, 0);
            container.addView(tvEmpty);
            return;
        }

        for (Booking booking : bookings) {
            View view = getLayoutInflater().inflate(R.layout.item_event, container, false);
            
            TextView tvTitle = view.findViewById(R.id.tvEventName);
            TextView tvDetails = view.findViewById(R.id.tvEventDetails);
            TextView tvStatus = view.findViewById(R.id.tvEventPrice);

            tvTitle.setText(booking.getPackageType());
            tvDetails.setText(String.format("%s | %s", booking.getEventDate(), booking.getVenue()));
            
            String status = booking.getStatus();
            tvStatus.setText(status);
            
            if ("Approved".equalsIgnoreCase(status)) {
                tvStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_light));
            } else {
                tvStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_light));
            }

            container.addView(view);
        }
    }

    private void fetchEnquiries(LinearLayout container) {
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        apiService.getUserInquiries().enqueue(new Callback<ApiResponse<List<Inquiry>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Inquiry>>> call, Response<ApiResponse<List<Inquiry>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayEnquiries(response.body().getData(), container);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Inquiry>>> call, Throwable t) {}
        });
    }

    private void displayEnquiries(List<Inquiry> inquiries, LinearLayout container) {
        if (container == null) return;
        container.removeAllViews();

        if (inquiries == null || inquiries.isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("No enquiries found.");
            tvEmpty.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            tvEmpty.setAlpha(0.6f);
            container.addView(tvEmpty);
            return;
        }

        for (Inquiry inq : inquiries) {
            View view = getLayoutInflater().inflate(R.layout.item_event, container, false);
            TextView tvSubject = view.findViewById(R.id.tvEventName);
            TextView tvMessage = view.findViewById(R.id.tvEventDetails);
            TextView tvStatus = view.findViewById(R.id.tvEventPrice);

            tvSubject.setText(inq.getSubject());
            tvMessage.setText(inq.getMessage());
            
            if (inq.getAdminReply() != null && !inq.getAdminReply().isEmpty()) {
                tvStatus.setText("Replied");
                tvStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_light));
                
                view.setOnClickListener(v -> {
                    new AlertDialog.Builder(this)
                        .setTitle("Admin Reply")
                        .setMessage("Your Message: " + inq.getMessage() + "\n\nReply: " + inq.getAdminReply())
                        .setPositiveButton("OK", null)
                        .show();
                });
            } else {
                tvStatus.setText("Sent");
                tvStatus.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                tvStatus.setAlpha(0.6f);
            }

            container.addView(view);
        }
    }
}
