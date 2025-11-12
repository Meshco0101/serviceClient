package com.carservice.client.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.carservice.client.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

public class EditVehicleActivity extends AppCompatActivity {

    EditText etReg;
    Button btnSave;
    FirebaseFirestore db;
    String vehicleId, uid;

    // SA car plate pattern
    private final Pattern REG_PATTERN = Pattern.compile("^[A-Z0-9\\- ]{1,10}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_vehicle);

        etReg = findViewById(R.id.etReg);
        btnSave = findViewById(R.id.btnSave);

        db = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getUid();
        vehicleId = getIntent().getStringExtra("vehicleId");

        loadVehicleData();

        btnSave.setOnClickListener(v -> updateVehicle());
    }

    private void loadVehicleData() {
        db.collection("vehicles").document(vehicleId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        etReg.setText(doc.getString("registrationNumber"));
                    }
                });
    }

    private void updateVehicle() {
        String reg = etReg.getText().toString().trim().toUpperCase();

        if (reg.isEmpty()) {
            etReg.setError("Registration required");
            return;
        }

        if (!REG_PATTERN.matcher(reg).matches()) {
            etReg.setError("Invalid SA plate (letters, numbers, space, '-')");
            return;
        }

        // prevent editing if vehicle already assigned
        db.collection("serviceRequests")
                .whereEqualTo("vehicleId", vehicleId)
                .whereEqualTo("status", "accepted")
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        Toast.makeText(this, "Cannot edit!!! This vehicle currently in service", Toast.LENGTH_LONG).show();
                        return;
                    }

                    db.collection("vehicles").document(vehicleId)
                            .update("registrationNumber", reg)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Vehicle Updated", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                });
    }
}
