package com.carservice.client.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.carservice.client.R;
import com.carservice.client.models.Invoice;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InvoiceDetailsActivity extends AppCompatActivity {

    private Invoice invoice;

    TextView tvInvoiceRef, tvMechanic, tvInvoiceDate, tvWork, tvParts, tvCost, tvStatus;
    Button btnDownloadPDF, btnShareInvoice, btnPayNow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_details);

        // Get invoice object
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            invoice = getIntent().getSerializableExtra("invoice", Invoice.class);
        } else {
            invoice = (Invoice) getIntent().getSerializableExtra("invoice");
        }

        // Bind UI
        tvInvoiceRef = findViewById(R.id.tvInvoiceRef);
        tvMechanic = findViewById(R.id.tvMechanic);
        tvInvoiceDate = findViewById(R.id.tvInvoiceDate);
        tvWork = findViewById(R.id.tvWork);
        tvParts = findViewById(R.id.tvParts);
        tvCost = findViewById(R.id.tvCost);
        tvStatus = findViewById(R.id.tvStatus);
        btnDownloadPDF = findViewById(R.id.btnDownloadPDF);
        btnShareInvoice = findViewById(R.id.btnShareInvoice);
        btnPayNow = findViewById(R.id.btnPayNow); // Pay button

        if (invoice != null) {
            populateUI();
            btnDownloadPDF.setOnClickListener(v -> generatePDF());
            btnShareInvoice.setOnClickListener(v -> sharePDF());
            btnPayNow.setOnClickListener(v -> openPayFast()); // PayFast integration
        } else {
            Toast.makeText(this, "Invoice not found.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void populateUI() {
        String formattedDate;
        try {
            formattedDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    .format(new Date(invoice.getTimestamp()));
        } catch (Exception e) {
            formattedDate = "N/A";
        }

        tvInvoiceRef.setText(getString(R.string.invoice_ref, invoice.getId()));
        tvMechanic.setText(invoice.getMechanicName());
        tvInvoiceDate.setText(formattedDate);
        tvWork.setText(invoice.getWorkDone());
        tvParts.setText(invoice.getPartsUsed());
        tvCost.setText(getString(R.string.invoice_cost, invoice.getTotalCost()));

        if (invoice.isPaid()) {
            tvStatus.setText(R.string.status_paid);
            tvStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
            btnPayNow.setEnabled(false);
        } else {
            tvStatus.setText(R.string.status_unpaid);
            tvStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
            btnPayNow.setEnabled(true);
        }
    }

    // Opens PayFast Checkout
    private void openPayFast() {
        String ref = invoice.getId();
        double amount = invoice.getTotalCost();
        String userEmail = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getEmail() : "client@example.com";

        // PayFast sandbox URL
        String payfastUrl = "https://sandbox.payfast.co.za/eng/process?" +
                "merchant_id=10000100" +
                "&merchant_key=46f0cd694581a" +
                "&return_url=myapp://payment-success?invoiceId=" + ref +
                "&cancel_url=myapp://payment-cancel?invoiceId=" + ref +
                "&amount=" + amount +
                "&item_name=Invoice%20" + ref +
                "&email_address=" + userEmail;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(payfastUrl));
        startActivity(intent);
    }

    private void generatePDF() {
        try {
            PdfDocument pdf = new PdfDocument();
            Paint paint = new Paint();

            PdfDocument.PageInfo pageInfo =
                    new PdfDocument.PageInfo.Builder(595, 842, 1).create();
            PdfDocument.Page page = pdf.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            int y = 25;
            paint.setTextSize(14f);

            canvas.drawText("Invoice Receipt", 90, y, paint);
            y += 25;
            canvas.drawText("Ref: " + invoice.getId(), 10, y, paint);
            y += 18;
            canvas.drawText("Mechanic: " + invoice.getMechanicName(), 10, y, paint);
            y += 18;
            canvas.drawText("Total: R" + invoice.getTotalCost(), 10, y, paint);
            y += 18;

            String date = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    .format(new Date(invoice.getTimestamp()));
            canvas.drawText("Date: " + date, 10, y, paint);
            y += 20;

            canvas.drawText("Work Done:", 10, y, paint);
            y += 18;
            canvas.drawText(invoice.getWorkDone(), 10, y, paint);
            y += 18;

            canvas.drawText("Parts Used:", 10, y, paint);
            y += 18;
            canvas.drawText(invoice.getPartsUsed(), 10, y, paint);

            pdf.finishPage(page);

            File file = new File(getExternalFilesDir(null),
                    "Invoice_" + invoice.getId() + ".pdf");

            pdf.writeTo(new FileOutputStream(file));
            pdf.close();

            Toast.makeText(this, "PDF saved successfully", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "PDF error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void sharePDF() {
        File file = new File(getExternalFilesDir(null),
                "Invoice_" + invoice.getId() + ".pdf");

        if (!file.exists()) {
            generatePDF();
            if (!file.exists()) {
                Toast.makeText(this, "Failed to generate PDF", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Uri uri = FileProvider.getUriForFile(
                this,
                getPackageName() + ".provider",
                file
        );

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("application/pdf");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(share, "Share Invoice"));
    }
}
