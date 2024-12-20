package org.example.carrentingproject.models;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "operators_table")
public class Operator extends User{

    public Operator() {
        super();
    }

    public Operator(String name, String email, String password, String accountType) {
        super(name, email, password, accountType); // Извикване на конструктора на класа-родител
    }
}
