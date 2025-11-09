package com.carservice.client.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import com.carservice.client.R;
import com.google.firebase.auth.FirebaseAuth;

public class DashboardActivity extends AppCompatActivity {

    Button btnMyVehicles, btnLogout, btnMyServices, btnMyInvoices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        btnMyVehicles = findViewById(R.id.btnMyVehicles);
        btnLogout = findViewById(R.id.btnLogout);
        btnMyServices = findViewById(R.id.btnMyServices);
        btnMyInvoices = findViewById(R.id.btnMyInvoices);
        //btnSubmit = findViewById(R.id.btnSubmitRequest);

        // vehicle list button
        btnMyVehicles.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, VehicleListActivity.class));
        });

        // services list button
        btnMyServices.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, ServiceListActivity.class))
        );

        // invoice list button
        btnMyInvoices.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, InvoiceListActivity.class))
        );

        // Logout button
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
            finish();
        });
    }
}
