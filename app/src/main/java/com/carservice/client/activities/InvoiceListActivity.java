package com.carservice.client.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.widget.Toast;

import com.carservice.client.R;
import com.carservice.client.adapters.InvoiceAdapter;
import com.carservice.client.models.Invoice;
import com.carservice.client.utils.FirebaseUtils;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class InvoiceListActivity extends AppCompatActivity {

    RecyclerView rvInvoices;
    InvoiceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_list);

        rvInvoices = findViewById(R.id.recyclerViewInvoices);
        rvInvoices.setLayoutManager(new LinearLayoutManager(this));

        adapter = new InvoiceAdapter(this);
        rvInvoices.setAdapter(adapter);

        loadInvoices();
    }

    private void loadInvoices() {
        String uid = FirebaseUtils.getUid();
        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUtils.getFirestore()
                .collection("invoices")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener((QuerySnapshot snap) -> {
                    List<Invoice> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snap) {
                        Invoice inv = doc.toObject(Invoice.class);
                        inv.setId(doc.getId());
                        list.add(inv);
                    }
                    adapter.setItems(list);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load invoices: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}
