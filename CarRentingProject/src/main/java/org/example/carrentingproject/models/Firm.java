package org.example.carrentingproject.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "firms_table")
public class Firm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "operator_names")
    private String operatorNames;

    @OneToMany(mappedBy = "firm", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users = new ArrayList<>();

    // Празен конструктор за Hibernate
    public Firm() {}

    public Firm(String name, String address, String operatorNames) {
        this.name = name;
        this.address = address;
        this.operatorNames = operatorNames;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOperatorNames() {
        return operatorNames;
    }

    public void setOperatorNames(String operatorNames) {
        this.operatorNames = operatorNames;
    }

    @Override
    public String toString() {
        return "Име: " + this.name;
    }
}