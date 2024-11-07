package org.example.rentingcars;

import javax.persistence.*;

@Entity
@Table(name = "cars") // Името на таблицата в базата данни
public class Cars {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "brand")
    private String brand;

    @Column(name = "model")
    private String model;

    @Column(name = "category")
    private String category; // седан, сув, комби

    @Column(name = "type")
    private String type;     // луксозна, семейна, градска

    @Column(name = "year")
    private int year;

    @Column(name = "features")
    private String features; // характеристики (напр. автоматик, бензин и т.н.)

    @Column(name = "is_available")
    private boolean isAvailable;  // дали е на разположение за наемане

    public Cars() {
    }

    public Cars(String brand, String model, String category, String type, int year, String features, boolean isAvailable) {
        this.brand = brand;
        this.model = model;
        this.category = category;
        this.type = type;
        this.year = year;
        this.features = features;
        this.isAvailable = isAvailable;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}