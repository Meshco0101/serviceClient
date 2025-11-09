package com.carservice.client.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carservice.client.R;
import com.carservice.client.models.ServiceRequest;
import java.util.ArrayList;
import java.util.List;

public class ServiceRequestAdapter extends RecyclerView.Adapter<ServiceRequestAdapter.ViewHolder> {

    private List<ServiceRequest> list = new ArrayList<>();

    public ServiceRequestAdapter(List<ServiceRequest> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ServiceRequest s = list.get(position);
        if (s == null) return; // Extra safety check

        // Null-safe text setting to prevent crashes from incomplete data
        holder.title.setText(s.getServiceType() != null ? s.getServiceType() : "No Type");
        holder.status.setText("Status: " + (s.getStatus() != null ? s.getStatus() : "N/A"));
        holder.vehicle.setText("Vehicle ID: " + (s.getVehicleId() != null ? s.getVehicleId() : "Unknown"));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    
    public void setItems(List<ServiceRequest> serviceList) {
        this.list = (serviceList != null) ? serviceList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, vehicle, status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvServiceTitle);
            vehicle = itemView.findViewById(R.id.tvServiceVehicle);
            status = itemView.findViewById(R.id.tvServiceStatus);
        }
    }
}
