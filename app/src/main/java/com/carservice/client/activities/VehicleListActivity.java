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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Arrays;
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

        adapter = new VehicleAdapter(vehicles, new VehicleAdapter.OnVehicleAction() {
            @Override
            public void onBook(Vehicle v) {
                Intent i = new Intent(VehicleListActivity.this, BookServiceActivity.class);
                i.putExtra("vehicleId", v.getId());
                startActivity(i);
            }

            @Override
            public void onEdit(Vehicle v) {
                Intent i = new Intent(VehicleListActivity.this, EditVehicleActivity.class);
                i.putExtra("vehicleId", v.getId());
                startActivity(i);
            }

            @Override
            public void onDelete(Vehicle v) {
                confirmDelete(v.getId());
            }
        });

        rv.setAdapter(adapter);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnAdd.setOnClickListener(v -> startActivity(new Intent(this, AddVehicleActivity.class)));

        loadVehiclesRealtime();
    }

    private void confirmDelete(String vehicleId) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Delete Vehicle?")
                .setMessage("Are you sure you want to delete this vehicle?")
                .setPositiveButton("Delete", (dialog, which) -> deleteVehicle(vehicleId))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteVehicle(String vehicleId) {
        // Prevent deletion if vehicle is in an active service
        db.collection("serviceRequests")
                .whereEqualTo("vehicleId", vehicleId)
                .whereIn("status", Arrays.asList("pending", "accepted", "in_progress"))
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        Toast.makeText(this, "Vehicle cannot be deleted while in an active service.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // If not in service, proceed with deletion
                    db.collection("vehicles").document(vehicleId)
                            .delete()
                            .addOnSuccessListener(unused ->
                                    Toast.makeText(this, "Vehicle deleted", Toast.LENGTH_SHORT).show()
                            )
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Error deleting: " + e.getMessage(), Toast.LENGTH_LONG).show()
                            );
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error checking service status: " + e.getMessage(), Toast.LENGTH_LONG).show());
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
