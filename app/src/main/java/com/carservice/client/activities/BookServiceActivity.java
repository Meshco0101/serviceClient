package com.carservice.client.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import com.carservice.client.R;
import com.carservice.client.models.ServiceRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Date;


public class BookServiceActivity extends AppCompatActivity {

    Spinner spServiceType;
    EditText etDescription;
    Button btnSubmit;
    FirebaseFirestore db;
    FirebaseAuth auth;
    String vehicleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_service);

        vehicleId = getIntent().getStringExtra("vehicleId");

        spServiceType = findViewById(R.id.spServiceType);
        etDescription = findViewById(R.id.etDescription);
        btnSubmit = findViewById(R.id.btnSubmitRequest);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        btnSubmit.setOnClickListener(v -> submitRequest());
    }

    private void submitRequest() {
        String serviceType = spServiceType.getSelectedItem().toString();
        String desc = etDescription.getText().toString().trim();

        String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (uid == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Build request model
        ServiceRequest request = new ServiceRequest();
        request.setUserId(uid);
        request.setVehicleId(vehicleId);
        request.setServiceType(serviceType);
        request.setDescription(desc);
        request.setStatus("pending");
        request.setCreatedAt(new Date());



        DocumentReference docRef = db.collection("service_requests").document();
        request.setId(docRef.getId());

        docRef.set(request)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Service request submitted!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}
