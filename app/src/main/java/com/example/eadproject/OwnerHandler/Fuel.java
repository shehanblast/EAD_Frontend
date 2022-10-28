package com.example.eadproject.OwnerHandler;

public class Fuel {
    private String fuelId;
    private String stationId;
    private String stationName;
    private String fuelType;
    private String email;
    private Boolean finishStatus;

    public  Fuel(){}

    public Fuel(String fuelId, String stationId, String stationName, String fuelType, String email, Boolean finishStatus) {
        this.fuelId = fuelId;
        this.stationId = stationId;
        this.stationName = stationName;
        this.fuelType = fuelType;
        this.email = email;
        this.finishStatus = finishStatus;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFuelId() {
        return fuelId;
    }

    public void setFuelId(String fuelId) {
        this.fuelId = fuelId;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public Boolean getFinishStatus() {
        return finishStatus;
    }

    public void setFinishStatus(Boolean finishStatus) {
        this.finishStatus = finishStatus;
    }
}
