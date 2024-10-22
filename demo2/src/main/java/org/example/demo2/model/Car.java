package org.example.demo2.model;

public class Car {
    private String brand;
    private String model;
    private String category; // седан, сув, комби
    private String type;     // луксозна, семейна, градска
    private String year;
    private String features; // характеристики (напр. автоматик, бензин и т.н.)
    private boolean isAvailable;  // дали е на разположение за наемане

    public Car(String brand, String model, String category, String type, String year, String features, boolean isAvailable) {
        this.brand = brand;
        this.model = model;
        this.category = category;
        this.type = type;
        this.year = year;
        this.features = features;
        this.isAvailable = isAvailable;
    }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public String getCategory() { return category; }
    public String getType() { return type; }
    public String getYear() { return year; }
    public String getFeatures() { return features; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    @Override
    public String toString() {
        return brand + " " + model + " (" + category + ", " + type + ") - " + features;
    }
}
