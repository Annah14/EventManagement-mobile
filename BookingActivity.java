package com.example.annaheventsls;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.annaheventsls.models.ApiResponse;
import com.example.annaheventsls.models.BookingRequest;
import com.example.annaheventsls.network.ApiClient;
import com.example.annaheventsls.network.ApiService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingActivity extends AppCompatActivity {

    private SessionManager session;
    private EditText etEventType, etEventDate, etVenue, etGuests, etPaymentDetail, etPaymentPin, etMessage;
    private Spinner spinnerPackage, spinnerPayment;
    private LinearLayout llPaymentDetails;
    private TextView tvPaymentDetailLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        session = new SessionManager(this);

        ImageView ivBg = findViewById(R.id.ivBookingBg);
        etEventType = findViewById(R.id.etEventType);
        etEventDate = findViewById(R.id.etEventDate);
        etVenue = findViewById(R.id.etVenue);
        etGuests = findViewById(R.id.etGuests);
        etPaymentDetail = findViewById(R.id.etPaymentDetail);
        etPaymentPin = findViewById(R.id.etPaymentPin);
        etMessage = findViewById(R.id.etMessage);
        spinnerPackage = findViewById(R.id.spinnerPackage);
        spinnerPayment = findViewById(R.id.spinnerPayment);
        llPaymentDetails = findViewById(R.id.llPaymentDetails);
        tvPaymentDetailLabel = findViewById(R.id.tvPaymentDetailLabel);
        Button btnSubmit = findViewById(R.id.btnSubmitBooking);

        if (ivBg != null) {
            Glide.with(this)
                .load("https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?auto=format&fit=crop&w=1600&q=80")
                .centerCrop()
                .into(ivBg);
        }

        setupSpinners();
        etEventDate.setOnClickListener(v -> showDatePicker());
        btnSubmit.setOnClickListener(v -> submitBooking());
    }

    private void setupSpinners() {
        // Fetch packages from API
        fetchPackages();

        List<String> payments = new ArrayList<>();
        payments.add("-- Select Payment Method --");
        payments.add("Bank Transfer");
        payments.add("Mobile Money");
        payments.add("PayPal");
        payments.add("Cash on Meeting");

        ArrayAdapter<String> payAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, payments);
        payAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPayment.setAdapter(payAdapter);

        spinnerPayment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = payments.get(position);
                if (selected.equals("Mobile Money") || selected.equals("Bank Transfer")) {
                    llPaymentDetails.setVisibility(View.VISIBLE);
                    tvPaymentDetailLabel.setText(selected.equals("Mobile Money") ? "Mobile Number & Payment PIN" : "Bank Account Number & Reference");
                } else {
                    llPaymentDetails.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void fetchPackages() {
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        apiService.getPackages().enqueue(new Callback<ApiResponse<Map<String, List<Object>>>>() {
            @Override
            public void onResponse(Call<ApiResponse<Map<String, List<Object>>>> call, Response<ApiResponse<Map<String, List<Object>>>> response) {
                List<String> names = new ArrayList<>();
                names.add("-- Select Package --");
                
                if (response.isSuccessful() && response.body() != null) {
                    List<Object> packageList = response.body().getData().get("packages");
                    if (packageList != null) {
                        for (Object obj : packageList) {
                            if (obj instanceof Map) {
                                names.add((String) ((Map) obj).get("name"));
                            }
                        }
                    }
                } else {
                    // Fallback defaults if API fails
                    names.add("Basic Package");
                    names.add("Premium Package");
                    names.add("Luxury Package");
                }
                
                ArrayAdapter<String> pkgAdapter = new ArrayAdapter<>(BookingActivity.this, R.layout.spinner_item, names);
                pkgAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerPackage.setAdapter(pkgAdapter);
            }

            @Override
            public void onFailure(Call<ApiResponse<Map<String, List<Object>>>> call, Throwable t) {
                List<String> names = new ArrayList<>();
                names.add("-- Select Package --");
                names.add("Basic Package");
                names.add("Premium Package");
                names.add("Luxury Package");
                
                ArrayAdapter<String> pkgAdapter = new ArrayAdapter<>(BookingActivity.this, R.layout.spinner_item, names);
                pkgAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerPackage.setAdapter(pkgAdapter);
            }
        });
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(this, (view, y, m, d) -> etEventDate.setText(y + "-" + (m + 1) + "-" + d), year, month, day);
        dpd.show();
    }

    private void submitBooking() {
        String email = session.getUserEmail();
        String pkg = spinnerPackage.getSelectedItem().toString();
        String type = etEventType.getText().toString().trim();
        String date = etEventDate.getText().toString().trim();
        String venue = etVenue.getText().toString().trim();
        String guestsStr = etGuests.getText().toString().trim();
        String detail = etPaymentDetail.getText().toString().trim();
        String pin = etPaymentPin.getText().toString().trim();
        String msg = etMessage.getText().toString().trim();
        String payMethod = spinnerPayment.getSelectedItem().toString();

        if (type.isEmpty() || date.isEmpty() || venue.isEmpty() || guestsStr.isEmpty()) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pkg.contains("Select") || payMethod.contains("Select")) {
            Toast.makeText(this, "Please select a Package and Payment Method", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullPin = detail + " | PIN: " + pin;
        
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        BookingRequest request = new BookingRequest(date, type, venue, Integer.parseInt(guestsStr), msg, pkg, payMethod, fullPin);
        
        apiService.book(request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    Toast.makeText(BookingActivity.this, "Booking Request Sent!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    String error = "Booking failed.";
                    if (response.body() != null && response.body().getMessage() != null) error = response.body().getMessage();
                    Toast.makeText(BookingActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(BookingActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
