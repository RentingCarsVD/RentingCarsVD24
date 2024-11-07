package org.example.rentingcars;

import javax.persistence.*;

@Entity
@Table(name = "users") // Името на таблицата в базата данни
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Автоматично генериране на ID
    private Long id;

    @Column(name = "name", nullable = false) // Полето не може да бъде NULL
    private String name;

    @Column(name = "email", nullable = false, unique = true) // Уникален имейл
    private String email;

    @Column(name = "password", nullable = false) // Полето не може да бъде NULL
    private String password;

    @Column(name = "account_type", nullable = false) // Съответства на колоната в базата
    private String accountType;

    // Празен конструктор е необходим за Hibernate
    public Users() {
    }

    public Users(String name, String email, String password, String accountType) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.accountType = accountType;
    }

    // Гетъри и сетъри
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}

