package org.example.carrentingproject.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "clients_table")
public class Client extends User{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_firm_id")
    private Firm selectedFirm;

    @Column(name = "client_name", nullable = false)
    private String clientName;

    @OneToMany(mappedBy = "clientName", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<RentalRequest> rentalRequests = new HashSet<>();


    // Задължителен празен конструктор за Hibernate
    public Client() {
        super(); // Важно: извиква се конструкторът на класа-родител
    }

    public Client(String name, String email, String password, String accountType) {
        super(name, email, password, accountType);
        this.clientName = name;
    }

    // Гетъри и сетъри за полетата
    public Set<RentalRequest> getRentalRequests() {
        return rentalRequests;
    }

    public void setRentalRequests(Set<RentalRequest> rentalRequests) {
        this.rentalRequests = rentalRequests;
    }

    public void addRentalRequest(RentalRequest rentalRequest) {
        rentalRequests.add(rentalRequest);
        rentalRequest.setClientName(this);  // Свързваме заявката с клиента
    }

    public Firm getSelectedFirm() {
        return selectedFirm;
    }

    public void setSelectedFirm(Firm selectedFirm) {
        this.selectedFirm = selectedFirm;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
}