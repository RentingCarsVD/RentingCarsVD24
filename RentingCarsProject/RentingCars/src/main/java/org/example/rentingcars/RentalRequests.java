package org.example.rentingcars;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "rental_requests")

public class RentalRequests {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Cars car;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "car_brand")
    private String carBrand;

    @Column(name = "request_time")
    private Date requestTime;

    @Column(name = "status")
    private String status; // Например: "pending", "approved", "declined"

    public RentalRequests() {
    }

    public RentalRequests(Long id, Cars car, String clientName, String carBrand, Date requestTime, String status) {
        this.id = id;
        this.car = car;
        this.clientName = clientName;
        this.carBrand = carBrand;
        this.requestTime = requestTime;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cars getCar() {
        return car;
    }

    public void setCar(Cars car) {
        this.car = car;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}