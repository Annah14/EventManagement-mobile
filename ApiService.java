package com.example.annaheventsls.network;

import com.example.annaheventsls.models.AdminDataResponse;
import com.example.annaheventsls.models.ApiResponse;
import com.example.annaheventsls.models.AuthResponseData;
import com.example.annaheventsls.models.Booking;
import com.example.annaheventsls.models.BookingRequest;
import com.example.annaheventsls.models.ContactRequest;
import com.example.annaheventsls.models.Inquiry;
import com.example.annaheventsls.models.LoginRequest;
import com.example.annaheventsls.models.RegisterRequest;
import com.example.annaheventsls.models.ReplyRequest;
import com.example.annaheventsls.models.StatusUpdateRequest;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @POST("login.php")
    Call<ApiResponse<AuthResponseData>> login(@Body LoginRequest request);

    @POST("register.php")
    Call<ApiResponse<AuthResponseData>> register(@Body RegisterRequest request);

    @GET("services.php")
    Call<ApiResponse<Map<String, List<Object>>>> getServices();

    @GET("packages.php")
    Call<ApiResponse<Map<String, List<Object>>>> getPackages();

    @POST("book.php")
    Call<ApiResponse<Void>> book(@Body BookingRequest request);

    @POST("contact.php")
    Call<ApiResponse<Void>> contact(@Body ContactRequest request);

    @GET("user_bookings.php")
    Call<ApiResponse<List<Booking>>> getUserBookings();

    @GET("admin_data.php")
    Call<ApiResponse<AdminDataResponse>> getAdminData();

    @POST("update_booking_status.php")
    Call<ApiResponse<Void>> updateBookingStatus(@Body StatusUpdateRequest request);

    @POST("reply_inquiry.php")
    Call<ApiResponse<Void>> replyInquiry(@Body ReplyRequest request);

    @POST("add_service.php")
    Call<ApiResponse<Void>> addService(@Body Map<String, Object> body);

    @POST("delete_service.php")
    Call<ApiResponse<Void>> deleteService(@Body Map<String, Integer> body);

    @POST("add_package.php")
    Call<ApiResponse<Void>> addPackage(@Body com.example.annaheventsls.models.PackageRequest request);

    @POST("delete_package.php")
    Call<ApiResponse<Void>> deletePackage(@Body Map<String, Integer> body);

    @GET("user_inquiries.php")
    Call<ApiResponse<List<Inquiry>>> getUserInquiries();

}
