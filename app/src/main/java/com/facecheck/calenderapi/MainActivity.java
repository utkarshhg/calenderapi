package com.facecheck.calenderapi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Razorpay Imports
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import org.json.JSONObject;

// Retrofit Imports
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements PaymentResultListener {

    CalendarView calendarView;
    TextView textDate;
    RecyclerView recyclerView;
    Button btnShareWhatsapp, btnPay, btnViewMap;
    ApiInterface api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Preload Razorpay for faster loading
        Checkout.preload(getApplicationContext());

        // Initialize Views
        calendarView = findViewById(R.id.calendarView);
        textDate = findViewById(R.id.textDate);
        recyclerView = findViewById(R.id.recyclerView);
        btnShareWhatsapp = findViewById(R.id.btnShareWhatsapp);
        btnPay = findViewById(R.id.btnPay);
        btnViewMap = findViewById(R.id.btnViewMap);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Retrofit Setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://example.com/api/") // ⚠️ Replace with your actual API URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(ApiInterface.class);

        // 1. WhatsApp Button Logic
        btnShareWhatsapp.setOnClickListener(v -> {
            String message = "Join me at this event on " + textDate.getText().toString();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.setPackage("com.whatsapp");
            intent.putExtra(Intent.EXTRA_TEXT, message);
            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "WhatsApp is not installed", Toast.LENGTH_SHORT).show();
            }
        });

        // 2. Maps Button Logic
        btnViewMap.setOnClickListener(v -> {
            // This assumes you have created MapsActivity.java
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent);
        });

        // 3. Payment Button Logic
        btnPay.setOnClickListener(v -> startPayment());

        // 4. Calendar Selection Logic (RESTORED)
        calendarView.setOnDateChangeListener((view, year, month, day) -> {
            // Month is 0-indexed (Jan = 0), so we add 1
            int correctMonth = month + 1;
            String date = year + "-" + correctMonth + "-" + day;

            textDate.setText("Selected Date: " + date);

            // Fetch events from API
            Call<List<Event>> call = api.getEvents(date);
            call.enqueue(new Callback<List<Event>>() {
                @Override
                public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        EventAdapter adapter = new EventAdapter(response.body());
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(MainActivity.this, "No events found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Event>> call, Throwable t) {
                    textDate.setText("Failed to load events");
                }
            });
        });
    }

    private void startPayment() {
        Checkout checkout = new Checkout();
        // ⚠️ REPLACE THIS WITH YOUR OWN TEST KEY ID FROM RAZORPAY DASHBOARD
        checkout.setKeyID("rzp_test_YourKeyHere123");

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Calendar App");
            options.put("description", "Event Registration Fee");
            options.put("currency", "INR");
            options.put("amount", "10000"); // 10000 paise = ₹100
            options.put("prefill.contact", "9876543210");
            options.put("prefill.email", "test@example.com");

            checkout.open(this, options);
        } catch(Exception e) {
            Toast.makeText(this, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int code, String response) {
        Toast.makeText(this, "Payment Failed: " + response, Toast.LENGTH_SHORT).show();
    }
}