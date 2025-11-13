package com.carservice.client.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carservice.client.R;
import com.carservice.client.models.Vehicle;

import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VH> {

    public interface OnVehicleAction {
        void onBook(Vehicle v);
        void onEdit(Vehicle v);
        void onDelete(Vehicle v);
    }

    private List<Vehicle> items;
    private final OnVehicleAction listener;

    public VehicleAdapter(List<Vehicle> items, OnVehicleAction listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vehicle, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Vehicle v = items.get(position);

        holder.tvTitle.setText(v.getMake() + " " + v.getModel() + " (" + v.getYear() + ")");
        holder.tvSubtitle.setText("Reg: " + v.getRegistrationNumber() + " • VIN: " + v.getVin());

        holder.btnBook.setOnClickListener(view -> listener.onBook(v));
        holder.btnEdit.setOnClickListener(view -> listener.onEdit(v));
        holder.btnDelete.setOnClickListener(view -> listener.onDelete(v));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<Vehicle> newList) {
        this.items.clear();
        this.items.addAll(newList);
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubtitle;
        Button btnBook, btnEdit, btnDelete;

        VH(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvVehicleTitle);
            tvSubtitle = itemView.findViewById(R.id.tvVehicleSubtitle);
            btnBook = itemView.findViewById(R.id.btnBook);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
