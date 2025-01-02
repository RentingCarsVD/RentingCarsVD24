package org.example.carrentingproject.models;

import org.example.carrentingproject.controllers.RentalRequestController;

import javax.persistence.*;
import java.lang.ref.Cleaner;
import java.time.LocalDate;
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

    // Сума, която клиентът дължи
    @Column(name = "amount_due")
    private Double amountDue;

    private LocalDate rentalStartDate;
    private LocalDate rentalEndDate;

    @Column(name = "request_time", nullable = false)
    private LocalDateTime requestTime; // Датата на подаване на заявката

    @Column(name = "rented_days", nullable = false)
    private int rentalDays;

    @Column(name = "initialrentaldays")
    private Integer initialRentalDays;

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
        this.requestTime = requestTime;
        this.rentalDays = rentalDays;
        this.status = status;
        this.operatorName = operatorName;
        this.firm = firm;
        this.carConditionBefore = carConditionBefore;
        this.carConditionAfter = carConditionAfter;
        this.returnStatus = returnStatus;
    }

    public String getCarName() {
        return this.car.getBrand() + " " + car.getModel(); // Ако обектът `Car` е свързан с заявката
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getRentalStartDate() {
        return rentalStartDate;
    }

    public void setRentalStartDate(LocalDate rentalStartDate) {
        this.rentalStartDate = rentalStartDate;
    }

    public LocalDate getRentalEndDate() {
        return rentalEndDate;
    }

    public void setRentalEndDate(LocalDate rentalEndDate) {
        this.rentalEndDate = rentalEndDate;
    }

    public Double getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(double amountDue) {
        this.amountDue = amountDue;
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

    public int getInitialRentalDays() {
        return initialRentalDays;
    }

    public void setInitialRentalDays(int initialRentalDays) {
        this.initialRentalDays = initialRentalDays;
    }

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
        if (status == status.COMPLETED) {
            return "Request ID: " + id +
                    ", Client: " + clientName +
                    ", Status: " + status;
        } else {
            return "Заявка за кола: {" +
                    car.getBrand() + " " + car.getModel() +
                    ", status=" + status +
                    '}';
        }
    }
}
