package com.carservice.client.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.carservice.client.R;
import com.carservice.client.models.Vehicle;
import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VH> {

    public interface OnItemClick {
        void onClick(Vehicle vehicle);
    }

    private List<Vehicle> items;
    private final OnItemClick listener;

    public VehicleAdapter(List<Vehicle> items, OnItemClick listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vehicle, parent, false);
        return new VH(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Vehicle v = items.get(position);
        holder.tvTitle.setText(v.getMake() + " " + v.getModel() + " (" + v.getYear() + ")");
        holder.tvSubtitle.setText("Reg: " + v.getRegistrationNumber() + " • VIN: " + v.getVin());
        holder.itemView.setOnClickListener(view -> {
            if (listener != null) listener.onClick(v);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<Vehicle> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubtitle;
        VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvVehicleTitle);
            tvSubtitle = itemView.findViewById(R.id.tvVehicleSubtitle);
        }
    }
}
