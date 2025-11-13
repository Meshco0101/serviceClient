package com.carservice.client.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.carservice.client.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class DashboardActivity extends AppCompatActivity {

    Button btnMyVehicles, btnLogout, btnMyServices, btnMyInvoices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // user welcome & settings
        TextView tvWelcome = findViewById(R.id.tvWelcome);
        ImageView btnSettings = findViewById(R.id.btnSettings);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        FirebaseFirestore.getInstance().collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String name = documentSnapshot.getString("firstname");
                    tvWelcome.setText(getString(R.string.welcome_user, name));
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch user info", Toast.LENGTH_SHORT).show());
        btnSettings.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, SettingsActivity.class))
        );


        btnMyVehicles = findViewById(R.id.btnMyVehicles);
        btnLogout = findViewById(R.id.btnLogout);
        btnMyServices = findViewById(R.id.btnMyServices);
        btnMyInvoices = findViewById(R.id.btnMyInvoices);
        //btnSubmit = findViewById(R.id.btnSubmitRequest);

        // vehicle list button
        btnMyVehicles.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, VehicleListActivity.class)));

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
