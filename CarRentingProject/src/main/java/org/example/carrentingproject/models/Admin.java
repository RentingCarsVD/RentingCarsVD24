package org.example.carrentingproject.models;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "admins_table")
public class Admin extends User{

    public Admin() {
        super();
    }

    public Admin(String name, String email, String password, String accountType) {
        super(name, email, password, accountType); // Извикване на конструктора на класа-родител
    }
}
