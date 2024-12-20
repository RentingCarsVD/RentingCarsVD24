package org.example.carrentingproject.models;

import org.example.carrentingproject.controllers.RentalRequestController;

import javax.persistence.*;
import java.lang.ref.Cleaner;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "rental_requests")
public class RentalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car; // Обект от тип Cars

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client clientName; // Клиентът, направил заявката

//    @Column(name = "car_brand", nullable = false)
//    private String carBrand; // Марката на колата

    @Column(name = "request_time", nullable = false)
    private LocalDateTime requestTime; // Датата на подаване на заявката

    @Column(name = "rented_days", nullable = false)
    private int rentalDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RentalRequestController.RentalStatus status; // Статус на заявката

    @ManyToOne
    @JoinColumn(name = "operator_id")
    private Operator operatorName; // Операторът, обработващ заявката

    @ManyToOne
    @JoinColumn(name = "firm_id")
    private Firm firm; // Поле за фирмата, към която принадлежи заявката

    // Нови полета за опис протокол
    @Column(name = "car_condition_before")
    private String carConditionBefore;

    @Column(name = "car_condition_after")
    private String carConditionAfter;

    @Column(name = "return_status")
    private String returnStatus;

    public RentalRequest() {}

    public RentalRequest(Car car, Client clientName, LocalDateTime requestTime, int rentalDays, RentalRequestController.RentalStatus status, Operator operatorName, Firm firm, String carConditionBefore, String carConditionAfter, String returnStatus) {
        this.car = car;
        this.clientName = clientName;
        //this.carBrand = carBrand;
        this.requestTime = requestTime;
        this.rentalDays = rentalDays;
        this.status = status;
        this.operatorName = operatorName;
        this.firm = firm;
        this.carConditionBefore = carConditionBefore;
        this.carConditionAfter = carConditionAfter;
        this.returnStatus = returnStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Client getClientName() {
        return clientName;
    }

    public void setClientName(Client clientName) {
        this.clientName = clientName;
    }

//    public String getCarBrand() {
//        return carBrand;
//    }

//    public void setCarBrand(String carBrand) {
//        this.carBrand = carBrand;
//    }

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }

    public int getRentalDays() {
        return rentalDays;
    }

    public void setRentalDays(int rentalDays) {
        this.rentalDays = rentalDays;
    }

    public RentalRequestController.RentalStatus getStatus() {
        return status;
    }

    public void setStatus(RentalRequestController.RentalStatus status) {
        this.status = status;
    }

    public Operator getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(Operator operatorName) {
        this.operatorName = operatorName;
    }

    public Firm getFirm() {
        return firm;
    }

    public void setFirm(Firm firm) {
        this.firm = firm;
    }

    public String getCarConditionBefore() {
        return carConditionBefore;
    }

    public void setCarConditionBefore(String carConditionBefore) {
        this.carConditionBefore = carConditionBefore;
    }

    public String getCarConditionAfter() {
        return carConditionAfter;
    }

    public void setCarConditionAfter(String carConditionAfter) {
        this.carConditionAfter = carConditionAfter;
    }

    public String getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(String returnStatus) {
        this.returnStatus = returnStatus;
    }

    @Override
    public String toString() {
        return "Заявка за кола: {" +
                car.getBrand() + " " + car.getModel() +
                ", status=" + status +
                '}';
    }
}
