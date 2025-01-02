package org.example.carrentingproject.models;

import org.example.carrentingproject.controllers.RentalRequestController;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "cars_table") // Името на таблицата в базата данни
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "brand")
    private String brand; // Марка

    @Column(name = "model")
    private String model; // Модел

    @Column(name = "category")
    private String category; // седан, сув, комби

    @Column(name = "type")
    private String type; // луксозна, семейна, градска

    @Column(name = "year")
    private int year; // Година

    @Column(name = "features")
    private String features; // характеристики (напр. автоматик, бензин и т.н.)

    @Column(name = "is_available")
    private boolean isAvailable;  // Дали е на разположение за наемане

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "firm_id")  // Връзка с фирмата
    private Firm firm;

    @Column(name = "kilometers_driven")
    private Double kilometersDriven;
    @Column(name = "kilometers_atStart")
    private Double kilometersAtStart;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RentalRequest> rentalRequests;

    public Car() {}

    public Car(String brand, String model, String category, String type, int year, String features, boolean isAvailable, Firm firm, Double kilometersDriven) {
        this.brand = brand;
        this.model = model;
        this.category = category;
        this.type = type;
        this.year = year;
        this.features = features;
        this.isAvailable = isAvailable;
        this.firm = firm;
        this.kilometersDriven = kilometersDriven;
    }

    public Double getKilometersAtStart() {
        return kilometersAtStart;
    }

    public void setKilometersAtStart(Double kilometersAtStart) {
        this.kilometersAtStart = kilometersAtStart;
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

    public List<RentalRequest> getRentalRequests() {
        return rentalRequests;
    }

    public void setRentalRequests(List<RentalRequest> rentalRequests) {
        this.rentalRequests = rentalRequests;
    }

    public Firm getFirm() {
        return firm;
    }

    public void setFirm(Firm firm) {
        this.firm = firm;
    }

    public Double getKilometersDriven() {
        return kilometersDriven;
    }

    public void setKilometersDriven(Double kilometersDriven) {
        this.kilometersDriven = kilometersDriven;
    }

    @Override
    public String toString()
    {
        if (!isAvailable) {
            return brand + " " + model + " (" + year + ") : наета";
        } else {
            return brand + " " + model + " (" + year + ") : налична";
        }
    }
}
