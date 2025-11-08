package com.carservice.client.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carservice.client.R;
import com.carservice.client.models.Invoice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InvoiceListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private InvoiceAdapter adapter;
    private List<Invoice> invoiceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_list);

        recyclerView = findViewById(R.id.recyclerViewInvoices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //To add Temporary list for demo purposes
        invoiceList = new ArrayList<>();


        adapter = new InvoiceAdapter(this, invoiceList);
        recyclerView.setAdapter(adapter);
    }


    static class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder> {

        private final Context context;
        private List<Invoice> items;

        public InvoiceAdapter(Context context, List<Invoice> items) {
            this.context = context;
            this.items = items;
        }

        @NonNull
        @Override
        public InvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.item_invoice, parent, false);
            return new InvoiceViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull InvoiceViewHolder holder, int position) {
            Invoice inv = items.get(position);

            holder.tvTitle.setText("Service ID: " + inv.getServiceId());
            holder.tvAmount.setText("R" + inv.getTotalCost());
            holder.tvStatus.setText(inv.isPaid() ? "PAID" : "NOT PAID");

            String date = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    .format(new Date(inv.getTimestamp()));
            holder.tvDate.setText(date);

            holder.itemView.setOnClickListener(v -> {
                Intent i = new Intent(context, InvoiceDetailsActivity.class);
                i.putExtra("invoice", inv);
                context.startActivity(i);
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class InvoiceViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvAmount, tvStatus, tvDate;

            public InvoiceViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tvInvoiceTitle);
                tvAmount = itemView.findViewById(R.id.tvInvoiceCost);
                tvStatus = itemView.findViewById(R.id.tvInvoiceStatus);
                tvDate = itemView.findViewById(R.id.tvInvoiceDate);
            }
        }
    }
}
