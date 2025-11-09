package com.carservice.client.activities;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.carservice.client.R;
import com.carservice.client.adapters.VehicleAdapter;
import com.carservice.client.models.Vehicle;
import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
//import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class VehicleListActivity extends AppCompatActivity {

    RecyclerView rv;
    Button btnAdd;
    FirebaseAuth auth;
    FirebaseFirestore db;
    VehicleAdapter adapter;
    List<Vehicle> vehicles = new ArrayList<>();
    private ListenerRegistration vehicleListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_list);

        rv = findViewById(R.id.rvVehicles);
        btnAdd = findViewById(R.id.btnAddVehicle);

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VehicleAdapter(vehicles, vehicle -> {
            Toast.makeText(this, "Booking" + vehicle.getMake(), Toast.LENGTH_SHORT).show();
            // on item click — open detail or booking flow
            Intent intent = new Intent(this, BookServiceActivity.class);
            intent.putExtra("vehicleId", vehicle.getId());
            startActivity(intent);
        });
        rv.setAdapter(adapter);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnAdd.setOnClickListener(v -> startActivity(new Intent(this, AddVehicleActivity.class)));

        loadVehiclesRealtime();
    }

    private void loadVehiclesRealtime() {
        String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (uid == null) { Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show(); return; }

        vehicleListener = db.collection("vehicles")
                .whereEqualTo("ownerId", uid)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Listen failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    vehicles.clear();
                    if (value != null) {
                        for (var doc : value.getDocuments()) {
                            Vehicle v = doc.toObject(Vehicle.class);
                            if (v != null) {
                                v.setId(doc.getId());
                                vehicles.add(v);
                            }
                        }
                    }
                    adapter.setItems(vehicles);
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (vehicleListener != null) {
            vehicleListener.remove();
        }
    }
}
