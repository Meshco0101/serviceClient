package com.carservice.client.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class ServiceRequest {
    private String id;
    private String userId;
    private String vehicleId;
    private String serviceType;
    private String status; // pending, in-progress, completed
    private String description;

    @ServerTimestamp
    private Date createdAt;

    public ServiceRequest() {}

    public ServiceRequest(String userId, String vehicleId, String serviceType, String description) {
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.serviceType = serviceType;
        this.description = description;
        this.status = "pending";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
