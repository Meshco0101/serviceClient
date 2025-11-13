package com.carservice.client.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.carservice.client.R;
import com.carservice.client.activities.InvoiceDetailsActivity;
import com.carservice.client.models.Invoice;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder> {

    private List<Invoice> invoices = new ArrayList<>();
    private final Context context;

    public InvoiceAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public InvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invoice, parent, false);
        return new InvoiceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceViewHolder holder, int position) {
        Invoice inv = invoices.get(position);
        if (inv == null) return;

        holder.title.setText(inv.getServiceId() != null ? inv.getServiceId() : "No Service ID");
        holder.amount.setText("R" + inv.getTotalCost());
        holder.status.setText(inv.isPaid() ? "PAID" : "UNPAID");

        if (inv.getTimestamp() > 0) {
            String date = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date(inv.getTimestamp()));
            holder.date.setText(date);
        } else {
            holder.date.setText("No Date");
        }

        // Hide Pay button for paid invoices
        holder.btnPay.setVisibility(inv.isPaid() ? View.GONE : View.VISIBLE);

        // Open invoice details
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, InvoiceDetailsActivity.class);
            i.putExtra("invoice", inv);
            context.startActivity(i);
        });

        // Handle PayFast link
        holder.btnPay.setOnClickListener(v -> {
            String amount = String.format(Locale.getDefault(), "%.2f", inv.getTotalCost());
            String ref = inv.getId();
            String userEmail = inv.getUserId(); // or replace with actual email from Firestore if needed

            String returnUrl = new Uri.Builder()
                .scheme("myapp")
                .authority("payment-success")
                .appendQueryParameter("invoiceId", ref)
                .build()
                .toString();

            String cancelUrl = new Uri.Builder()
                .scheme("myapp")
                .authority("payment-cancel")
                .appendQueryParameter("invoiceId", ref)
                .build()
                .toString();

            Uri payfastUri = new Uri.Builder()
                    .scheme("https")
                    .authority("sandbox.payfast.co.za")
                    .path("eng/process")
                    .appendQueryParameter("merchant_id", "10043645")
                    .appendQueryParameter("merchant_key", "u65m7eqssf6i4")
                    .appendQueryParameter("return_url", returnUrl)
                    .appendQueryParameter("cancel_url", cancelUrl)
                    .appendQueryParameter("amount", amount)
                    .appendQueryParameter("item_name", "Invoice " + ref)
                    .appendQueryParameter("email_address", userEmail)
                    .build();

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, payfastUri);
            context.startActivity(browserIntent);
        });
    }

    @Override
    public int getItemCount() {
        return invoices.size();
    }

    public void setItems(List<Invoice> newInvoices) {
        if (newInvoices == null) {
            newInvoices = new ArrayList<>();
        }
        InvoiceDiffCallback diffCallback = new InvoiceDiffCallback(this.invoices, newInvoices);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.invoices.clear();
        this.invoices.addAll(newInvoices);
        diffResult.dispatchUpdatesTo(this);
    }

    public static class InvoiceViewHolder extends RecyclerView.ViewHolder {
        TextView title, amount, status, date;
        Button btnPay;

        InvoiceViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvInvoiceTitle);
            amount = itemView.findViewById(R.id.tvInvoiceCost);
            status = itemView.findViewById(R.id.tvInvoiceStatus);
            date = itemView.findViewById(R.id.tvInvoiceDate);
            btnPay = itemView.findViewById(R.id.btnPay);
        }
    }

    private static class InvoiceDiffCallback extends DiffUtil.Callback {
        private final List<Invoice> oldList;
        private final List<Invoice> newList;

        InvoiceDiffCallback(List<Invoice> oldList, List<Invoice> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return Objects.equals(oldList.get(oldItemPosition).getId(), newList.get(newItemPosition).getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Invoice oldInvoice = oldList.get(oldItemPosition);
            Invoice newInvoice = newList.get(newItemPosition);

            // Compare fields that affect the UI
            return oldInvoice.isPaid() == newInvoice.isPaid() &&
                   Double.compare(oldInvoice.getTotalCost(), newInvoice.getTotalCost()) == 0 &&
                   oldInvoice.getTimestamp() == newInvoice.getTimestamp() &&
                   Objects.equals(oldInvoice.getServiceId(), newInvoice.getServiceId());
        }
    }
}
