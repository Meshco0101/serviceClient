package com.carservice.client.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Vehicle {
    private String id;
    private String ownerId;
    private String make;
    private String model;
    private int year;
    private String vin;
    private String registrationNumber;
    @ServerTimestamp
    private Date createdAt;

    public Vehicle() {} // for Firestore

    public Vehicle(String ownerId, String make, String model, int year, String vin, String registrationNumber) {
        this.ownerId = ownerId;
        this.make = make;
        this.model = model;
        this.year = year;
        this.vin = vin;
        this.registrationNumber = registrationNumber;
    }

    // getters and setters

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
