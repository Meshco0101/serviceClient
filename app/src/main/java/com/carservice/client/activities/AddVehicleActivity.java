package com.carservice.client.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.carservice.client.R;
import com.carservice.client.models.Vehicle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddVehicleActivity extends AppCompatActivity {

    EditText etMake, etModel, etYear, etVIN, etReg;
    Button btnSave;
    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        etMake = findViewById(R.id.etMake);
        etModel = findViewById(R.id.etModel);
        etYear = findViewById(R.id.etYear);
        etVIN = findViewById(R.id.etVIN);
        etReg = findViewById(R.id.etReg);
        btnSave = findViewById(R.id.btnSaveVehicle);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnSave.setOnClickListener(v -> saveVehicle());
    }

    private void saveVehicle() {
        String make = etMake.getText().toString().trim();
        String model = etModel.getText().toString().trim();
        String yearStr = etYear.getText().toString().trim();
        String vin = etVIN.getText().toString().trim().toUpperCase();
        String reg = etReg.getText().toString().trim();

        if (TextUtils.isEmpty(make)) { etMake.setError("Required"); return; }
        if (TextUtils.isEmpty(model)) { etModel.setError("Required"); return; }
        if (TextUtils.isEmpty(yearStr)) { etYear.setError("Required"); return; }
        if (TextUtils.isEmpty(vin)) { etVIN.setError("Required"); return; }
        if (TextUtils.isEmpty(reg)) { etReg.setError("Required"); return; }

        int year;
        try { year = Integer.parseInt(yearStr); }
        catch (NumberFormatException e) { etYear.setError("Invalid year"); return; }

        String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (uid == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        Vehicle vehicle = new Vehicle(uid, make, model, year, vin, reg);

        DocumentReference docRef = db.collection("vehicles").document();
        vehicle.setId(docRef.getId());

        docRef.set(vehicle)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Vehicle saved", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
