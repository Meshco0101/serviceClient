package com.carservice.client.models;

import java.io.Serializable;

public class Invoice implements Serializable {
    private String id;
    private String serviceId;
    private String userId;
    private String vehicleId;
    private String mechanicName;
    private String workDone;
    private String partsUsed;
    private double totalCost;
    private long timestamp;
    private boolean paid;

    public Invoice() {}

    // Getters and setters...
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }

    public String getMechanicName() { return mechanicName; }
    public void setMechanicName(String mechanicName) { this.mechanicName = mechanicName; }

    public String getWorkDone() { return workDone; }
    public void setWorkDone(String workDone) { this.workDone = workDone; }

    public String getPartsUsed() { return partsUsed; }
    public void setPartsUsed(String partsUsed) { this.partsUsed = partsUsed; }

    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }
}
