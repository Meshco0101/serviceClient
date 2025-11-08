package com.carservice.client.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.carservice.client.R;
import com.carservice.client.activities.InvoiceDetailsActivity;
import com.carservice.client.models.Invoice;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder> {

    private List<Invoice> invoices = new ArrayList<>();
    private Context context;

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
        if (inv == null) return; // Extra safety check

        // Null-safe text setting
        holder.title.setText(inv.getServiceId() != null ? inv.getServiceId() : "No Service ID");
        holder.amount.setText("R" + inv.getTotalCost());

        String status = inv.isPaid() ? "PAID" : "UNPAID";
        holder.status.setText(status);

        // Format timestamp, handle potential zero value
        if (inv.getTimestamp() > 0) {
            String date = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date(inv.getTimestamp()));
            holder.date.setText(date);
        } else {
            holder.date.setText("No Date");
        }


        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, InvoiceDetailsActivity.class);
            i.putExtra("invoice", inv);
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return invoices.size();
    }

    public void setItems(List<Invoice> invoices) {
        this.invoices = (invoices != null) ? invoices : new ArrayList<>();
        notifyDataSetChanged();
    }

    public static class InvoiceViewHolder extends RecyclerView.ViewHolder {
        TextView title, amount, status, date;
        InvoiceViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvInvoiceTitle);
            amount = itemView.findViewById(R.id.tvInvoiceCost);
            status = itemView.findViewById(R.id.tvInvoiceStatus);
            date = itemView.findViewById(R.id.tvInvoiceDate);
        }
    }
}
