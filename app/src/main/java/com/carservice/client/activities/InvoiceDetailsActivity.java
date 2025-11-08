package com.carservice.client.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Build;
import android.widget.TextView;
import android.widget.Toast;
import com.carservice.client.R;
import com.carservice.client.models.Invoice;

public class InvoiceDetailsActivity extends AppCompatActivity {

    TextView tvWorkDone, tvParts, tvCost, tvStatus, tvMechanic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_details);

        Invoice invoice;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            invoice = getIntent().getSerializableExtra("invoice", Invoice.class);
        } else {
            invoice = (Invoice) getIntent().getSerializableExtra("invoice");
        }

        tvWorkDone = findViewById(R.id.tvWork);
        tvParts = findViewById(R.id.tvParts);
        tvCost = findViewById(R.id.tvCost);
        tvStatus = findViewById(R.id.tvStatus);
        tvMechanic = findViewById(R.id.tvMechanic);

        if (invoice != null) {
            tvWorkDone.setText(invoice.getWorkDone());
            tvParts.setText(invoice.getPartsUsed());
            tvCost.setText("Total: R" + invoice.getTotalCost());
            tvStatus.setText(invoice.isPaid() ? "PAID" : "UNPAID");
            tvMechanic.setText("Mechanic: " + invoice.getMechanicName());
        } else {
            Toast.makeText(this, "Could not load invoice details.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
