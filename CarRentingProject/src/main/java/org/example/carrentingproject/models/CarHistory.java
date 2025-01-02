package org.example.carrentingproject.models;


import javax.persistence.*;

@Entity
@Table(name = "car_history_table")
public class CarHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private RentalRequest rentalRequest;

    public CarHistory() {}

    public CarHistory(RentalRequest rentalRequest) {
        this.rentalRequest = rentalRequest;
    }

}
