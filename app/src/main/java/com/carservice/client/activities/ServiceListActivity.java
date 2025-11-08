package com.carservice.client.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.carservice.client.R;
import com.carservice.client.adapters.ServiceRequestAdapter;
import com.carservice.client.models.ServiceRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ServiceListActivity extends AppCompatActivity {

    private static final String TAG = "ServiceListActivity";
    RecyclerView rvServices;
    ServiceRequestAdapter adapter;
    FirebaseFirestore db;
    FirebaseAuth auth;
    private ListenerRegistration serviceListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_list);

        rvServices = findViewById(R.id.rvServices);
        rvServices.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        adapter = new ServiceRequestAdapter(new ArrayList<>());
        rvServices.setAdapter(adapter);

        loadServices();
    }

    private void loadServices() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please log in to view service requests", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String uid = currentUser.getUid();

        serviceListener = db.collection("serviceRequests")
                .whereEqualTo("userId", uid)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w(TAG, "Listen failed.", error);
                        return;
                    }

                    if (value == null) return;

                    ArrayList<ServiceRequest> serviceList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : value) {
                        ServiceRequest req = doc.toObject(ServiceRequest.class);
                        if (req != null) { 
                            req.setId(doc.getId());
                            serviceList.add(req);
                        }
                    }
                    adapter.setItems(serviceList);
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (serviceListener != null) {
            serviceListener.remove();
        }
    }
}
