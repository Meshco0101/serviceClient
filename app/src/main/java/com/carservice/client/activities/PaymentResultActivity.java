package com.carservice.client.activities;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.carservice.client.R;
import com.carservice.client.utils.FirebaseUtils;
import com.google.firebase.firestore.FirebaseFirestore;

public class PaymentResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_result);

        Uri data = getIntent().getData();
        if (data != null) {
            String host = data.getHost();
            String invoiceId = data.getQueryParameter("invoiceId");

            if ("payment-success".equals(host)) {
                markInvoicePaid(invoiceId);
            } else if ("payment-cancel".equals(host)) {
                Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void markInvoicePaid(String invoiceId) {
        if (invoiceId == null) {
            Toast.makeText(this, "No invoice ID found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        FirebaseFirestore db = FirebaseUtils.getFirestore();
        db.collection("invoices").document(invoiceId)
                .update("paid", true)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Payment successful! Invoice updated.", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error updating invoice: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                });
    }
}
