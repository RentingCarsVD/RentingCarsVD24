package org.example.carrentingproject;

public class CarStatus {
    private final String carName;
    private final String status;

    public CarStatus(String carName, String status) {
        this.carName = carName;
        this.status = status;
    }

    public String getCarName() {
        return carName;
    }

    public String getStatus() {
        return status;
    }
}

