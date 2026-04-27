package com.example.annaheventsls;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.annaheventsls.models.AdminDataResponse;
import com.example.annaheventsls.models.ApiResponse;
import com.example.annaheventsls.models.Booking;
import com.example.annaheventsls.models.Inquiry;
import com.example.annaheventsls.models.StatusUpdateRequest;
import com.example.annaheventsls.network.ApiClient;
import com.example.annaheventsls.network.ApiService;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ScrollView scrollBookings, scrollServices, scrollPackages, scrollInquiries;
    private LinearLayout bookingsContainer, inquiriesContainer, servicesContainer, packagesContainer;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        session = new SessionManager(this);

        // UI Components
        Toolbar toolbar = findViewById(R.id.toolbarAdmin);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view_admin);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Sections
        scrollBookings = findViewById(R.id.scrollBookings);
        scrollServices = findViewById(R.id.scrollServices);
        scrollPackages = findViewById(R.id.scrollPackages);
        scrollInquiries = findViewById(R.id.scrollInquiries);

        // Containers
        bookingsContainer = findViewById(R.id.adminBookingsContainer);
        inquiriesContainer = findViewById(R.id.adminInquiriesContainer);
        servicesContainer = findViewById(R.id.adminServicesListContainer);
        packagesContainer = findViewById(R.id.adminPackagesListContainer);

        // Inputs
        EditText etTitle = findViewById(R.id.etAdminServiceTitle);
        EditText etShort = findViewById(R.id.etAdminServiceShort);
        Button btnPublish = findViewById(R.id.btnAddServiceAdmin);

        EditText etPkgName = findViewById(R.id.etAdminPkgName);
        EditText etPkgPrice = findViewById(R.id.etAdminPkgPrice);
        EditText etPkgShort = findViewById(R.id.etAdminPkgShort);
        Button btnLaunchPkg = findViewById(R.id.btnLaunchPkgAdmin);

        ImageView ivBg = findViewById(R.id.ivAdminBg);
        if (ivBg != null) {
            Glide.with(this)
                .load("https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?auto=format&fit=crop&w=1600&q=80")
                .centerCrop()
                .into(ivBg);
        }

        refreshData();

        // Listeners
        if (btnPublish != null) {
            btnPublish.setOnClickListener(v -> {
                String title = etTitle.getText().toString().trim();
                String shortDesc = etShort.getText().toString().trim();
                if (!title.isEmpty() && !shortDesc.isEmpty()) {
                    addService(title, shortDesc);
                } else {
                    Toast.makeText(this, "Title and Tagline required", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (btnLaunchPkg != null) {
            btnLaunchPkg.setOnClickListener(v -> {
                String name = etPkgName.getText().toString().trim();
                String priceStr = etPkgPrice.getText().toString().trim();
                String shortDesc = etPkgShort.getText().toString().trim();
                if (!name.isEmpty() && !priceStr.isEmpty()) {
                    addPackage(name, Double.parseDouble(priceStr), shortDesc);
                } else {
                    Toast.makeText(this, "Name and Price required", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // Hide all
        scrollBookings.setVisibility(View.GONE);
        scrollServices.setVisibility(View.GONE);
        scrollPackages.setVisibility(View.GONE);
        scrollInquiries.setVisibility(View.GONE);

        if (id == R.id.nav_admin_bookings) {
            scrollBookings.setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle("Bookings");
        } else if (id == R.id.nav_admin_services) {
            scrollServices.setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle("Services");
        } else if (id == R.id.nav_admin_packages) {
            scrollPackages.setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle("Packages");
        } else if (id == R.id.nav_admin_inquiries) {
            scrollInquiries.setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle("Inquiries");
        } else if (id == R.id.nav_admin_logout) {
            session.logout();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void refreshData() {
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        apiService.getAdminData().enqueue(new Callback<ApiResponse<AdminDataResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AdminDataResponse>> call, Response<ApiResponse<AdminDataResponse>> response) {
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    AdminDataResponse data = response.body().getData();
                    displayBookings(data.getBookings());
                    displayInquiries(data.getInquiries());
                    displayServices(data.getServices());
                    displayPackages(data.getPackages());
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<AdminDataResponse>> call, Throwable t) {}
        });
    }

    private void addService(String t, String s) {
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("title", t);
        body.put("short_desc", s);
        body.put("long_desc", "Details coming soon...");
        body.put("icon_class", "fa-star");
        
        apiService.addService(body).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminDashboardActivity.this, "Service Published", Toast.LENGTH_SHORT).show();
                    refreshData();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {}
        });
    }

    private void addPackage(String n, double p, String s) {
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        apiService.addPackage(new com.example.annaheventsls.models.PackageRequest(n, p, s, "Full details in consultation.", "fa-box")).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminDashboardActivity.this, "Package Launched", Toast.LENGTH_SHORT).show();
                    refreshData();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {}
        });
    }

    private void displayServices(List<java.util.Map<String, Object>> services) {
        if (servicesContainer == null || services == null) return;
        servicesContainer.removeAllViews();
        for (java.util.Map<String, Object> s : services) {
            View view = getLayoutInflater().inflate(R.layout.item_event, servicesContainer, false);
            TextView tvName = view.findViewById(R.id.tvEventName);
            TextView tvTag = view.findViewById(R.id.tvEventDetails);
            TextView tvAction = view.findViewById(R.id.tvEventPrice);

            tvName.setText((String) s.get("title"));
            tvTag.setText((String) s.get("short_desc"));
            tvAction.setText("DELETE");
            tvAction.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));

            view.setOnClickListener(v -> {
                int id = ((Double) s.get("id")).intValue();
                new AlertDialog.Builder(this)
                    .setTitle("Delete Service?")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Delete", (dialog, which) -> deleteItem("service", id))
                    .setNegativeButton("Cancel", null)
                    .show();
            });
            servicesContainer.addView(view);
        }
    }

    private void displayPackages(List<java.util.Map<String, Object>> packages) {
        if (packagesContainer == null || packages == null) return;
        packagesContainer.removeAllViews();
        for (java.util.Map<String, Object> p : packages) {
            View view = getLayoutInflater().inflate(R.layout.item_event, packagesContainer, false);
            TextView tvName = view.findViewById(R.id.tvEventName);
            TextView tvPrice = view.findViewById(R.id.tvEventDetails);
            TextView tvAction = view.findViewById(R.id.tvEventPrice);

            tvName.setText((String) p.get("name"));
            tvPrice.setText("$" + p.get("price"));
            tvAction.setText("DELETE");
            tvAction.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));

            view.setOnClickListener(v -> {
                int id = ((Double) p.get("id")).intValue();
                new AlertDialog.Builder(this)
                    .setTitle("Delete Package?")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Delete", (dialog, which) -> deleteItem("package", id))
                    .setNegativeButton("Cancel", null)
                    .show();
            });
            packagesContainer.addView(view);
        }
    }

    private void deleteItem(String type, int id) {
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        java.util.Map<String, Integer> body = new java.util.HashMap<>();
        if (type.equals("service")) body.put("service_id", id);
        else body.put("package_id", id);

        Call<ApiResponse<Void>> call = type.equals("service") ? apiService.deleteService(body) : apiService.deletePackage(body);
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    refreshData();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {}
        });
    }

    private void displayBookings(List<Booking> all) {
        if (bookingsContainer == null) return;
        bookingsContainer.removeAllViews();
        for (Booking b : all) {
            View view = getLayoutInflater().inflate(R.layout.item_event, bookingsContainer, false);
            TextView tvName = view.findViewById(R.id.tvEventName);
            TextView tvDetails = view.findViewById(R.id.tvEventDetails);
            TextView tvStatus = view.findViewById(R.id.tvEventPrice);

            tvName.setText(b.getFullname() + " - " + b.getPackageType());
            tvDetails.setText(b.getEventDate() + " | " + b.getVenue());
            tvStatus.setText(b.getStatus().toUpperCase());

            // Set dynamic status colors
            if ("Approved".equalsIgnoreCase(b.getStatus())) {
                tvStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_light));
            } else if ("Rejected".equalsIgnoreCase(b.getStatus())) {
                tvStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
            } else {
                tvStatus.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            }

            view.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                    .setTitle("Manage Booking")
                    .setMessage("Update status for " + b.getFullname() + "?")
                    .setPositiveButton("Approve", (dialog, which) -> updateStatus(b.getId(), "Approved"))
                    .setNegativeButton("Reject", (dialog, which) -> updateStatus(b.getId(), "Rejected"))
                    .setNeutralButton("Cancel", null)
                    .show();
            });
            bookingsContainer.addView(view);
        }
    }

    private void updateStatus(int bookingId, String status) {
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        apiService.updateBookingStatus(new StatusUpdateRequest(bookingId, status)).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) refreshData();
            }
            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {}
        });
    }

    private void displayInquiries(List<Inquiry> inqs) {
        if (inquiriesContainer == null) return;
        inquiriesContainer.removeAllViews();
        for (Inquiry inq : inqs) {
            View view = getLayoutInflater().inflate(R.layout.item_event, inquiriesContainer, false);
            TextView tvName = view.findViewById(R.id.tvEventName);
            TextView tvSubject = view.findViewById(R.id.tvEventDetails);
            TextView tvStatus = view.findViewById(R.id.tvEventPrice);

            tvName.setText(inq.getFullname());
            tvSubject.setText(inq.getSubject());
            
            if (inq.getAdminReply() != null) {
                tvStatus.setText("REPLIED");
                tvStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_light));
            } else {
                tvStatus.setText("PENDING");
                tvStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_light));
            }

            view.setOnClickListener(v -> {
                EditText etReply = new EditText(this);
                etReply.setHint("Type your response here...");
                new AlertDialog.Builder(this)
                    .setTitle("Reply to " + inq.getFullname())
                    .setMessage("Subject: " + inq.getSubject() + "\nMessage: " + inq.getMessage())
                    .setView(etReply)
                    .setPositiveButton("Send Reply", (dialog, which) -> {
                        String replyText = etReply.getText().toString().trim();
                        if (!replyText.isEmpty()) {
                            sendReply(inq.getId(), replyText);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            });
            inquiriesContainer.addView(view);
        }
    }

    private void sendReply(int id, String text) {
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        apiService.replyInquiry(new com.example.annaheventsls.models.ReplyRequest(id, text)).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminDashboardActivity.this, "Reply sent", Toast.LENGTH_SHORT).show();
                    refreshData();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {}
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
